// xmarek69
/**
 * Implementace My MALloc
 * Demonstracni priklad pro 1. ukol IPS/2018
 * Ales Smrcka
 */

#include "mmal.h"
#include <sys/mman.h> // mmap
#include <stdbool.h> // bool
#include <assert.h> // assert

#ifndef MAP_ANONYMOUS
#define MAP_ANONYMOUS 0x20
#endif

#ifdef NDEBUG
/**
 * The structure header encapsulates data of a single memory block.
 *   ---+------+----------------------------+---
 *      |Header|DDD not_free DDDDD...free...|
 *   ---+------+-----------------+----------+---
 *             |-- Header.asize -|
 *             |-- Header.size -------------|
 */
typedef struct header Header;
struct header {

    /**
     * Pointer to the next header. Cyclic list. If there is no other block,
     * points to itself.
     */
    Header *next;

    /// size of the block
    size_t size;

    /**
     * Size of block in bytes allocated for program. asize=0 means the block 
     * is not used by a program.
     */
    size_t asize;
};

/**
 * The arena structure.
 *   /--- arena metadata
 *   |     /---- header of the first block
 *   v     v
 *   +-----+------+-----------------------------+
 *   |Arena|Header|.............................|
 *   +-----+------+-----------------------------+
 *
 *   |--------------- Arena.size ---------------|
 */
typedef struct arena Arena;
struct arena {

    /**
     * Pointer to the next arena. Single-linked list.
     */
    Arena *next;

    /// Arena size.
    size_t size;
};

#define PAGE_SIZE (128*1024)

#endif // NDEBUG

Arena *first_arena = NULL;

/**
 * Return size alligned to PAGE_SIZE
 */
static
size_t allign_page(size_t size)
{
    size = (size / PAGE_SIZE + 1) * PAGE_SIZE;
    return size;
}

/**
 * Allocate a new arena using mmap.
 * @param req_size requested size in bytes. Should be alligned to PAGE_SIZE.
 * @return pointer to a new arena, if successfull. NULL if error.
 * @pre req_size > sizeof(Arena) + sizeof(Header)
 */

/**
 *   +-----+------------------------------------+
 *   |Arena|....................................|
 *   +-----+------------------------------------+
 *
 *   |--------------- Arena.size ---------------|
 */
static
Arena *arena_alloc(size_t req_size)
{
    assert(req_size > sizeof(Arena) + sizeof(Header));
    Arena *addr;
    int siz = allign_page(req_size);
    addr = mmap(NULL, siz, PROT_WRITE | PROT_READ, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    if (addr == MAP_FAILED){
        return NULL;
    }
    if(first_arena == NULL){
        first_arena = addr;
    }
    addr->next=NULL;
    addr->size=siz;
    return addr;
}

/**
 * Appends a new arena to the end of the arena list.
 * @param a already allocated arena
 */
static
void arena_append(Arena *a)
{
    if(first_arena!=a){
        Arena *tmp = first_arena;
        while(tmp->next!=NULL){
            tmp=tmp->next;
        }
        tmp->next=a;
    }
}

/**
 * Header structure constructor (alone, not used block).
 * @param hdr       pointer to block metadata.
 * @param size      size of free block
 * @pre size > 0
 */
/**
 *   +-----+------+------------------------+----+
 *   | ... |Header|........................| ...|
 *   +-----+------+------------------------+----+
 *
 *                |-- Header.size ---------|
 */
static
void hdr_ctor(Header *hdr, size_t size)
{
    assert(size > 0);   
    hdr->next = ((Header*)(&first_arena[1]));
    hdr->size = size;
    hdr->asize = 0;
}   

/**
 * Checks if the given free block should be split in two separate blocks.
 * @param hdr       header of the free block
 * @param size      requested size of data
 * @return true if the block should be split
 * @pre hdr->asize == 0
 * @pre size > 0
 */
static
bool hdr_should_split(Header *hdr, size_t size)
{
    assert(hdr->asize == 0);
    assert(size > 0);
    if(sizeof(Header)<((hdr->size) - size)){
        return true;
    }
    return false;
}

/**
 * Splits one block in two.
 * @param hdr       pointer to header of the big block
 * @param req_size  requested size of data in the (left) block.
 * @return pointer to the new (right) block header.
 * @pre   (hdr->size >= req_size + 2*sizeof(Header))
 */
/**
 * Before:        |---- hdr->size ---------|
 *
 *    -----+------+------------------------+----
 *         |Header|........................|
 *    -----+------+------------------------+----
 *            \----hdr->next---------------^
 */
/**
 * After:         |- req_size -|
 *
 *    -----+------+------------+------+----+----
 *     ... |Header|............|Header|....|
 *    -----+------+------------+------+----+----
 *             \---next--------^  \--next--^
 */
static
Header *hdr_split(Header *hdr, size_t req_size)
{
    assert((hdr->size >= req_size + 2*sizeof(Header)));
    if(hdr_should_split(hdr, req_size)){
        Header *tmp;
        tmp = ((void*)hdr)+sizeof(Header)+req_size;
        hdr_ctor(tmp, hdr->size - (sizeof(Header) + req_size));
        tmp->next=hdr->next;
        hdr->next=tmp;
        hdr->size=req_size;
        return tmp;
    }
    return hdr;
}

/**
 * Detect if two adjacent blocks could be merged.
 * @param left      left block
 * @param right     right block
 * @return true if two block are free and adjacent in the same arena.
 * @pre left->next == right
 * @pre left != right
 */
static
bool hdr_can_merge(Header *left, Header *right)
{
    assert(left->next == right);
    assert(left != right);
    if(left->size+((char*)left)+(sizeof(Header))!=((char*)right)){
        return false;

    }
    if(left->asize==0 && right->asize==0 && ((void*)left)+sizeof(Header)+left->size==((void*)right)){
        return true;
    }
    return false;
}

/**
 * Merge two adjacent free blocks.
 * @param left      left block
 * @param right     right block
 * @pre left->next == right
 * @pre left != right
 */
static
void hdr_merge(Header *left, Header *right)
{
    assert(left->next == right);
    assert(left != right);
    if(hdr_can_merge(left,right)){
        left->size=(left->size + right->size+sizeof(Header));
        left->next=right->next;
    }
    // FIXME
}

/**
 * Finds the first free block that fits to the requested size.
 * @param size      requested size
 * @return pointer to the header of the block or NULL if no block is available.
 * @pre size > 0
 */
static
Header *first_fit(size_t size)
{
    assert(size > 0);
 
    bool flag=true;
    Header *tmp_header = (Header*)(&first_arena[1]);

    if (tmp_header!=NULL && first_arena!=NULL){
        Header *prvni_header;
        prvni_header=tmp_header;
        while(flag || tmp_header!=prvni_header)
        {
            flag = false;
            if(tmp_header->size>=size && tmp_header->asize==0){
                return tmp_header;
            }
            else{
                tmp_header=tmp_header->next;
            }
        }
    }
    return NULL;
}

/**
 * Search the header which is the predecessor to the hdr. Note that if 
 * @param hdr       successor of the search header
 * @return pointer to predecessor, hdr if there is just one header.
 * @pre first_arena != NULL
 * @post predecessor->next == hdr
 */
static
Header *hdr_get_prev(Header *hdr)
{
    assert(first_arena != NULL);
    Header *tmp = (Header*)(&first_arena[1]);

    if(tmp->next==hdr){
        return tmp;
    }
    while(tmp->next!=hdr)
    {
        tmp=tmp->next;
    }
    return tmp;

}

/**
 * Allocate memory. Use first-fit search of available block.
 * @param size      requested size for program
 * @return pointer to allocated data or NULL if error or size = 0.
 */
void *mmalloc(size_t size) 
{   
    
    Header *tmp = first_fit(size);

    if(tmp!=NULL){

        Header *alokovany;
        alokovany=tmp;
        Header *tmp2 = tmp->next;
        tmp=hdr_split(alokovany,size);
        if(tmp!=NULL){
            
            if(tmp2!=((Header*)(&first_arena[1]))){

                hdr_merge(tmp, tmp2);
            }
            
        }
        if(alokovany!=NULL){
            alokovany->asize=size;

            return (((char*)alokovany) + sizeof(Header));

        }
    }
    else{

        Arena *tmp_arena;
        tmp_arena = arena_alloc(size+sizeof(Arena)+sizeof(Header));
        arena_append(tmp_arena);
        Header *tmp_header = ((Header*)(&tmp_arena[1]));
        hdr_ctor(tmp_header,tmp_arena->size - sizeof(Arena)- sizeof(Header));
        tmp=tmp_header;
        if(tmp_arena!=first_arena){
            Header *posledni_header=hdr_get_prev(((Header*)(&first_arena[1])));
            posledni_header->next=tmp;            
        }
        tmp_header=hdr_split(tmp_header,size);
        if(tmp_header!=NULL){
            tmp_header->next=((Header*)(&first_arena[1]));
            tmp->next=tmp_header;
        }
        if(tmp!=NULL){
            tmp->asize=size;

            return (((char*)tmp) + sizeof(Header));   
        }
        


    }
    return NULL;
}

/**
 * Free memory block.
 * @param ptr       pointer to previously allocated data
 * @pre ptr != NULL
 */
void mfree(void *ptr)
{   assert(ptr != NULL);    
    Header *tmp_header;
    tmp_header = (((void*)ptr) - sizeof(Header));
    tmp_header->asize = 0;
    Header *tmp_header2 = tmp_header->next;
    if(tmp_header2->asize==0 && tmp_header2!=((Header*)(&first_arena[1]))){
        hdr_merge(tmp_header, tmp_header2);
    }
    tmp_header2=hdr_get_prev(tmp_header);
    if(tmp_header2->asize==0 && tmp_header!=((Header*)(&first_arena[1]))){
        hdr_merge(tmp_header2, tmp_header);
    }

    
}

/**
 * Reallocate previously allocated block.
 * @param ptr       pointer to previously allocated data
 * @param size      a new requested size. Size can be greater, equal, or less
 * then size of previously allocated block.
 * @return pointer to reallocated space or NULL if size equals to 0.
 */
void *mrealloc(void *ptr, size_t size)
{
    if(size>0){
        Header *tmp_header = ((void*)ptr)-sizeof(Header);
        Header *tmp_header2= ((void*)ptr)-sizeof(Header);
        if (tmp_header->size>=size){
            if(tmp_header!=NULL){
                tmp_header->asize=size;
                tmp_header2=hdr_split(tmp_header,size);
                mfree(((char*)tmp_header2) + sizeof(Header));
                return (((char*)tmp_header) + sizeof(Header));  
            }
        }
        else{
            tmp_header2=tmp_header->next;
            if(tmp_header2->asize==0 && ((((void*)tmp_header) + sizeof(Header) + tmp_header->size)==tmp_header2) && ((tmp_header2->size+tmp_header->size)>size)){
                tmp_header->size = tmp_header2->size + tmp_header->size + sizeof(Header);
                tmp_header2->size=0;
                tmp_header->next=tmp_header2->next;
                tmp_header->asize=size;
                    if(tmp_header->size-size>2*sizeof(Header)){
                        tmp_header2=((void*)tmp_header)+sizeof(Header)+size;                    
                        hdr_ctor(tmp_header2, (tmp_header->size-size-sizeof(Header)));
                        tmp_header2->next=tmp_header->next;
                        tmp_header->next=tmp_header2;
                        tmp_header->size=size;
                    }
            }
            else{
                tmp_header=((void*)mmalloc(size))-sizeof(Header);
                char *adresa=((char*)ptr);
                char *newadresa=((char*)tmp_header)+sizeof(Header);
                for (unsigned int i = 0; i < size; ++i){
                    newadresa[i]=adresa[i];
                }
                mfree(ptr);
                tmp_header->asize=size;
            }
            if(tmp_header!=NULL){
                return (((char*)tmp_header) + sizeof(Header));   
            }

        }
    }
    return NULL;
}

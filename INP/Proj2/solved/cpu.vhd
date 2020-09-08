-- cpu.vhd: Simple 8-bit CPU (BrainF*ck interpreter)
-- Copyright (C) 2018 Brno University of Technology,
--                    Faculty of Information Technology
-- Author(s): xmarek69
--

library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_arith.all;
use ieee.std_logic_unsigned.all;

-- ----------------------------------------------------------------------------
--                        Entity declaration
-- ----------------------------------------------------------------------------
entity cpu is
 port (
   CLK   : in std_logic;  -- hodinovy signal
   RESET : in std_logic;  -- asynchronni reset procesoru
   EN    : in std_logic;  -- povoleni cinnosti procesoru
 
   -- synchronni pamet ROM
   CODE_ADDR : out std_logic_vector(11 downto 0); -- adresa do pameti
   CODE_DATA : in std_logic_vector(7 downto 0);   -- CODE_DATA <- rom[CODE_ADDR] pokud CODE_EN='1'
   CODE_EN   : out std_logic;                     -- povoleni cinnosti
   
   -- synchronni pamet RAM
   DATA_ADDR  : out std_logic_vector(9 downto 0); -- adresa do pameti
   DATA_WDATA : out std_logic_vector(7 downto 0); -- mem[DATA_ADDR] <- DATA_WDATA pokud DATA_EN='1'
   DATA_RDATA : in std_logic_vector(7 downto 0);  -- DATA_RDATA <- ram[DATA_ADDR] pokud DATA_EN='1'
   DATA_RDWR  : out std_logic;                    -- cteni z pameti (DATA_RDWR='1') / zapis do pameti (DATA_RDWR='0')
   DATA_EN    : out std_logic;                    -- povoleni cinnosti
   
   -- vstupni port
   IN_DATA   : in std_logic_vector(7 downto 0);   -- IN_DATA obsahuje stisknuty znak klavesnice pokud IN_VLD='1' a IN_REQ='1'
   IN_VLD    : in std_logic;                      -- data platna pokud IN_VLD='1'
   IN_REQ    : out std_logic;                     -- pozadavek na vstup dat z klavesnice
   
   -- vystupni port
   OUT_DATA : out  std_logic_vector(7 downto 0);  -- zapisovana data
   OUT_BUSY : in std_logic;                       -- pokud OUT_BUSY='1', LCD je zaneprazdnen, nelze zapisovat,  OUT_WE musi byt '0'
   OUT_WE   : out std_logic                       -- LCD <- OUT_DATA pokud OUT_WE='1' a OUT_BUSY='0'
 );
end cpu;


-- ----------------------------------------------------------------------------
--                      Architecture declaration
-- ----------------------------------------------------------------------------
architecture behavioral of cpu is

    signal pc: std_logic_vector(11 downto 0) := X"000";
    signal pc_inc: std_logic := '0';
    signal pc_dec: std_logic := '0';
    
    signal cnt: std_logic_vector(7 downto 0) := X"00"; 
    signal cnt_inc: std_logic := '0';
    signal cnt_dec: std_logic := '0';
    
    signal ptr: std_logic_vector(9 downto 0) := "0000000000";   
    signal ptr_inc: std_logic := '0';
    signal ptr_dec: std_logic := '0';  
    
    signal tmp_signal: std_logic_vector(7 downto 0) := X"00";
    signal mpx: std_logic_vector(1 downto 0) := "00";
    
    type fsm_states is (
        start, fsm,
        inkr_uk, dekr_uk,
        inkr_hodn, dekr_hodn, pricti, odecti,
        while_start, while_end, while_2,
        while_3, while_4,
        while_end_2, while_end_3, while_end_4, while_end_5,
        while_end_6, koment, koment_2, koment_cycle, getchar, putchar,
        putchar_2, ret, in_code_comment,
        hex_cisla, hex_pismena       
    );
    
    
    signal curr_s: fsm_states;
    signal next_s: fsm_states;
    
begin
    PROGRAM_COUNTER: process(CLK, RESET, pc, pc_inc, pc_dec)
    begin
        if(RESET = '1') then
            pc <= X"000";
        elsif(rising_edge(CLK)) then
            if(pc_inc = '1') then
                pc <= pc + 1;
            elsif(pc_dec = '1') then
                pc <= pc - 1;
            end if;
        end if;
        CODE_ADDR <= pc;
    end process PROGRAM_COUNTER;
 
    CITAC: process(CLK, RESET, cnt_inc, cnt_dec)
    begin
        if(RESET = '1') then
            cnt <= X"00";
        elsif(rising_edge(CLK)) then
            if(cnt_inc = '1') then
                cnt <= cnt + 1;
            elsif(cnt_dec = '1') then
                cnt <= cnt - 1;
            end if;
        end if;
    end process CITAC;
 
    DATOVY_CITAC: process(CLK, RESET, ptr, ptr_inc, ptr_dec)
    begin
        if(RESET = '1') then
            ptr <= "0000000000";
        elsif(rising_edge(CLK)) then
            if(ptr_inc = '1') then
                ptr <= ptr + 1;
            elsif(ptr_dec = '1') then
                ptr <= ptr - 1;
            end if;
        end if;
        DATA_ADDR <= ptr;
    end process DATOVY_CITAC;

    
    process(CLK, RESET)
    begin
        if(RESET = '1') then
            curr_s <= start;
        elsif(CLK'event and CLK = '1') then
            if(EN = '1') then
                curr_s <= next_s;
            end if;
        end if;
    end process;
  
    process(IN_DATA, DATA_RDATA, mpx, tmp_signal)
    begin
        case(mpx) is
            when "00" => DATA_WDATA <= IN_DATA;
            when "01" => DATA_WDATA <= DATA_RDATA + 1;
            when "10" => DATA_WDATA <= DATA_RDATA - 1;
            when "11" => DATA_WDATA <= tmp_signal;
            when others =>
        end case;
    end process;
    
    fsm_process: process(CODE_DATA, IN_VLD, OUT_BUSY, DATA_RDATA, cnt, curr_s)
    begin
 
        pc_inc <= '0';  
        ptr_inc <= '0'; 
        cnt_inc <= '0';
        pc_dec <= '0';
        ptr_dec <= '0';
        cnt_dec <= '0';
        CODE_EN <= '1';
        DATA_EN <= '0';
        OUT_WE <= '0';
        IN_REQ <= '0';
        mpx <= "00";
	  DATA_RDWR <= '0';        
       
        case curr_s is
            when start =>
                CODE_EN <= '1';
                next_s <= fsm;
            when fsm =>					
                case(CODE_DATA) is
                    when X"3E" => next_s <= inkr_uk;
                    when X"3C" => next_s <= dekr_uk;
                    when X"2B" => next_s <= inkr_hodn;   
                    when X"2D" => next_s <= dekr_hodn; 
                    when X"5B" => next_s <= while_start; 
                    when X"5D" => next_s <= while_end;   
                    when X"2E" => next_s <= putchar;
                    when X"2C" => next_s <= getchar;   
                    when X"23" => next_s <= koment;       
                    when X"30" | X"31" | X"32" | X"33" | X"34" | X"35" | X"36" | X"37" | X"38" | X"39" => next_s <= hex_cisla;
                    when X"41" | X"43" | X"44" | X"45" | X"46" => next_s <= hex_pismena;
                    when X"00" => next_s <= ret;
                    when others => next_s <= in_code_comment;
                end case;                
            when inkr_uk =>
                ptr_inc <= '1';
                pc_inc <= '1';
                next_s <= start;                  
            when dekr_uk =>
                ptr_dec <= '1';
                pc_inc <= '1';
                next_s <= start;                   
            when inkr_hodn =>
                DATA_EN <= '1';
                DATA_RDWR <= '1';
                next_s <= pricti;
            when pricti =>
                mpx <= "01";
                DATA_EN <= '1';
                DATA_RDWR <= '0';
                pc_inc <= '1';
                next_s <= start;        
            when dekr_hodn =>
                DATA_EN <= '1';
                DATA_RDWR <= '1';
                next_s <= odecti;
            when odecti =>
                mpx <= "10";
                DATA_EN <= '1';
                DATA_RDWR <= '0';
                pc_inc <= '1';
                next_s <= start;
            when while_start =>
                pc_inc <= '1';
                DATA_EN <= '1';
                DATA_RDWR <= '1';
                next_s <= while_2;
            when while_2 =>
                if(DATA_RDATA = X"00") then
                    cnt_inc <= '1';
                    next_s <= while_3;
                else
                    next_s <= start;
                end if;
            when while_3 =>
                if(cnt = X"00") then
                    next_s <= start;
                else
                    CODE_EN <= '1';
                    next_s <= while_4;
                end if;
            when while_4 =>
                if(CODE_DATA = X"5B") then
                    cnt_inc <= '1';
                elsif(CODE_DATA = X"5D") then
                    cnt_dec <= '1';
                end if;
                pc_inc <= '1';
                next_s <= while_3;
            when while_end =>
                DATA_EN <= '1';
                DATA_RDWR <= '1';
                next_s <= while_end_2;
            when while_end_2 =>
                if(DATA_RDATA = X"00") then
                    pc_inc <= '1';
                    next_s <= start;
                else
                    next_s <= while_end_3;
                end if;
            when while_end_3 =>
                cnt_inc <= '1';
                pc_dec <= '1';
                next_s <= while_end_4;
            when while_end_4 =>
                if(cnt = X"00") then
                    next_s <= start;
                else
                    CODE_EN <= '1';
                    next_s <= while_end_5;
                end if;
            when while_end_5 =>
                if(CODE_DATA = X"5D") then
                    cnt_inc <= '1';
                elsif(CODE_DATA = X"5B") then
                    cnt_dec <= '1';
                end if;
                next_s <= while_end_6;
            when while_end_6 =>
                if(cnt = X"00") then
                    pc_inc <= '1';
                else
                    pc_dec <= '1';
                end if;
                next_s <= while_end_4;     
            when putchar =>
                if(OUT_BUSY = '1') then
                    next_s <= putchar;
                else
                    DATA_EN <= '1';
                    DATA_RDWR <= '1';
                    next_s <= putchar_2;
                end if;
            when putchar_2 =>
                OUT_WE <= '1';
                OUT_DATA <= DATA_RDATA;
                pc_inc <= '1';
                next_s <= start;
            when getchar =>
                IN_REQ <= '1';
                if(IN_VLD = '0') then
                    next_s <= getchar;
                else
                    mpx <= "00";
                    DATA_EN <= '1';
                    DATA_RDWR <= '0';
                    pc_inc <= '1';
                    next_s <= start;
                end if;             
            when koment =>
		    pc_inc <= '1';
                next_s <= koment_2;
            when koment_2 =>
                CODE_EN <= '1';
                next_s <= koment_cycle;
            when koment_cycle =>
                if CODE_DATA = X"23" then
                    pc_inc <= '1';
                    next_s <= start;
                else
                    next_s <= koment;
                end if;        
            when hex_cisla =>
                DATA_EN <= '1';
                pc_inc <= '1';
                mpx <= "11";
                tmp_signal <= CODE_DATA(3 downto 0) & X"0";
                next_s <= start;
            when hex_pismena =>
                DATA_EN <= '1';
                pc_inc <= '1';
                mpx <= "11";
					 tmp_signal <= (CODE_DATA(3 downto 0) + std_logic_vector(conv_unsigned(9, tmp_signal'LENGTH)(3 downto 0))) & "0000";
                next_s <= start;  
            when ret =>
                next_s <= ret;
            when in_code_comment =>
               pc_inc <= '1';
                next_s <= start;
            when others =>
        end case;
    end process;
end behavioral;
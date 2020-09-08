<?PHP 
//Trida Parser slouzi k analyze vstupniho souboru
class Parser
{
    const INSTRUCTION_POSITION = 0; 
    
    //Pole pro ukladani spravne zapsanych instrukci ve vstupnim souboru
    private $instructions = array();
    

    //funkce analyzuje soubor predavany v parametru
    public function parse($file){

        $firstLine = fgets($file);

        if(!$firstLine){
            fwrite(STDERR, " CHYBA 21: chybná nebo chybějící hlavička ve zdrojovém kódu!");
            exit(ERROR_21);                
        }
        $firstLine=trim($this->removeComment(strtoupper($firstLine)));
        
        while(trim($firstLine)!=".IPPCODE19"){
            $firstLine = fgets($file);
            
            if(!$firstLine){
                fwrite(STDERR, " CHYBA 21: chybná nebo chybějící hlavička ve zdrojovém kódu!");
                exit(ERROR_21);               
            }
            $firstLine=trim($this->removeComment(strtoupper($firstLine)));

            if(empty($firstLine)){  
                continue;
            } 
            if($firstLine == ".IPPCODE19"){        
                break;
            }           
            fwrite(STDERR, " CHYBA 21: chybná nebo chybějící hlavička ve zdrojovém kódu!");
            exit(ERROR_21);
        }

        //Dokud jsou nejake data ve vstupnim souboru k dispozici, tak provadej v cyklu tento kod:
        while($line = fgets($file)){
            //V nactenem radku smaz komentare a bile znaky na okrajich
            $line = trim($this->removeComment($line));
            //Pokud je radek prazdny, tak prejdi k dalsi iteraci
            if(empty($line)){
                continue;
            }
            //Pokud radek nebyl prazdny tak jej analyzuj pomoci funkce parseLine()
            $this->parseLine($line);
        }
                
    }    
    
    //Odstrani komentare z retezce, ktery je teto funkci predany jako argument
    public function removeComment($line){
        //Urci pozici #. Pokud je false, tak v retezci zadne komentare nejsou. V opacnem pripade retezec oreze a vrati jej bez komentaru
        $pos = strpos($line, "#");
        if($pos === false){
            return $line;
        }else{
            if($pos > 0){
                return (substr($line, 0, $pos) . "\n");
            }else{
                return '';
            }
        }
    }
    
    //Funkce bere jako parametr retezec a tento retezec analyzuje
    public function parseLine($line){        
        //Rozdeli predavany retezec na zaklade bilych znaku mezi nimi
        $instruction_parts = preg_split ('/\s+/',trim($line));

        //u instrukce nezalezi na velikosti pisma, proto se budou pouzivat velka pismena
        $instruction = strtoupper($instruction_parts[self::INSTRUCTION_POSITION]);        
        //Vytvor z pole $instruction_parts pole obsahujici pouze argumenty
        $args = $output = array_slice($instruction_parts, 1,count($instruction_parts)-1);
        
        //Nakonec zkontroluj jestli je radek spravne zapsany 
        $this->checkInstructionName($instruction, $args);
        
    }
    
    //Vraci pole ulozenych instrukci
    function getInstructions(){
        return $this->instructions;
    }
   

    //Kontroluje, zda je spravny format instrukce a argumentu predavanych jako argument
    function checkInstructionName($instruction, $args){
        //Nastavi pocet argumentu
        $arg_cout = count($args);
        //Vytvori nove pole pro objekty typu Argument
        $arguments = array();
        
        for($i = 0; $i < $arg_cout; $i++){
            //Vytvori z kazdeho argumentu predavaneho v parametru funkce objekt typu Argument
            $arguments[$i] = new Argument($args[$i]);
        }

        /*Rozhodne o tom jestli je program napsany lexikalne a syntakticky spravne. Vzdy se nejprve zeptame na pocet argumentu a pokud neni nulovy, tak take na typy jednotlivych argumentu. Pokud jsou instrukce predavana v argumentu i jeji argumenty zadany spravne, tak se vytvori objekt typu Instruction a prida se do pole instrukci*/
        switch ($instruction){
            //〈var〉 〈symb〉
            case "MOVE":
            case "NOT":
            case "INT2CHAR":
            case "STRLEN":
            case "TYPE":
                if($arg_cout == 2){
                    if($arguments[0]->isVar() && $arguments[1]->isSymb()){
                        $ins = new Instruction($instruction, $arguments);
                        array_push($this->instructions, $ins);
                    }else{
                        fwrite(STDERR, "CHYBA 23: spatny typ argumentu pro instrukci: " . $instruction . "!\n");
                        exit(ERROR_23);
                    }
                }else{
                    fwrite(STDERR, "CHYBA 23: spatny pocet argumentu pro instrukci: " . $instruction . "!\n");
                    exit(ERROR_23);
                }
                break;
                //nic
            case "CREATEFRAME":
            case "PUSHFRAME":
            case "POPFRAME":
            case "RETURN":
            case "BREAK":
                if($arg_cout == 0){
                    $ins = new Instruction($instruction);
                    array_push($this->instructions, $ins);
                }else{
                    fwrite(STDERR, "CHYBA 23: spatny pocet argumentu pro instrukci: " . $instruction . "!\n");
                    exit(ERROR_23);
                }
                break;
                //〈var〉
            case "DEFVAR":
            case "POPS":
                if($arg_cout == 1){
                    if($arguments[0]->isVar()){
                        $ins = new Instruction($instruction, $arguments);
                        array_push($this->instructions, $ins);                        
                    }else{
                        fwrite(STDERR, "CHYBA 23: spatny typ argumentu pro instrukci: " . $instruction . "!\n");
                        exit(ERROR_23);
                    }
                }else{
                    fwrite(STDERR, "CHYBA 23: spatny pocet argumentu pro instrukci: " . $instruction . "!\n");
                    exit(ERROR_23);
                }
                
                break;
                //〈label〉
            case "CALL":
            case "LABEL":
            case "JUMP":
                if($arg_cout == 1){
                    if($arguments[0]->isLabel()){
                        $ins = new Instruction($instruction, $arguments);
                        array_push($this->instructions, $ins);
                    }else{
                        fwrite(STDERR, "CHYBA 23: spatny typ argumentu pro instrukci: " . $instruction . "!\n");
                        exit(ERROR_23);
                    }
                }else{
                    fwrite(STDERR, "CHYBA 23: spatny pocet argumentu pro instrukci: " . $instruction . "!\n");
                    exit(ERROR_23);
                }
                break;
                //〈symb〉
            case "PUSHS":
            case "WRITE":
            case "EXIT":
            case "DPRINT":
                if($arg_cout == 1){
                    if($arguments[0]->isSymb()){
                        $ins = new Instruction($instruction, $arguments);
                        array_push($this->instructions, $ins);
                    }else{
                        fwrite(STDERR, "CHYBA 23: spatny typ argumentu pro instrukci: " . $instruction . "!\n");
                        exit(ERROR_23);
                    }
                }else{
                    fwrite(STDERR, "CHYBA 23: spatny pocet argumentu pro instrukci: " . $instruction . "!\n");
                    exit(ERROR_23);
                }
                break;
                //〈var〉 〈symb1〉 〈symb2〉
            case "ADD":
            case "SUB":
            case "MUL":
            case "IDIV":
            case "LT":
            case "GT":
            case "EQ":
            case "AND":
            case "OR":
            case "STRI2INT":
            case "CONCAT":
            case "GETCHAR":
            case "SETCHAR":
                if($arg_cout == 3){
                    if($arguments[0]->isVar() && $arguments[1]->isSymb() && $arguments[2]->isSymb()){
                        $ins = new Instruction($instruction, $arguments);
                        array_push($this->instructions, $ins);
                    }else{
                        fwrite(STDERR, "CHYBA 23: spatny typ argumentu pro instrukci: " . $instruction . "!\n");
                        exit(ERROR_23);
                    }
                }else{
                    fwrite(STDERR, "CHYBA 23: spatny pocet argumentu pro instrukci: " . $instruction . "!\n");
                    exit(ERROR_23);
                }
                break;
                //〈var〉 〈type〉
            case "READ":
                if($arg_cout == 2){
                    if($arguments[0]->isVar() && $arguments[1]->isIntBoolString()){
                        $arguments[1]->setType("type");
                        $ins = new Instruction($instruction, $arguments);
                        array_push($this->instructions, $ins);
                    }else{
                        fwrite(STDERR, "CHYBA 23: spatny typ argumentu pro instrukci: " . $instruction . "!\n");
                        exit(ERROR_23);
                    }
                }else{
                    fwrite(STDERR, "CHYBA 23: spatny pocet argumentu pro instrukci: " . $instruction . "!\n");
                    exit(ERROR_23);
                }
                break;
                //〈label〉 〈symb1〉 〈symb2〉
            case "JUMPIFEQ":
            case "JUMPIFNEQ":
                if($arg_cout == 3){
                    if($arguments[0]->isLabel() && $arguments[1]->isSymb() && $arguments[2]->isSymb()){
                        $ins = new Instruction($instruction, $arguments);
                        array_push($this->instructions, $ins);
                    }else{
                        fwrite(STDERR, "CHYBA 23: spatny typ argumentu pro instrukci: " . $instruction . "!\n");
                        exit(ERROR_23);
                    }
                }else{
                    fwrite(STDERR, "CHYBA 23: spatny pocet argumentu pro instrukci: " . $instruction . "!\n");
                    exit(ERROR_23);
                }
                break;
            default:
                fwrite(STDERR, "CHYBA 22: neznámý nebo chybný operační kód ve zdrojovém kódu!\n");
                exit(ERROR_22);
                break;
            }
    }
}
?>
<?PHP 
//Trida argument slouzi pro reprezentaci argumentu. Obsahuje spoustu funkci, ktere usnadnuji praci s argumentem (napriklad ruzne kontroly...)
class Argument
{
    
    
    private $type, $value, $arg='';
    private $var, $label, $symb, $type_type;
    public function __construct($a) {
        $this->arg = $a;
        //Nastavi typ argumentu
        $this->setTypes();
        //nastavi hodnotu argumentu
        $this->setValues();
    }
    
    //Vraci retezec popisujici argument
    public function getArg(){
        return (string)$this->arg;
    }
    
    //Vraci retezec popisujici typ argumentu
    public function getType(){
        return $this->type;
    }
    
    //Vraci retezec popisujici hodnotu argumentu
    public function getValue(){
        return $this->value;
    }

    //Nastavi typ argumentu dle zadaneho parametru $t 
    public function setType($t){
        $this->type = $t;
    }
    
    //Nastavi hodnotu argumentu dle zadaneho parametru $t
    public function setValue($v){
        $this->value = $v;
    }    
    
    //vraci true pokud tento argument patri do <var>, jinak vraci false
    public function isVar(){
        return $this->var;
    }
    
    //vraci true pokud tento argument patri do <label>, jinak vraci false
    public function isLabel(){
        return $this->label;
    }
    
    //vraci true pokud tento argument patri do <symb>, jinak vraci false
    public function isSymb(){
        return $this->symb;
    }
    
    //vraci true, pokud je tento argument stejneho typu jako ten predavany v parametru, jinak vraci false
    public function isSameType($a){
        return ($this->getType() == $a->getType());
    }
    
    //vraci true, pokud je typ tohoto argumentu prvkem mnoziny {int, bool, string}, jinak vraci false
    public function isIntBoolString(){
        return $this->type_type;
    }
    
    //Nastavi typ argumentu na zaklade regularnich vyrazu
    public function setTypes(){
        $this->var = false;
        $this->symb = false;
        $this->label = false;
        $type = '';
        
        //Ulozi postupne do jednotlivych promennych jestli dany argument odpovida nejakemu danemu vzoru a pote v podminkach rozhodneme typ argumentu (ktery na konci funkce ulozime do typu) a take jestli je argument var, symb, label nebo type
        $var = preg_match("/^(TF|LF|GF)@((\p{L}|-|[_$&%*!?])(\p{L}|-|[_$&%*!?]|[0-9])*)$/u", $this->arg);
        $int =  preg_match("/^int@([\+\-])?([0-9])*$/u", $this->arg);
        $bool = preg_match("/^bool@(true|false)$/u", $this->arg);
        $string = preg_match("/^string@(\p{L}|[^(\w\\\)]|\d|[_]|\\\\([0-9][0-9][0-9]))*$/u", $this->arg);
        $nil = preg_match("/^nil@nil$/u", $this->arg);
        $label = preg_match("/^((\p{L}|[_$&%*!?])(\p{L}|-|[_$&%*!?]|[0-9])*)$/u", $this->arg);
        if($var){
            $type = "var";
            $this->var = true;
            $this->symb = true;
        }
        if($int){
            $type = "int";
            $this->symb = true;
        }
        if($bool){
            $type = "bool";
            $this->symb = true;
        }
        if($string){
            $type = "string";
            $this->symb = true;
        }
        if($nil){
            $type = "nil";
            $this->symb = true;
        }
        if($label){
            if(preg_match("/^(int|bool|string)$/u", $this->arg)){
                $this->type_type = true;
            }
            $type = "label";
            $this->label = true;            
        }
        if(!($var||$int||$bool||$string||$nil||$label)){
            fwrite(STDERR, "CHYBA ".ERROR_23." - spatny argument\n");
            exit(ERROR_23);
        }
        $this->setType($type);
    }
    
    //Nastavi hodnotu argumentu na zaklade jeho typu
    public function setValues(){
        if($this->getType() == "var"){
            $this->setValue($this->getArg());
        }else if($this->getType() == "int"){
            $pos = strpos($this->getArg(), "@");
            $this->setValue(substr($this->getArg(), $pos+1));
        }else if($this->getType() == "bool"){
            $pos = strpos($this->getArg(), "@");
            $this->setValue(substr($this->getArg(), $pos+1));
        }else if($this->getType() == "string"){
            $pos = strpos($this->getArg(), "@");
            $this->setValue(substr($this->getArg(), $pos+1));
        }else if($this->getType() == "nil"){
            $pos = strpos($this->getArg(), "@");
            $this->setValue(substr($this->getArg(), $pos+1));
        }else if($this->getType() == "label"){
            $this->setValue($this->getArg());
        }else if($this->getType() == "type"){
            $this->setValue($this->getArg());
        }else{
            $this->setValue("CHYBA");
        }
    }
}
?>
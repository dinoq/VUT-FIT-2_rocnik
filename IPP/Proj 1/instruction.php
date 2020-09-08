<?PHP 
//Trida Instruction slouzi pro reprezentaci instrukce. Uchovava si v sobe pole argumentu a svoje jmeno.
class Instruction
{
    public $instruction_name;
    public $args = array();
    public $arg_value = array();

    public function __construct($i_name, $args = array())
    {
    	//ulozi nazev funkce
        $this->instruction_name = $i_name;
        //Ulozi do instrukce predavane pole argumentu
        $this->args = $args;
        
    }
}
?>
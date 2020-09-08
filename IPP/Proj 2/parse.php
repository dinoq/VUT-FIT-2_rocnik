<?PHP

//konstanty pro exit()
const ERROR_10 = 10; 
const ERROR_21 = 21; 
const ERROR_22 = 22; 
const ERROR_23 = 23; 

const SUCCESS = 0; 

require_once("./parser.php");
require_once("./XML.php");
require_once("./instruction.php");
require_once("./argument.php");    
    
//Zkontroluje argumenty se kterymi byl program spusteny. V pripade spatneho poctu ci formatu argumentu skonci program s chybou
function checkProgramArgs($argc, $argv){
  if($argc < 3 || $argc > 0){
      if($argc == 2){
        
          //Pokud je pocet argumentu 2 a druhy argument je -h nebo --help, tak vytiskne napovedu
          if((strtoupper($argv[1])=="-H") || (strtoupper($argv[1])=="--HELP")){
              fwrite(STDOUT, "Tento program ma nepovinny argument --help ktery vypise tuto napovedu a ukonci se. Bez jeho zadani provede konstrolu lexikalni a syntakticke spravnosti kodu. Bude nacitat standardni vstup a na standardni vystup vytiskne odpovidajici XML reprezentaci kodu\n");
              exit(SUCCESS);
          }else{
              fwrite(STDERR, "CHYBA 10: Špatně zadané argumenty!\n");
          }
      }
  }else{           
    fwrite(STDERR, "CHYBA 10: Špatný počet argumentů nebo špatně zadané argumenty!\n");
    exit(ERROR_10);
  }
}

//Vrati standardni vstup jako soubor
function getSTDIN(){         
    $stdin = fopen('php://stdin', 'r');     
    return $stdin;
}

//Hlavni funkce, ve ktere program zacne sve provadeni
function main($argc, $argv){
    //Zkontroluje argumenty se kterymi byl program spusten
    checkProgramArgs($argc, $argv);
    //ulozi do promenne standardni vstup jako soubor
    $source = getSTDIN();
    
    //Vytvori objekt typu Parser
    $parser = new Parser();
    //Analyzuje $source (standardni vstup)
    $parser->parse($source);
    //Ulozi pole instrukci, ktere vytvoril $parser do pole $parsedInstructions
    $parsedInstructions = $parser->getInstructions();
    
    //Vytvori objekt typu IppXML
    $XML = new IppXML();
    //Vytvoti XML dokument a vypise jej na standardni vystup
    $XML->instructionsToXML($parsedInstructions);
    
    //Ukonci program s exit-code 0
    exit(SUCCESS);
}
main($argc, $argv);
?>
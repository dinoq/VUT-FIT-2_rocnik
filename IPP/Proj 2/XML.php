<?PHP 
//Trida slouzi pro vytvareni a vypsani XML
class IppXML
{
    
    //Vytvori a vypise na standardni vystup XML soubor, vytvoreny na zaklade pole instrukci, predavaneho jako argument
    public function instructionsToXML($parsedInstructions){
        
        //Vytvori novy objekt typu XMLWriter
        $xml = new XMLWriter();
        
        $xml->openMemory();
        
        //Nastavi odsazeni
        $xml->setIndent(true);

        //Vlozi do dokumentu hlavicku
        $xml->startDocument('1.0', 'utf-8');
        
        //Vytvori element program
        $xml->startElement("program");

        //Prida elementu program pozadovany atribut        
        $xml->writeAttribute('language', 'IPPcode19');
        
        //Cyklus postupne vytvari elementy instruce podle predaneho pole instrukci v parametru a v pripade ze obsahuji nejake argumenty tak vytovri elementy take pro ne
        for($i = 0; $i < count($parsedInstructions); $i++){
            $xml->startElement("instruction");
            $xml->writeAttribute('order', ($i+1));
            $xml->writeAttribute('opcode', $parsedInstructions[$i]->instruction_name);
            for($j = 0; $j < count($parsedInstructions[$i]->args); $j++){
                $element_name = "arg" . ($j + 1);
                
                $xml->startElement($element_name);
                $xml->writeAttribute('type', $parsedInstructions[$i]->args[$j]->getType());//int, bool, string, nil, label, type, var
                
                $xml->text($parsedInstructions[$i]->args[$j]->getValue());
                $xml->endElement();
            }
            //Ukonci element instrukce
            $xml->endElement();
        }
        
        //ukonci element program
        $xml->endElement();
        
        //Vytiskne vytvoreny dokument na standardni vystup
        echo $xml->outputMemory();
    }
}
?>
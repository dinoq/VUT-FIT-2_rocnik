package assets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class MoveParser {

    /**
     * 
     * 
     * 
     */
    public MoveParser(){
    }
    Boolean rosadabila=false;
    Boolean rosadacerna=false;
    String whiteMoves[];
    String blackMoves[];
    String blackMovesOriginal[];//puvodni 
    String whiteMovesOriginal[];//puvodni
    /**
     * 
     * @return pole s bilymi tahy na zpracovani
     * 
     */
	public String[] getWhiteMoves() {
		return this.whiteMoves;
	}
	/**
     * 
     * @return pole s cernymi tahy na zpracovani
     * 
     */
	public String[] getBlackMoves() {
		return this.blackMoves;
	}
    /**
     * 
     * @return pole s bilymi tahy na zobrazeni
     * 
     */
        public String[] getWhiteOriginalMoves() {
		return this.whiteMovesOriginal;
	}
    /**
     * 
     * @return pole s cernymi tahy na zobrazeni
     * 
     */
        public String[] getBlackOriginalMoves() {
		return this.blackMovesOriginal;
	}
     /**
     * 
     * nastavi pole s cernymi a bilymi tahy
     * @param file soubor s tahy
     * 
     */
	
    /**
     * nastavi pole s cernymi a bilymi tahy
     * @param file soubor s tahy
     * @return vraci uspesnost nacteni a analyzy souboru. Vraci 0 pokud byl uspech, -1 v pripade neuspechu.
     * @throws java.io.IOException
     */
    public int parseFile(File file) throws IOException {
        boolean skipcerny=false;
        int success=0;
    	int pocetRadku = 0;
    	File countRowsFile = new File(file.getPath());
    	
    	BufferedReader countRowsFileReader = null;
        try {
        	countRowsFileReader = new BufferedReader(new FileReader(countRowsFile));
            while(countRowsFileReader.readLine() != null) {
            	pocetRadku++;
            }
        }
        catch(Exception e){
            //System.out.println(e);
        }
        
        
        this.blackMoves=new String[pocetRadku];
        this.whiteMoves=new String[pocetRadku];
        this.blackMovesOriginal=new String[pocetRadku];
        this.whiteMovesOriginal=new String[pocetRadku];
    
        BufferedReader reader = null;
        String tah;
        try {
            reader = new BufferedReader(new FileReader(file));
        }
        catch(Exception e){
            success=-1;
                    return success;
        }
        String bily="";
        String cerny="";
        String cernytah="";
        String bilytah="";
        String cernyprefix="";
        String bilyprefix="";
            int index=0;
             while((tah = reader.readLine())!=null){
            bilytah="";

            try{
                bily= operateString(tah.split(" ")[1]);
                this.whiteMovesOriginal[index]=tah.split(" ")[1];
                

            }
            catch(Exception e){
                success=-1;
                    return success;
            }
            
           
            try{
                bilyprefix=bily.split("-")[0];
            }
            catch(Exception e){
                success=-1;
                    return success;
            }
            try{
                cerny = operateString(tah.split(" ")[2]);
                this.blackMovesOriginal[index]=tah.split(" ")[2];

            }
            catch(Exception e){
                if (bilyprefix.matches(".*MATE.*")){
                    skipcerny=true;
                }else{
                    success=-1;
                    return success;  
                }
            }
            try{
                
                cernyprefix=cerny.split("-")[0];
            }
            catch(Exception e){
                if (bilyprefix.matches(".*MATE.*")){
                    skipcerny=true;
                }else{
                    success=-1;
                    return success;  
                }
                
            }
            
            if (!skipcerny){
                try{
                    cernytah=cerny.split("-")[1];
                }
                catch(Exception e){
                   if (cernyprefix.matches("SMALLCASTLING.*") || cernyprefix.matches("BIGCASTLING.*")){
                        this.rosadacerna=true;
                    }else {
                        success=-1;
                        return success;
                    }
                }
            }
            try{
                bilytah=bily.split("-")[1];
            }
            catch(Exception e){
                if (bilyprefix.matches("SMALLCASTLING.*") || bilyprefix.matches("BIGCASTLING.*")){
                    this.rosadabila=true;
                }else {
                    success=-1;
                    return success;
                }
            }
            if(!skipcerny){
                if (this.rosadacerna){
                        this.rosadacerna=false;
                        if (cernyprefix.matches("SMALLCASTLING.*")){
                            this.blackMoves[index]=cernyprefix+" "+"Kg8*";
                        }else if(cernyprefix.matches("BIGCASTLING.*")){
                            this.blackMoves[index]=cernyprefix+" "+"Kc8*";                     
                        } 
                    }else{
                        if (cernytah.matches("(K|D|V|S|J|p)[a-h][0-8][a-h][0-8]") || cernytah.matches("[a-h][0-8][a-h][0-8]")){

                            if ("".equals(cernyprefix)){
                                if ( cernytah.matches("[a-h][0-8][a-h][0-8]")){
                                    cernytah="p"+cernytah;
                                }
                                this.blackMoves[index]="NOTAG"+" "+cernytah;
                            }else {
                                if ( cernytah.matches("[a-h][0-8][a-h][0-8]")){
                                    cernytah="p"+cernytah;
                                }   
                                this.blackMoves[index]=cernyprefix+" "+cernytah;
                            }
                        }else if (cernytah.matches("(K|D|V|S|J|p)[a-h][0-8]")||cernytah.matches("(K|D|V|S|J|p)[a-h,0-8][a-h][0-8]")||cernytah.matches("[a-h,0-8][a-h][0-8]")||cernytah.matches("[a-h][0-8]")){

                            if ("".equals(cernyprefix)){
                                if ( cernytah.matches("[a-h,0-8][a-h][0-8]")||cernytah.matches("[a-h][0-8]")){
                                    cernytah="p"+cernytah;
                                } 
                                if (cernytah.matches("p[a-h,0-8][a-h][0-8]")){
                                    cernytah=cernytah+"&";
                                }
                                this.blackMoves[index]="NOTAG"+" "+cernytah+"*";
                                if ( cernytah.matches("[a-h,0-8][a-h][0-8]")||cernytah.matches("[a-h][0-8]")){
                                    cernytah="p"+cernytah;
                                }
                                if (cernytah.matches("p[a-h,0-8][a-h][0-8]")){
                                    cernytah=cernytah+"&";
                                }
                            }else {
                                this.blackMoves[index]=cernyprefix+" "+cernytah+"*";
                            }                    
                        }
                        else{
                            success=-1;
                            return success;
                        }
                    }
                }
                if (this.rosadabila){
                    this.rosadabila=false;
                    if (bilyprefix.matches("SMALLCASTLING.*")){
                        this.whiteMoves[index]=bilyprefix+" "+"Kg1*";
                    }else if(bilyprefix.matches("BIGCASTLING.*")){
                        this.whiteMoves[index]=bilyprefix+" "+"Kc1*";                     
                    } 
                }else{
                    if (bilytah.matches("(K|D|V|S|J|p)[a-h][0-8][a-h][0-8]") || bilytah.matches("[a-h][0-8][a-h][0-8]")){

                        if ("".equals(bilyprefix)){
                            if ( bilytah.matches("[a-h][0-8][a-h][0-8]")){
                                bilytah="p"+bilytah;
                            }                            
                            this.whiteMoves[index]="NOTAG"+" "+bilytah;
                        }else{
                            if ( bilytah.matches("[a-h][0-8][a-h][0-8]")){
                                bilytah="p"+bilytah;
                            }                              
                            this.whiteMoves[index]=bilyprefix+" "+bilytah;
                        }
                    }else if (bilytah.matches("(K|D|V|S|J|p)[a-h][0-8]")||bilytah.matches("(K|D|V|S|J|p)[a-h,0-8][a-h][0-8]")||bilytah.matches("[a-h,0-8][a-h][0-8]")||bilytah.matches("[a-h][0-8]")){
                     
                        if ("".equals(bilyprefix)){
                            if ( bilytah.matches("[a-h,0-8][a-h][0-8]")||bilytah.matches("[a-h][0-8]")){
                                bilytah="p"+bilytah;
                            }
                            if (bilytah.matches("p[a-h,0-8][a-h][0-8]")){
                                bilytah=bilytah+"&";
                            }
                            this.whiteMoves[index]="NOTAG"+" "+bilytah+"*";
                        }else {
                            if ( bilytah.matches("[a-h,0-8][a-h][0-8]")||bilytah.matches("[a-h][0-8]")){
                                bilytah="p"+bilytah;
                            }
                            if (bilytah.matches("p[a-h,0-8][a-h][0-8]")){
                                bilytah=bilytah+"&";
                            }
                            this.whiteMoves[index]=bilyprefix+" "+bilytah+"*";
                        }                    
                    }
                    else{
                        success=-1;
                    return success;
                    }
                }
                if (skipcerny){
                     this.blackMoves[index]="";
                     this.blackMovesOriginal[index]="";
                }
        index++;
        }
            //System.out.println("Toto je array upravenych bilych tahu: "+Arrays.toString(this.whiteMoves));
           // System.out.println("Toto je array upravenych cernych tahu: "+Arrays.toString(this.blackMoves)+"\n");
            //System.out.println("Toto je array puvodnich bilych tahu: "+Arrays.toString(this.whiteMovesOriginal));
            //System.out.println("Toto je array puvodnich cernych tahu: "+Arrays.toString(this.blackMovesOriginal));
        return success;
	}


    /**
     * kontroluje tah a nastavuje prefixy
     * @param tah jeden tah ktery se bude zpracovavat
     * @return vraci upraveny tah pro dalsi zpracovani
     */
    public static String operateString(String tah){
        String prefix="";
        if (tah.matches(".+\\#")){
            tah=tah.replaceFirst("\\#","");
            prefix="MATE "+prefix;

        }
        if (tah.matches(".*x.*")|| (tah.matches("x.*"))){
            tah=tah.replaceFirst("x","");
            prefix="TAKE "+prefix;
        }
        if (tah.matches(".*\\+")){
            tah=tah.replaceFirst("\\+","");
            prefix="CHECK "+prefix;
        }
        if (tah.matches("0\\-0\\-0.*")){
            tah=tah.replaceFirst("0-0-0","");
            prefix="BIGCASTLING "+prefix;
        }
        if (tah.matches("0\\-0.*")){
            tah=tah.replaceFirst("0-0","");
            prefix="SMALLCASTLING "+prefix;

        }
        if (tah.matches(".+K") && !tah.matches("(K|D|V|S|J).*")){
            tah=tah.replaceFirst("K","");
            prefix="CHANGEK "+prefix;

        }else if (tah.matches(".+D") && !tah.matches("(K|D|V|S|J).*")){
            tah=tah.replaceFirst("D","");
            prefix="CHANGED "+prefix;

        }else if (tah.matches(".+V") && !tah.matches("(K|D|V|S|J).*")){
            tah=tah.replaceFirst("V","");
            prefix="CHANGEV "+prefix;

        }else if (tah.matches(".+S") && !tah.matches("(K|D|V|S|J).*")){
            tah=tah.replaceFirst("S","");
            prefix="CHANGES "+prefix;

        }else if (tah.matches(".+J") && !tah.matches("(K|D|V|S|J).*")){
            tah=tah.replaceFirst("J","");
            prefix="CHANGEJ "+prefix;

        }
        if (prefix.length()>2)
            prefix=prefix.substring(0,prefix.length()-1);
       return prefix+"-"+tah; 
    }
    
    /**
     * nastavi pole s cernymi a bilymi tahy
     * @return 
     */

    /**
     * vrátí analyzovaný řetězec do formátu, v jakém s ním bude možné dále pracovat
     * @param stringToParse Řetězec který se má analyzovat
     * @return řetězec ve formátu pro snadné další zpracování
     */
    public static String parseString(String stringToParse) {
		String outputString = "";
		String prefix = "";
		String tah = "";
		
		boolean rosada = false;
		
        try{
        	outputString = operateString(stringToParse);
        }
        catch(Exception e){
            return outputString;
        }
        
        try{
        	prefix = outputString.split("-")[0];
        }
        catch(Exception e){
            return outputString;
        }
                
        try{
        	tah = outputString.split("-")[1];
        }
        catch(Exception e){
           if (prefix.matches("SMALLCASTLING.*") || prefix.matches("BIGCASTLING.*")){
        	   rosada = true;
            }else {
                return outputString;
            }
        }
        
        if (rosada){
        	rosada = false;
                if (prefix.matches("SMALLCASTLING.*")){
                	outputString = prefix+" "+"Kg8*";
                }else if(prefix.matches("BIGCASTLING.*")){
                	outputString = prefix+" "+"Kc8*";                     
                } 
        }else{
            if (tah.matches("(K|D|V|S|J|p)[a-h][0-8][a-h][0-8]") || tah.matches("[a-h][0-8][a-h][0-8]")){
                
                if ("".equals(prefix)){
                    if ( tah.matches("[a-h][0-8][a-h][0-8]")){
                    	tah="p"+tah;
                    }
                    outputString = "NOTAG"+" "+tah;
                }else {
                    if ( tah.matches("[a-h][0-8][a-h][0-8]")){
                    	tah="p"+tah;
                    }   
                    outputString = prefix+" "+tah;
                }
            }else if (tah.matches("(K|D|V|S|J|p)[a-h][0-8]")||tah.matches("(K|D|V|S|J|p)[a-h,0-8][a-h][0-8]")||tah.matches("[a-h,0-8][a-h][0-8]")||tah.matches("[a-h][0-8]")){
               
                if ("".equals(prefix)){
                    if ( tah.matches("[a-h,0-8][a-h][0-8]")||tah.matches("[a-h][0-8]")){
                    	tah="p"+tah;
                    } 
                    if (tah.matches("p[a-h,0-8][a-h][0-8]")){
                    	tah=tah+"&";
                    }
                    outputString="NOTAG"+" "+tah+"*";
                    if ( tah.matches("[a-h,0-8][a-h][0-8]")||tah.matches("[a-h][0-8]")){
                    	tah="p"+tah;
                    }
                    if (tah.matches("p[a-h,0-8][a-h][0-8]")){
                    	tah=tah+"&";
                    }
                }else {
                	outputString = tah+" "+tah+"*";
                }                    
            }
            else{
                return outputString;
            }
        }
		
		return outputString;
	}

}

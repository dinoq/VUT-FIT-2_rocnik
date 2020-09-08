import java.net.*;
import java.io.*;

public class Handler extends Thread  
{ 

     BufferedReader in; 
     PrintWriter out; 
     Socket clientSocket; 
      

    public Handler(Socket clientSocket, BufferedReader in, PrintWriter out)  
    { 
        this.clientSocket = clientSocket; 
        this.in = in; 
        this.out = out; 
    } 

    @Override
    public void run() {

        //constant for changing first compoment of HTTP header (in case of error occurred)
        final int STATUS_CODE_INDEX = 0;

        try{

                final int HTTP_MESAGE_SIZE = 30;
                String response[] = new String[HTTP_MESAGE_SIZE];
                int responseIndex = 0;
                int error = 505;
                String errorMessage = "";

                while(!in.ready()){}//Wait for redy input stream 

                boolean errorOccurred = false;
                String request[] = new String[HTTP_MESAGE_SIZE];//Array for all requests string
                for(int i = 0; ;i++){//Save all request lines to request array
                    request[i] = in.readLine();
                    if ((request[i] == null) || (request[i].length() == 0)){
                        break;
                    }
                    if(i>=request.length-1){
                        request = extendArray(request);
                    }
                    if(request[i].contains("HTTP/1.1")){
                        error = -1;
                        errorMessage = "";
                    }
                    //addToArray(responseIndex++, response, request[i] + "\r\n"); // End of headers
                    //System.out.println(request[i] + "\r\n"); // End of headers
                }
                


                String getVariable = getGetVariable(request);
                if((error == -1) && !getVariable.equals("method_not_supported")){                   
                    addToArray(responseIndex++, response, "HTTP/1.1 200 OK\r\n");// Version & status code OK
                }else{
                    if(error == -1){
                        error = 405;  
                        errorMessage = "Chyba 405: Server nepodporuje metodu, se kterou byl zaslán požadavek na zdroj. Je potřeba zasílat požadavky pomocí metody GET.";                    
                        addToArray(responseIndex++, response, "HTTP/1.1 405 Method Not Allowed\r\n");// Version & error 405
                    }else{
                        errorMessage = "Chyba 505: Server nepodporuje verzi protokolu HTTP použitou v požadavku. Se serverem je potřeba komunikovat pomocí HTTP verze 1.1.";
                        addToArray(responseIndex++, response, "HTTP/1.1 505 HTTP Version Not Supported\r\n");// Version & error 505
                    }
                }

                String contentType = getContentType(request);
                if(contentType == "content-type-not-specified"){
                    addToArray(responseIndex++, response, "Content-Type: text/plain\r\n");
                    error = 406;
                    errorMessage = "Chyba 406: Server umí generovat pouze odpověď, která není klientem podporována. Podporované typy odpovědí jsou: text/plain a application/json.";

                }else{
                    addToArray(responseIndex++, response, "Content-Type: " + contentType + "\r\n");                 
                }

                addToArray(responseIndex++, response, "Connection: keep-alive\r\n");
                addToArray(responseIndex++, response, "Keep-Alive: timeout=60, max=20\r\n");

                addToArray(responseIndex++, response, "\r\n"); // End of headers


                boolean json = false;

                //If error doesn't occurred
                if(error == -1){
                        

                    if(contentType.equals("application/json")){//Set content type
                        json = true;
                    }

                    switch (getVariable){
                        case "hostname":
                            if(json){
                                addToArray(responseIndex++, response, "{ \"hostname\" : \"" + getHostName(request) + "\" }");
                            }else{
                                addToArray(responseIndex++, response, getHostName(request));
                            }
                            break;
                        case "cpu-name":
                            if(json){
                                addToArray(responseIndex++, response, "{ \"cpu-name\" : \"" + getCpuInfo() + "\" }");
                            }else{
                                addToArray(responseIndex++, response, getCpuInfo());
                            }
                            break;
                        case "load":
                            if(json){
                                addToArray(responseIndex++, response, "{ \"load\" : \"" + getLoad(out) + "\" }");
                            }else{
                                addToArray(responseIndex++, response, getLoad(out));
                            }
                            
                            break;
                        default:
                            if((getVariable.startsWith("load?refresh=")) && (getVariable.length() > "load?refresh=".length())){
                                int time = repeatGetLoad(getVariable, out);
                                if(time != -1){            
                                    //Set string for refresh
                                    String refresh = "Refresh: " + time + "; url=" + getURL(getHostName(request), getPort(request), time) + "\r\n"; 

                                    //add string to header, and not to data
                                    addToHTTPHeader(response, refresh);
                                    responseIndex++;//Because we add line to http header

                                    if(json){
                                        addToArray(responseIndex++, response, "{ \"load\" : \"" + getLoad(out) + "\" }");
                                    }else{
                                        addToArray(responseIndex++, response, getLoad(out));
                                    }

                                }
                            }else{
                                //replace first field with error
                                replaceHTTPMessage(STATUS_CODE_INDEX, response, "HTTP/1.1 404 Not Found\r\n");

                                if(json){
                                    addToArray(responseIndex++, response, "{ \"error\" : \"Chyba 404: Stránka nenalezena. Umím pouze /load, /load?refresh=čas, /hostname a /cpu-name\" }");
                                }else{
                                    addToArray(responseIndex++, response, "Chyba 404: Stránka nenalezena. Umím pouze /load, /load?refresh=čas, /hostname a /cpu-name");
                                }
                            }
                            break;
                    }
                }else{
                    if(contentType == "content-type-not-specified"){
                        addToArray(responseIndex++, response, errorMessage);
                    }else{
                        if(contentType.equals("application/json")){//Set content type
                            json = true;
                        }

                        if(json){
                            addToArray(responseIndex++, response, "{ \"error\" : \"" + errorMessage + "\" }");
                        }else{
                            addToArray(responseIndex++, response, errorMessage);
                        }
                    }
                }

                printResponse(response, out);

                out.flush();
                out.close(); 
                in.close();
                clientSocket.close();

        } catch (Exception e) {
            System.out.println("Exception occured: "+e);
        }
    }

    private static void replaceHTTPMessage(int index, String[] response, String line){
        response[index] = line;
    }
    private static void addToHTTPHeader(String response[], String line){
        for(int i = 0; i < response.length; i++){
            if(response == null){
                break;
            }
            if(response[i].equals("\r\n")){
                addToArray(i, response, line); 
                break;                
            }
        }
    }

    private static void addToArray(int index, String response[], String line){
        if(index >= response.length){
            response = extendArray(response);
        }
        if((response[index] != null) /*&& (response[index] != "\r\n")*/){
            //first make space for a new member and then add it
            String tmp = response[index];
            String tmp2;
            response[index] = line;
            if((index+1) < response.length){
                tmp2 = response[index+1];
                response[index+1] = tmp;
                for(int i = index+1; i < response.length-1; i++){
                    tmp = response[i+1];
                    response[i+1] = tmp2;
                    tmp2 = tmp;
                }
            }
        }else{
            response[index] = line;
        }
    }
    private static String[] extendArray(String[] original){
        String[] newArray = new String[original.length*2];
        for(int i = 0; i < original.length; i++){
            newArray[i] = original[i];
        }
        return newArray;
    }

    private static String getGetVariable(String[] request){
        String getVariable = "method_not_supported"; //store content of GET variable of request here
        int i1, i2;
        String searchedString = "GET /";
        for(String req : request){//Search for GET variable of request
            if(req == null){
                break;
            }
            i1 = req.indexOf(searchedString);
            i2 = req.indexOf(' ', i1+searchedString.length());
            if((i1 != -1) && (i2 != -1)){
                getVariable = req.substring(i1+searchedString.length(), i2);
            }
        }
        return getVariable;
    }


    private static String getContentType(String[] request){
        String contentType = "content-type-not-specified";
        String searchedString = "Accept:";
        for(String req : request){//Search for GET variable of request
            if(req == null){
                break;
            }
            if(req.startsWith(searchedString)){
                if(req.contains("text/plain") || req.contains("*/*")){
                    contentType = "text/plain";
                }else if(req.contains("application/json")){
                    contentType = "application/json";                    
                }
            }

        }
        return contentType;
    }

    private static String getHostName(String[] request){
        String hostName = "not_found"; //store content of GET variable of request here
        int i1, i2;
        String searchedString = "Host: ";
        for(String req : request){//Search for GET variable of request
            if(req == null){
                break;
            }
            i1 = req.indexOf(searchedString);
            i2 = req.indexOf(':', i1+searchedString.length());
            if((i1 != -1) && (i2 != -1)){
                hostName = req.substring(i1+searchedString.length(), i2);
            }
        }
        return hostName;
    }



    private static String getPort(String[] request){
        String port = "not_found"; 
        int i1, i2;
        String searchedString = "Host: ";
        for(String req : request){
            if(req == null){
                break;
            }
            i1 = req.indexOf(searchedString);
            i2 = req.indexOf(':', i1+searchedString.length());
            if((i1 != -1) && (i2 != -1)){
                port = req.substring(i2+1, req.length());
            }
        }
        return port;
    }

    private static String getCpuInfo(){

        String clientOutput;
        String line; 
        int idx;

        File file = new File("/proc/cpuinfo"); 

        try{
            BufferedReader br = new BufferedReader(new FileReader(file)); 
            while ((line = br.readLine()) != null) {
                if(line.contains("model name")){
                    idx = line.indexOf(": ")+2;
                    clientOutput = line.substring(idx);
                    return clientOutput;
                }
            }
        }
        catch (Exception e){
            System.err.println("Doslo k chybe:");
            System.err.println(e.toString());
        }

            return "Doslo k chybe - udaj o procesoru nenalezen v souboru '/proc/cpuinfo'";
    }
    private static String getLoad(PrintWriter out){
        String clientOutput;
        String line; 
        int idx;
        try{
            File file = new File("/proc/stat"); 
            BufferedReader br = new BufferedReader(new FileReader(file)); 

            String firstLine = br.readLine();
            String[] data = firstLine.split("\\s+");
            //user    nice   system  idle      iowait irq   softirq  steal
            long prevuser = Long.parseLong(data[1]);
            long prevnice = Long.parseLong(data[2]);
            long prevsystem = Long.parseLong(data[3]);
            long previdle = Long.parseLong(data[4]);
            long previowait = Long.parseLong(data[5]);
            long previrq = Long.parseLong(data[6]);
            long prevsoftirq = Long.parseLong(data[7]);
            long prevsteal = Long.parseLong(data[8]);

            br.close();

            Thread.sleep(900);

            br = new BufferedReader(new FileReader(file)); 
            firstLine = br.readLine();
            data = firstLine.split("\\s+");

            long user = Long.parseLong(data[1]);
            long nice = Long.parseLong(data[2]);
            long system = Long.parseLong(data[3]);
            long idle = Long.parseLong(data[4]);
            long iowait = Long.parseLong(data[5]);
            long irq = Long.parseLong(data[6]);
            long softirq = Long.parseLong(data[7]);
            long steal = Long.parseLong(data[8]);

            long PrevIdle = previdle + previowait;
            long Idle = idle + iowait;

            long PrevNonIdle = prevuser + prevnice + prevsystem + previrq + prevsoftirq + prevsteal;
            long NonIdle = user + nice + system + irq + softirq + steal;

            long PrevTotal = PrevIdle + PrevNonIdle;
            long Total = Idle + NonIdle;

            long totald = Total - PrevTotal;
            long idled = Idle - PrevIdle;
            double CPU_Percentage = 100 * ((totald - idled)/(double)totald);

            br.close();
            int percent = (int) Math.round(CPU_Percentage);

            return (Integer.toString(percent) + "%");
        }
        catch (Exception e){
            System.err.println("Doslo k chybe");
            System.err.println(e.toString());
        }

            return "Doslo k chybe";
    }
    private static int repeatGetLoad(String line, PrintWriter out){

        int indexOfNumber = 0;
        String getVar = "load?refresh=";
        String clientOutput = "";
        int time = -1;

        indexOfNumber = getVar.length();
        clientOutput = line.substring(indexOfNumber);
        
        try{ 
            time = Integer.parseInt(clientOutput); 
        }  
        catch (NumberFormatException e) 
        { 
            System.err.println(clientOutput + " is not a valid integer number"); 
        } 

        return time;
    }

    private static void printResponse(String[] response, PrintWriter out){

        for(String resp : response){
            if(resp == null){
                break;
            }
            out.print(resp); 
            //System.out.print("R:"+resp); 
        }
    }

    private static String getURL(String hostName, String port, int time){
        return "http://" + hostName + ":" + port + "/load?refresh=" + time;
    }
}

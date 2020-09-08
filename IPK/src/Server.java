import java.net.*;
import java.io.*;


public class Server {

    
    public static void main(String[] args) {
        
        int portNumber=12345;

        if (args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }else{
        	try{
        		portNumber = Integer.parseInt(args[0]);
			} catch (Exception e) {
	            System.out.println("Exception occured: "+e);
	            System.exit(1);
	        }
        }

        
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        //constant for changing first compoment of HTTP header (in case of error occurred)
        final int STATUS_CODE_INDEX = 0;

        try{

            serverSocket = new ServerSocket(portNumber);

            for (;;) {//Infinite loop for "infinite" possible connections
                clientSocket = serverSocket.accept();// Wait for a client


                // Get input and output streams to talk to the client
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());


                Thread t = new Handler(clientSocket, in, out); 
                t.start(); 

			}
        } catch (Exception e) {
            System.out.println("Exception occured: "+e);
        }
	}

}

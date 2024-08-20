package pt.ipp.isep.dei.examples.basic.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple TCP/IP server that sends back lines as they are typed. The server port must be passed as a command-line argument.
 * The server replaces a specified keyword in the received string with 'X', hides it, and counts its occurrences.
 * The result, along with the modified string, is sent back to the client.
 */
public class server_java_tcp {

    private ServerSocket serverSocket;

    /**
     * Starts the EchoServer, binding it to the specified port.
     *
     * @param port The port bound to the server at localhost.
     */
    public server_java_tcp(int port){
        serverSocket = null;

        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port: " + port);

        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Waits for a client connection, receives a string and keyword, replaces the keyword with 'X',
     * counts its occurrences, and sends the modified string and instance counter back to the client.
     */
    public void waitConnections(){
        Socket clientSocket = null;

        while(true){
            try {
                clientSocket = serverSocket.accept();

                System.out.println("Connected to " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String strg= in.readLine();
                String keyWord= in.readLine();

                //while ((inputLine = in.readLine()) != null) {


                // replace keyword with 'X' repeated
                String resultString = replaceWord(strg,keyWord);

                //check number of instances of keyword in user string
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(String.valueOf(keyWord)) + "\\b", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(String.valueOf(strg).toLowerCase());
                int instanceCounter = 0;
                while (matcher.find()) {
                    instanceCounter++;
                }
                //System.err.println("instanceCounter> " + instanceCounter);
                //return instanceCounter;

                // send string to client
                //out.println(concatenatedString);
                out.println(resultString);

                // send instance counter to client
                out.println(instanceCounter);

                System.out.println("Client exiting...");
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Server exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Anonymizes the input string containing the keyword, by replacing each character with 'X'.
     *
     * @param keyword The input string to be anonymized, containing the keyword.
     * @return A string with the same length as the input, where each character is replaced with 'X'.
     */
    private String anonymizationData(String keyword) {

        char[] keywordCharsArray = keyword.toCharArray();
        char[] hiddenKeyword = new char[keywordCharsArray.length];

        for (int i = 0; i < keywordCharsArray.length; i++) {
            keywordCharsArray[i] = 'X';
            hiddenKeyword[i] = keywordCharsArray[i];
        }
        return new String(hiddenKeyword);
    }



    /**
     * Replaces occurrences of a specified word (keyword) in a given sentence with a hidden representation.
     * The replacement is case-insensitive.
     *
     * @param str     The original sentence in which the replacement will be performed.
     * @param keyword The keyword to be replaced in the original sentence.
     * @return A new string with occurrences of the keyword replaced by a hidden representation.
     */
    private String replaceWord(String str, String keyword) {

        String newStr = anonymizationData(keyword);

        // Use a case-insensitive regular expression for replacement
        return str.replaceAll("(?i)" + Pattern.quote(keyword), newStr);
    }

    /**
     * Creates a TCP server and waits for client connections.
     *
     * @param args The server's port should be passed here.
     */
    public static void main(String[] args) {
        if (args.length < 1){
            System.err.println("Usage: java <path to TCP server file> <port number>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        server_java_tcp echoServer = new server_java_tcp(port);

        echoServer.waitConnections();
    }

    /**
     * Checks the number of instances of the keyword in the user string.
     * Matches the keyword (pattern) in the userString using a case-insensitive regular expression.
     *
     * @param strgKeywordReceived The keyword to be counted within the string.
     * @param strgReceived        The string in which occurrences of the keyword are counted.
     * @return The number of instances of the specified keyword in the given string.
     * @link https://www.w3schools.com/java/java_regex.asp
     */
    /*private static int getInstanceCounter(String strgKeywordReceived, String strgReceived) {
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(String.valueOf(strgKeywordReceived)) + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(String.valueOf(strgReceived).toLowerCase());
        int instanceCounter = 0;
        while (matcher.find()) {
            instanceCounter++;
        }
        System.err.println("instanceCounter> " + instanceCounter);
        return instanceCounter;
    }
     */
}
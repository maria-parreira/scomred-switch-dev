package pt.ipp.isep.dei.examples.basic.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Represents a TCP client that connects to a TCP server and communicates by sending and receiving data.
 * The client accepts user input for the server's hostname, port, a user string, and a user keyword.
 * It establishes a connection to the server, sends the user string and keyword, and receives a modified
 * string and an instance counter from the server.
 */
public class client_java_tcp {
    private Socket socket;

    /**
     * Constructor method to create socket objects.
     *
     * @param hostname The name of the TCP server to which the client is going to connect.
     * @param port     The port binded to the server living at hostname.
     */
    public client_java_tcp(String hostname, int port) {

        socket = null;
        try {
            socket = new Socket(hostname, port);
        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    /**
     * Repeatedly reads a line from the terminal, sends it to the server, and waits for a reply.
     * This method also reads data from the server and prints it to the screen.
     * The word "Socket Programming" is printed a number of times based on the received instance counter.
     *
     * @param userStr     The user-provided string to be sent to the server.
     * @param userKeyword The user-provided keyword to be sent to the server.
     */
    public void sendData(String userStr, String userKeyword) {

        try {

            PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn =
                    new BufferedReader(
                            new InputStreamReader(System.in));


            // to send userStr to server
            out.println(userStr);
            //to send userKeyword to server
            out.println(userKeyword);

            //read and print the modified string
            System.out.println(in.readLine());

            //read and print the instanceCounter associated string
            String instanceCounter = in.readLine();
            //System.out.println(instanceCounter);
            for (int i = 0; i < Integer.parseInt(instanceCounter); i++) {
                System.out.println("Socket Programming");
            }


            System.out.println("Closing client...");
            // close buffered reader
            in.close();
            // close printwriter
            out.close();
            // close buffered reader teclado
            stdIn.close();
            // close client socket
            socket.close();

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }

    }

    /**
     * The entry point of the TCP client application.
     * Creates an instance of the TCP client with the specified hostname and port and starts the communication with the server.
     *
     * @param args Command-line arguments. Expects the server's hostname, port number, user string, and user keyword.
     *             If any argument is missing or invalid, the program exits with an error message.
     * @throws IOException If an I/O error occurs during the communication with the server.
     */
    public static void main(String[] args) throws IOException {

        BufferedReader stdInUserInput =
                new BufferedReader(
                        new InputStreamReader(System.in));

        // IP address from client
        System.out.println("Enter server name or IP address: ");
        String hostName = stdInUserInput.readLine();

        // int port is in the range from 1024 to 49151
        System.out.println("Enter port: ");
        int port = Integer.parseInt(stdInUserInput.readLine());

        //string from client
        System.out.println("Enter string: ");
        String userStr = stdInUserInput.readLine();

        //verify if port is valid
        if (port < 1024) {
            System.out.println("Invalid port number. Terminating!");
            System.exit(1);
        }
        if (port > 49151) {
            System.out.println("Invalid port number. Terminating!");
            System.exit(1);
        }


        //verify if user string is valid
        if (userStr == null || userStr.trim().isEmpty()) {
            System.err.println("Did not receive valid string from client. Terminating!");
            System.exit(1);
        }

        // keyword from client
        System.out.println("Enter keyword: ");
        String userKeyword = stdInUserInput.readLine();

        //verify if user keyword is valid
        if (userKeyword == null || userKeyword.trim().isEmpty()) {
            System.err.println("Did not receive valid keyword from client. Terminating!");
            System.exit(1);
        }

        client_java_tcp echoClient = new client_java_tcp(hostName, port);

        echoClient.sendData(userStr, userKeyword);

    }
}
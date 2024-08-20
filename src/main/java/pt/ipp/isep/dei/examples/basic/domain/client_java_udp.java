package pt.ipp.isep.dei.examples.basic.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;


/**
 * This class represents a UDP client that sends a string and a keyword to a specified server,
 * receives a modified string with the hidden keyword and a count of how many times the keyword occurred.
 */
public class client_java_udp {

    private DatagramSocket udpSocket;

    private byte[] receiveData = new byte[256];
    private byte[] sendData = new byte[256];


    /**
     * Initializes the client with a timeout for receiving responses.
     * The timeout can be set to a specific value in milliseconds.
     * If the timeout is reached, the client will throw a SocketTimeoutException.
     *
     * @param timeout The timeout in milliseconds before the reception times out.
     */
    public client_java_udp(int timeout) {
        // Create a new DatagramSocket

        try {
            udpSocket = new DatagramSocket();
        // Set the timeout for receiving responses
            udpSocket.setSoTimeout(timeout);// socket closes if it doesn't receive anything before the timeout defined
        } catch (SocketException e) {
            System.err.println("Socket error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a string and a keyword to the server and processes the response.
     * The method sends the length of the string and the keyword to the server, then sends the string and keyword
     * in segments if necessary. It then receives and prints the modified string and the count of how many times
     * the keyword occurred.
     * The method also receives and prints the instance counter from the server.
     * Finally, it closes the UDP socket and other resources on the client side.
     *
     * @param hostName The server's hostname or IP address.
     * @param port     The port number where the server is expected to be listening for incoming UDP packets.
     * @param strg     The string to be sent to the server.
     * @param keyWord  The keyword to be found within the string.
     */
    public void sendData(String hostName, int port, String strg, String keyWord) {

        BufferedReader stdIn =
                new BufferedReader(
                        new InputStreamReader(System.in));

        // Placeholder for actual socket initialization
        DatagramPacket packet = null;


        try {

            InetAddress address = InetAddress.getByName(hostName);

            // Send the length of the main string message to prepare the server for the incoming data size
            byte[] lengthData = ByteBuffer.allocate(4).putInt(strg.length()).array();

            DatagramPacket packetStringLength = new DatagramPacket(lengthData, lengthData.length, address, port);
            udpSocket.send(packetStringLength);



            // Determine the maximum packet size allowed and decide if segmentation is necessary
            int maxPacketSize = udpSocket.getSendBufferSize();
            int chunkSize = Math.min(sendData.length, maxPacketSize);


            StringBuilder appendedData = new StringBuilder();

            // Handle the message segmentation if necessary when it exceeds the maximum packet size
            if (strg.length() > chunkSize) {
                // The main string message is too long and needs to be sent in several packets
                sendToServerInSeveralPackets(port, strg, address, chunkSize, appendedData);
            } else {
                // The main string message is short enough to be sent in a single packet
                sendToServerIn1Packet(hostName, port, strg);
            }


            // Repeat the same process for the keyword
            byte[] lengthKeywordData = ByteBuffer.allocate(4).putInt(keyWord.length()).array();

            DatagramPacket packetKeywordLength = new DatagramPacket(lengthKeywordData, lengthKeywordData.length, address, port);
            udpSocket.send(packetKeywordLength);


            StringBuilder appendedDataKeyword = new StringBuilder();


            if (keyWord.length() > chunkSize) {
                sendToServerInSeveralPackets(port, keyWord, address, chunkSize, appendedDataKeyword);
            } else {

                sendToServerIn1Packet(hostName, port, keyWord);
            }


            // Receive and print the encrypted message from the server
            DatagramPacket packet2 = new DatagramPacket(receiveData, receiveData.length);
            udpSocket.receive(packet2);
            String received = new String(packet2.getData(), 0, packet2.getLength());
            System.out.println(received);

            // Receive, decode, and print the instance counter from the server
            receiveAndPrintInstanceCounter();

            // Close the UDP socket and other resources on the client side

            close();

        } catch (SocketTimeoutException e) {
            System.err.println("Timeout reached: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    /**
     * Closes the DatagramSocket
     */
    public void close() {
        udpSocket.close();
    }

    /**
     * The main method reads server details, the message, and keyword from the user,
     * performs input validation, and sends the data to the server.
     *
     * @param args Command line arguments
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        // Create a BufferedReader object to read user input
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter server IP address: ");
        String hostName = stdIn.readLine();
        // Collect server address, port, user string, and keyword
        System.out.println("Enter server port: ");
        int port = Integer.parseInt(stdIn.readLine());

        System.out.println("Enter string: ");
        String userStr = stdIn.readLine();

        System.out.println("Enter keyword: ");
        String keyWord = stdIn.readLine();

        // Verifies if the port is valid (between 1024 and 49151)
        if (port < 1024) {
            System.out.println("Invalid port number. Terminating!");
            System.exit(1);
        }
        if (port > 49151) {
            System.out.println("Invalid port number. Terminating!");
            System.exit(1);
        }

        // Verifies if the string that was given is valid (not null or empty)
        if (userStr == null || userStr.trim().isEmpty()) {
            System.err.println("Did not receive valid string from client. Terminating!");
            System.exit(1);
        }

        // Verifies if the keyword that was given is valid (not null or empty)
        if (keyWord == null || keyWord.trim().isEmpty()) {
            System.err.println("Did not receive valid keyword from client. Terminating!");
            System.exit(1);
        }
        // Create a new client object and send the data to the server
        client_java_udp echoClient = new client_java_udp(10000);

        echoClient.sendData(hostName, port, userStr, keyWord);
        // Close the BufferedReader object and the client object after the process is complete
        echoClient.close();

    }

    /**
     * Converts a byte array into an integer. This method wraps the byte array into a ByteBuffer
     * and extracts an int value from it.
     *
     * @param bytes The byte array to convert to an integer.
     * @return The integer value represented by the provided byte array.
     */
    public int byteArrayToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getInt();
    }

    /**
     * Attempts to send a keyword to the server in a single UDP packet. The method retries up to five times
     * if acknowledgments (ACK) are not received from the server. If an ACK is received within the first three attempts,
     * it breaks out of the loop. Otherwise, it terminates the process after five failed attempts.
     *
     * @param hostName The hostname or IP address of the server.
     * @param port The port number on the server.
     * @param keyWord The keyword to be sent to the server.
     * @throws IOException If an I/O error occurs during the sending process.
     */
    private void sendToServerIn1Packet(String hostName, int port, String keyWord) throws IOException {
        DatagramPacket packet;
        // counter tries to send string
        for (int counterTries = 0; counterTries < 5; counterTries++) {
            if (counterTries < 3) {
                // Sends the keyword to the server
                sendData = keyWord.getBytes();
                packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(hostName), port);
                udpSocket.send(packet);
                // Receives ACK from the server
                String receivedACK = receiveAck();
                // If the ACK is received, break out of the loop
                if (receivedACK.equals("ACK")) {
                    break;
                }
            } else {
                System.out.println("Failed to send string. Terminating!");
                close();
            }
        }
    }

    /**
     * Sends a string to the server in several packets if its size exceeds the maximum packet size.
     * It breaks the string into segments and sends each segment individually, awaiting an acknowledgment (ACK) for each segment
     * before proceeding. The process retries up to five times for each segment if ACKs are not received.
     * @param port The port number on the server.
     * @param strg The string to be sent to the server.
     * @param address The InetAddress of the server.
     * @param chunkSize The size of each data chunk to be sent in a single packet.
     * @param appendedData A StringBuilder to accumulate the segments of the string that have been successfully sent.
     * @throws IOException If an I/O error occurs during the segment sending process.
     */
    private void sendToServerInSeveralPackets(int port, String strg, InetAddress address, int chunkSize, StringBuilder appendedData) throws IOException {
        for (int counterTries = 0; counterTries < 5; counterTries++) {
            if (counterTries < 3) {
                // Implementation details provided, focusing on segmenting the string, sending each segment,
                // and handling retries for acknowledgment.
                String receivedACK = null;

                for (int i = 0; i < strg.length(); i += chunkSize) {
                    int endIndex = Math.min(i + chunkSize, strg.length());
                    String segment = strg.substring(i, endIndex);

                    sendData = segment.getBytes();
                    DatagramPacket segmentPacket = new DatagramPacket(sendData, sendData.length, address, port);
                    udpSocket.send(segmentPacket);

                    // Receives ACK from the server
                    receivedACK = receiveAck();

                    // Append the current segment to the StringBuilder
                    appendedData.append(segment);

                }
                // If the ACK is received and the appended data matches the original string, break out of the loop
                if (receivedACK.equals("ACK") & appendedData.toString().equals(strg)) {
                    break;
                }
            } else {
                // If the ACK is not received after five attempts, terminate the process
                System.out.println("Failed to send string. Terminating!");
                close();
            }
        }
    }
    /**
     * Waits for and processes an acknowledgment (ACK) from the server. This method is used after sending
     * each packet to ensure the server has received it. The method prints the received ACK for confirmation.
     *
     * @return The acknowledgment message received from the server, typically "ACK".
     * @throws IOException If an I/O error occurs during the reception of the ACK.
     */
    public String receiveAck() throws IOException {
        DatagramPacket ackPacket = new DatagramPacket(receiveData, receiveData.length);
        udpSocket.receive(ackPacket);
        String receivedACK = new String(ackPacket.getData(), 0, ackPacket.getLength());
        System.out.println(receivedACK);
        return receivedACK;
    }

    /**
     * Receives the instance counter from the server and prints a specific message multiple times.
     * This method assumes that the server sends the instance counter as an integer value.
     * After receiving the instance counter, it prints the counter and a predefined message
     * ("Socket Programming") multiple times based on the value of the counter.
     * If an I/O error occurs during the reception, an IOException is thrown.
     *
     * @throws IOException If an I/O error occurs during the reception.
     */
    private void receiveAndPrintInstanceCounter() throws IOException {
        DatagramPacket packet6 = new DatagramPacket(receiveData, receiveData.length);
        udpSocket.receive(packet6);
        int instanceCounter = byteArrayToInt(packet6.getData());
        for (int i = 0; i < instanceCounter; i++) {
            System.out.println("Socket Programming");
        }
    }
}


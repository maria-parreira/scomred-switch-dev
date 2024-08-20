package pt.ipp.isep.dei.examples.basic.domain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple UDP/IP server that receives and processes client data.
 * The server waits for incoming datagrams, processes the received data, and sends responses back to the clients.
 * The server port must be passed as a command-line argument.
 *
 * This class provides methods for creating a UDP server, waiting for client packets,
 * and handling the communication by receiving and processing data in segments.
 *
 * The server supports the reception of two separate data segments: the main string and a keyword.
 * It replaces occurrences of the keyword in the main string with a hidden representation before sending it back to the client.
 * Additionally, the server counts the number of instances of the keyword in the main string and sends the count as part of the response.
 */
public class server_java_udp {

    private DatagramSocket udpSocket;
    private byte[] receiveData = new byte[256];
    private byte[] sendData = new byte[256];

    /**
     * Constructs a UDP server and binds it to the specified port.
     *
     * @param port The UDP port to which the server is bound.
     */
    public server_java_udp(int port) {
        udpSocket = null;

        try {
            //The UDP server creates a DatagramSocket on port x,
            //indicating that it's ready to listen for incoming datagrams.
            this.udpSocket = new DatagramSocket(port);

            System.out.println("Created UDP socket at " + udpSocket.getLocalPort());
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Waits for client data, processes it, and sends back responses.
     * This method enters an infinite loop where the server continuously listens for incoming datagrams,
     * processes the received data in segments, and sends appropriate responses back to the clients.
     *
     * The method handles the reception of two separate data segments: the main string and a keyword.
     * It replaces occurrences of the keyword in the main string with a hidden representation before sending it back to the client.
     * Additionally, the server counts the number of instances of the keyword in the main string and sends the count as part of the response.
     *
     * If a timeout occurs during the reception, the server terminates with an error message.
     * If an I/O error occurs during the process, an error message is printed.
     */
    public void waitPackets() {

        InetAddress remoteHostName = null;
        int remotePort = 0;

        //The server enters an infinite loop where it continuously listens for incoming datagrams.

        while (true) {

            try {

                //receive length
                DatagramPacket packet3 = new DatagramPacket(receiveData, receiveData.length);
                this.udpSocket.receive(packet3);
                int packet3Length = byteArrayToInt(packet3.getData());
                //The receive method of the DatagramSocket waits until a datagram packet is received.
                //The received data is stored in the receiveData byte array.

                int timeOut = 500;

                // Receive the string in segments
                StringBuilder strgReceived = new StringBuilder();
                int receivedLength = 0;

                Map<Integer, String> segmentsMap = new TreeMap<>(); // TreeMap to maintain order

                int sequenceNumber = 0;

                while (receivedLength < packet3Length) {
                    DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                    this.udpSocket.receive(packet);

                    // Retrieve details from the client
                    remoteHostName = packet.getAddress();
                    remotePort = packet.getPort();

                    // Byes to string conversion
                    String segment = new String(packet.getData(), 0, packet.getLength());


                    // Saves the segment in the map with the sequence number as the key
                    segmentsMap.put(sequenceNumber, segment);
                    receivedLength += segment.length();

                    // Send ACK for the segment
                    sendAck(remoteHostName, remotePort);

                    sequenceNumber++;
                }

                // Merges the segments in the map into a single string in the correct order
                for (String segment : segmentsMap.values()) {
                    strgReceived.append(segment);
                }


                //-----------------------------------------------------------------------------------------------
                //KEYWORD
                //receive length keyword
                DatagramPacket packet4 = new DatagramPacket(receiveData, receiveData.length);
                this.udpSocket.receive(packet4);
                int packet4Length = byteArrayToInt(packet4.getData());

                int timeOutKeyword = 500;

                // Receive the string in segments
                StringBuilder strgKeywordReceived = new StringBuilder();
                int receivedKeywordLength = 0;

                Map<Integer, String> segmentsKeywordMap = new TreeMap<>(); // TreeMap to maintain order

                int sequenceNumberKeyword = 0;

                while (receivedKeywordLength < packet4Length) {
                    DatagramPacket packet2 = new DatagramPacket(receiveData, receiveData.length);
                    this.udpSocket.receive(packet2);

                    // Extract client details
                    remoteHostName = packet2.getAddress();
                    remotePort = packet2.getPort();

                    // Convert Bytes to String
                    String segmentKeyword = new String(packet2.getData(), 0, packet2.getLength());


                    // Store the segment in the map based on the sequence number
                    segmentsKeywordMap.put(sequenceNumberKeyword, segmentKeyword);
                    receivedKeywordLength += segmentKeyword.length();

                    // Send ACK for the segment
                    sendAck(remoteHostName, remotePort);

                    sequenceNumberKeyword++;
                }
                // Concatenate segments in order to reconstruct the original string
                for (String segmentKeyword : segmentsKeywordMap.values()) {
                    strgKeywordReceived.append(segmentKeyword);
                }


                //check number of instances of keyword in user string
                int instanceCounter = getInstanceCounter(strgKeywordReceived, strgReceived);


                // send string with hidden keyword
                System.out.println(strgReceived);
                String reply2 = replaceWord(String.valueOf(strgReceived), String.valueOf(strgKeywordReceived));

                // convert a string where the message is stored in array of bytes
                sendData = reply2.getBytes();
                // DatagramPacket object that sends data to the client. To do this, it needs the message, the number of bytes, the hostName and the client port.
                DatagramPacket packet5 = new DatagramPacket(sendData, sendData.length, remoteHostName, remotePort);
                udpSocket.send(packet5);

                //send counter with number of instances of keyword in user string
                sendData = ByteBuffer.allocate(4).putInt(instanceCounter).array();
                DatagramPacket packet6 = new DatagramPacket(sendData, sendData.length, remoteHostName, remotePort);
                udpSocket.send(packet6);
            }

            // if time out is reached
            catch (SocketTimeoutException ste) {
                System.err.println("Did not receive valid string from client. Terminating!");
                System.exit(1);
            } catch (IOException e) {
                System.err.println("I/O error: " + e.getMessage());
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
     * The entry point of the UDP server application.
     * Creates an instance of the UDP server with the specified port and starts waiting for client packets.
     *
     * @param args Command-line arguments. Expects the server's port number as the first argument.
     *             If the port number is not provided, the program exits with an error message.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java <path to UDP server file> <port number>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        server_java_udp echoServer = new server_java_udp(port);

        echoServer.waitPackets();
    }

    /**
     * Converts a byte array to an integer value using ByteBuffer.
     *
     * @param bytes The byte array to be converted to an integer.
     * @return The integer value represented by the input byte array.
     */
    public int byteArrayToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getInt();
    }

    /**
     * Sends an acknowledgment message ("ACK") to the specified remote host and port.
     *
     * @param remoteHostName The InetAddress of the remote host to which the acknowledgment is sent.
     * @param remotePort     The port number of the remote host to which the acknowledgment is sent.
     * @throws IOException If an I/O error occurs while sending the acknowledgment.
     */
    public void sendAck(InetAddress remoteHostName, int remotePort) throws IOException {
        String ackMessage = "ACK";
        sendData = ackMessage.getBytes();
        DatagramPacket ackPacket = new DatagramPacket(sendData, sendData.length, remoteHostName, remotePort);
        udpSocket.send(ackPacket);
    }


    /**
     *  @link https://www.w3schools.com/java/java_regex.asp
     *  Checks the number of instances of keyword in user string
     *  Matches the keyword (pattern) in usrString
     *  Removes toLowerCase if it wants to match exactly the provided keyword
     *  Counts the occurrences of the specified word
     *
     * @param strgKeywordReceived
     * @param strgReceived
     */

    private static int getInstanceCounter(StringBuilder strgKeywordReceived, StringBuilder strgReceived) {
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(String.valueOf(strgKeywordReceived)) + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(String.valueOf(strgReceived).toLowerCase());
        int instanceCounter = 0;

        while (matcher.find()) {
            instanceCounter++;
        }
        return instanceCounter;
    }
}
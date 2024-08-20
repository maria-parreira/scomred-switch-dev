# Project Overview

This project consists of a server-client communication system using TCP/IP protocol.
Both TCP (Transmission Control Protocol) and UDP (User Datagram Protocol) are used to communicate between server and client
in the two separate protocols that were implemented.
In these protocols, the server processes a string received from the client, where
it replaces a specified keyword with "X" in the string, counts the occurrences of the keyword,
and sends the modified string to the client.

# TCP - Transmission Control Protocol

TCP is a reliable, connection-oriented protocol in the Internet Protocol suite.
It ensures error-free, ordered data delivery between server and client, which is why TCP
is widely used for applications requiring dependable communication.

## Files

1. **server_java_tcp.java**
 - Contains the TCP server implementation;
 - Listens for client connections on a specified port;
 - Anonymizes a given keyword in a received string;
 - Counts the occurrences of the keyword;
 - Sends the modified string and count back to the client.

2. **client_java_tcp.java**
 - Implements the TCP client;
 - Connects to the specified server and port;
 - Sends a string and keyword to the server;
 - Receives the modified string and repeats the sentence "Socket Programming" based on the keyword count.

## Usage

### TCP Server:

1. Run the `server_java_tcp.java` file.
2. The TCP server will start listening for client connections on the specified port.
3. Anonymize a given keyword in a received string.
4. Count the occurrences of the keyword.
5. Send the modified string and count back to the client.

## Core Elements

 - *ServerSocket:* The main component used for listening to incoming client connections on a specified port. It initializes the server and connects it to a port, making it listen for connections.
 - *BufferedReader:* Reads lines of text from the client through the socket's input stream.
 - *PrintWriter:* Sends data back to the client through the socket's output stream.

## TCP Client:

1. Run the `client_java_tcp.java` file.
2. Connect to the specified server and port using TCP.
3. Send a string and keyword to the server.
4. Receive the modified string.
5. Observe the repeated sentence "Socket Programming" based on the keyword count.

## Core Elements

 - *Socket:* Used for establishing and managing a connection with the server, allowing for two-way communication.
 - *BufferedReader:* Utilized for reading data from both the user input via the system console and the server responses through the socket's input stream.
 - *PrintWriter:* Responsible for sending data to the server by writing to the socket's output stream.



# UDP - User Datagram Protocol

UDP is a simple and fast communication protocol in the Internet Protocol suite. 
Unlike TCP, it doesn't ensure reliability or ordered delivery of data. 
This makes it suitable for situations where a quick and lightweight 
communication method is more important than ensuring every piece of data 
arrives in order and reliably.

## Files

1. **server_java_udp.java**
    - Contains the UDP server implementation.
    - Listens for incoming datagrams on a specified port.
    - Receives data packets, such as keyword lengths and segmented strings, from clients.
    - Anonymizes a given keyword in a received string and counts occurrences.
    - Sends the modified string and count back to the client.

2. **client_java_udp.java**
    - Implements the UDP client.
    - Connects to the specified server and port using datagrams.
    - Sends data packets, including keyword lengths and segmented strings, to the server.
    - Receives the modified string and repeats the sentence "Socket Programming" based on the keyword count.

## Usage

1. **Server:**
    - Run the `server_java_udp.java` file.
    - The server will start listening for incoming UDP datagrams on the specified port.

## Core Elements
- *UDP Communication:* Facilitates sending and receiving data packets through UDP sockets.
- *Segment Handling:* Collects and reconstructs message segments for complete data processing.
- *Keyword Processing:* Obscures specified keywords within messages to ensure confidentiality.
- *Acknowledgment Responses:* Confirms receipt of data packets to clients, enhancing reliability.
- *Keyword Occurrence Tracking:* Counts the frequency of the keyword within the received messages.
- *Dynamic Data Assembly:* Utilizes a TreeMap to order message segments based on sequence for accurate message assembly.
- *Server Readiness Reporting:* Notifies when the server is ready and listening on a specified port.

    
2. **Client:**
    - Run the `client_java_udp.java` file.
    - The client will establish a connection with the server using UDP datagrams.
    - Input a string and a keyword for processing.
    - Receive the modified string and observe the repeated sentence based on the keyword count.

## Core Elements
- *Timeout:* Sets the maximum waiting time for receiving responses from the server.
- *Send Buffer:* A byte array for storing and sending data to the server.
- *Receive Buffer:* A byte array for receiving data from the server.
- *Server IP Address:* Specifies the destination server's IP address.
- *Server Port Number:* The port number on the server to which data is sent.
- *Acknowledgment Reception:* Handles the reception of acknowledgment messages from the server, specifically looking for "ACK"


## References
- lmn@isep.ipp.pt - 27/12/2023 -"TCPEchoServer.java" (N/A) Type: source code
- lmn@isep.ipp.pt - 27/12/2023 -"TCPEchoClient.java" (N/A) Type: source code
- lmn@isep.ipp.pt - 27/12/2023 -"UDPEchoServer.java" (N/A) Type: source code
- lmn@isep.ipp.pt - 27/12/2023 -"UDPEchoClient.java" (N/A) Type: source code
- Oracle(2023)"Class DatagramSocket" [Official Java Documentation]. Java Platform Standard Edition 8 Documentation.
- Oracle (2023) "Class BufferedReader" [Official Documentation]. Java Platform Standard Edition 8 Documentation.
- Oracle(2023)"Class InetAddress" [Official Java Documentation], Java Platform Standard Edition 8 Documentation.
- Oracle (2023) "Class ServerSocket" [Official Documentation]. Java Platform Standard Edition 8 Documentation.
- Oracle (2023) "Class InputStreamReader" [Official Documentation]. Java Platform Standard Edition 8 Documentation
- Oracle (2023) "Class String" [Official Documentation]. Java Platform Standard Edition 8 Documentation.
- Oracle (2023) "Class StringBuilder" [Official Documentation]. Java Platform Standard Edition 8 Documentation.
- Oracle (2023) "Class Pattern" [Official Documentation]. Java Platform Standard Edition 8 Documentation.
- Oracle (2023) "Class Matcher" [Official Documentation]. Java Platform Standard Edition 8 Documentation.
- Oracle (2023) "Class ByteBuffer" [Official Documentation]. Java Platform Standard Edition 8 Documentation.
- Oracle (2023) "Class Map" [Official Documentation]. Java Platform Standard Edition 8 Documentation.
- https://www.w3schools.com/java/java_regex.asp


## Contributors

- [Maria Parreira](https://github.com/mariaparreira-code)
- [Filipa Cardoso](https://github.com/filipacardoso)
- [Maria Neto](https://github.com/maria-neto)
- [Francisco Martins](https://github.com/FranciscoRamosMartins)
- [Tiago Pereira](https://github.com/tiagopereiraswitch)
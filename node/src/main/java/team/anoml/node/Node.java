package team.anoml.node;

import team.anoml.node.core.RoutingTable;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.exception.NodeException;
import team.anoml.node.impl.TCPServer;
import team.anoml.node.impl.UDPServer;
import team.anoml.node.util.SystemSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Node {

    private static Logger logger = Logger.getLogger(Node.class.getName());

    private static UDPServer udpServer;
    private static TCPServer tcpServer;

    private static RoutingTable routingTable = RoutingTable.getRoutingTable();

    private static String bootstrapIP = SystemSettings.getBootstrapIP();
    private static int bootstrapPort = SystemSettings.getBootstrapPort();
    private static String username = SystemSettings.getUsername();

    public static void main(String[] args) {
        try (Socket clientSocket = new Socket(bootstrapIP, bootstrapPort);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String messageText = String.format(SystemSettings.REG_MSG_FORMAT, bootstrapIP, bootstrapPort, username);

            String lengthText = "0000" + (messageText.length() + 5);
            lengthText = lengthText.substring(lengthText.length() - 4);
            messageText = lengthText + " " + messageText;

            out.println(messageText);

            char[] chars = new char[8192];
            int read;
            read = in.read(chars);

            String response = String.valueOf(chars, 0, read);

            String[] parts = response.split(" ");
            String noOfNodes = parts[2];
            switch (Integer.valueOf(noOfNodes)) {
                case 0:
                    //nothing to do until another node finds it out
                    break;
                case 1:
                    routingTable.addEntry(new RoutingTableEntry(parts[3], Integer.valueOf(parts[4])));
                    break;
                case 2:
                    routingTable.addEntry(new RoutingTableEntry(parts[3], Integer.valueOf(parts[4])));
                    routingTable.addEntry(new RoutingTableEntry(parts[5], Integer.valueOf(parts[6])));
                    break;
                default:
                    Random random = new Random();
                    int randInt1 = random.nextInt(Integer.valueOf(noOfNodes));
                    int randInt2 = random.nextInt(Integer.valueOf(noOfNodes));

                    while (randInt1 == randInt2) {
                        randInt2 = random.nextInt(Integer.valueOf(noOfNodes));
                    }

                    routingTable.addEntry(new RoutingTableEntry(parts[(randInt1 + 1) * 2 + 1], Integer.valueOf(parts[(randInt1 + 1) * 2 + 2])));
                    routingTable.addEntry(new RoutingTableEntry(parts[(randInt1 + 1) * 2 + 1], Integer.valueOf(parts[(randInt1 + 1) * 2 + 2])));
                    break;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Starting node failed", e);
            throw new NodeException("Starting node failed", e);
        }

        for (RoutingTableEntry entry : routingTable.getAllEntries()) {
            logger.log(Level.INFO, entry.getIP() + ":" + entry.getPort());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(Node::stopServers));

        startServers();
    }

    private static void startServers() {
        tcpServer = new TCPServer();
        udpServer = new UDPServer();

        Thread tcpServerThread = new Thread(tcpServer);
        Thread udpServerThread = new Thread(udpServer);

        tcpServerThread.start();
        udpServerThread.start();

        try {
            udpServerThread.join();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Waiting for UDP server unsuccessful", e);
        }
    }

    private static void stopServers() {
        tcpServer.stopServer();
        udpServer.stopServer();
    }
}

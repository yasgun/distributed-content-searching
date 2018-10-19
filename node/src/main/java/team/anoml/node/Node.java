package team.anoml.node;

import team.anoml.node.impl.RoutingTable;
import team.anoml.node.impl.RoutingTableEntry;
import team.anoml.node.impl.UDPServer;
import team.anoml.node.util.SystemSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Node {

    private static UDPServer udpServer;
    private static RoutingTable routingTable;
    private static String ipAddress;
    private static int port;
    private static String username;

    private static Socket clientSocket = null;
    private static PrintWriter out = null;
    private static BufferedReader in = null;

    public static void main(String[] args) {
        routingTable = new RoutingTable();
        ipAddress = SystemSettings.getIPAddress();

        Scanner reader = new Scanner(System.in);
        System.out.println("Enter the port number: ");
        port = reader.nextInt();

        System.out.println(port);

        username = SystemSettings.getUsername();
        udpServer = new UDPServer(port);
        udpServer.setRoutingTable(routingTable);

        // Add Bootstrapping logic here
        try {
            startConnection(SystemSettings.BOOTSTRAP_IP, SystemSettings.BOOTSTRAP_PORT);
            String messageText = String.format(SystemSettings.REG_MSG_FORMAT, ipAddress, port, username);

            String lengthText = "0000" + String.valueOf(messageText.length() + 5);
            lengthText = lengthText.substring(lengthText.length() - 4);
            messageText = lengthText + " " + messageText;

            System.out.println(messageText);

            String response = sendMessage(messageText);

            String[] parts = response.split(" ");
            String noOfNodes = parts[2];
            switch (Integer.valueOf(noOfNodes)) {
                case 0:
                    //
                    break;
                case 1:
                    routingTable.addEntry(new RoutingTableEntry(parts[3], Integer.valueOf(parts[4])));
                    break;
                case 2:
                    routingTable.addEntry(new RoutingTableEntry(parts[3], Integer.valueOf(parts[4])));
                    routingTable.addEntry(new RoutingTableEntry(parts[5], Integer.valueOf(parts[6])));
                    break;
//                default:
//                    Random random = new Random();
//                    int randInt1 = random.nextInt(Integer.valueOf(noOfNodes));
//                    int randInt2 = random.nextInt(Integer.valueOf(noOfNodes));
//
//                    while (randInt1 == randInt2) {
//                        randInt2 = random.nextInt(Integer.valueOf(noOfNodes));
//                    }
//
//                    routingTable.addEntry(new RoutingTableEntry(parts[randInt1 * 2 + 1], Integer.valueOf(parts[randInt1 * 2 + 2])));
//                    routingTable.addEntry(new RoutingTableEntry(parts[randInt2 * 2 + 1], Integer.valueOf(parts[randInt2 * 2 + 2])));
//                    break;
            }

            for (RoutingTableEntry entry : routingTable.getAllEntries()) {
                System.out.print(entry.getIP() + ":" + entry.getPort());
            }

            Runtime.getRuntime().addShutdownHook(new Thread(Node::stopConnection));

            udpServer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public static void stopConnection() {
        try {
            if (in != null && out != null & clientSocket != null) {
                in.close();
                out.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String sendMessage(String outMessage) {
        out.println(outMessage);

        char[] chars = new char[8192];
        int read = 0;
        try {
            read = in.read(chars);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(chars, 0, read);
    }
}

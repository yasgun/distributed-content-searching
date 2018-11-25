package team.anoml.node;

import team.anoml.node.core.*;
import team.anoml.node.exception.NodeException;
import team.anoml.node.impl.TCPServer;
import team.anoml.node.impl.UDPServer;
import team.anoml.node.util.NodeUtils;
import team.anoml.node.util.SystemSettings;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Node {

    private static Logger logger = Logger.getLogger(Node.class.getName());

    private static boolean running = false;

    private static UDPServer udpServer;
    private static TCPServer tcpServer;

    private static RoutingTable routingTable = RoutingTable.getRoutingTable();
    private static FileTable fileTable = FileTable.getFileTable();

    private static String bootstrapIP = SystemSettings.getBootstrapIP();
    private static int bootstrapPort = SystemSettings.getBootstrapPort();
    private static String username = SystemSettings.getUsername();

    public static void main(String[] args) {

        logger.log(Level.INFO, "Connecting to Bootstrap Server at: " + bootstrapIP + " through port: " + bootstrapPort);

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

            logger.log(Level.INFO, response);

            String[] parts = response.split(" ");
            String noOfNodes = parts[2];

            if (parts[1].equals(SystemSettings.ERROR_MSG)) {
                throw new NodeException("Starting node failed", new Throwable("Error response: " + parts[2]));

            } else if (parts[1].equals(SystemSettings.REGOK_MSG)) {

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

            } else {
                throw new NodeException("Starting node failed", new Throwable("Unknown message format"));
            }

            generateFiles();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Starting node failed", e);
            throw new NodeException("Starting node failed", e);
        }

        for (RoutingTableEntry entry : routingTable.getAllEntries()) {
            logger.log(Level.INFO, entry.getIP() + ":" + entry.getPort());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(Node::stopServers));

        startServers();

        while (running) {
            try {
                Scanner keyboard = new Scanner(System.in);
                System.out.println("Enter Command: ");
                String request = keyboard.nextLine();

                String[] incomingResult = request.split(" ", 3);
                String command = incomingResult[0];
                String fileName = incomingResult[1];

                switch (command) {
                    case SystemSettings.SHOW_FILES:
                        System.out.println("Printing File Table...");
                        NodeUtils.printFileTable(fileTable);
                        break;
                    case SystemSettings.SHOW_ROUTES:
                        System.out.println("Printing Routing Table...");
                        NodeUtils.printRoutingTable(routingTable);
                        break;
                    case SystemSettings.SEARCH:
                        System.out.println("Executing Search Request...");
                        //TODO: check whether file table contains matching files using regex
                        FileTableEntry entry =fileTable.getEntryByFileName(fileName);
                        if (entry == null){
                            sendSearchReq(fileName);
                        }else{
                            System.out.println("File found :" + entry.getFileName());
                            // TODO: What do we do next?
                        }
                        //TODO: print SEROK results - need to decide how to print (wait here or not)
                        break;
                    case SystemSettings.DOWNLOAD:
                        System.out.println("Executing Download Request...");
                        //TODO: send download request to destination TCP server and handle download
                        break;
                    case SystemSettings.EXIT:
                        System.out.println("Terminating Node...");
                        stopServers();
                        break;
                }

            } catch (Exception e) {
                System.out.println("Invalid Request! Please Try Again");
            }
        }

        System.out.printf("Good Bye! Node Terminated Successfully");
    }

    private static void startServers() {
        tcpServer = new TCPServer();
        udpServer = new UDPServer();

        Thread tcpServerThread = new Thread(tcpServer);
        Thread udpServerThread = new Thread(udpServer);

        tcpServerThread.start();
        udpServerThread.start();

        running = true;
    }

    private static void stopServers() {
        running = false;

        for (int i = 0; i < SystemSettings.getShutdownRetryCount(); i++) {
            String response = sendUnregisterMessage();

            String[] parts = response.split(" ");

            if (parts[1].equals(SystemSettings.UNROK_MSG)) {
                break;
            }
        }

        tcpServer.stopServer();
        udpServer.stopServer();

        try {
            Thread.sleep(SystemSettings.getShutdownGracePeriod());
        } catch (InterruptedException ignored) {

        }
    }

    private static String sendUnregisterMessage() {
        String response = null;

        try (Socket clientSocket = new Socket(bootstrapIP, bootstrapPort);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String messageText = String.format(SystemSettings.UNREG_MSG_FORMAT, bootstrapIP, bootstrapPort, username);

            String lengthText = "0000" + (messageText.length() + 5);
            lengthText = lengthText.substring(lengthText.length() - 4);
            messageText = lengthText + " " + messageText;

            out.println(messageText);

            char[] chars = new char[8192];
            int read;
            read = in.read(chars);

            response = String.valueOf(chars, 0, read);

        } catch (IOException e) {
            logger.log(Level.WARNING, "Sending unregister message to bootstrap server failed", e);
        }
        return response;
    }

    private static void generateFiles() throws IOException {

        String[] fileNames = SystemSettings.FILE_NAMES;

        Random random = new Random();

        //Each node contributing 3-5 files
        int noOfFiles = random.nextInt(3) + 3;

        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 1; i < fileNames.length; i++) {
            list.add(i);
        }

        Collections.shuffle(list);

        for (int i = 0; i < noOfFiles; i++) {
            String fileName = fileNames[list.get(i)];
            File file = NodeUtils.createFile(fileName);
            fileTable.addEntry(new FileTableEntry(fileName, NodeUtils.getMD5Hex(file)));
        }
    }

    private static void sendSearchReq(String fileName){
        Collection<RoutingTableEntry> routingTableEntries = RoutingTable.getRoutingTable().getAllEntries();

        for (RoutingTableEntry entry : routingTableEntries) {
            String ipAddress = entry.getIP();
            int port = entry.getPort();

            try (DatagramSocket datagramSocket = new DatagramSocket()) {
                // TODO: Add the number of hops as a system setting
                String response = String.format(SystemSettings.SER_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort(), fileName, 10);
                NodeUtils.sendRequest(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
                ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.SEROK_MSG + ":" + ipAddress, new Date());
                logger.log(Level.INFO, "Requested a search for "+ fileName +" from ip: " + ipAddress + " port: " + port);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Sending SER request failed", e);
            }
        }
    }
}

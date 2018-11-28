package team.anoml.node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.*;
import team.anoml.node.exception.NodeException;
import team.anoml.node.sender.request.JoinRequestSender;
import team.anoml.node.sender.request.LeaveRequestSender;
import team.anoml.node.sender.request.SearchRequestSender;
import team.anoml.node.server.TCPServer;
import team.anoml.node.server.UDPServer;
import team.anoml.node.util.NodeUtils;
import team.anoml.node.util.SystemSettings;

import java.io.*;
import java.net.*;
import java.util.*;

import static team.anoml.node.core.RoutingTable.getRoutingTable;

public class Node {

    private static Logger logger = LogManager.getLogger(Node.class.getName());

    private static boolean running = false;

    private static UDPServer udpServer;
    private static TCPServer tcpServer;

    private static RoutingTable routingTable = getRoutingTable();
    private static FileTable fileTable = FileTable.getFileTable();

    private static String bootstrapIP = SystemSettings.getBootstrapIP();
    private static int bootstrapPort = SystemSettings.getBootstrapPort();
    private static String username = SystemSettings.getUsername();

    public static void main(String[] args) {

        logger.info("Connecting to Bootstrap Server at: " + bootstrapIP + " through port: " + bootstrapPort);

        sendUnregisterMessage();

        try (Socket clientSocket = new Socket()) {

            clientSocket.connect(new InetSocketAddress(bootstrapIP, bootstrapPort), SystemSettings.getTCPTimeout());

            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String messageText = String.format(SystemSettings.REG_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort(), username);

                String lengthText = "0000" + (messageText.length() + 5);
                lengthText = lengthText.substring(lengthText.length() - 4);
                messageText = lengthText + " " + messageText;

                out.println(messageText);

                char[] chars = new char[SystemSettings.getMaxMessageCharSize()];
                int read;
                read = in.read(chars);

                String response = String.valueOf(chars, 0, read);

                logger.info("Response from BS: " + response);

                String[] parts = response.split(" ");

                if (parts[1].equals(SystemSettings.ERROR_MSG)) {

                    throw new NodeException("Starting node failed", new Throwable("Error response: " + parts[2]));

                } else if (parts[1].equals(SystemSettings.REGOK_MSG)) {

                    String noOfNodes = parts[2];

                    switch (Integer.valueOf(noOfNodes)) {
                        case 0:
                            //nothing to do until another node finds out
                            break;
                        case 1:
                            sendJoinRequest(parts[3], Integer.valueOf(parts[4]));
                            break;
                        case 2:
                            sendJoinRequest(parts[3], Integer.valueOf(parts[4]));
                            sendJoinRequest(parts[6], Integer.valueOf(parts[7]));
                            break;
                        default:
                            int[] numbers = NodeUtils.getDistinctOrderedTwoRandomNumbers(Integer.valueOf(noOfNodes));
                            for (int i : numbers) {
                                sendJoinRequest(parts[i * 3 + 3], Integer.valueOf(parts[i * 3 + 3]));
                            }
                            break;
                    }

                } else {
                    throw new NodeException("Starting node failed", new Throwable("Unknown message format"));
                }

                generateFiles();
            }

        } catch (IOException e) {
            logger.error("Starting node failed", e);
            throw new NodeException("Starting node failed", e);
        }

        for (RoutingTableEntry entry : routingTable.getAllEntries()) {
            logger.info("Entry " + entry.getIPAddress() + ":" + entry.getPort());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(Node::stopServers));

        startServers();

        while (running) {
            try {
                Scanner keyboard = new Scanner(System.in);
                System.out.println("Enter Command: ");
                String request = keyboard.nextLine();

                String[] incomingResult = request.split(" ", 2);
                String command = incomingResult[0];

                switch (command) {
                    case SystemSettings.SHOW_FILES:
                        NodeUtils.printFileTable(fileTable);
                        break;
                    case SystemSettings.SHOW_ROUTES:
                        NodeUtils.printRoutingTable(routingTable);
                        break;
                    case SystemSettings.SEARCH:
                        System.out.println("Executing Search Request...");
                        String fileName = incomingResult[1];

                        Collection<FileTableEntry> entries = fileTable.getEntriesByFileName(fileName);
                        System.out.println(entries.isEmpty());
                        if (entries.isEmpty()) {
                            sendSearchRequest(fileName);
                        } else {
                            System.out.println("Files found in this node:");
                            for (FileTableEntry entry : entries) {
                                System.out.println(entry.getFileName());
                            }
                        }
                        break;
                    case SystemSettings.DOWNLOAD:
                        String[] incomingResultForDownload = incomingResult[1].split(" ", 3);
                        System.out.println("Executing Download Request...");
                        downloadFile(incomingResultForDownload[1], Integer.valueOf(incomingResultForDownload[2]), incomingResultForDownload[3]);
                        break;
                    case SystemSettings.EXIT:
                        System.out.println("Terminating Node...");
                        running = false;
                        break;
                }

            } catch (Exception e) {
                System.out.println("Invalid Request! Please Try Again");
            }
        }

        System.exit(0);
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


        for (RoutingTableEntry entry : routingTable.getAllEntries()) {
            LeaveRequestSender sender = new LeaveRequestSender();
            sender.setDestinationIpAddress(entry.getIPAddress());
            sender.setDestinationPort(entry.getPort());

            logger.debug("Executing LEAVE request sender");
            sender.send();
        }

        try {
            Thread.sleep(SystemSettings.getShutdownGracePeriod());
        } catch (InterruptedException ignored) {

        }
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

            char[] chars = new char[SystemSettings.getMaxMessageCharSize()];
            int read;
            read = in.read(chars);

            response = String.valueOf(chars, 0, read);

        } catch (IOException e) {
            logger.warn("Sending unregister message to bootstrap server failed", e);
        }
        return response;
    }

    private static void sendSearchRequest(String fileName) {
        Collection<RoutingTableEntry> routingTableEntries = getRoutingTable().getAllEntries();

        for (RoutingTableEntry entry : routingTableEntries) {
            String ipAddress = entry.getIPAddress();
            int port = entry.getPort();

            SearchRequestSender sender = new SearchRequestSender();

            sender.setDestinationIpAddress(ipAddress);
            sender.setDestinationPort(port);
            sender.setTargetIpAddress(SystemSettings.getNodeIP());
            sender.setTargetPort(SystemSettings.getUDPPort());
            sender.setFileName(fileName);
            sender.setHopsCount(0);

            logger.debug("Executing request sender");
            sender.send();
        }
    }

    private static void sendJoinRequest(String ipAddress, int port) {
        JoinRequestSender sender = new JoinRequestSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);

        logger.debug("Executing JOIN request sender");
        sender.send();
    }

    private static void downloadFile(String ipAddress, int port, String fileName) {
        try {
            URL url = new URL("http://" + ipAddress + ":" + port + "/download/" + fileName);
            HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
            long completeFileSize = httpConnection.getContentLength();

            try (BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
                 FileOutputStream fos = new FileOutputStream(SystemSettings.getFilePath() + "downloaded/" + fileName);
                 BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);) {

                byte[] data = new byte[1024];
                long downloadedFileSize = 0;
                int x;

                while ((x = in.read(data, 0, 1024)) >= 0) {
                    downloadedFileSize += x;
                    final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100000d);
                    System.out.println(currentProgress);
                    bout.write(data, 0, x);
                }
            }

        } catch (IOException e) {
            logger.error("File download failed", e);
        }
    }
}

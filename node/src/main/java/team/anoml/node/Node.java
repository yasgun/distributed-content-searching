package team.anoml.node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.*;
import team.anoml.node.exception.NodeException;
import team.anoml.node.impl.TCPServer;
import team.anoml.node.impl.UDPServer;
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

                    String ipAddress1;
                    int port1;

                    String ipAddress2;
                    int port2;

                    switch (Integer.valueOf(noOfNodes)) {
                        case 0:
                            //nothing to do until another node finds out
                            break;
                        case 1:
                            String ipAddress = parts[3];
                            int port = Integer.valueOf(parts[4]);

                            getRoutingTable().addEntry(new RoutingTableEntry(ipAddress, port));

                            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {
                                String request = String.format(SystemSettings.JOIN_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                                ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.JOINOK_MSG, ipAddress, port, new Date());
                                sendUDPMessage(datagramSocket, request, new InetSocketAddress(ipAddress, port).getAddress(), port);
                                logger.info("Sent JOIN request to ip: " + ipAddress + " port: " + port);

                            } catch (IOException e) {
                                logger.info("Sending JOIN request to " + ipAddress + ":" + port + " failed", e);
                            }
                            break;
                        case 2:
                            ipAddress1 = parts[3];
                            port1 = Integer.valueOf(parts[4]);

                            ipAddress2 = parts[5];
                            port2 = Integer.valueOf(parts[6]);

                            getRoutingTable().addEntry(new RoutingTableEntry(ipAddress1, port1));

                            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {
                                String request = String.format(SystemSettings.JOIN_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                                ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.JOINOK_MSG, ipAddress1, port1, new Date());
                                sendUDPMessage(datagramSocket, request, new InetSocketAddress(ipAddress1, port1).getAddress(), port1);
                                logger.info("Sent JOIN request to ip: " + ipAddress1 + " port: " + port1);

                            } catch (IOException e) {
                                logger.info("Sending JOIN request to " + ipAddress1 + ":" + port1 + " failed", e);
                            }

                            getRoutingTable().addEntry(new RoutingTableEntry(ipAddress2, port2));

                            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {
                                String request = String.format(SystemSettings.JOIN_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                                ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.JOINOK_MSG, ipAddress2, port2, new Date());
                                sendUDPMessage(datagramSocket, request, new InetSocketAddress(ipAddress2, port2).getAddress(), port2);
                                logger.info("Sent JOIN request to ip: " + ipAddress2 + " port: " + port2);

                            } catch (IOException e) {
                                logger.info("Sending JOIN request to " + ipAddress2 + ":" + port2 + " failed", e);
                            }

                            break;
                        default:
                            Random random = new Random();
                            int randInt1 = random.nextInt(Integer.valueOf(noOfNodes));
                            int randInt2 = random.nextInt(Integer.valueOf(noOfNodes));

                            while (randInt1 == randInt2) {
                                randInt2 = random.nextInt(Integer.valueOf(noOfNodes));
                            }

                            ipAddress1 = parts[(randInt1) * 2 + 3];
                            port1 = Integer.valueOf(parts[(randInt1) * 2 + 3]);

                            ipAddress2 = parts[(randInt2) * 2 + 3];
                            port2 = Integer.valueOf(parts[(randInt2) * 2 + 3]);


                            getRoutingTable().addEntry(new RoutingTableEntry(ipAddress1, port1));

                            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {
                                String request = String.format(SystemSettings.JOIN_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                                ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.JOINOK_MSG, ipAddress1, port1, new Date());
                                sendUDPMessage(datagramSocket, request, new InetSocketAddress(ipAddress1, port1).getAddress(), port1);
                                logger.info("Sent JOIN request to ip: " + ipAddress1 + " port: " + port1);

                            } catch (IOException e) {
                                logger.info("Sending JOIN request to " + ipAddress1 + ":" + port1 + " failed", e);
                            }

                            getRoutingTable().addEntry(new RoutingTableEntry(ipAddress2, port2));

                            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {
                                String request = String.format(SystemSettings.JOIN_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                                ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.JOINOK_MSG, ipAddress2, port2, new Date());
                                sendUDPMessage(datagramSocket, request, new InetSocketAddress(ipAddress2, port2).getAddress(), port2);
                                logger.info("Sent JOIN request to ip: " + ipAddress2 + " port: " + port2);

                            } catch (IOException e) {
                                logger.info("Sending JOIN request to " + ipAddress2 + ":" + port2 + " failed", e);
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
            logger.info("Entry " + entry.getIP() + ":" + entry.getPort());
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
                        if (entries == null) {
                            sendSearchRequest(fileName);
                        } else {
                            System.out.println("Files found in this node:");
                            for (FileTableEntry entry : entries) {
                                System.out.println(entry.getFileName());
                            }
                        }
                        break;
                    case SystemSettings.DOWNLOAD:
                        System.out.println("Executing Download Request...");
                        downloadFile(incomingResult[1], incomingResult[2], Integer.valueOf(incomingResult[3]));
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
            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {

                String response = String.format(SystemSettings.LEAVE_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.LEAVEOK_MSG, entry.getIP(), entry.getPort(), new Date());
                sendUDPMessage(datagramSocket, response, new InetSocketAddress(entry.getIP(), entry.getPort()).getAddress(), entry.getPort());
                logger.info("sent LEAVE request to ip: " + entry.getIP() + " port: " + entry.getPort());

            } catch (IOException e) {
                logger.info("Sending LEAVE request failed", e);
            }
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
            String ipAddress = entry.getIP();
            int port = entry.getPort();

            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {
                String response = String.format(SystemSettings.SER_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort(), fileName, 0);
                ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.SEROK_MSG, ipAddress, port, new Date());
                sendUDPMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
                logger.info("Requested a search for " + fileName + " from ip: " + ipAddress + " port: " + port);
            } catch (IOException e) {
                logger.error("Sending SER request failed", e);
            }
        }
    }

    private static void sendUDPMessage(DatagramSocket datagramSocket, String response, InetAddress address, int port) throws IOException {
        String lengthText = "0000" + (response.length() + 5);
        lengthText = lengthText.substring(lengthText.length() - 4);
        response = lengthText + " " + response;

        DatagramPacket datagramPacket = new DatagramPacket(response.getBytes(), response.length(), address, port);

        for (int i = 0; i < SystemSettings.getRequestTryCount(); i++) {
            datagramSocket.send(datagramPacket);
            try {
                Thread.sleep(SystemSettings.getRequestTryDelay());
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static void downloadFile(String fileName, String ipAddress, int port) {
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

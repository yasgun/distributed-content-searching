package team.anoml.node.impl;

import team.anoml.node.Node;
import team.anoml.node.api.NodeServer;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UDPServer implements NodeServer {

    private RoutingTable routingTable;
    Timer timer = new Timer(true);

    Executor executor = Executors.newSingleThreadExecutor();

    private final int numOfRetries = SystemSettings.RETRIES_COUNT;
    private boolean listening = false;
    private final int port;

    public UDPServer(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        if (listening) {
            return;
        }

        try {
            listen();
        } catch (Exception e) {
            //
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void listen() {
        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
            listening = true;
            startGossiping();
            while (listening) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(incoming);

                byte[] data = incoming.getData();
                String request = new String(data, 0, incoming.getLength());

                try {
                    handleRequest(request, incoming);
                } catch (Exception e) {
                    //TODO Retry
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred when listening", e);
        }
    }

    public void startGossiping() {
        TimerTask timerTask = new GossipingTimerTask();
        //running timer task as daemon thread
        timer.scheduleAtFixedRate(timerTask, 10000, 10 * 1000);
        System.out.println("TimerTask started");
    }

    protected void stopGossiping() {
        timer.cancel();
    }

    public void setRoutingTable(RoutingTable routingTable) {
        this.routingTable = routingTable;
    }

    private void handleRequest(String request, DatagramPacket incoming) throws IOException {
        String[] incomingResult = request.split(" ", 3);
        String command = incomingResult[1];

        InetSocketAddress recipient = new InetSocketAddress(incoming.getAddress(), incoming.getPort());
        switch (command) {
            case SystemSettings.JOIN_REQUEST:
                executor.execute(() -> handleJoinRequest(incomingResult[2], recipient));
                break;
            case SystemSettings.JOIN_OK:
                executor.execute(() -> handleJoinOKRequest(incomingResult[2], recipient));
                break;
            case SystemSettings.NEIGHBOUR_REQUEST:
                executor.execute(() -> handleNeighbourRequest(incomingResult[2], recipient));
                break;
            case SystemSettings.NEIGHBOUR_OK:
                executor.execute(() -> handleNeighbourOKRequest(incomingResult[2], recipient));
                break;
        }
    }

    private void handleJoinRequest(String request, InetSocketAddress recipient) {
        String[] parts = request.split(" ");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            String response = String.format(SystemSettings.JOINOK_MSG_FORMAT, 0);
            sendResponse(datagramSocket, response.length() + " " + response, new InetSocketAddress(ipAddress, port).getAddress(), port);
            routingTable.addEntry(new RoutingTableEntry(ipAddress, port));
        } catch (IOException e) {
            //
        }
    }

    private void handleJoinOKRequest(String request, InetSocketAddress recipient) {
        int value = Integer.valueOf(request);

        if (value == 0) {
            routingTable.getEntryByIP(recipient.getHostName()).validate();
        }
    }

    private void handleNeighbourRequest(String request, InetSocketAddress recipient) {

    }

    private void handleNeighbourOKRequest(String request, InetSocketAddress recipient) {

    }

    public static void sendResponse(DatagramSocket datagramSocket, String response, InetAddress address, int port) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(response.getBytes(), response.length(), address, port);
        datagramSocket.send(datagramPacket);
    }

    @Override
    public void stop() {
        stopGossiping();
        if (listening) {
            listening = false;
            try {
                Thread.sleep(SystemSettings.SHUTDOWN_GRACE_PERIOD_MS);
            } catch (InterruptedException e) {
                //
            }
        }
    }
}

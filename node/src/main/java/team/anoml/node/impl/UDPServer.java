package team.anoml.node.impl;

import team.anoml.node.Node;
import team.anoml.node.api.NodeServer;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UDPServer implements NodeServer {

    private Node node;

    Executor executor = Executors.newSingleThreadExecutor();

    private final int numOfRetries = SystemSettings.RETRIES_COUNT;
    private boolean listening = false;
    private final int port;

    public UDPServer(int port) {
        this.port = port;
    }

    @Override
    public void start(Node node) {
        if (listening) {
            return;
        }

        this.node = node;

        try {
            listen();
        } catch (Exception e) {
            //
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    @Override
    public void listen() {
        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
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

    private void handleRequest(String request, DatagramPacket incoming) throws IOException {
        String[] incomingResult = request.split(" ", 3);
        String command = incomingResult[1];

        InetSocketAddress recipient = new InetSocketAddress(incoming.getAddress(), incoming.getPort());
        switch (command) {
            case SystemSettings.JOIN_REQUEST:
                executor.execute(() -> handleJoinRequest(request, recipient));
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

        node.addEntry(new RoutingTableEntry(ipAddress, port));
    }

    private void handleJoinOKRequest(String request, InetSocketAddress recipient) {

    }

    private void handleNeighbourRequest(String request, InetSocketAddress recipient) {

    }

    private void handleNeighbourOKRequest(String request, InetSocketAddress recipient) {

    }

    @Override
    public void stop() {
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

package team.anoml.node.sender;

import team.anoml.node.core.FileTable;
import team.anoml.node.core.RoutingTable;
import team.anoml.node.core.SocketManager;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public abstract class AbstractSender {

    private static DatagramSocket datagramSocket = SocketManager.getDatagramSocket();

    protected static final String nodeIpAddress = SystemSettings.getNodeIP();
    protected static final int nodePort = SystemSettings.getUDPPort();

    private String destinationIpAddress;
    private int destinationPort;

    private RoutingTable routingTable = RoutingTable.getRoutingTable();
    private FileTable fileTable = FileTable.getFileTable();

    public abstract void send();

    protected void sendMessage(String response, String ipAddress, int port) throws IOException {

        String lengthText = "0000" + (response.length() + 5);
        lengthText = lengthText.substring(lengthText.length() - 4);
        response = lengthText + " " + response;

        DatagramPacket datagramPacket = new DatagramPacket(response.getBytes(), response.length(), new InetSocketAddress(ipAddress, port).getAddress(), port);

        for (int i = 0; i < SystemSettings.getRequestTryCount(); i++) {
            datagramSocket.send(datagramPacket);
            try {
                Thread.sleep(SystemSettings.getRequestTryDelay());
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void setDestinationIpAddress(String destinationIpAddress) {
        this.destinationIpAddress = destinationIpAddress;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    protected RoutingTable getRoutingTable() {
        return routingTable;
    }

    protected FileTable getFileTable() {
        return fileTable;
    }

    protected String getDestinationIpAddress() {
        return destinationIpAddress;
    }

    protected int getDestinationPort() {
        return destinationPort;
    }

}

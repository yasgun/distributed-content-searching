package team.anoml.node.handler;

import team.anoml.node.core.FileTable;
import team.anoml.node.core.RoutingTable;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class AbstractHandler implements Runnable {

    private RoutingTable routingTable = RoutingTable.getRoutingTable();
    private FileTable fileTable = FileTable.getFileTable();
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    protected void sendMessage(DatagramSocket datagramSocket, String response, InetAddress address, int port) throws IOException {
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

    protected String getMessage() {
        return message;
    }

    protected RoutingTable getRoutingTable() {
        return routingTable;
    }

    protected FileTable getFileTable() {
        return fileTable;
    }
}

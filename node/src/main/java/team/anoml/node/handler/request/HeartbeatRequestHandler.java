package team.anoml.node.handler.request;

import team.anoml.node.impl.UDPServer;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartbeatRequestHandler extends AbstractRequestHandler {

    private static Logger logger = Logger.getLogger(HeartbeatRequestHandler.class.getName());

    private UDPServer udpServer;

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            String healthStatus = udpServer.getHealthStatus();
            String response = String.format(SystemSettings.HBOK_MSG_FORMAT, healthStatus);
            sendMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
            logger.log(Level.INFO, "Sent health status : " + healthStatus + " to " + ipAddress + ":" + port);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Handling HB request failed", e);
        }
    }

    public void setUdpServer(UDPServer udpServer) {
        this.udpServer = udpServer;
    }
}
package team.anoml.node.handler.response;

import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.handler.request.JoinRequestHandler;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NeighbourResponseHandler extends AbstractResponseHandler {

    private static Logger logger = Logger.getLogger(JoinRequestHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        int neighborsCount = Integer.parseInt(parts[0]);

        for (int i = 1; i < neighborsCount * 2; i += 2) {
            String ipAddress = parts[i];
            int port = Integer.parseInt(parts[i + 1]);
            getRoutingTable().addEntry(new RoutingTableEntry(ipAddress, port));

            try (DatagramSocket datagramSocket = new DatagramSocket()) {
                String request = String.format(SystemSettings.JOIN_MSG_FORMAT, ipAddress, port);
                sendMessage(datagramSocket, request, new InetSocketAddress(ipAddress, port).getAddress(), port);
                logger.log(Level.INFO, "Sent join request to ip: " + ipAddress + " port: " + port);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Sending JOIN request to " + ipAddress + ":" + port + " failed", e);
            }
        }
    }
}

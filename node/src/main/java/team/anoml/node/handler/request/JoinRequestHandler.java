package team.anoml.node.handler.request;

import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JoinRequestHandler extends AbstractRequestHandler {

    private static Logger logger = Logger.getLogger(JoinRequestHandler.class.getName());

    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            String response = String.format(SystemSettings.JOINOK_MSG_FORMAT, 0);
            sendMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
            getRoutingTable().addEntry(new RoutingTableEntry(ipAddress, port));
            logger.log(Level.INFO, "Added ip: " + ipAddress + " port: " + port + " to routing table");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Handling JOIN request failed", e);
        }
    }
}

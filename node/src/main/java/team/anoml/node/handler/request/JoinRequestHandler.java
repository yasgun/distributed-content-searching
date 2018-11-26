package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class JoinRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(JoinRequestHandler.class.getName());

    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            if (getRoutingTable().getAllEntries().size() < SystemSettings.getRoutingTableLimit()) {

                String response = String.format(SystemSettings.JOINOK_MSG_FORMAT, 0);
                sendMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
                getRoutingTable().addEntry(new RoutingTableEntry(ipAddress, port));
                logger.info("Added ip: " + ipAddress + " port: " + port + " to routing table");

            } else {
                String response = String.format(SystemSettings.JOINOK_MSG_FORMAT, 9999);
                sendMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
                logger.warn("Error while adding ip: " + ipAddress + " port: " + port + " to routing table. Routing table limit exceeded");
            }

        } catch (IOException e) {
            logger.error("Handling JOIN request failed", e);
        }
    }
}

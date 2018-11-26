package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class LeaveRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(LeaveRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        try (DatagramSocket datagramSocket = new DatagramSocket()) {

            String response = String.format(SystemSettings.LEAVEOK_MSG_FORMAT, 0);
            sendMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
            getRoutingTable().removeEntry(ipAddress, port);
            logger.info("Node " + ipAddress + ":" + port + " removed from routing table");

        } catch (IOException e) {
            logger.error("Handling LEAVE request from node " + ipAddress + ":" + port + " failed", e);
        }
    }
}

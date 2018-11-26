package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class HeartbeatRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(HeartbeatRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        if (getRoutingTable().getEntry(ipAddress, port) != null) {

            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {
                String response = String.format(SystemSettings.HBOK_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());

                sendMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
                logger.info("Sent HB response to " + ipAddress + ":" + port);

            } catch (IOException e) {
                logger.error("Handling HB request failed", e);
            }
        }
    }
}

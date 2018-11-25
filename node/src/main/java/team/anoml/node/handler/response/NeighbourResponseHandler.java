package team.anoml.node.handler.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class NeighbourResponseHandler extends AbstractResponseHandler {

    private static Logger logger = LogManager.getLogger(NeighbourResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        int neighborsCount = Integer.parseInt(parts[0]);

        for (int i = 1; i < neighborsCount * 2; i += 2) {

            String ipAddress = parts[i];
            int port = Integer.parseInt(parts[i + 1]);

            if (getRoutingTable().getAllEntries().size() < SystemSettings.getRoutingTableLimit()
                    && !(ipAddress.equals(SystemSettings.getNodeIP()) && port == SystemSettings.getUDPPort())) {

                getRoutingTable().addEntry(new RoutingTableEntry(ipAddress, port));

                try (DatagramSocket datagramSocket = new DatagramSocket()) {
                    String request = String.format(SystemSettings.JOIN_MSG_FORMAT, ipAddress, port);
                    sendMessage(datagramSocket, request, new InetSocketAddress(ipAddress, port).getAddress(), port);
                    logger.info("Sent JOIN request to ip: " + ipAddress + " port: " + port);

                } catch (IOException e) {
                    logger.info("Sending JOIN request to " + ipAddress + ":" + port + " failed", e);
                }

            } else {
                break;
            }
        }
    }
}

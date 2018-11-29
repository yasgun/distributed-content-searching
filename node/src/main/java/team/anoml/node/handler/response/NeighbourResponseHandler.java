package team.anoml.node.handler.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.sender.request.JoinRequestSender;
import team.anoml.node.util.SystemSettings;

public class NeighbourResponseHandler extends AbstractResponseHandler {

    private static Logger logger = LogManager.getLogger(NeighbourResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        int neighborsCount = Integer.parseInt(parts[0]);

        logger.info("Received message: [" + getMessage() + "] from " + getClientIpAddress() + ":" + getClientPort());

        for (int i = 1; i < neighborsCount * 2; i += 2) {

            String ipAddress = parts[i];
            int port = Integer.parseInt(parts[i + 1]);

            if (getRoutingTable().getCount() < SystemSettings.getRoutingTableLimit() && !(ipAddress.equals(nodeIpAddress) && port == nodePort)
                    && getRoutingTable().getEntry(ipAddress, port) == null) {

                getRoutingTable().addEntry(new RoutingTableEntry(ipAddress, port));

                JoinRequestSender sender = new JoinRequestSender();
                sender.setDestinationIpAddress(ipAddress);
                sender.setDestinationPort(port);
                sender.send();
            }
        }
    }
}

package team.anoml.node.sender.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.NodeUtils;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.util.Collection;

public class NeighbourResponseSender extends AbstractResponseSender {

    private static Logger logger = LogManager.getLogger(NeighbourResponseSender.class.getName());

    @Override
    protected void sendResponse() {
        try {
            Collection<RoutingTableEntry> routingTableEntries = getRoutingTable().getAllEntries();
            StringBuilder neighborDetails = new StringBuilder();

            int neighborDetailsSize;

            if (routingTableEntries.size() <= 2) {
                neighborDetailsSize = routingTableEntries.size();

                for (RoutingTableEntry entry : routingTableEntries) {
                    neighborDetails.append(entry.getIPAddress()).append(" ").append(entry.getPort()).append(" ");
                }

            } else {
                neighborDetailsSize = 2;
                int[] randomNumbers = NodeUtils.getDistinctOrderedTwoRandomNumbers(getRoutingTable().getCount());

                int i = 0;

                for (RoutingTableEntry neighbour : routingTableEntries) {
                    if (i == randomNumbers[0] || i == randomNumbers[1]) {
                        neighborDetails.append(neighbour.getIPAddress()).append(" ").append(neighbour.getPort()).append(" ");
                    }
                    if (i > randomNumbers[1]) {
                        break;
                    }
                    i++;
                }
            }

            String response = String.format(SystemSettings.NBROK_MSG_FORMAT, neighborDetailsSize, neighborDetails.toString().trim());
            sendMessage(response, getDestinationIpAddress(), getDestinationPort());

            logger.info("Sent neighbor details: " + neighborDetails + " to " + getDestinationIpAddress() + ":" + getDestinationPort());
        } catch (IOException e) {
            logger.error("Sending NBR response to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
        }
    }
}

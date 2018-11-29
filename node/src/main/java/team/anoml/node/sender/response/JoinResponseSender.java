package team.anoml.node.sender.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;

public class JoinResponseSender extends AbstractResponseSender {

    private static Logger logger = LogManager.getLogger(JoinResponseSender.class.getName());

    @Override
    protected void sendResponse() {
        try {
            if (getRoutingTable().getCount() < SystemSettings.getRoutingTableLimit() && getRoutingTable().getEntry(getDestinationIpAddress(), getDestinationPort()) == null) {

                String response = String.format(SystemSettings.JOINOK_MSG_FORMAT, 0);
                sendMessage(response, getDestinationIpAddress(), getDestinationPort());
                getRoutingTable().addEntry(new RoutingTableEntry(getDestinationIpAddress(), getDestinationPort()));
                logger.info("Sent message: [" + response + "] from " + getDestinationIpAddress() + ":" + getDestinationPort());

            } else {
                String response = String.format(SystemSettings.JOINOK_MSG_FORMAT, 9999);
                sendMessage(response, getDestinationIpAddress(), getDestinationPort());
                logger.info("Sent message: [" + response + "] from " + getDestinationIpAddress() + ":" + getDestinationPort());
            }

        } catch (IOException e) {
            logger.error("Sending response to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
        }
    }
}

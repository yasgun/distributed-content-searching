package team.anoml.node.sender.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;

public class LeaveResponseSender extends AbstractResponseSender {

    private static Logger logger = LogManager.getLogger(LeaveResponseSender.class.getName());

    @Override
    protected void sendResponse() {
        try {
            String response = String.format(SystemSettings.LEAVEOK_MSG_FORMAT, 0);
            sendMessage(response, getDestinationIpAddress(), getDestinationPort());
            getRoutingTable().removeEntry(getDestinationIpAddress(), getDestinationPort());
            logger.info("Removed " + getDestinationIpAddress() + ":" + getDestinationPort() + " from routing table");

        } catch (IOException e) {
            logger.error("Sending LEAVE response to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
        }
    }
}

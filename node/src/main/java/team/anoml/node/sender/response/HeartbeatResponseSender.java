package team.anoml.node.sender.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;

public class HeartbeatResponseSender extends AbstractResponseSender {

    private static Logger logger = LogManager.getLogger(JoinResponseSender.class.getName());

    @Override
    protected void sendResponse() {

        if (getRoutingTable().getEntry(getDestinationIpAddress(), getDestinationPort()) != null) {

            try {
                String response = String.format(SystemSettings.HBOK_MSG_FORMAT, nodeIpAddress, nodePort);
                sendMessage(response, getDestinationIpAddress(), getDestinationPort());
                logger.info("Sent HB response to " + getDestinationIpAddress() + ":" + getDestinationPort());

            } catch (IOException e) {
                logger.error("Sending HB response to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
            }
        } else {
            logger.warn("HB response to " + getDestinationIpAddress() + ":" + getDestinationPort() + " cannot be processed");
        }
    }
}

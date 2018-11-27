package team.anoml.node.sender.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;

public class JoinRequestSender extends AbstractRequestSender {

    private static Logger logger = LogManager.getLogger(JoinRequestSender.class.getName());

    protected void sendRequest() {
        try {
            String request = String.format(SystemSettings.JOIN_MSG_FORMAT, nodeIpAddress, nodePort);
            sendMessage(request, getDestinationIpAddress(), getDestinationPort());
            logger.info("Sent JOIN request to " + getDestinationIpAddress() + ":" + getDestinationPort());

        } catch (IOException e) {
            logger.info("Sending JOIN request to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
        }
    }
}

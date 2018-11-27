package team.anoml.node.sender.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;

public class ErrorResponseSender extends AbstractResponseSender {

    private static Logger logger = LogManager.getLogger(ErrorResponseSender.class.getName());

    @Override
    protected void sendResponse() {
        try {
            String response = SystemSettings.ERROR_MSG_FORMAT;
            sendMessage(response, getDestinationIpAddress(), getDestinationPort());
            logger.info("Sent ERROR response to " + getDestinationIpAddress() + ":" + getDestinationPort());

        } catch (IOException e) {
            logger.error("Sending ERROR response to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
        }
    }

}

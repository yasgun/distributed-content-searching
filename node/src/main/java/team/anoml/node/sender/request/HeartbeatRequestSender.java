package team.anoml.node.sender.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.util.Date;

public class HeartbeatRequestSender extends AbstractRequestSender {

    private static Logger logger = LogManager.getLogger(HeartbeatRequestSender.class.getName());

    @Override
    protected void sendRequest() {
        try {
            String request = String.format(SystemSettings.HB_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
            ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.HBOK_MSG, getDestinationIpAddress(), getDestinationPort(), new Date());
            sendMessage(request, getDestinationIpAddress(), getDestinationPort());
            logger.info("Sent message: [" + request + "] from " + getDestinationIpAddress() + ":" + getDestinationPort());

        } catch (IOException e) {
            logger.error("Sending request to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
        }
    }
}

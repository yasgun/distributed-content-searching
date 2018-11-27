package team.anoml.node.sender.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.util.Date;

public class LeaveRequestSender extends AbstractRequestSender {

    private static Logger logger = LogManager.getLogger(LeaveRequestSender.class.getName());

    @Override
    protected void sendRequest() {
        try {
            String response = String.format(SystemSettings.LEAVE_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
            ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.LEAVEOK_MSG, getDestinationIpAddress(), getDestinationPort(), new Date());
            sendMessage(response, getDestinationIpAddress(), getDestinationPort());
            logger.info("Sent LEAVE request to " + getDestinationIpAddress() + ":" + getDestinationPort());

        } catch (IOException e) {
            logger.info("Sending LEAVE request to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
        }
    }
}

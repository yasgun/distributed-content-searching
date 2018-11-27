package team.anoml.node.handler.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LeaveResponseHandler extends AbstractResponseHandler {

    private static Logger logger = LogManager.getLogger(LeaveResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        logger.info("LEAVEOK: " + getMessage() + " from node " + getClientIpAddress() + ":" + getClientPort());
    }
}

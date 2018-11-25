package team.anoml.node.handler.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ErrorResponseHandler extends AbstractResponseHandler {

    private static Logger logger = LogManager.getLogger(ErrorResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        logger.warn("Error response: " + getMessage() + " from node " + getClientIpAddress() + ":" + getClientPort());
    }

}

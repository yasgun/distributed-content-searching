package team.anoml.node.handler.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.util.SystemSettings;

public class HeartbeatResponseHandler extends AbstractResponseHandler {

    private static Logger logger = LogManager.getLogger(JoinResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        if (ResponseTracker.getResponseTracker().consumeWaitingResponse(SystemSettings.HBOK_MSG, ipAddress, port)) {
            logger.info("Received HB OK from : " + ipAddress + " port: " + port);
        }
    }
}

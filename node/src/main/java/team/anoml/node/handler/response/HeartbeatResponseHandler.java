package team.anoml.node.handler.response;

import team.anoml.node.core.ResponseTracker;
import team.anoml.node.util.SystemSettings;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartbeatResponseHandler extends AbstractResponseHandler {
    private static Logger logger = Logger.getLogger(JoinResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        ResponseTracker.getResponseTracker().consumeWaitingResponse(SystemSettings.HBOK_MSG + ":" + ipAddress);
        logger.log(Level.INFO, "Received HB OK from : " + ipAddress + " port: " + port );
    }
}

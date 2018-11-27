package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.sender.response.HeartbeatResponseSender;

public class HeartbeatRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(HeartbeatRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        HeartbeatResponseSender sender = new HeartbeatResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);

        logger.debug("Executing HBOK response sender");
        sender.send();
    }
}

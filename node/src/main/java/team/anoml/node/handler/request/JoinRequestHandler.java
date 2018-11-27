package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.sender.response.JoinResponseSender;

public class JoinRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(JoinRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        JoinResponseSender sender = new JoinResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);

        logger.debug("Executing JOINOK response sender for " + ipAddress + ":" + port);
        sender.send();
    }
}

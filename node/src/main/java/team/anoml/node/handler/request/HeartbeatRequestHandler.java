package team.anoml.node.handler.request;

import team.anoml.node.sender.response.HeartbeatResponseSender;

public class HeartbeatRequestHandler extends AbstractRequestHandler {

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        HeartbeatResponseSender sender = new HeartbeatResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);
        sender.send();
    }
}

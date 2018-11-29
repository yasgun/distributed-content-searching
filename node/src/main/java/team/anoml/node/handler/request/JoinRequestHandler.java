package team.anoml.node.handler.request;

import team.anoml.node.sender.response.JoinResponseSender;

public class JoinRequestHandler extends AbstractRequestHandler {

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        JoinResponseSender sender = new JoinResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);

        sender.send();
    }
}

package team.anoml.node.handler.request;

import team.anoml.node.sender.response.LeaveResponseSender;

public class LeaveRequestHandler extends AbstractRequestHandler {

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        LeaveResponseSender sender = new LeaveResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);
        sender.send();
    }
}

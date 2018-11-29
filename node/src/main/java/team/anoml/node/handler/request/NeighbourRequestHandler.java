package team.anoml.node.handler.request;

import team.anoml.node.sender.response.NeighbourResponseSender;

public class NeighbourRequestHandler extends AbstractRequestHandler {

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        NeighbourResponseSender sender = new NeighbourResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);
        sender.send();
    }
}

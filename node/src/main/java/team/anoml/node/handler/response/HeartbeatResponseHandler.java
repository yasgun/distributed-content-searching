package team.anoml.node.handler.response;

public class HeartbeatResponseHandler extends AbstractResponseHandler {
    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        // TODO: Handle HB Response
    }
}

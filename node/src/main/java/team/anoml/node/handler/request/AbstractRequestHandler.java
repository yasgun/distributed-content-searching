package team.anoml.node.handler.request;

import team.anoml.node.handler.AbstractHandler;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

abstract class AbstractRequestHandler extends AbstractHandler {

    @Override
    public void run() {
        handleRequest();
    }

    void sendResponse(DatagramSocket datagramSocket, String response, InetAddress address, int port) throws IOException {
        String lengthText = "0000" + (response.length() + 5);
        lengthText = lengthText.substring(lengthText.length() - 4);
        response = lengthText + " " + response;

        DatagramPacket datagramPacket = new DatagramPacket(response.getBytes(), response.length(), address, port);

        for (int i = 0; i < SystemSettings.getRequestTryCount(); i++) {
            datagramSocket.send(datagramPacket);
            try {
                Thread.sleep(SystemSettings.getRequestTryDelay());
            } catch (InterruptedException ignored) {
            }
        }

    }

    protected abstract void handleRequest();
}

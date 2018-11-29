package team.anoml.node.sender.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class SearchRequestSender extends AbstractRequestSender {

    private static Logger logger = LogManager.getLogger(SearchRequestSender.class.getName());

    private static ConcurrentHashMap<String, Integer> sentRequests = new ConcurrentHashMap<>();

    private String targetIpAddress;
    private int targetPort;
    private String fileName;
    private int hopsCount;
    private String id;

    @Override
    protected void sendRequest() {
        if (!sentRequests.containsKey(id) || (sentRequests.containsKey(id) && sentRequests.get(id) == hopsCount)) {
            sentRequests.put(id, hopsCount);
            try {
                String request = String.format(SystemSettings.SER_MSG_FORMAT, targetIpAddress, targetPort, fileName.replaceAll(" ", "_"), hopsCount, id);
                sendMessage(request, getDestinationIpAddress(), getDestinationPort());
                logger.info("Sent message: [" + request + "] from " + getDestinationIpAddress() + ":" + getDestinationPort());
            } catch (IOException e) {
                logger.error("Sending request to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
            }
        }
    }

    public void setTargetIpAddress(String targetIpAddress) {
        this.targetIpAddress = targetIpAddress;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setHopsCount(int hopsCount) {
        this.hopsCount = hopsCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    protected void sendMessage(String response, String ipAddress, int port) throws IOException {

        String lengthText = "0000" + (response.length() + 5);
        lengthText = lengthText.substring(lengthText.length() - 4);
        response = lengthText + " " + response;

        DatagramPacket datagramPacket = new DatagramPacket(response.getBytes(), response.length(), new InetSocketAddress(ipAddress, port).getAddress(), port);

        datagramSocket.send(datagramPacket);
    }
}

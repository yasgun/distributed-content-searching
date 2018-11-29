package team.anoml.node.task;

import team.anoml.node.sender.AbstractSender;

import java.util.TimerTask;

abstract class AbstractTimerTask extends TimerTask {

    void sendRequest(AbstractSender sender, String ipAddress, int port) {
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);
        sender.send();
    }
}

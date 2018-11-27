package team.anoml.node.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.sender.AbstractSender;

import java.util.TimerTask;

abstract class AbstractTimerTask extends TimerTask {

    private static Logger logger = LogManager.getLogger(AbstractTimerTask.class.getName());

    void sendRequest(AbstractSender sender, String ipAddress, int port) {
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);

        logger.debug("Executing request sender");
        sender.send();
    }
}

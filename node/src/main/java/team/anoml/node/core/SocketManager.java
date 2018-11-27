package team.anoml.node.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.util.SystemSettings;

import java.net.DatagramSocket;
import java.net.SocketException;

public class SocketManager {

    private static Logger logger = LogManager.getLogger(SocketManager.class.getName());
    private static DatagramSocket datagramSocket;

    static {
        try {
            datagramSocket = new DatagramSocket(SystemSettings.getUDPPort());
        } catch (SocketException e) {
            logger.error("Initializing UDP socket connection failed", e);
        }
    }

    public static DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
}

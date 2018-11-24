package team.anoml.node.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemSettings {

    private static Logger logger = Logger.getLogger(SystemSettings.class.getName());

    /********************************************
     * Communication with the Bootstrap Server
     ********************************************/

    public static final String REG_MSG_FORMAT = "REG %s %d %s";
    public static final String UNREG_MSG_FORMAT = "UNREG %s %d %s";

    /********************************************
     * Communication with the Other Nodes
     ********************************************/

    public static final String JOIN_MSG_FORMAT = "JOIN %s %d";
    public static final String JOINOK_MSG_FORMAT = "JOINOK %d";
    public static final String LEAVE_MSG_FORMAT = "LEAVE %s %d";
    public static final String LEAVEOK_MSG_FORMAT = "LEAVEOK %d";

    public static final String NBR_MSG_FORMAT = "NBR %s %d";
    public static final String NBROK_MSG_FORMAT = "NBROK %d %s";

    public static final String HB_MSG_FORMAT = "HB %s %d";
    public static final String HBOK_MSG_FORMAT = "HBOK %s";

    public static final String ERROR_MSG_FORMAT = "ERROR %s";

    public static final String SER_MSG_FORMAT = "SER %s %d %s %d";
    public static final String SEROK_MSG_FORMAT = "SEROK %d %s %d %d %s";

    /********************************************
     * Request Types
     ********************************************/

    public static final String JOIN_MSG = "JOIN";
    public static final String LEAVE_MSG = "LEAVE";
    public static final String NBR_MSG = "NBR";
    public static final String HB_MSG = "HB";
    public static final String SER_MSG = "SER";

    /********************************************
     * Response Types
     ********************************************/

    public static final String ERROR_MSG = "ERROR";
    public static final String JOINOK_MSG = "JOINOK";
    public static final String LEAVEOK_MSG = "LEAVEOK";
    public static final String NBROK_MSG = "NBROK";
    public static final String HBOK_MSG = "HBOK";
    public static final String SEROK_MSG = "SEROK";

    /********************************************
     * TCP Communication
     ********************************************/

    public static final String DOWN_MSG_FORMAT = "DOWN %s";
    public static final String DOWN_MSG = "DOWN";

    /********************************************
     * Configurations
     ********************************************/

    private static Properties properties = new Properties();

    static {
        String propFileName = "config.properties";
        try (InputStream inputStream = SystemSettings.class.getClassLoader().getResourceAsStream(propFileName);) {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Reading Configuration Failed", e);
        }
    }

    public static String getBootstrapIP() {
        return properties.getProperty("bootstrap.ip", "127.0.0.1");
    }

    public static int getBootstrapPort() {
        return Integer.valueOf(properties.getProperty("bootstrap.port", "80"));
    }

    public static String getNodeIP() {
        return properties.getProperty("node.ip", "127.0.0.1");
    }

    public static String getUsername() {
        return properties.getProperty("node.username", "username");
    }

    public static int getRequestTryCount() {
        return Integer.valueOf(properties.getProperty("request.try.count", "3"));
    }

    public static int getRequestTryDelay() {
        return Integer.valueOf(properties.getProperty("request.try.delay", "1000"));
    }

    public static int getUDPPort() {
        return Integer.valueOf(properties.getProperty("server.udp.port", "1234"));
    }

    public static int getUDPShutdownGracePeriod() {
        return Integer.valueOf(properties.getProperty("server.udp.shutdown_grace_period", "5000"));
    }

    public static int getUDPGossipPeriod() {
        return Integer.valueOf(properties.getProperty("server.udp.gossip.period", "20000"));
    }

    public static int getUDPGossipDelay() {
        return Integer.valueOf(properties.getProperty("server.udp.gossip.delay", "30000"));
    }

    public static int getUDPHeartbeatPeriod() {
        return Integer.valueOf(properties.getProperty("server.udp.heartbeat.period", "20000"));
    }

    public static int getUDPHeartbeatDelay() {
        return Integer.valueOf(properties.getProperty("server.udp.heartbeat.delay", "30000"));
    }

    public static int getTCPPort() {
        return Integer.valueOf(properties.getProperty("server.tcp.port", "9191"));
    }

}

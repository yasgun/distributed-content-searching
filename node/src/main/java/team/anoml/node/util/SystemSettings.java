package team.anoml.node.util;

public class SystemSettings {

    private SystemSettings() {
    }

    public static int BOOTSTRAP_PORT = 55555;
    public static String BOOTSTRAP_IP = "127.0.0.1";

    public static final int RETRIES_COUNT = 3;
    public static final int RETRY_TIMEOUT_MS = 5000;
    public static final int SHUTDOWN_GRACE_PERIOD_MS = 5000;
    public static final int CROSSTALK_FREQUENCY_MS = 20000;
    public static final int CROSSTALK_STARTUP_DELAY = 30000;

    //Communication with the Bootstrap Server
    public static final String REG_MSG_FORMAT = "REG %s %d %s";
    public static final String UNREG_MSG_FORMAT = "UNREG %s %d %s";

    //Communication with other nodes
    public static final String JOIN_MSG_FORMAT = "JOIN %s %d %s";
    public static final String JOINOK_MSG_FORMAT = "JOINOK %d";
    public static final String LEAVE_MSG_FORMAT = "LEAVE %s %d";
    public static final String LEAVEOK_MSG_FORMAT = "LEAVEOK %d";

    public static final String NBR_MSG_FORMAT = "NBR %s %d";
    public static final String NBROK_MSG_FORMAT = "NBROK %d %s";

    public static final String ERROR_MSG_FORMAT = "ERROR %s";

    public static final String JOIN_REQUEST = "JOIN";
    public static final String JOIN_OK = "JOINOK";
    public static final String NEIGHBOUR_REQUEST = "NBR";
    public static final String NEIGHBOUR_OK = "NBROK";
}

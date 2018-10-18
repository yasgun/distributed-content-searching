package team.anoml.distributed.systems;

/**
 * team.anoml.distributed.systems.server.Constants
 */
public class Constants {

    private Constants() { }

    /** Need to set the bootstrap IP and Port here */
    public static int BOOTSTRAP_PORT = 55555;
    public static String BOOTSTRAP_IP = "127.0.0.1";

    /** How many times a given UDP request be retried */
    public static final int RETRIES_COUNT = 5;
    public static final int RETRY_TIMEOUT_MS = 5000;
    public static final int GRACE_PERIOD_MS = 5000;
    public static final int HEARTBEAT_FREQUENCY_MS = 20000;
    public static final int HEARTBEAT_INITIAL_DELAY = 30000;

    public static final int ADDRESS_SPACE_SIZE = 36;
    public static final int CHARACTER_SPACE_SIZE = 36;
    public static final int ADDRESSES_PER_CHARACTER = ADDRESS_SPACE_SIZE / CHARACTER_SPACE_SIZE;

    /** REG ${ip} ${port} ${username} */
    public static final String REG_MSG_FORMAT = "REG %s %d %s";
    /** UNREG ${ip} ${port} ${username} */
    public static final String UNREG_MSG_FORMAT = "UNREG %s %d %s";
    /** NEWNODE ${ip} ${port} ${nodeId} */
    public static final String NEWNODE_MSG_FORMAT = "NEWNODE %s %d %d";
    /** NEWENTRY ${keyword} ${node} ${file} */
    public static final String NEWENTRY_MSG_FORMAT = "NEWENTRY %s %d %s";
    /** QUERY ${keyword} ${filename} */
    public static final String QUERY_MSG_FORMAT = "QUERY %s %s";
    /** QUERY ${keyword} ${filename} */
    public static final String KEYWORD_MSG_FORMAT = "KEYWORD %s";
    /** PING - Pings and gets the entries of the corresponding node */
    public static final String PING_MSG_FORMAT = "PING %d %s";
    /** SYNC ${type} ${serialized_object} - For syncing table entries and routing tables */
    public static final String SYNC_MSG_FORMAT = "SYNC %s %s";

    /** Message format to be used when sending a request to the bootstrap server. ${length} ${msg} */
    public static final String MSG_FORMAT = "%04d %s";
    public static final String REGOK = "REGOK";
    public static final String UNROK = "UNROK";

    /** Types of syncs */
    public static final String TYPE_ROUTING = "RTBL";
    public static final String TYPE_ENTRIES = "ETBL";

    /** Message commands to be used in client server communications **/
    public static final String GET_ROUTING_TABLE = "GETRTBL";
    public static final String NEW_NODE = "NEWNODE";
    public static final String QUERY = "QUERY";
    public static final String KEYWORD = "KEYWORD";
    public static final String NEW_ENTRY = "NEWENTRY";
    public static final String RESPONSE_OK = "OK";
    public static final String RESPONSE_FAILURE = "FAILED";
    /** SYNC - sync the entry table entries by handing over anything that should belong to that node */
    public static final String SYNC = "SYNC";
    public static final String PING = "PING";

    /** File name generation and relevant constants **/
    public static final int MIN_FILE_COUNT = 3;
    public static final int MAX_FILE_COUNT = MIN_FILE_COUNT + 2;
    public static final String[] FILE_NAME_ARRAY = {"Adventures of Tintin", "Jack and Jill", "Glee",
            "The Vampire Diarie", "King Arthur", "Windows XP", "Harry Potter", "Kung Fu Panda", "Lady Gaga", "Twilight",
            "Windows 8", "Mission Impossible", "Turn Up The Music", "Super Mario", "American Pickers",
            "Microsoft Office 2010", "Happy Feet", "Modern Family", "American Idol", "Hacking for Dummies"};

    /**
     * This will return the expected response of any command. For example, <strong>GETRTBL</strong>'s response will look
     * like <strong>GETRTBLOK</strong>
     *
     * @param command Original command sent by client
     * @return command suffixed with OK
     */
    public static String getOkCommand(String command) {
        return command + "OK";
    }
}

package team.anoml.node.core;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseTracker {

    private static ResponseTracker responseTracker = new ResponseTracker();

    private ConcurrentHashMap<String, Date> waitingResponses = new ConcurrentHashMap<>();

    private ResponseTracker() {
        //nothing required here
    }

    public static ResponseTracker getResponseTracker() {
        return responseTracker;
    }

    public void addWaitingResponse(String requestType, String ip, Date requestTime) {
        waitingResponses.put(requestType + ":" + ip, requestTime);
    }

    public boolean consumeWaitingResponse(String requestType, String ip, int port) {
        return waitingResponses.remove(requestType + ":" + ip) != null;
    }
}

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

    public synchronized boolean addWaitingResponse(String responseKey, Date requestTime) {
        if (waitingResponses.containsKey(responseKey)) {
            return false;
        }
        waitingResponses.put(responseKey, requestTime);
        return true;
    }

    public synchronized boolean consumeWaitingResponse(String responseKey) {
        if (waitingResponses.containsKey(responseKey)) {
            waitingResponses.remove(responseKey);
            return true;
        }
        return false;
    }
}

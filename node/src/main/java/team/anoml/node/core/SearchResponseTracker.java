package team.anoml.node.core;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class SearchResponseTracker {

    private static SearchResponseTracker searchResponseTracker = new SearchResponseTracker();

    private ConcurrentHashMap<String, Date> receivedResponses = new ConcurrentHashMap<>();

    private SearchResponseTracker() {
        //nothing required here
    }

    public static SearchResponseTracker getSearchResponseTracker() {
        return searchResponseTracker;
    }

    public boolean addReceivedResponse(String ip, int port, Date requestTime) {

        if (receivedResponses.containsKey(ip + ":" + port)) {
            return false;
        }

        receivedResponses.put(ip + ":" + port, requestTime);
        return true;
    }

    public void refresh() {
        receivedResponses.clear();
    }

}

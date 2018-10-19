package team.anoml.node.impl;

import java.util.Date;
import java.util.TimerTask;

public class GossipingTimerTask extends TimerTask {

    @Override
    public void run() {
        System.out.println("Timer task started at:" + new Date());
        completeTask();
        System.out.println("Timer task finished at:" + new Date());
    }

    private void completeTask() {
        //gossiping logic goes here
        System.out.printf("Gossiping");
    }
}

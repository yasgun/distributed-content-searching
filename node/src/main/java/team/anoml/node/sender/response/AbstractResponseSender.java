package team.anoml.node.sender.response;

import team.anoml.node.sender.AbstractSender;

abstract class AbstractResponseSender extends AbstractSender {

    public void send() {
        sendResponse();
    }

    protected abstract void sendResponse();

}

package team.anoml.node.sender.request;

import team.anoml.node.sender.AbstractSender;

abstract class AbstractRequestSender extends AbstractSender {

    public void send() {
        sendRequest();
    }

    protected abstract void sendRequest();
}

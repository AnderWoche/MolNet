package de.moldiy.molnet.exchange;

public class MessageExchanger<T> {

    private T messageExchanger;

    public void setMessageExchanger(T messageExchanger) {
        this.messageExchanger = messageExchanger;
    }

    public T getMessageExchanger() {
        return messageExchanger;
    }
}

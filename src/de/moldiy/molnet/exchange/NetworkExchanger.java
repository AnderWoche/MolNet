package de.moldiy.molnet.exchange;

public abstract class NetworkExchanger<T extends NetworkExchanger> {

    protected final MessageExchangerManager exchangerManager = new MessageExchangerManager();

    public void loadMessageExchanger(MessageExchanger<T> serverMessageExchanger) {
        this.exchangerManager.loadMassageExchanger(serverMessageExchanger);
    }

    protected abstract NetworkExchanger<T> getNetworkExchanger();
}

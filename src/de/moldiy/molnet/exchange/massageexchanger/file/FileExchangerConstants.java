package de.moldiy.molnet.exchange.massageexchanger.file;

public class FileExchangerConstants {

    public static final int SAVED_FILE_BYTES_IN_RAM = 1_048_576; // 2^20 = 1_048_576 | 2^21 = 2_097_152

    // Passive
    //Receive
    public static final String PASSIVE_FILE_RECEIVER_NEW = "molnet.file.passive.new";
    public static final String PASSIVE_FILE_RECEIVER_WRITE = "molnet.file.passive.write";
    // Send
    public static final String PASSIVE_FILE_SENDER_REQUEST = "molnet.file.passive.request";

    // Active
    public static final String ACTIVE_FILE_PACKET_NEW_FILE = "molnet.file.active.new.file";
    public static final String ACTIVE_FILE_PACKET_NEW_PACKET = "molnet.file.active.new.packet";
    public static final String ACTIVE_FILE_PACKET_CLOSE = "molnet.file.active.close";
    public static final String ACTIVE_FILE_PACKET_WRITE = "molnet.file.active.write";
    public static final String ACTIVE_FILE_PACKET_PROVIDER_REQUEST = "molnet.file.active.request";

}

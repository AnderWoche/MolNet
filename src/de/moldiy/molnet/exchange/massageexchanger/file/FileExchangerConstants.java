package de.moldiy.molnet.exchange.massageexchanger.file;

public class FileExchangerConstants {

    public static final int SAVED_FILE_BYTES_IN_RAM = 2_097_152; // 2^20 = 1_048_576 | 2^21 = 2_097_152

    // Passive
    //Receive
    public static final String PASSIVE_FILE_RECEIVER_NEW = "molnet.file.passive.new";
    public static final String PASSIVE_FILE_RECEIVER_WRITE = "molnet.file.passive.write";
    // Send
    public static final String PASSIVE_FILE_SENDER_REQUEST = "molnet.file.passive.request";

    // Active
    //Recive
    public static final String ACTIVE_FILE_PACKET_WRITE = "molnet.file.active.write";
    public static final String ACTIVE_FILE_PACKET_DO_NOT_EXISTS = "molnet.file.active.packetDoNotExist";
    //Send
    public static final String ACTIVE_FILE_PACKET_PROVIDER_GET_FILE_PACKED = "molnet.file.active.get";
    public static final String ACTIVE_FILE_PACKET_PROVIDER_REQUEST = "molnet.file.active.request";


}

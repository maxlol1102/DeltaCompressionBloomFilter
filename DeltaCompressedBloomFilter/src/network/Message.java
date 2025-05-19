package network;

public class Message {
    public enum Type { FULL_FILTER, COMPRESSED_FILTER, DELTA_UPDATE }

    public Type type;
    public byte[] data;
    public int originalBitLength;
    public int senderId;

    public Message(Type type, byte[] data, int originalBitLength, int senderId) {
        this.type = type;
        this.data = data;
        this.originalBitLength = originalBitLength;
        this.senderId = senderId;
    }
}

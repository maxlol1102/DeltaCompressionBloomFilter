package compress;

public interface Compressor {
    byte[] compress(boolean[] bits);
    boolean[] decompress(byte[] data, int originalLength);
}

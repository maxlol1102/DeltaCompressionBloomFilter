package hash;

public class FNVHash implements HashFunction {
    private static final int FNV_32_PRIME = 0x01000193;
    private static final int FNV_32_INIT = 0x811c9dc5;

    @Override
    public int hash(String input, int seed) {
        int hash = FNV_32_INIT ^ seed;
        for (char c : input.toCharArray()) {
            hash *= FNV_32_PRIME;
            hash ^= c;
        }
        return Math.abs(hash);
    }
}

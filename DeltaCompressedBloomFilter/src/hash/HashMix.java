package hash;

public class HashMix implements HashFunction {
    @Override
    public int hash(String input, int seed) {
        int hash = seed;
        for (char c : input.toCharArray()) {
            hash ^= (hash << 5) + (hash >> 2) + c;
        }
        return Math.abs(hash);
    }
}

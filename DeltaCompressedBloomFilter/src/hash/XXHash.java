package hash;

public class XXHash implements HashFunction {
    @Override
    public int hash(String input, int seed) {
        int hash = seed ^ 0x9E3779B9;
        for (int i = 0; i < input.length(); i++) {
            hash += input.charAt(i) * 0x85EBCA6B;
            hash = Integer.rotateLeft(hash, 13);
            hash *= 0xC2B2AE35;
        }
        return Math.abs(hash);
    }
}

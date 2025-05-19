package hash;

public class BadHash implements HashFunction {
    @Override
    public int hash(String input, int seed) {
        // Very bad: only uses first character and seed
        return Math.abs((input.charAt(0) + seed) % 1000);
    }
}

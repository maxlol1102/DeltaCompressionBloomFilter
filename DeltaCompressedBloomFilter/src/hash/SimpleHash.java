package hash;

public class SimpleHash implements HashFunction {
   @Override
    public int hash(String input, int seed) {
        int hash = 0;
        for (char c : input.toCharArray()) {
            hash = seed * hash + c;
        }
        return Math.abs(hash);
    }
}

package hash;

import java.math.BigInteger;
import java.util.Random;

public class UniversalHash implements HashFunction {
    private final int p; // a large prime
    private final int m; // table size (filter size)
    private final Random rand;

    public UniversalHash(int m) {
        this.p = 2147483647; // 2^31 - 1, safe for int
        this.m = m;
        this.rand = new Random(System.currentTimeMillis());
    }

    @Override
    public int hash(String input, int seed) {
        int key = input.hashCode();

        // Use different a, b for each seed to simulate k hash functions
        int a = Math.floorMod(seed * 31 + 17, p - 1) + 1; // a ∈ [1, p-1]
        int b = Math.floorMod(seed * 53 + 29, p);         // b ∈ [0, p-1]

        // Use BigInteger to avoid overflow
        BigInteger bigA = BigInteger.valueOf(a);
        BigInteger bigB = BigInteger.valueOf(b);
        BigInteger bigKey = BigInteger.valueOf(key);

        BigInteger hash = bigA.multiply(bigKey).add(bigB).mod(BigInteger.valueOf(p)).mod(BigInteger.valueOf(m));
        return hash.intValue();
    }
}

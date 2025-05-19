package bloom;

import hash.HashFunction;
import java.util.Arrays;

public class CountingBloomFilter {
    private final int[] counters;
    private final int m, k;
    private final HashFunction hashFunction;

    public CountingBloomFilter(int m, int k, HashFunction hashFunction) {
        this.m = m;
        this.k = k;
        this.hashFunction = hashFunction;
        this.counters = new int[m];
    }

    public void add(String item) {
        for (int i = 0; i < k; i++) {
            int index = Math.abs(hashFunction.hash(item, i)) % m;
            counters[index]++;
        }
    }

    public void remove(String item) {
        for (int i = 0; i < k; i++) {
            int index = Math.abs(hashFunction.hash(item, i)) % m;
            if (counters[index] > 0) counters[index]--;
        }
    }

    public boolean mightContain(String item) {
        for (int i = 0; i < k; i++) {
            int index = Math.abs(hashFunction.hash(item, i)) % m;
            if (counters[index] == 0) return false;
        }
        return true;
    }

    public int[] getCounters() {
        return counters;
    }

    public int getSize() {
        return m;
    }
}

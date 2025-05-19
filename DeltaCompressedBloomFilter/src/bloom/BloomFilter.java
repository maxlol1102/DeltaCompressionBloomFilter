package bloom;

import hash.HashFunction;
import java.util.BitSet;

public class BloomFilter {
    private final BitSet bitSet;
    private final int size;
    private final int hashCount;
    private final HashFunction hashFunction;

    public BloomFilter(int size, int hashCount, HashFunction hashFunction) {
        this.size = size;
        this.hashCount = hashCount;
        this.bitSet = new BitSet(size);
        this.hashFunction = hashFunction;
    }

    public void add(String element) {
        for (int i = 0; i < hashCount; i++) {
            int hash = hashFunction.hash(element, i + 1);
            int index = hash % size;
            bitSet.set(index);
        }
    }

    public boolean mightContain(String element) {
        for (int i = 0; i < hashCount; i++) {
            int hash = hashFunction.hash(element, i + 1);
            int index = hash % size;
            if (!bitSet.get(index)) {
                return false;
            }
        }
        return true;
    }

    public BitSet getBitSet() {
        return bitSet;
    }

    public int getSize() {
        return size;
    }

    public int getHashCount() {
        return hashCount;
    }
}

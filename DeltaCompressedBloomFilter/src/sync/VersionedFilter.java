package sync;

import bloom.BloomFilter;

import java.util.HashMap;
import java.util.Map;

public class VersionedFilter {
    private BloomFilter filter;
    private int currentVersion;
    private final DeltaEncoder encoder;
    private final Map<Integer, byte[]> deltas = new HashMap<>();

    public VersionedFilter(BloomFilter initialFilter, DeltaEncoder encoder) {
        this.filter = initialFilter;
        this.currentVersion = 0;
        this.encoder = encoder;
    }

    public void applyUpdate(BloomFilter newFilter) {
        byte[] delta = encoder.encodeDelta(filter, newFilter);
        deltas.put(currentVersion + 1, delta);
        filter = newFilter;
        currentVersion++;
    }

    public boolean applyDelta(byte[] delta) {
        try {
            encoder.applyDelta(filter, delta);
            currentVersion++;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public BloomFilter getFilter() {
        return filter;
    }

    public int getVersion() {
        return currentVersion;
    }

    public byte[] getDeltaTo(int version) {
        return deltas.get(version);
    }

    public boolean hasDeltaTo(int version) {
        return deltas.containsKey(version);
    }
}

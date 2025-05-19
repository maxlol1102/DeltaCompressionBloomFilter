package sync;

import bloom.CountingBloomFilter;
import compress.RLECompressor;

public class CountingDeltaEncoder {
    private final RLECompressor compressor;

    public CountingDeltaEncoder(RLECompressor compressor) {
        this.compressor = compressor;
    }

    public byte[] encodeDelta(CountingBloomFilter oldF, CountingBloomFilter newF) {
        int[] oldCounters = oldF.getCounters();
        int[] newCounters = newF.getCounters();
        int[] delta = new int[oldF.getSize()];

        for (int i = 0; i < delta.length; i++) {
            delta[i] = newCounters[i] - oldCounters[i];
        }

        // convert int[] to boolean[] for compression — RLE treats sign bits well
        boolean[] bits = new boolean[delta.length * 4]; // crude encoding (expandable)
        for (int i = 0; i < delta.length; i++) {
            int val = delta[i];
            for (int j = 0; j < 4; j++) {
                bits[i * 4 + j] = ((val >> j) & 1) == 1;
            }
        }

        return compressor.compress(bits);
    }

    public RLECompressor getCompressor() {
        return compressor;
    }
}

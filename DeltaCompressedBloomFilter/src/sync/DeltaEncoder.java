package sync;

import bloom.BloomFilter;
import compress.Compressor;
import java.util.BitSet;

public class DeltaEncoder {
    private final Compressor compressor;

    public DeltaEncoder(Compressor compressor) {
        this.compressor = compressor;
    }
    
     public Compressor getCompressor() {
        return compressor;
    }


    public byte[] encodeDelta(BloomFilter oldFilter, BloomFilter newFilter) {
        BitSet oldBits = oldFilter.getBitSet();
        BitSet newBits = newFilter.getBitSet();
        int size = oldFilter.getSize();

        boolean[] delta = new boolean[size];
        for (int i = 0; i < size; i++) {
            delta[i] = oldBits.get(i) ^ newBits.get(i);
        }
        return compressor.compress(delta);
    }

    public void applyDelta(BloomFilter baseFilter, byte[] compressedDelta) {
        int size = baseFilter.getSize();
        boolean[] delta = compressor.decompress(compressedDelta, size);
        BitSet base = baseFilter.getBitSet();

        for (int i = 0; i < size; i++) {
            if (delta[i]) {
                base.flip(i);
            }
        }
    }
}

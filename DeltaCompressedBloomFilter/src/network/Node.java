package network;

import bloom.BloomFilter;
import compress.Compressor;
import hash.HashFunction;
import java.util.BitSet;
import sync.DeltaEncoder;

public class Node {
    private final int id;
    private final BloomFilter bloomFilter;
    private final DeltaEncoder encoder;

    public Node(int id, int size, int hashCount, HashFunction hashFunction, Compressor compressor) {
        this.id = id;
        this.bloomFilter = new BloomFilter(size, hashCount, hashFunction);
        this.encoder = new DeltaEncoder(compressor);
    }

    public int getId() {
        return id;
    }

    public BloomFilter getFilter() {
        return bloomFilter;
    }

    // Method to send the full filter in a compressed form
    public Message sendFullFilter() {
        // Convert the bloom filter's BitSet to a boolean array manually
        BitSet bitSet = bloomFilter.getBitSet();
        boolean[] bits = new boolean[bitSet.length()]; // Create a boolean[] of the same size as the BitSet

        // Populate the boolean array with the BitSet values
        for (int i = 0; i < bits.length; i++) {
            bits[i] = bitSet.get(i);  // Set the boolean array based on the BitSet
        }
        
        // Compress the bits before sending
        byte[] compressedData = encoder.getCompressor().compress(bits);
        
        return new Message(Message.Type.FULL_FILTER, compressedData, bits.length, id);
    }

    // Method to send a delta update between this filter and another node's filter
    public Message sendDelta(Node other) {
        // Encode the delta update between the two filters
        byte[] delta = encoder.encodeDelta(other.getFilter(), this.bloomFilter);
        
        return new Message(
            Message.Type.DELTA_UPDATE,
            delta,
            bloomFilter.getSize(),
            id
        );
    }

    // Method to receive a message and apply it (either full filter or delta update)
    public void receive(Message msg) {
        switch (msg.type) {
            case FULL_FILTER:
                // Decompress the received full filter
                boolean[] bits = encoder.getCompressor().decompress(msg.data, msg.originalBitLength);

                // Update the bloom filter with the received bits
                for (int i = 0; i < bits.length; i++) {
                    if (bits[i]) {
                        bloomFilter.getBitSet().set(i);
                    } else {
                        bloomFilter.getBitSet().clear(i);
                    }
                }
                break;

            case DELTA_UPDATE:
                // Apply the delta update to the bloom filter
                encoder.applyDelta(bloomFilter, msg.data);
                break;
        }
    }
}


// import bloom.BloomFilter;
// import compress.RLECompressor;
// import hash.SimpleHash;
// import sync.DeltaEncoder;


// public class Main {
//     public static void main(String[] args) {
//         int size = 1000;         // bits
//         int hashCount = 3;       // number of hash functions

//         BloomFilter filter = new BloomFilter(size, hashCount, new SimpleHash());

//         // Add items
//         filter.add("apple");
//         filter.add("banana");
//         filter.add("cherry");

//         // Test membership
//         System.out.println("apple?  " + filter.mightContain("apple"));   // true
//         System.out.println("banana? " + filter.mightContain("banana"));  // true
//         System.out.println("grape?  " + filter.mightContain("grape"));   // maybe false


//         BloomFilter updatedFilter = new BloomFilter(size, hashCount, new SimpleHash());
//         updatedFilter.add("apple");
//         updatedFilter.add("banana");
//         updatedFilter.add("cherry");
//         updatedFilter.add("date"); // NEW 

//         // Delta
//         DeltaEncoder encoder = new DeltaEncoder(new RLECompressor());
//         byte[] delta = encoder.encodeDelta(filter, updatedFilter);

//         System.out.println("Delta size (bytes): " + delta.length);

//         // Apply delta to original
//         encoder.applyDelta(filter, delta);

//         // Confirm update
//         System.out.println("date?    " + filter.mightContain("date")); // Should be true



//     }
// }



// public class Main {
//     public static void main(String[] args) {
//         int size = 1000;
//         int hashCount = 3;

//         NetworkSimulator simulator = new NetworkSimulator();

//         // Create 3 nodes
//         for (int i = 0; i < 3; i++) {
//             Node node = new Node(i, size, hashCount, new SimpleHash(), new RLECompressor());
//             simulator.addNode(node);
//         }

//         // Add same initial items to Node 0
//         Node node0 = simulator.getNodes().get(0);
//         node0.getFilter().add("apple");
//         node0.getFilter().add("banana");

//         simulator.broadcastFull(0); // Node 0 sends full filter to others

//         // Node 0 adds new element
//         node0.getFilter().add("grape");

//         simulator.broadcastDelta(0); // Node 0 sends delta to others
//     }
// }



package main;

import benchmark.BenchmarkRunner;

public class Main {
    public static void main(String[] args) {
        try {
            String outputFile = "benchmark_results.csv";
            BenchmarkRunner runner = new BenchmarkRunner();
            runner.run(outputFile);

            System.out.println("All experiments completed. Results saved to: " + outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

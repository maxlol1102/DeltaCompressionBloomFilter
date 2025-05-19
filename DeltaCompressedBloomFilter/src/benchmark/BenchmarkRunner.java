package benchmark;

import bloom.BloomFilter;
import bloom.CountingBloomFilter;
import compress.RLECompressor;
import hash.*;
import java.util.*;
import sync.CountingDeltaEncoder;
import sync.DeltaEncoder;
import util.DataGenerator;


public class BenchmarkRunner {

    public void run(String filename) throws Exception {
        ReportGenerator report = new ReportGenerator(filename);
        Random rand = new Random();
        double targetFalsePositiveRate = 0.01;

        String filePath = "C:/Users/ADMIN/Desktop/DeltaCompressedBloomFilter/src/data/data.txt";

        List<String> allPasswords = new ArrayList<>(DataGenerator.loadPasswordsFromFile(filePath, Integer.MAX_VALUE));


        String[] hashNames = {
            "SimpleHash", "BadHash", "UniversalHash",
            "MurmurHash", "FNVHash", "XXHash", "HashMix"
        };

        for (String hashName : hashNames) {
            for (int numElements = 10_000; numElements <= 100_000; numElements += 10_000) {

                if (numElements >= allPasswords.size()) {
                    System.out.println("Skipping " + numElements + " — not enough data.");
                    continue;
                }

                int m = (int) Math.ceil(-1 * numElements * Math.log(targetFalsePositiveRate) / (Math.pow(Math.log(2), 2)));
                int hashCount = (int) Math.ceil((m / (double) numElements) * Math.log(2));
                int originalSizeBytes = m / 8;

                HashFunction hashFunction;
                switch (hashName) {
                    case "SimpleHash": hashFunction = new SimpleHash(); break;
                    case "BadHash": hashFunction = new BadHash(); break;
                    case "UniversalHash": hashFunction = new UniversalHash(m); break;
                    case "MurmurHash": hashFunction = new MurmurHash(); break;
                    case "FNVHash": hashFunction = new FNVHash(); break;
                    case "XXHash": hashFunction = new XXHash(); break;
                    case "HashMix": hashFunction = new HashMix(); break;
                    default: throw new RuntimeException("Unknown hash function: " + hashName);
                }

                int baseEnd = Math.min(numElements, allPasswords.size());
                int deltaSize = Math.min(numElements / 10, allPasswords.size() - baseEnd);
                int updateStart = baseEnd;
                int updateEnd = updateStart + deltaSize;

                Set<String> baseSet = new HashSet<>(allPasswords.subList(0, baseEnd));
                Set<String> newItems = new HashSet<>(allPasswords.subList(updateStart, updateEnd));

                BloomFilter base = new BloomFilter(m, hashCount, hashFunction);
                for (String item : baseSet) base.add(item);

                BloomFilter updated = new BloomFilter(m, hashCount, hashFunction);
                for (String item : baseSet) updated.add(item);
                for (String item : newItems) updated.add(item);

                DeltaEncoder encoder = new DeltaEncoder(new RLECompressor());

                boolean[] fullBits = new boolean[m];
                for (int i = 0; i < m; i++) fullBits[i] = updated.getBitSet().get(i);
                long startFull = System.nanoTime();
                byte[] fullCompressed = encoder.getCompressor().compress(fullBits);
                long endFull = System.nanoTime();
                long timeFullMs = (endFull - startFull) / 1_000_000;
                long memoryFull = getUsedMemory();
                double fullCompressionRatio = fullCompressed.length / (double) originalSizeBytes;

                long startDelta = System.nanoTime();
                byte[] delta = encoder.encodeDelta(base, updated);
                long endDelta = System.nanoTime();
                long timeDeltaMs = (endDelta - startDelta) / 1_000_000;
                long memoryDelta = getUsedMemory();
                double deltaCompressionRatio = delta.length / (double) originalSizeBytes;

                int falsePositives = 0;
                int totalTests = 1000;
                for (int i = 0; i < totalTests; i++) {
                    String query = "notpresent" + rand.nextInt(1_000_000);
                    if (updated.mightContain(query)) falsePositives++;
                }
                double fpRate = falsePositives / (double) totalTests;

                report.log("HashTest", numElements, hashName + "-Full", fullCompressed.length, fpRate, memoryFull / 1024, timeFullMs, fullCompressionRatio);
                report.log("HashTest", numElements, hashName + "-Delta", delta.length, fpRate, memoryDelta / 1024, timeDeltaMs, deltaCompressionRatio);


                // -------------------- CBF Test --------------------
                CountingBloomFilter cbfOld = new CountingBloomFilter(m, hashCount, hashFunction);
                CountingBloomFilter cbfNew = new CountingBloomFilter(m, hashCount, hashFunction);

                for (String item : baseSet) cbfOld.add(item);
                for (String item : baseSet) cbfNew.add(item);
                for (String item : newItems) cbfNew.add(item);

                CountingDeltaEncoder cbfEncoder = new CountingDeltaEncoder(new RLECompressor());
                long startCBF = System.nanoTime();
                byte[] cbfDelta = cbfEncoder.encodeDelta(cbfOld, cbfNew);
                long endCBF = System.nanoTime();
                long timeCBFMs = (endCBF - startCBF) / 1_000_000;
                long memoryCBF = getUsedMemory();
                double cbfRatio = cbfDelta.length / (double) (m * 2);

                int cbfFalsePositives = 0;
                for (int i = 0; i < 1000; i++) {
                    String query = "notpresent" + rand.nextInt(1_000_000);
                    if (cbfNew.mightContain(query)) cbfFalsePositives++;
                }
                double cbfFpRate = cbfFalsePositives / 1000.0;

                report.log("HashTest", numElements, hashName + "-CBF", cbfDelta.length, cbfFpRate, memoryCBF / 1024, timeCBFMs, cbfRatio);
           
            }
        }

        report.close();
        System.out.println("All hash function tests complete. Results saved to " + filename);
    }

    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}


































// public class BenchmarkRunner {

//     public void run(String filename) throws Exception {
//         ReportGenerator report = new ReportGenerator(filename);
//         Random rand = new Random();
//         double targetFalsePositiveRate = 0.01;

//         String filePath = "C:/Users/ADMIN/Desktop/DeltaCompressedBloomFilter/src/data/data.txt";
//         List<String> allPasswords = new ArrayList<>(DataGenerator.loadPasswordsFromFile(filePath, 100000));

//         String[] hashNames = {
//             "SimpleHash", "BadHash", "UniversalHash",
//             "MurmurHash", "FNVHash", "XXHash", "HashMix"
//         };

//         for (String hashName : hashNames) {
//             for (int numElements = 10_000; numElements <= 100_000; numElements += 10_000) {

//                 if (numElements + numElements / 10 > allPasswords.size()) {
//                     System.out.println("Skipping " + numElements + " — not enough data.");
//                     continue;
//                 }

//                 int m = (int) Math.ceil(-1 * numElements * Math.log(targetFalsePositiveRate) / (Math.pow(Math.log(2), 2)));
//                 int hashCount = (int) Math.ceil((m / (double) numElements) * Math.log(2));
//                 int originalSizeBytes = m / 8;

//                 HashFunction hashFunction;
//                 switch (hashName) {
//                     case "SimpleHash": hashFunction = new SimpleHash(); break;
//                     case "BadHash": hashFunction = new BadHash(); break;
//                     case "UniversalHash": hashFunction = new UniversalHash(m); break;
//                     case "MurmurHash": hashFunction = new MurmurHash(); break;
//                     case "FNVHash": hashFunction = new FNVHash(); break;
//                     case "XXHash": hashFunction = new XXHash(); break;
//                     case "HashMix": hashFunction = new HashMix(); break;
//                     default: throw new RuntimeException("Unknown hash function: " + hashName);
//                 }

//                 Set<String> baseSet = new HashSet<>(allPasswords.subList(0, numElements));
//                 int updateStart = Math.min(numElements, allPasswords.size() - numElements / 10);
//                 Set<String> newItems = new HashSet<>(allPasswords.subList(updateStart, updateStart + numElements / 10));

//                 BloomFilter base = new BloomFilter(m, hashCount, hashFunction);
//                 for (String item : baseSet) base.add(item);

//                 BloomFilter updated = new BloomFilter(m, hashCount, hashFunction);
//                 for (String item : baseSet) updated.add(item);
//                 for (String item : newItems) updated.add(item);

//                 DeltaEncoder encoder = new DeltaEncoder(new RLECompressor());

//                 boolean[] fullBits = new boolean[m];
//                 for (int i = 0; i < m; i++) fullBits[i] = updated.getBitSet().get(i);
//                 long startFull = System.nanoTime();
//                 byte[] fullCompressed = encoder.getCompressor().compress(fullBits);
//                 long endFull = System.nanoTime();
//                 long timeFullMs = (endFull - startFull) / 1_000_000;
//                 long memoryFull = getUsedMemory();
//                 double fullCompressionRatio = fullCompressed.length / (double) originalSizeBytes;

//                 long startDelta = System.nanoTime();
//                 byte[] delta = encoder.encodeDelta(base, updated);
//                 long endDelta = System.nanoTime();
//                 long timeDeltaMs = (endDelta - startDelta) / 1_000_000;
//                 long memoryDelta = getUsedMemory();
//                 double deltaCompressionRatio = delta.length / (double) originalSizeBytes;

//                 int falsePositives = 0;
//                 int totalTests = 1000;
//                 for (int i = 0; i < totalTests; i++) {
//                     String query = "notpresent" + rand.nextInt(1_000_000);
//                     if (updated.mightContain(query)) falsePositives++;
//                 }
//                 double fpRate = falsePositives / (double) totalTests;

//                 report.log("HashTest", numElements, hashName + "-Full", fullCompressed.length, fpRate, memoryFull / 1024, timeFullMs, fullCompressionRatio);
//                 report.log("HashTest", numElements, hashName + "-Delta", delta.length, fpRate, memoryDelta / 1024, timeDeltaMs, deltaCompressionRatio);

//                 // -------------------- CBF Test --------------------
//                 CountingBloomFilter cbfOld = new CountingBloomFilter(m, hashCount, hashFunction);
//                 CountingBloomFilter cbfNew = new CountingBloomFilter(m, hashCount, hashFunction);

//                 for (String item : baseSet) cbfOld.add(item);
//                 for (String item : baseSet) cbfNew.add(item);
//                 for (String item : newItems) cbfNew.add(item);

//                 CountingDeltaEncoder cbfEncoder = new CountingDeltaEncoder(new RLECompressor());
//                 long startCBF = System.nanoTime();
//                 byte[] cbfDelta = cbfEncoder.encodeDelta(cbfOld, cbfNew);
//                 long endCBF = System.nanoTime();
//                 long timeCBFMs = (endCBF - startCBF) / 1_000_000;
//                 long memoryCBF = getUsedMemory();
//                 double cbfRatio = cbfDelta.length / (double) (m * 2);

//                 int cbfFalsePositives = 0;
//                 for (int i = 0; i < 1000; i++) {
//                     String query = "notpresent" + rand.nextInt(1_000_000);
//                     if (cbfNew.mightContain(query)) cbfFalsePositives++;
//                 }
//                 double cbfFpRate = cbfFalsePositives / 1000.0;

//                 report.log("HashTest", numElements, hashName + "-CBFDelta", cbfDelta.length, cbfFpRate, memoryCBF / 1024, timeCBFMs, cbfRatio);
//             }
//         }

//         report.close();
//         System.out.println("All hash function tests complete. Results saved to " + filename);
//     }

//     private long getUsedMemory() {
//         Runtime runtime = Runtime.getRuntime();
//         return runtime.totalMemory() - runtime.freeMemory();
//     }
// }

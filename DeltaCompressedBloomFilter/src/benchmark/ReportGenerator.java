package benchmark;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ReportGenerator {
    private BufferedWriter writer;

    public ReportGenerator(String filename) throws IOException {
        writer = new BufferedWriter(new FileWriter(filename));
        writer.write("Test,Elements,FilterType,Size(Bytes),FalsePositiveRate,Memory(KB),Time(ms),CompressionRatio\n");
    }

    public void log(String testName, int elements, String type, int size, double fpRate, long memoryKB, long timeMs, double compressionRatio) throws IOException {
        writer.write(testName + "," + elements + "," + type + "," + size + "," + fpRate + "," + memoryKB + "," + timeMs + "," + String.format("%.3f", compressionRatio) + "\n");
    }

    public void close() throws IOException {
        writer.close();
    }
}

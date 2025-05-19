package compress;

import java.util.ArrayList;
import java.util.List;

public class RLECompressor implements Compressor {

    @Override
    public byte[] compress(boolean[] bits) {
        List<Byte> output = new ArrayList<>();
        boolean current = bits[0];
        int count = 1;

        for (int i = 1; i < bits.length; i++) {
            if (bits[i] == current && count < 255) {
                count++;
            } else {
                output.add((byte) (current ? 1 : 0));
                output.add((byte) count);
                current = bits[i];
                count = 1;
            }
        }

        output.add((byte) (current ? 1 : 0));
        output.add((byte) count);

        byte[] result = new byte[output.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = output.get(i);
        }
        return result;
    }

    @Override
    public boolean[] decompress(byte[] data, int originalLength) {
        boolean[] bits = new boolean[originalLength];
        int index = 0;

        for (int i = 0; i < data.length; i += 2) {
            boolean value = data[i] == 1;
            int count = data[i + 1] & 0xFF;

            for (int j = 0; j < count; j++) {
                if (index < originalLength) {
                    bits[index++] = value;
                }
            }
        }
        return bits;
    }
}

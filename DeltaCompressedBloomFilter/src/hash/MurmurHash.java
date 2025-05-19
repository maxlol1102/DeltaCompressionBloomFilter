package hash;

public class MurmurHash implements HashFunction {
    @Override
    public int hash(String key, int seed) {
        byte[] data = key.getBytes();
        int len = data.length;
        int h = seed;
        int c1 = 0xcc9e2d51;
        int c2 = 0x1b873593;
        int i = 0;

        while (len >= 4) {
            int k = (data[i] & 0xff) | ((data[i + 1] & 0xff) << 8)
                    | ((data[i + 2] & 0xff) << 16) | ((data[i + 3] & 0xff) << 24);
            i += 4;
            len -= 4;

            k *= c1;
            k = (k << 15) | (k >>> 17);
            k *= c2;

            h ^= k;
            h = (h << 13) | (h >>> 19);
            h = h * 5 + 0xe6546b64;
        }

        int k1 = 0;
        switch (len) {
            case 3:
                k1 ^= (data[i + 2] & 0xff) << 16;
            case 2:
                k1 ^= (data[i + 1] & 0xff) << 8;
            case 1:
                k1 ^= (data[i] & 0xff);
                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);
                k1 *= c2;
                h ^= k1;
        }

        h ^= data.length;
        h ^= (h >>> 16);
        h *= 0x85ebca6b;
        h ^= (h >>> 13);
        h *= 0xc2b2ae35;
        h ^= (h >>> 16);

        return Math.abs(h);
    }
}

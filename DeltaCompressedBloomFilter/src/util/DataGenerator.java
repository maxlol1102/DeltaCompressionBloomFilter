package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class DataGenerator {




     public static Set<String> loadPasswordsFromFile(String filePath, int limit) {
        Set<String> passwords = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null && count < limit) {
                line = line.trim();
                if (!line.isEmpty()) { // ✅ skip blank lines
                    passwords.add(line);
                    count++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading password file: " + e.getMessage());
        }
        return passwords;
    }


    
    public static Set<String> generateRandomStrings(int count) {
        Set<String> set = new HashSet<>();
        Random rand = new Random();

        while (set.size() < count) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                char c = (char) ('a' + rand.nextInt(26));
                sb.append(c);
            }
            set.add(sb.toString());
        }

        return set;
    }
}

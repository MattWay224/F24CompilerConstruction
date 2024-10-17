package things;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InputFileReader {
    public static String readInputFromFile(String filePath) throws IOException {
        StringBuilder input = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n");
            }
        }
        return input.toString();
    }
}

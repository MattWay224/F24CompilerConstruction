import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File text = new File("src/test.txt");
        File out = new File("src/output.txt");

        try (Scanner scanner = new Scanner(text); FileWriter writer = new FileWriter(out)) {
            StringBuilder input = new StringBuilder();
            while (scanner.hasNext()) {
                input.append(scanner.nextLine()).append("\n");
            }

            Flexer lexer = new Flexer(input.toString());
            List<Flexer.Token> tokens = lexer.tokenize();

            StringBuilder output = new StringBuilder();
            for (Flexer.Token token : tokens) {
                output.append(token.toString()).append("\n");
            }

            writer.write(output.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

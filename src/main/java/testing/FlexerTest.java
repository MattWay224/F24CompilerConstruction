package testing;

import steps.Flexer;
import steps.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.List;

public class FlexerTest {
    StringBuilder input;
    Flexer lexer;

    public String test(String test) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(test))) {
            this.input = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n");
            }

            this.lexer = new Flexer();
            this.lexer.setInput(input.toString());

            List<Token> tokens = lexer.tokenize();

            StringBuilder output = new StringBuilder();
            for (Token token : tokens) {
                output.append(token.toString()).append("\n");
            }
            return output.toString();
        } catch (IOException e) {
            throw new IOException(e);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}


package testing;

import ast.nodes.ASTNode;
import steps.ASTPrinter;
import steps.Flexer;
import steps.Parser;
import steps.Token;
import visitors.PrettyVisitor;

import java.io.Writer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.List;

public class FsyntaxerTest {
    StringBuilder input;
    Parser parser;
    Flexer flexer;

    public String test(String test) throws IOException {
        input = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(test))) {
            this.parser = new Parser();
            this.flexer = new Flexer();
            Writer sw = new StringWriter();
            PrettyVisitor prettyVisitor = new PrettyVisitor();

            String line;
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n");
            }

            this.flexer.setInput(input.toString());
            List<Token> tokens = this.flexer.tokenize();
            this.parser.setTokens(tokens);
            ASTNode ast = this.parser.parse();

            ASTPrinter.printAST(sw, ast, prettyVisitor, 0);
            String output = sw.toString();
            sw.flush();
            sw.close();
            return output;
        } catch (IOException e) {
            throw new IOException(e);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}

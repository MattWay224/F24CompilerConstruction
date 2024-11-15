import ast.nodes.ASTNode;
import steps.*;
import things.ASTPrinter;
import things.InputFileReader;
import visitors.PrettyVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Main {
    private static final int TOTAL_TESTS = 17;

    public static void main(String[] args) throws Exception {
        PrettyVisitor visitor = new PrettyVisitor();
        Flexer lexer = new Flexer();
        Parser parser = new Parser();
        FSemanter semanter = new FSemanter(parser.getGlobalScope());
        FGenerator generator=new FGenerator(parser.getGlobalScope(),semanter);

        for (int i = 0; i <= 1; i++) {
            processTestFile(i, lexer, parser, visitor, semanter, generator);
        }
    }

    private static void processTestFile(int testNumber, Flexer lexer, Parser parser, PrettyVisitor visitor, FSemanter semanter,FGenerator generator) {
        String inputPath = "src/main/resources/inputs/test" + testNumber + ".txt";
        File outputLexerFile = new File("src/main/resources/flexer/outputs/output" + testNumber + ".txt");
        File outputParserFile = new File("src/main/resources/fsyntaxer/outputs/output" + testNumber + ".txt");

        try (Writer writerLexer = new FileWriter(outputLexerFile);
             Writer writerParser = new FileWriter(outputParserFile)) {

            String input = InputFileReader.readInputFromFile(inputPath);
            List<Token> tokens = tokenizeInput(lexer, input);
            writeTokens(writerLexer, tokens);

            ASTNode ast = parseTokens(parser, tokens);
            semanter.analyze(ast);
            generator.generate(ast);
            ASTPrinter.printAST(writerParser, ast, visitor, 0);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage() + " Test: " + testNumber);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + " Test: " + testNumber);
            throw new RuntimeException(e);
        }
    }

    private static List<Token> tokenizeInput(Flexer lexer, String input) throws Exception {
        lexer.setInput(input);
        return lexer.tokenize();
    }

    private static void writeTokens(Writer writer, List<Token> tokens) throws IOException {
        StringBuilder output = new StringBuilder();
        for (Token token : tokens) {
            output.append(token.toString()).append("\n");
        }
        writer.write(output.toString());
    }

    private static ASTNode parseTokens(Parser parser, List<Token> tokens) throws Exception {
        parser.setTokens(tokens);
        return parser.parse();
    }
}

package testing;

import ast.nodes.ASTNode;
import things.ASTPrinter;
import steps.Flexer;
import steps.Parser;
import steps.Token;
import things.InputFileReader;
import visitors.PrettyVisitor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class FsyntaxerTest {
    private Parser parser;

    public String test(String testFilePath) throws Exception {
        String input = InputFileReader.readInputFromFile(testFilePath);
        this.parser = new Parser();
        Flexer flexer = new Flexer();
        flexer.setInput(input);
        List<Token> tokens = flexer.tokenize();
        ASTNode ast = parseTokens(tokens);
        return printAST(ast);
    }

    private ASTNode parseTokens(List<Token> tokens) throws Exception {
        parser.setTokens(tokens);
        return parser.parse();
    }

    private String printAST(ASTNode ast) throws IOException {
        Writer stringWriter = new StringWriter();
        PrettyVisitor prettyVisitor = new PrettyVisitor();
        ASTPrinter.printAST(stringWriter, ast, prettyVisitor, 0);
        String output = stringWriter.toString();
        stringWriter.flush();
        return output;
    }
}

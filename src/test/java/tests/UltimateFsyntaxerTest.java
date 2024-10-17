package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import testing.FsyntaxerTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class UltimateFsyntaxerTest {
    private FsyntaxerTest fsyntaxer;

    @BeforeEach
    void setUp() {
        fsyntaxer = new FsyntaxerTest();
    }

    @Test
    void testParserTreeWithTest1File() {
        runParserTreeTest(1);
    }

    @Test
    void testParserTreeWithTest2File() {
        runParserTreeTest(2);
    }

    @Test
    void testParserTreeWithTest3File() {
        runParserTreeTest(3);
    }

    @Test
    void testParserTreeWithTest4File() {
        runParserTreeTest(4);
    }

    @Test
    void testParserTreeWithTest5File() {
        runParserTreeTest(5);
    }

    @Test
    void testParserTreeWithTest6File() {
        runParserTreeTest(6);
    }

    @Test
    void testParserTreeWithTest7File() {
        runParserTreeTest(7);
    }

    @Test
    void testParserTreeWithTest8File() {
        runParserTreeTest(8);
    }

    @Test
    void testParserTreeWithTest9File() {
        runParserTreeTest(9);
    }

    @Test
    void testParserTreeWithTest10File() {
        runParserTreeTest(10);
    }

    @Test
    void testParserTreeWithTest11File() {
        runParserTreeTest(11);
    }

    @Test
    void testParserTreeWithTest12File() {
        runParserTreeTest(12);
    }

    @Test
    void testParserTreeWithTest13File() {
        runParserTreeTest(13);
    }

    @Test
    void testParserTreeWithTest14File() {
        runParserTreeTest(14);
    }

    @Test
    @Disabled("Test 15 for parser tree is disabled until fix")
    void testTParserTreeWithTest15File() {
        runParserTreeTest(15);
    }

    @Test
    void testParserTreeWithTest16File() {
        runParserTreeTest(16);
    }

    @Test
    void testParserTreeWithTest17File() {
        runParserTreeTest(17);
    }

    private void runParserTreeTest(int testNumber) {
        String testFilePath = String.format("src/test/resources/inputs/test%d.txt", testNumber);
        String solutionFilePath = String.format("src/test/resources/fsyntaxer/solutions/test%d_solution.txt", testNumber);

        try {
            String actualOutput = fsyntaxer.test(testFilePath);
            assertNotNull(actualOutput, "The output should not be null.");
            String expectedOutput = Files.readString(Path.of(solutionFilePath));
            assertThat(actualOutput).isEqualTo(expectedOutput);
        } catch (RuntimeException | IOException e) {
            fail("Exception occurred during test execution for test" + testNumber + ": " + e.getMessage());
        }
    }

    @Test
    void testParserTreeWithInvalidFile() {
        String testFilePath = "src/test/resources/inputs/nonexistent.txt";

        try {
            fsyntaxer.test(testFilePath);
            fail("Expected FileNotFoundException was not thrown");
        } catch (IOException e) {
            assertThat(e.getMessage()).contains("nonexistent.txt");
        } catch (RuntimeException e) {
            fail("Unexpected exception occurred: " + e.getMessage());
        }
    }
}

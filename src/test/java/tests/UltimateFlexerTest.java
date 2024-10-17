package tests; // Make sure to include your package declaration

import testing.FlexerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class UltimateFlexerTest {

    private FlexerTest flexerTest;

    @BeforeEach
    void setUp() {
        flexerTest = new FlexerTest();
    }

    @Test
    void testTokenizationWithTest1File() {
        runTokenizationTest(1);
    }

    @Test
    void testTokenizationWithTest2File() {
        runTokenizationTest(2);
    }

    @Test
    void testTokenizationWithTest3File() {
        runTokenizationTest(3);
    }

    @Test
    void testTokenizationWithTest4File() {
        runTokenizationTest(4);
    }

    @Test
    void testTokenizationWithTest5File() {
        runTokenizationTest(5);
    }

    @Test
    void testTokenizationWithTest6File() {
        runTokenizationTest(6);
    }

    @Test
    void testTokenizationWithTest7File() {
        runTokenizationTest(7);
    }

    @Test
    void testTokenizationWithTest8File() {
        runTokenizationTest(8);
    }

    @Test
    void testTokenizationWithTest9File() {
        runTokenizationTest(9);
    }

    @Test
    void testTokenizationWithTest10File() {
        runTokenizationTest(10);
    }

    @Test
    void testTokenizationWithTest11File() {
        runTokenizationTest(11);
    }

    @Test
    void testTokenizationWithTest12File() {
        runTokenizationTest(12);
    }

    @Test
    void testTokenizationWithTest13File() {
        runTokenizationTest(13);
    }

    @Test
    void testTokenizationWithTest14File() {
        runTokenizationTest(14);
    }

    @Test
    void testTokenizationWithTest15File() {
        runTokenizationTest(15);
    }

    @Test
    void testTokenizationWithTest16File() {
        runTokenizationTest(16);
    }

    @Test
    void testTokenizationWithTest17File() {
        runTokenizationTest(17);
    }

    private void runTokenizationTest(int testNumber) {
        String testFilePath = String.format("src/test/resources/inputs/test%d.txt", testNumber);
        String solutionFilePath = String.format("src/test/resources/flexer/solutions/test%d_solution.txt", testNumber);

        try {
            String actualOutput = flexerTest.test(testFilePath);
            assertNotNull(actualOutput, "The output should not be null.");
            String expectedOutput = Files.readString(Path.of(solutionFilePath));
            assertThat(actualOutput).isEqualTo(expectedOutput);
        } catch (Exception e) {
            fail("Exception occurred during test execution for test" + testNumber + ": " + e.getMessage());
        }
    }

    @Test
    void testTokenizationWithInvalidFile() {
        String testFilePath = "src/test/resources/inputs/nonexistent.txt";

        try {
            flexerTest.test(testFilePath);
            fail("Expected FileNotFoundException was not thrown");
        } catch (IOException e) {
            assertThat(e.getMessage()).contains("nonexistent.txt");
        } catch (Exception e) {
            fail("Unexpected exception occurred: " + e.getMessage());
        }
    }
}

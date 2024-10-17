import tests.FlexerTest;
import tests.FsyntaxerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class UltimateTest {

    private FlexerTest flexerTest;
    private FsyntaxerTest fsyntaxerTest;

    @BeforeEach
    void setUp() {
        flexerTest = new FlexerTest();
        fsyntaxerTest = new FsyntaxerTest();
    }

    @Test
    void testTokenizationWithValidFile() {
        for (int i = 1; i <= 17; i++) {
            String testFilePath = String.format("src/test/resources/inputs/test%d.txt", i);
            String solutionFilePath = String.format("src/test/resources/flexer/solutions/test%d_solution.txt", i);

            try {
                String actualOutput = flexerTest.test(testFilePath);
                assertNotNull(actualOutput, "The output should not be null.");
                String expectedOutput = Files.readString(Path.of(solutionFilePath));
                assertThat(actualOutput).isEqualTo(expectedOutput);

            } catch (Exception e) {
                fail("Exception occurred during test execution: " + e.getMessage());
            }
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

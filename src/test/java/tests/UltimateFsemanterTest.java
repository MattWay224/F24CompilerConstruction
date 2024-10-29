package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import testing.FsemanterTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class UltimateFsemanterTest {
	private FsemanterTest fSemanter;

	@BeforeEach
	void setUp() {
		fSemanter = new FsemanterTest();
	}

	@Test
	void testSemanterTreeWithTest1File() {
		runSemanterTreeTest(1);
	}

	@Test
	void testSemanterTreeWithTest2File() {
		runSemanterTreeTest(2);
	}

	@Test
	void testSemanterTreeWithTest3File() {
		runSemanterTreeTest(3);
	}

	@Test
	void testSemanterTreeWithTest4File() {
		runSemanterTreeTest(4);
	}

	@Test
	void testSemanterTreeWithTest5File() {
		runSemanterTreeTest(5);
	}

	@Test
	void testSemanterTreeWithTest6File() {
		runSemanterTreeTest(6);
	}

	@Test
	void testSemanterTreeWithTest7File() {
		runSemanterTreeTest(7);
	}

	@Test
	void testSemanterTreeWithTest8File() {
		runSemanterTreeTest(8);
	}

	@Test
	void testSemanterTreeWithTest9File() {
		runSemanterTreeTest(9);
	}

	@Test
	void testSemanterTreeWithTest10File() {
		runSemanterTreeTest(10);
	}

	@Test
	void testSemanterTreeWithTest11File() {
		runSemanterTreeTest(11);
	}

	@Test
	void testSemanterTreeWithTest12File() {
		runSemanterTreeTest(12);
	}

	@Test
	void testSemanterTreeWithTest13File() {
		runSemanterTreeTest(13);
	}

	@Test
	void testSemanterTreeWithTest14File() {
		runSemanterTreeTest(14);
	}

	@Test
	@Disabled("Test 15 for parser tree is disabled until fix")
	void testSemanterTreeWithTest15File() {
		runSemanterTreeTest(15);
	}

	@Test
	void testSemanterTreeWithTest16File() {
		runSemanterTreeTest(16);
	}

	@Test
	void testSemanterTreeWithTest17File() {
		runSemanterTreeTest(17);
	}

	private void runSemanterTreeTest(int testNumber) {
		String testFilePath = String.format("src/test/resources/inputs/test%d.txt", testNumber);
		String solutionFilePath = String.format("src/test/resources/fsemantecer/solutions/test%d_solution.txt", testNumber);

		try {
			String actualOutput = fSemanter.test(testFilePath);
			assertNotNull(actualOutput, "The output should not be null.");
			String expectedOutput = Files.readString(Path.of(solutionFilePath));
			assertThat(actualOutput).isEqualTo(expectedOutput);
		} catch (Exception e) {
			fail("Exception occurred during test execution for test" + testNumber + ": " + e.getMessage());
		}
	}

	@Test
	void testSemanterTreeWithInvalidFile() {
		String testFilePath = "src/test/resources/inputs/nonexistent.txt";

		try {
			fSemanter.test(testFilePath);
			fail("Expected FileNotFoundException was not thrown");
		} catch (IOException e) {
			assertThat(e.getMessage()).contains("nonexistent.txt");
		} catch (Exception e) {
			fail("Unexpected exception occurred: " + e.getMessage());
		}
	}
}

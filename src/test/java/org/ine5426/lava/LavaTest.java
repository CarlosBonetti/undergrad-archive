package org.ine5426.lava;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class LavaTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void prepareRunner() throws IOException {
		Runner.createTempDir();
	}

	@After
	public void releaseRunner() {
		Runner.deleteTempDir();
	}

	public void assertOutput(String code, String expectedOutput) {
		String actualOutput = "";

		try {
			actualOutput = Runner.compileAndRun(code);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(expectedOutput, actualOutput);
	}
}

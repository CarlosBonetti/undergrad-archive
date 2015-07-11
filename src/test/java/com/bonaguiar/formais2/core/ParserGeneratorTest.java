package com.bonaguiar.formais2.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ParserGeneratorTest {

	@Test
	public void test() throws Exception {
		GLC glc = new GLC("S -> a B \n B -> a B | b | C \n C -> c | &");
		ParserGenerator generator = new ParserGenerator(glc);
		JavaParser parser = generator.getParser();

		assertTrue(parser.run("aab").message().contains("S B B"));
		assertFalse(parser.run("aa").success());
	}
}

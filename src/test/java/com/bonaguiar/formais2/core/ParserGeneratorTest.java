package com.bonaguiar.formais2.core;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

public class ParserGeneratorTest {

	@Test
	public void test() throws ParseException, IOException {
		GLC glc = new GLC("S -> a B \n B -> a B | b | C \n C -> c | &");
		ParserGenerator generator = new ParserGenerator(glc);
		System.out.println(generator.getParser());
	}

}

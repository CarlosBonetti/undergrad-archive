package com.bonaguiar.formais2.core;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

public class ParserGeneratorTest {

	@Test
	public void test() throws ParseException, IOException {
		GLC glc = new GLC("S -> a B \n B -> b B | b");
		ParserGenerator generator = new ParserGenerator(glc);
		System.out.println(generator.getParser());
	}

}

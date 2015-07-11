package com.bonaguiar.formais2.core;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

public class ParserGeneratorTest {

	private GLC glc;
	private ParserGenerator generator;
	private JavaParser parser;

	@Before
	public void setup() throws Exception {
		glc = new GLC("E -> T E2 \n" + "E2 -> + T E2 | & \n" + "T -> F T2 \n" + "T2 -> * F T2 | & \n" + "F -> ( E ) | d");
		generator = new ParserGenerator(glc);
		parser = generator.getParser();
	}

	@Test
	public void testarAnaliseCorretas() throws Throwable {
		System.out.println(parser);
		assertEquals("TODO", parser.run("d+d"));
		assertEquals("TODO", parser.run("d*d"));
	}

	@Test(expected = ParseException.class)
	public void testarAnaliseIncorreta() throws Throwable {
		parser.run("");
	}
}

package com.bonaguiar.formais2.core;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

public class ParserGeneratorTest {

	private JavaParser parser1;
	private JavaParser parser2;

	@Before
	public void setup() throws Exception {
		GLC glc = new GLC("E -> T E2 \n" + "E2 -> + T E2 | & \n" + "T -> F T2 \n" + "T2 -> * F T2 | & \n" + "F -> ( E ) | d");
		ParserGenerator generator = new ParserGenerator(glc);
		parser1 = generator.getParser();

		glc = new GLC("A -> B C \n B -> b | & \n C -> c");
		generator = new ParserGenerator(glc);
		parser2 = generator.getParser();
	}

	@Test
	public void testarAnaliseCorretasDeParser2() throws Throwable {
		assertEquals("A B C", parser2.run("bc"));
		assertEquals("A B C", parser2.run("c"));
	}

	@Test
	public void testarAnaliseCorretas() throws Throwable {
		assertEquals("E T F T2 E2 T F T2 E2", parser1.run("d+d"));
		assertEquals("E T F T2 F T2 E2", parser1.run("d*d"));
		assertEquals("E T F E T F T2 E2 T F T2 E2 T2 F E T F E T F T2 E2 T2 E2 T2 E2", parser1.run("(d+d)*((d))"));
	}

	@Test(expected = ParseException.class)
	public void testarAnaliseIncorretaParser2() throws Throwable {
		parser2.run("b");
	}

	@Test(expected = ParseException.class)
	public void testarAnaliseIncorreta1() throws Throwable {
		parser1.run("");
	}
}

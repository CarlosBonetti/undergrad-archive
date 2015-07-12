package com.bonaguiar.formais2.core;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

public class ParserGeneratorTest {

	private JavaParser parser1;
	private JavaParser parser2;
	private JavaParser parser3;
	private JavaParser parser4;
	private JavaParser parser5;
	private JavaParser parser6;

	@Before
	public void setup() throws Exception {
		GLC glc = new GLC("E -> T E2 \n" + "E2 -> + T E2 | & \n" + "T -> F T2 \n" + "T2 -> * F T2 | & \n" + "F -> ( E ) | d");
		ParserGenerator generator = new ParserGenerator(glc);
		parser1 = generator.getParser();

		glc = new GLC("A -> B C \n B -> b | & \n C -> c");
		generator = new ParserGenerator(glc);
		parser2 = generator.getParser();
		
		glc = new GLC("S -> L L1\nL1 -> = L | &\nL -> * L | id");
		generator = new ParserGenerator(glc);
		parser3 = generator.getParser();
		
		glc = new GLC("S -> A a A b | B b B a\nA -> &\nB -> &");
		generator = new ParserGenerator(glc);
		parser4 = generator.getParser();
		
		glc = new GLC("P -> begin D C end\nD -> int I | &\nI -> , id I | &\nC -> ; T = E C1 | T = E | com\nC1-> ; T = E C1| &\nE ->  + T E1\nE1 -> + T E1 | &\nT -> id T1 \nT1 -> [ E ] | &\n");
		generator = new ParserGenerator(glc);
		parser5 = generator.getParser();
	
		glc = new GLC("P -> B P1\nP1 -> ; B P1 | &\nI -> , id I | &\nB -> K V C\nK -> c K | &\nV -> v V | &\nC -> b C2 | &\nC2 -> K V ; C e C1 | C e C1 \nC1 -> com C1 | &\n");
		generator = new ParserGenerator(glc);
		parser6 = generator.getParser();
		
		
	}

	@Test
	public void testarAnaliseCorretasDeParser6() throws Throwable {
		assertEquals("S", parser6.run("cv"));
//		assertEquals("S", parser5.run("begin com end"));
	}
	
	@Test
	public void testarAnaliseCorretasDeParser5() throws Throwable {
		assertEquals("S", parser5.run("begin com end"));
//		assertEquals("S", parser5.run("begin com end"));
	}

	@Test
	public void testarAnaliseCorretasDeParser4() throws Throwable {
		assertEquals("S", parser4.run("ab"));
		assertEquals("S", parser4.run("ba"));
	}
	
	@Test
	public void testarAnaliseCorretasDeParser3() throws Throwable {
		assertEquals("S L L1 L L", parser3.run("id=*id"));
		assertEquals("S L", parser3.run("id"));
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

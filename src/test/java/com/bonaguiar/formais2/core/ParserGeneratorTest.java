package com.bonaguiar.formais2.core;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.bonaguiar.formais2.test.Assert;

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

		glc = new GLC(
				"P -> begin D C end\nD -> int I | &\nI -> , id I | &\nC -> ; T = E C1 | T = E | com\nC1-> ; T = E C1| &\nE ->  + T E1\nE1 -> + T E1 | &\nT -> id T1 \nT1 -> [ E ] | &\n");
		generator = new ParserGenerator(glc);
		parser5 = generator.getParser();

		glc = new GLC("P -> B P1\nP1 -> ; B P1 | &\nI -> , id I | &\nB -> K V C\nK -> c K | &\nV -> v V | &\nC -> b C2 | &\nC2 -> K V ; C e C1 | C e C1 \nC1 -> com C1 | &\n");
		generator = new ParserGenerator(glc);
		parser6 = generator.getParser();
	}

	@Test
	public void testarAnaliseCorretas() throws Throwable {
		assertEquals("E T F T2 E2 T F T2 E2", parser1.run("d + d"));
		assertEquals("E T F T2 F T2 E2", parser1.run("d * d"));
		assertEquals("E T F E T F T2 E2 T F T2 E2 T2 F E T F E T F T2 E2 T2 E2 T2 E2", parser1.run("( d + d ) * ( ( d ) )"));
	}

	@Test
	public void testarAnaliseCorretasDeParser2() throws Throwable {
		assertEquals("A B C", parser2.run("b c"));
		assertEquals("A B C", parser2.run("c"));
	}

	@Test
	public void testarAnaliseCorretasDeParser3() throws Throwable {
		assertEquals("S L L1 L L", parser3.run("id = * id"));
		assertEquals("S L L1", parser3.run("id"));
	}

	@Test
	public void testarAnaliseCorretasDeParser4() throws Throwable {
		assertEquals("S A A", parser4.run("a b"));
		assertEquals("S B B", parser4.run("b a"));
	}

	@Test
	public void testarAnaliseCorretasDeParser5() throws Throwable {
		assertEquals("P D C", parser5.run("begin com end"));
		 assertEquals("P D I I I C", parser5.run("begin int , id , id com end"));
	}

	@Test
	public void testarAnaliseCorretasDeParser6() throws Throwable {
		assertEquals("P B K K V V C P1", parser6.run("c v"));
	}

	@Test
	public void testarAnaliseIncorretaParser1() throws Throwable {
		Assert.assertContains(parser1.run(""));
		Assert.assertContains(parser1.run("( * )"));
	}
	
	@Test
	public void testarAnaliseIncorretaParser2() throws Throwable {
		Assert.assertContains(parser2.run("b"));
		Assert.assertContains(parser2.run("c c"));
	}
	
	@Test
	public void testarAnaliseIncorretaParser3() throws Throwable {
		Assert.assertContains(parser3.run("id = ="));
		Assert.assertContains(parser3.run("= id"));
	}
	
	@Test
	public void testarAnaliseIncorretaParser4() throws Throwable {
		Assert.assertContains(parser4.run("a"));
		Assert.assertContains(parser4.run("b"));
	}
	
	@Test
	public void testarAnaliseIncorretaParser5() throws Throwable {
		Assert.assertContains(parser5.run("begin , id end"));
		Assert.assertContains(parser5.run("beging [ E ] end"));
	}
	
	@Test
	public void testarAnaliseIncorretaParser6() throws Throwable {
		Assert.assertContains(parser6.run("e e"));
		Assert.assertContains(parser6.run("; ; e com com"));
	}
}

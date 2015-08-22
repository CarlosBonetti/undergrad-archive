package com.bonaguiar.formais1.core.grammar;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.bonaguiar.formais1.core.exception.FormaisException;
import com.bonaguiar.formais1.core.grammar.GRParser.Producao;

public class GRParserTest {

	@Test
	public void testarParseLine() throws FormaisException {
		Producao p;
		p = GRParser.parseLine(" S   ->   a  ");
		assertEquals(new Character('S'), p.produtor);
		assertEquals(Arrays.asList("a"), p.producoes);

		p = GRParser.parseLine("B->bA");
		assertEquals(new Character('B'), p.produtor);
		assertEquals(Arrays.asList("bA"), p.producoes);

		p = GRParser.parseLine(" A   ->   aS |  |  bC  |a   |b|c  ");
		assertEquals(new Character('A'), p.produtor);
		assertEquals(Arrays.asList("aS", "bC", "a", "b", "c"), p.producoes);
	}

	@Test(expected=FormaisException.class)
	public void testarParseLineComErro1() throws FormaisException {
		GRParser.parseLine("| ab");
	}

	@Test(expected=FormaisException.class)
	public void testarParseLineComErro2() throws FormaisException {
		GRParser.parseLine("S-> ");
	}

	@Test(expected=FormaisException.class)
	public void testarParseLineComErro3() throws FormaisException {
		GRParser.parseLine("->bA");
	}

	@Test(expected=FormaisException.class)
	public void testarParseLineComErro4() throws FormaisException {
		GRParser.parseLine("AS->a");
	}

	@Test
	public void testarParse() throws FormaisException {
		String raw = "S -> aS | a | d  | bB\n"
				+ "B -> b | bB\n"
				+ "C -> c | aC | aS | b";

		GramaticaRegular gr = GRParser.parse(raw);
		assertEquals(new Character('S'), gr.getS());
		assertTrue(gr.getVt().containsAll(Arrays.asList('a', 'b', 'c', 'd')));
		assertEquals(4, gr.getVt().size());
		assertTrue(gr.getVn().containsAll(Arrays.asList('S', 'B', 'C')));
		assertEquals(3, gr.getVn().size());
	}

	@Test
	public void testarParseEpsilon() throws FormaisException {
		String raw = "S -> & | aS | a | d  | bB\n"
				+ "B -> b | bB\n"
				+ "C -> c | aC | aS | b";

		GramaticaRegular gr = GRParser.parse(raw);
		assertEquals(new Character('S'), gr.getS());
		assertTrue(gr.getVt().containsAll(Arrays.asList('a', 'b', 'c', 'd')));
		assertEquals(4, gr.getVt().size());
		assertTrue(gr.getVn().containsAll(Arrays.asList('S', 'B', 'C')));
		assertEquals(3, gr.getVn().size());
		assertTrue(gr.getAceitaEpsilon());
	}
}

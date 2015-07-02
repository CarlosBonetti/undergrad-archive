package com.bonaguiar.formais1.core.expr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bonaguiar.formais1.core.exception.FormaisException;

public class ERParserTest {

	@Test
	public void testarParseChar() throws FormaisException {
		Nodo<Character> nodo = ERParser.parseChar('a');
		assertEquals(new Character('a'), nodo.getConteudo());

		Nodo<Character> nodo2 = ERParser.parseChar('&');
		assertEquals(new Character('&'), nodo2.getConteudo());

		Nodo<Character> nodo3 = ERParser.parseChar('0');
		assertEquals(new Character('0'), nodo3.getConteudo());
	}

	@Test(expected = FormaisException.class)
	public void testarParseCharInvalido1() throws FormaisException {
		ERParser.parseChar('*');
	}

	@Test(expected = FormaisException.class)
	public void testarParseCharInvalido2() throws FormaisException {
		ERParser.parseChar('>');
	}

	@Test
	public void testarPos() {
		assertEquals(2, ERParser.posOperador("ab|dc", '|'));
		assertEquals(-1, ERParser.posOperador("ab|dc", '*'));
		assertEquals(-1, ERParser.posOperador("(ab|dc)", '|'));
		assertEquals(14, ERParser.posOperador("((ab|dc)de?|r)|p", '|'));
	}

	@Test
	public void testarPosConcat() {
		assertEquals(1, ERParser.posConcat("ab"));
		assertEquals(1, ERParser.posConcat("abc"));
		assertEquals(1, ERParser.posConcat("a(bc)"));
		assertEquals(4, ERParser.posConcat("(ab)c"));
		assertEquals(2, ERParser.posConcat("a*b(c)"));
		assertEquals(2, ERParser.posConcat("a?be"));
		assertEquals(6, ERParser.posConcat("(a|c)*bc"));
		assertEquals(0, ERParser.posConcat("a*"));
	}

	@Test
	public void testarParse() throws FormaisException {
		Nodo<Character> nodo = ERParser.parse("");
		assertEquals(null, nodo.getConteudo());

		nodo = ERParser.parse("(a)");
		assertEquals(new Character('a'), nodo.getConteudo());
		assertTrue(nodo.ehFolha());

		nodo = ERParser.parse("((((b))))");
		assertEquals(new Character('b'), nodo.getConteudo());
		assertTrue(nodo.ehFolha());

		nodo = ERParser.parse("a|b");
		assertEquals(new Character('|'), nodo.getConteudo());
		assertEquals(new Character('a'), nodo.getEsq().getConteudo());
		assertEquals(new Character('b'), nodo.getDir().getConteudo());

		nodo = ERParser.parse("xy");
		assertEquals(new Character('.'), nodo.getConteudo());
		assertEquals(new Character('x'), nodo.getEsq().getConteudo());
		assertEquals(new Character('y'), nodo.getDir().getConteudo());

		nodo = ERParser.parse("a(bc)");
		assertEquals(new Character('.'), nodo.getConteudo());
		assertEquals(new Character('a'), nodo.getEsq().getConteudo());
		assertEquals(new Character('.'), nodo.getDir().getConteudo());
		assertEquals(new Character('b'), nodo.getDir().getEsq().getConteudo());
		assertEquals(new Character('c'), nodo.getDir().getDir().getConteudo());

		nodo = ERParser.parse("a*");
		assertEquals(new Character('*'), nodo.getConteudo());
		assertEquals(new Character('a'), nodo.getEsq().getConteudo());
		assertEquals(null, nodo.getDir());

		nodo = ERParser.parse("b?");
		assertEquals(new Character('?'), nodo.getConteudo());
		assertEquals(new Character('b'), nodo.getEsq().getConteudo());
		assertEquals(null, nodo.getDir());

		nodo = ERParser.parse("(ab)*");
		assertEquals(new Character('*'), nodo.getConteudo());
		assertEquals(new Character('.'), nodo.getEsq().getConteudo());
		assertEquals(null, nodo.getDir());
		assertEquals(new Character('a'), nodo.getEsq().getEsq().getConteudo());
		assertEquals(new Character('b'), nodo.getEsq().getDir().getConteudo());

		nodo = ERParser.parse("((a|c))*bc?(a|b|c)*");
		assertEquals(new Character('.'), nodo.getConteudo());
		assertEquals(new Character('*'), nodo.getEsq().getConteudo());
		assertEquals(new Character('|'), nodo.getEsq().getEsq().getConteudo());
		assertEquals(new Character('a'), nodo.getEsq().getEsq().getEsq().getConteudo());
		assertEquals(new Character('c'), nodo.getEsq().getEsq().getDir().getConteudo());
		assertEquals(new Character('.'), nodo.getDir().getConteudo());
		assertEquals(new Character('b'), nodo.getDir().getEsq().getConteudo());
		assertEquals(new Character('.'), nodo.getDir().getDir().getConteudo());
		assertEquals(new Character('?'), nodo.getDir().getDir().getEsq().getConteudo());
		assertEquals(new Character('c'), nodo.getDir().getDir().getEsq().getEsq().getConteudo());
		assertEquals(new Character('*'), nodo.getDir().getDir().getDir().getConteudo());
		assertEquals(new Character('|'), nodo.getDir().getDir().getDir().getEsq().getConteudo());
		assertEquals(new Character('a'), nodo.getDir().getDir().getDir().getEsq().getEsq().getConteudo());
		assertEquals(new Character('|'), nodo.getDir().getDir().getDir().getEsq().getDir().getConteudo());
		assertEquals(new Character('b'), nodo.getDir().getDir().getDir().getEsq().getDir().getEsq().getConteudo());
		assertEquals(new Character('c'), nodo.getDir().getDir().getDir().getEsq().getDir().getDir().getConteudo());

		nodo = ERParser.parse("a+"); // traduz-se para 'aa*'
		assertEquals(new Character('.'), nodo.getConteudo());
		assertEquals(new Character('a'), nodo.getEsq().getConteudo());
		assertEquals(new Character('*'), nodo.getDir().getConteudo());
		assertEquals(new Character('a'), nodo.getDir().getEsq().getConteudo());

		nodo = ERParser.parse("(ab*)+"); // traduz-se para '(ab*)(ab*)*'
		assertEquals(new Character('.'), nodo.getConteudo());
		assertEquals(new Character('.'), nodo.getEsq().getConteudo());
		assertEquals(new Character('*'), nodo.getDir().getConteudo());
		assertEquals(new Character('.'), nodo.getDir().getEsq().getConteudo());
	}

	@Test(expected = FormaisException.class)
	public void testarParseDeExprInvalidas1() throws FormaisException {
		ERParser.parse("*");
	}

	@Test(expected = FormaisException.class)
	public void testarParseDeExprInvalidas2() throws FormaisException {
		ERParser.parse("abc ");
	}

	@Test
	public void testarTraduzirSimbolosEspeciais_Mais() {
		assertEquals("aa*", ERParser.traduzirSimbolosEspeciais("a+"));
		assertEquals("a*(bc?abb*)", ERParser.traduzirSimbolosEspeciais("a*(bc?ab+)"));
		assertEquals("a(bc?)(bc?)*", ERParser.traduzirSimbolosEspeciais("a(bc?)+"));
		assertEquals("a(ed(kl*|bc)(a)*)(ed(kl*|bc)(a)*)*", ERParser.traduzirSimbolosEspeciais("a(ed(kl*|bc)(a)*)+"));
	}
}

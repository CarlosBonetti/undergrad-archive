package com.bonaguiar.formais2.core;

import static com.bonaguiar.formais2.test.Assert.assertCollectionEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bonaguiar.formais2.core.GLC.FormaSentencial;

public class GLCTest {

	@Test
	public void criarGLCFormaSentencial() {
		GLC.FormaSentencial p = new GLC.FormaSentencial("a");
		assertEquals(Arrays.asList("a"), p);

		p = new GLC.FormaSentencial("ab");
		assertEquals(Arrays.asList("ab"), p);

		p = new GLC.FormaSentencial("A B abc");
		assertEquals(Arrays.asList("A", "B", "abc"), p);
	}

	@Test
	public void formaSentencialToString() {
		FormaSentencial p = new GLC.FormaSentencial("A B abc");
		assertEquals("A B abc", p.toString());

		p = new GLC.FormaSentencial("Z");
		assertEquals("Z", p.toString());

		p = new GLC.FormaSentencial("&");
		assertEquals("&", p.toString());
	}

	@Test
	public void addProducao() {
		GLC glc = new GLC();

		glc.addProducao("S", new GLC.FormaSentencial("abc X Y m"));
		glc.addProducao("S", new GLC.FormaSentencial("a S"));

		assertEquals(1, glc.producoes.size());
		assertEquals(Arrays.asList(new FormaSentencial("abc X Y m"), new FormaSentencial("a S")), glc.producoes.get("S"));

		glc.addProducao("A", "a M Fe");

		assertEquals(2, glc.producoes.size());
		assertEquals(Arrays.asList(new FormaSentencial("abc X Y m"), new FormaSentencial("a S")), glc.producoes.get("S"));
		assertEquals(Arrays.asList(new FormaSentencial("a M Fe")), glc.producoes.get("A"));
	}

	@Test
	public void addProducoes() throws Exception {
		GLC glc = new GLC();
		glc.addProducoes("E -> E + T | E - T | T");
		assertEquals(1, glc.producoes.size());
		assertEquals(Arrays.asList(new FormaSentencial("E + T"), new FormaSentencial("E - T"), new FormaSentencial("T")), glc.producoes.get("E"));
	}

	@Test(expected = ParseException.class)
	public void addProducoesMalFormadas1() throws Exception {
		GLC glc = new GLC();
		glc.addProducoes("hahaha");
	}

	@Test(expected = IllegalArgumentException.class)
	public void addProducoesMalFormadas2() throws Exception {
		GLC glc = new GLC();
		glc.addProducoes("S -> ");
	}

	@Test
	public void criarGLCComMultilineProducoes() throws Exception {
		String text = "E -> E + T | E - T | T\n" + "T -> T * F | T / F | F\n" + "F -> ( E ) | id";
		GLC glc = new GLC(text);

		assertEquals(3, glc.producoes.size());
		assertEquals(Arrays.asList(new FormaSentencial("E + T"), new FormaSentencial("E - T"), new FormaSentencial("T")), glc.producoes.get("E"));
		assertEquals(Arrays.asList(new FormaSentencial("T * F"), new FormaSentencial("T / F"), new FormaSentencial("F")), glc.producoes.get("T"));
		assertEquals(Arrays.asList(new FormaSentencial("( E )"), new FormaSentencial("id")), glc.producoes.get("F"));

		// Ordem de inserção deve ser preservada
		String r = "";
		for (String k : glc.producoes.keySet()) {
			r += k;
		}
		assertEquals("ETF", r);

		// Símbolo inicial deve ser a primeira produção fornecida
		assertEquals("E", glc.simboloInicial);

		// Deve salvar o texto original usado para criar a gramática
		assertEquals(text, glc.getRaw());
	}

	@Test(expected = ParseException.class)
	public void criarGLCComProducoesInvalidas1() throws Exception {
		new GLC("");
	}

	@Test(expected = ParseException.class)
	public void criarGLCComProducoesInvalidas2() throws Exception {
		new GLC("S -> aS | B -> lK");
	}

	@Test
	public void getListaProducoes() throws Exception {
		GLC glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertEquals("[T E', + T E', &, F T', * F T', &, ( E ), id]", glc.getListaProducoes().toString());
	}

	@Test
	public void getNaoTerminais() throws Exception {
		GLC glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertTrue(glc.getNaoTerminais().containsAll(Arrays.asList("E", "E'", "T", "T'", "F")));
		assertEquals(5, glc.getNaoTerminais().size());
	}

	@Test
	public void getTerminais() throws Exception {
		GLC glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertTrue(glc.getTerminais().containsAll(Arrays.asList("+", "*", "(", ")", "id")));
		assertEquals(5, glc.getTerminais().size());
	}

	@Test
	public void getProducoesDoSimbolo() throws Exception {
		GLC glc = new GLC("E -> A \n A -> a | &");
		assertEquals("[A]", glc.getProducoes("E").toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getProducoesDoSimboloInexistente() throws ParseException {
		GLC glc = new GLC("E -> T E \n");
		glc.getProducoes("T").toString();
	}

	@Test
	public void getFirstSet() throws Exception {
		GLC glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		Map<String, Set<String>> hash = glc.getFirstSet();
		assertCollectionEquals(Arrays.asList("id", "("), hash.get("F"));
		assertCollectionEquals(Arrays.asList("id", "("), hash.get("T"));
		assertCollectionEquals(Arrays.asList("id", "("), hash.get("E"));
		assertCollectionEquals(Arrays.asList("+", "&"), hash.get("E'"));
		assertCollectionEquals(Arrays.asList("&", "*"), hash.get("T'"));

		GLC glc2 = new GLC("S -> A b | A B c \n" + "B -> b B | A d | & \n" + "A -> a A | &");
		hash = glc2.getFirstSet();
		assertCollectionEquals(Arrays.asList("&", "a"), hash.get("A"));
		assertCollectionEquals(Arrays.asList("d", "&", "b", "a"), hash.get("B"));
		assertCollectionEquals(Arrays.asList("d", "c", "b", "a"), hash.get("S"));

		GLC glc3 = new GLC("S -> A B C \n" + "A -> a A | & \n" + "B -> b B | A C d \n" + "C -> c C | &");
		hash = glc3.getFirstSet();
		assertCollectionEquals(Arrays.asList("&", "a"), hash.get("A"));
		assertCollectionEquals(Arrays.asList("&", "c"), hash.get("C"));
		assertCollectionEquals(Arrays.asList("d", "b", "c", "a"), hash.get("B"));
		assertCollectionEquals(Arrays.asList("d", "b", "c", "a"), hash.get("S"));

		glc3 = new GLC("\nS -> a A | b B \n" + "C -> S | &\n" + "D -> S | &\n" + "A -> C b \n" + "B -> D a\n");
		hash = glc3.getFirstSet();
		assertCollectionEquals(Arrays.asList("&", "a", "b"), hash.get("D"));
		assertCollectionEquals(Arrays.asList("b", "a"), hash.get("A"));
		assertCollectionEquals(Arrays.asList("&", "a", "b"), hash.get("C"));
		assertCollectionEquals(Arrays.asList("a", "b"), hash.get("B"));
		assertCollectionEquals(Arrays.asList("b", "a"), hash.get("S"));

		glc3 = new GLC("S -> A B \n" + "A -> &\n" + "B -> & \n");
		hash = glc3.getFirstSet();
		assertCollectionEquals(Arrays.asList("&"), hash.get("S"));
		assertCollectionEquals(Arrays.asList("&"), hash.get("A"));
		assertCollectionEquals(Arrays.asList("&"), hash.get("B"));

		glc3 = new GLC("S -> A B a | a \n" + "A -> B | &\n" + "B -> A C | c \n" + "C -> & \n");
		hash = glc3.getFirstSet();
		assertCollectionEquals(Arrays.asList("c", "a"), hash.get("S"));
		assertCollectionEquals(Arrays.asList("c", "&"), hash.get("A"));
		assertCollectionEquals(Arrays.asList("&", "c"), hash.get("B"));
		assertCollectionEquals(Arrays.asList("&"), hash.get("C"));

		glc3 = new GLC("\nS -> A B C | b | c \n" + "A -> a A | &\n" + "B -> b B | A C d | c \n");
		// hash = glc3.getFirstSet(); // C nao existe na gramatica
		// validar adicao de grammar
	}

	@Test
	public void getFirstSetDeGramaticaComRecursao() throws ParseException {
		String text = "E -> E + T | E - T | T\n" + "T -> T * F | T / F | F\n" + "F -> ( E ) | id";
		GLC glc = new GLC(text);

		assertTrue(glc.first("F").containsAll(Arrays.asList("id", "(")));
		assertEquals(2, glc.first("F").size());

		assertTrue(glc.first("T").containsAll(Arrays.asList("id", "(")));
		assertEquals(2, glc.first("T").size());

		assertTrue(glc.first("E").containsAll(Arrays.asList("id", "(")));
		assertEquals(2, glc.first("E").size());

		glc = new GLC("S -> A S b | a\n" + "A -> &");
		assertCollectionEquals(Arrays.asList("&"), glc.first("A"));
		assertCollectionEquals(Arrays.asList("a"), glc.first("S"));
	}

	@Test
	public void getFirstSetDeGramaticaComRecursaoIndireta() throws ParseException {
		GLC glc = new GLC("S -> a A | b B \n" +
				"A -> C b \n " +
				"C -> S  | & \n" +
				"B -> D c \n" +
				"D -> S  | & \n");

		assertCollectionEquals(Arrays.asList("a", "b"), glc.first("S"));
		assertCollectionEquals(Arrays.asList("a", "b"), glc.first("A"));
		assertCollectionEquals(Arrays.asList("a", "b", "&"), glc.first("C"));
		assertCollectionEquals(Arrays.asList("a", "b", "c"), glc.first("B"));
		assertCollectionEquals(Arrays.asList("a", "b", "&"), glc.first("D"));
	}

	@Test
	public void getFollowSet() throws Exception {
		GLC glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertEquals("{E=[$, )], E'=[$, )], T=[$, +, )], T'=[$, +, )], F=[$, *, +, )]}", glc.getFollowSet().toString());

		GLC glc2 = new GLC("S -> A b | A B c \n" + "B -> b B | A d | & \n" + "A -> a A | &");
		assertEquals("{S=[$], B=[c], A=[d, b, c, a]}", glc2.getFollowSet().toString());

		GLC glc3 = new GLC("S -> A B C \n" + "A -> a A | & \n" + "B -> b B | A C d \n" + "C -> c C | &");
		assertEquals("{S=[$], A=[d, b, c, a], B=[c, $], C=[d, $]}", glc3.getFollowSet().toString());
	}

	@Test
	public void getFollowDeGramaticaComRecursao() throws ParseException {
		String text = "E -> E + T | E - T | T\n" + "T -> T * F | T / F | F\n" + "F -> ( E ) | id";
		GLC glc = new GLC(text);

		assertTrue(glc.follow("F").containsAll(Arrays.asList("$", "+", "-", ")", "*", "/")));
		assertEquals(6, glc.follow("F").size());

		assertTrue(glc.follow("T").containsAll(Arrays.asList("$", "+", "-", ")", "*", "/")));
		assertEquals(6, glc.follow("T").size());

		assertTrue(glc.follow("E").containsAll(Arrays.asList("$", "+", "-", ")")));
		assertEquals(4, glc.follow("E").size());
	}

	@Test
	public void getConflitosFF() throws ParseException {
		GLC glc = new GLC("S -> a A | b B \n" +
				"A -> a A b | b A' \n" +
				"A' -> B b | & \n" +
				"B -> a B' | b B a \n" +
				"B' -> A a | &");

		assertTrue(glc.getConflitosFF().containsAll(Arrays.asList("A'", "B'")));
		assertEquals(2, glc.getConflitosFF().size());
		
		glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertTrue(glc.getConflitosFF().isEmpty());

		glc = new GLC("A -> B C \n B -> b | & \n C -> c");
		assertTrue(glc.getConflitosFF().isEmpty());
	}

	@Test
	public void testarReqEsquerdaDireta() throws Exception {
		String text = "E -> T | E + T | E - T \n" + "T -> F | T * F | T / F \n" + "F -> ( E ) | id";
		GLC glc = new GLC(text);
		assertTrue(glc.getRecursaoEsquerdaDireta().contains("E"));
		assertTrue(glc.getRecursaoEsquerdaDireta().contains("T"));
		assertTrue(!glc.getRecursaoEsquerdaDireta().contains("F"));

		glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertTrue(!glc.getRecursaoEsquerdaDireta().contains("E"));
		assertTrue(!glc.getRecursaoEsquerdaDireta().contains("T"));
		assertTrue(!glc.getRecursaoEsquerdaDireta().contains("F"));
	}

	@Test
	public void testarReqEsquerdaInDireta() throws Exception {
		String text = "S -> A f \n" + "A -> B e | S f | d\n" + "B -> S d | A c | a";
		GLC glc = new GLC(text);
		assertCollectionEquals(Arrays.asList("B", "S", "A"), glc.getRecursaoEsquerdaIndireta());

		glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertTrue(glc.getRecursaoEsquerdaIndireta().isEmpty());

		glc = new GLC("S -> A | a | & \n" + "A -> B | a \n" + "B -> S | a");
		assertCollectionEquals(Arrays.asList("S", "A", "B"), glc.getRecursaoEsquerdaIndireta());

		glc = new GLC("S -> A S | a | & \n" + "A -> B | a \n" + "B -> B S | a | &");
		assertCollectionEquals(Arrays.asList("A", "B", "S"), glc.getRecursaoEsquerdaIndireta());

		glc = new GLC("S -> A S | a | & \n" + "A -> & | a \n" + "B ->  a | &\n" + "C ->  a | &");
		assertCollectionEquals(Arrays.asList("S"), glc.getRecursaoEsquerdaIndireta());

		glc = new GLC("S -> A | S | & \n" + "A -> a S | a \n");
		assertTrue(glc.getRecursaoEsquerdaIndireta().isEmpty());

		glc = new GLC("S -> A | S | & \n" + "A -> B S | a \n" + "B -> A S | a ");
		assertCollectionEquals(Arrays.asList("A", "B"), glc.getRecursaoEsquerdaIndireta());

		glc = new GLC("S -> A B C S | a | & \n" + "A -> & | a \n" + "B ->  a | &\n" + "C ->  a | &");
		assertCollectionEquals(Arrays.asList("S"), glc.getRecursaoEsquerdaIndireta());

		glc = new GLC("S -> B C | a  \n" + "A -> & | a | C \n" + "B ->  a | &\n" + "C ->  S A | a");
		System.out.println();
		assertCollectionEquals(Arrays.asList("S", "C"), glc.getRecursaoEsquerdaIndireta());
	}

	@Test
	public void testarTemNaoDeterminismoDireto() throws Exception {
		String text = "S -> A f \n" + "A -> B e | S f | d\n" + "B -> S d | A c | a";
		GLC glc = new GLC(text);
		assertTrue(glc.getFatoracaoDireta().isEmpty());

		glc = new GLC("S -> a S b | A C\n" + "A -> a A b | a D | b E\n" + "C -> c C | &\n" + "D -> a D | c\n" + "E -> b E | b");
		assertCollectionEquals(Arrays.asList("E", "A"), glc.getFatoracaoDireta());

		glc = new GLC("S -> S | S C | a\n" + "A -> a A b | A d | b e\n" + "C -> c C | &\n");
		assertCollectionEquals(Arrays.asList("S"), glc.getFatoracaoDireta());

	}

	@Test
	public void testarFatoracaoIndireta() throws Exception {
		// String text = "S -> A f \n" + "A -> B e | S f | d\n" + "B -> S d | A c | a";
		String text = "S -> A B | b C\n" + "A -> a A | C B | &\n" + "B -> b B | d\n" + "C -> c C | &";
		GLC glc = new GLC(text);
		assertCollectionEquals(Arrays.asList("S"), glc.getFatoracaoIndireta());

		glc = new GLC("S -> a | A C\n" + "A -> a A b | B\n" + "B -> d | a | C\n" + "C -> d | b\n");
		assertCollectionEquals(Arrays.asList("S", "A", "B"), glc.getFatoracaoIndireta());

		glc = new GLC("S -> L = R | R\n" + "R -> L\n" + "L -> * R | id");
		assertCollectionEquals(Arrays.asList("S"), glc.getFatoracaoIndireta());

		glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertTrue("" + glc.getFatoracaoIndireta().size(), glc.getFatoracaoIndireta().size() == 0);

		glc = new GLC("S -> S a | a | &\n" + "A -> S b | a \n");
		assertCollectionEquals(Arrays.asList("S", "A"), glc.getFatoracaoIndireta());

		glc = new GLC("S -> A B a | a \n" + "A -> B | &\n" + "B -> A C | c \n" + "C -> & \n");
		assertCollectionEquals(Arrays.asList("S", "A", "B"), glc.getFatoracaoIndireta());

		glc = new GLC("S -> A a | B c \n" + "A -> &\n" + "B -> &\n");
		assertTrue("" + glc.getFatoracaoIndireta().size(), glc.getFatoracaoIndireta().size() == 0);
	}
}

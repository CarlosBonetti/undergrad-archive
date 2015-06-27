package com.bonaguiar.formais2.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;

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
	public void getFirstSet() throws Exception {
		GLC glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertEquals("{F=[id, (], T=[id, (], E=[id, (], E'=[&, +], T'=[&, *]}", glc.getFirstSet().toString());

		GLC glc2 = new GLC("S -> A b | A B c \n" + "B -> b B | A d | & \n" + "A -> a A | &");
		assertEquals("{A=[&, a], B=[d, &, b, a], S=[d, b, c, a]}", glc2.getFirstSet().toString());

		GLC glc3 = new GLC("S -> A B C \n" + "A -> a A | & \n" + "B -> b B | A C d \n" + "C -> c C | &");
		assertEquals("{A=[&, a], C=[&, c], B=[d, b, c, a], S=[d, b, c, a]}", glc3.getFirstSet().toString());
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
	public void testarReqEsquerdaDireta() throws Exception {
		String text = "E -> E + T | E - T | T\n" + "T -> T * F | T / F | F\n" + "F -> ( E ) | id";
		GLC glc = new GLC(text);
		assertTrue(glc.getRecursaoEsquerdaDireta().contains("E"));
		assertTrue(glc.getRecursaoEsquerdaDireta().contains("T"));
		assertTrue(!glc.getRecursaoEsquerdaDireta().contains("F"));

		glc = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
		assertTrue(!glc.getRecursaoEsquerdaDireta().contains("E"));
		assertTrue(!glc.getRecursaoEsquerdaDireta().contains("T"));
		assertTrue(!glc.getRecursaoEsquerdaDireta().contains("F"));
	}
}

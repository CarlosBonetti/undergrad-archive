package com.bonaguiar.formais2.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GrammarUtilsTest {

	@Test
	public void ehTerminal() {
		assertTrue(GrammarUtils.ehTerminal("a"));
		assertTrue(GrammarUtils.ehTerminal("abc"));
		assertTrue(GrammarUtils.ehTerminal("&"));
		assertTrue(GrammarUtils.ehTerminal("1"));
		assertTrue(GrammarUtils.ehTerminal("1234"));
		assertTrue(GrammarUtils.ehTerminal("abc3423"));
		assertTrue(GrammarUtils.ehTerminal("id"));
		assertTrue(GrammarUtils.ehTerminal("+"));
		assertTrue(GrammarUtils.ehTerminal("*"));

		assertFalse(GrammarUtils.ehTerminal("A"));
		assertFalse(GrammarUtils.ehTerminal("Xa"));
		assertFalse(GrammarUtils.ehTerminal("Abacaxi"));
		assertFalse(GrammarUtils.ehTerminal("A23"));
	}

	@Test
	public void ehNaoTerminal() {
		assertTrue(GrammarUtils.ehNaoTerminal("A"));
		assertTrue(GrammarUtils.ehNaoTerminal("Xa"));
		assertTrue(GrammarUtils.ehNaoTerminal("Abacaxi"));
		assertTrue(GrammarUtils.ehNaoTerminal("A23"));
		assertTrue(GrammarUtils.ehNaoTerminal("E'"));

		assertFalse(GrammarUtils.ehNaoTerminal("a"));
		assertFalse(GrammarUtils.ehNaoTerminal("abc"));
		assertFalse(GrammarUtils.ehNaoTerminal("&"));
		assertFalse(GrammarUtils.ehNaoTerminal("1"));
		assertFalse(GrammarUtils.ehNaoTerminal("1234"));
		assertFalse(GrammarUtils.ehNaoTerminal("abc3423"));
		assertFalse(GrammarUtils.ehNaoTerminal("id"));
		assertFalse(GrammarUtils.ehNaoTerminal("+"));
		assertFalse(GrammarUtils.ehNaoTerminal("*"));
	}
}

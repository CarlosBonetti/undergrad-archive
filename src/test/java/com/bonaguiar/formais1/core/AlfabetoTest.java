package com.bonaguiar.formais1.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class AlfabetoTest {

	@Test
	public void testarInicializacao() {
		Alfabeto a1 = new Alfabeto(new char[] {'a', 'b'});
		assertTrue(a1.contains('a'));
		assertTrue(a1.contains('b'));
		assertFalse(a1.contains('c'));
		assertEquals(2, a1.size());

		Alfabeto a2 = new Alfabeto(new Character[] {'S', 'b'});
		assertTrue(a2.contains('S'));
		assertTrue(a2.contains('b'));
		assertEquals(2, a2.size());
	}
	
	@Test
	public void testarInicializacaoComDuplicados() {
		Alfabeto a1 = new Alfabeto(new char[] {'d', 'd'});
		assertEquals(1, a1.size());
	}
	
	@Test
	public void testarInicializacaoComListaInline() {
		Alfabeto a1 = new Alfabeto('a', 'e', 'f');
	}

}

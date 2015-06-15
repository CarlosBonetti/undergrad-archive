package com.bonaguiar.formais1.core.automata;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bonaguiar.formais1.core.Alfabeto;

public class AFMinTest {

	@Test
	public void testarInicializacao() {
		AFMin min = new AFMin(new Alfabeto("abc"));
		assertTrue(min instanceof AFD);
	}

}

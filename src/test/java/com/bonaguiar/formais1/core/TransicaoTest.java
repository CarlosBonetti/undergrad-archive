package com.bonaguiar.formais1.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class TransicaoTest {

	@Test
	public void testToString() {
		Transicao t = new Transicao("q0", 'x', "q1");
		assertEquals("(q0, x) -> q1", t.toString());		
	}

}

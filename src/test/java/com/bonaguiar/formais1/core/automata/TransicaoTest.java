package com.bonaguiar.formais1.core.automata;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bonaguiar.formais1.core.automata.Transicao;

public class TransicaoTest {

	@Test
	public void testToString() {
		Transicao t = new Transicao("q0", 'x', "q1");
		assertEquals("(q0, x) -> q1", t.toString());		
	}

}

package com.bonaguiar.formais1.core.automata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.exception.FormaisException;

public class AFMinTest {

	@Test
	public void testarInicializacao() {
		AFMin min = new AFMin(new Alfabeto("abc"));
		assertTrue(min instanceof AFD);
	}

	public static void assertAFEqual(AFMin af1, AFMin af2) throws FormaisException {
		assertTrue(af1.equals(af2));
		assertTrue(af2.equals(af1));
	}

	public static void assertAFNotEqual(AFMin af1, AFMin af2) throws FormaisException {
		assertTrue(!af1.equals(af2));
		assertTrue(!af2.equals(af1));
	}

	@Test
	public void testarEquals() throws FormaisException {
		AFMin af1 = new AFMin(new Alfabeto("abc"));
		AFMin afAlfa = new AFMin(new Alfabeto("abcd"));
		AFMin af2 = new AFMin(new Alfabeto("abc"));

		assertAFEqual(af1, af2);
		assertAFNotEqual(af1, afAlfa);
		assertAFNotEqual(af2, afAlfa);

		af1.addEstado("q0", true);
		af1.addEstado("q1", false);
		assertAFNotEqual(af1, af2);

		af2.addEstado("q2", false);
		af2.addEstado("q3", false);
		assertAFNotEqual(af1, af2);
		af2.estadosFinais.add("q2");
		assertAFEqual(af1, af2);

		af1.setEstadoInicial("q0");
		af1.addTransicao("q0", 'a', "q0");
		af1.addTransicao("q0", 'b', "q1");
		af1.addTransicao("q1", 'a', "q0");
		af1.addTransicao("q1", 'b', "q1");

		assertAFNotEqual(af1, af2);

		af2.setEstadoInicial("q2");
		af2.addTransicao("q2", 'a', "q2");
		af2.addTransicao("q2", 'b', "q3");
		af2.addTransicao("q3", 'a', "q2");
		af2.addTransicao("q3", 'c', "q3");
		assertAFNotEqual(af1, af2);

		af2.transicoes.get(3).simboloTransicao = 'b';
		assertAFEqual(af1, af2);
	}
}

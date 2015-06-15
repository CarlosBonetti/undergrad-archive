package com.bonaguiar.formais1.core.automata;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.automata.AF;
import com.bonaguiar.formais1.core.automata.AFD;
import com.bonaguiar.formais1.core.exception.FormaisException;

public class AFDTest {

	@Test
	public void testarSeAFDehUmAF() {
		AFD afd1 = new AFD(new Alfabeto("abc"));
		assertTrue(afd1 instanceof AF);		
	}
	
	@Test
	public void testarAddTransicaoCorreta() throws FormaisException {
		AFD afd1 = new AFD(new Alfabeto("abcde"));
		afd1.addEstado("q0", true);
		afd1.addEstado("q1", true);
		afd1.addTransicao("q0", 'e', "q1");
	}
	
	@Test(expected=FormaisException.class)
	public void testarAddEpsolonTransicao() throws FormaisException {
		AFD afd1 = new AFD(new Alfabeto("abcde"));
		afd1.addEstado("q0", true);
		afd1.addEstado("q1", true);
		afd1.addTransicao("q0", Alfabeto.EPSILON, "q1");
	}
	
	@Test(expected=FormaisException.class)
	public void testarAddTransicaoAmbigua() throws FormaisException {
		AFD afd1 = new AFD(new Alfabeto("abcde"));
		afd1.addEstado("q0", true);
		afd1.addEstado("q1", true);
		afd1.addTransicao("q0", 'a', "q1");
		afd1.addTransicao("q0", 'a', "q0");
	}

}

package com.bonaguiar.formais1.core.automata;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.automata.AF;
import com.bonaguiar.formais1.core.automata.AFD;
import com.bonaguiar.formais1.core.automata.AFD.ClasseEq;
import com.bonaguiar.formais1.core.exception.FormaisException;

public class AFDTest {

	private AFD afd;

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
	
	// =====================================================================================
	// Testes de Minimização + helper methods
	
	@Before
	public void setup() throws FormaisException {
		afd = new AFD(new Alfabeto("abc")); // (ab)* de tamanho par (não mínimo)
		afd.addEstado("q0", true);
		afd.addEstado("q1", false);
		afd.addEstado("q2", true);
		afd.addEstado("q_inalcancavel1", false);
		afd.addEstado("q_inalcancavel2", true);
		afd.addEstado("q_morto", false);
		afd.setEstadoInicial("q0");
		
		afd.addTransicao("q0", 'a', "q1");
		afd.addTransicao("q0", 'b', "q1");
		afd.addTransicao("q1", 'a', "q2");
		afd.addTransicao("q1", 'b', "q2");
		afd.addTransicao("q2", 'a', "q1");
		afd.addTransicao("q2", 'b', "q1");
		afd.addTransicao("q_inalcancavel1", 'b', "q_inalcancavel2");
		afd.addTransicao("q0", 'c', "q_morto");
	}
	
	@Test
	public void testarTransicaoCE() throws FormaisException {		
		ClasseEq classe1 = new ClasseEq();
		classe1.add("q0");
		classe1.add("q2");
		
		ClasseEq classe2 = new ClasseEq();
		classe2.add("q1");
				
		List<ClasseEq> classes = Arrays.asList(classe1, classe2);
		assertEquals(classe2, afd.transicaoCE("q0", 'a', classes));
		assertEquals(classe2, afd.transicaoCE("q0", 'b', classes));
		assertEquals(classe1, afd.transicaoCE("q1", 'a', classes));
		assertEquals(classe1, afd.transicaoCE("q1", 'b', classes));
		assertEquals(classe2, afd.transicaoCE("q2", 'a', classes));
		assertEquals(classe2, afd.transicaoCE("q2", 'b', classes));
	}
	
	@Test
	public void testarEquivalenteCE() throws FormaisException {	
		ClasseEq classe1 = new ClasseEq();
		classe1.add("q0");
		classe1.add("q2");
		
		ClasseEq classe2 = new ClasseEq();
		classe2.add("q1");
		
		List<ClasseEq> classes = Arrays.asList(classe1, classe2);		
		assertTrue(afd.equivalenteCE("q0", "q2", classes));
		assertFalse(afd.equivalenteCE("q0", "q1", classes));
	}
	
	@Test
	public void testarSplitCE() throws FormaisException {
		ClasseEq classe1 = new ClasseEq();
		classe1.add("q0");
		classe1.add("q2");
		
		ClasseEq classe2 = new ClasseEq();
		classe2.add("q1");
		
		List<ClasseEq> classes = Arrays.asList(classe1, classe2);		
		
		List<ClasseEq> novas = afd.splitCE(classe1, classes);
		assertEquals(1, novas.size());
		assertEquals(classe1, novas.get(0));
	}
	
	@Test
	public void testarGetMinimo() throws FormaisException {		
		AFMin min = afd.getAFMin();		
		
		assertTrue("Deve ser uma instância de AFMin", min instanceof AFMin);
		
		assertEquals("Deve ter o mesmo alfabeto", afd.getAlfabeto(), min.getAlfabeto());
		
		assertFalse("Estado inalcancável deve ser removido", min.contemEstado("q_inalcancavel1"));
		assertFalse("Estado inalcancável deve ser removido", min.contemEstado("q_inalcancavel2"));
		assertFalse("Estado morto deve ser removido", min.contemEstado("q_morto"));
		
		assertEquals(Arrays.asList("q0", "q1"), min.getEstados());
		assertEquals(2, min.getEstados().size());
		
		// Checar transições
		assertEquals(4, min.getTransicoes().size());
		assertEquals(Arrays.asList("q1"), min.transicao("q0", 'a'));
		assertEquals(Arrays.asList("q1"), min.transicao("q0", 'b'));
		assertEquals(Arrays.asList("q0"), min.transicao("q1", 'a'));
		assertEquals(Arrays.asList("q0"), min.transicao("q1", 'b'));
	}

}

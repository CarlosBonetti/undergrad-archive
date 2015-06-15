package com.bonaguiar.formais1.core.expr;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.bonaguiar.formais1.core.AFD;
import com.bonaguiar.formais1.core.exception.FormaisException;

public class ExprRegularTest {

	@Test
	public void testarCriacao() throws FormaisException {
		ExprRegular expr1 = new ExprRegular("(ab)*");
		assertEquals("(ab)*", expr1.getExpr());
	}
	
	@Test
	public void testarArvoreSintatica() throws FormaisException {
		ExprRegular expr1 = new ExprRegular("(ab)*(cc)*");
		assertEquals(new Character('.'), expr1.getTree().getConteudo());
		assertEquals(new Character('*'), expr1.getTree().getEsq().getConteudo());
		assertEquals(new Character('*'), expr1.getTree().getDir().getConteudo());
		assertEquals(new Character('.'), expr1.getTree().getEsq().getEsq().getConteudo());
		assertEquals(null, expr1.getTree().getEsq().getDir());
		assertEquals(null, expr1.getTree().getDir().getDir());		
		assertEquals(new Character('a'), expr1.getTree().getEsq().getEsq().getEsq().getConteudo());
		assertEquals(new Character('b'), expr1.getTree().getEsq().getEsq().getDir().getConteudo());
		assertEquals(new Character('c'), expr1.getTree().getDir().getEsq().getEsq().getConteudo());
		assertEquals(new Character('c'), expr1.getTree().getDir().getEsq().getDir().getConteudo());
		
		// Deve estar costurada
		assertEquals(expr1.getTree(), expr1.getTree().getEsq().getCostura());
	}
	
	@Test
	public void testarExtrairAlfabeto() throws FormaisException {
		ExprRegular exp = new ExprRegular("(ab)*|c?de");
		assertTrue(exp.extrairAlfabeto().containsAll(Arrays.asList('a', 'b', 'c', 'd', 'd')));
		assertEquals(5, exp.extrairAlfabeto().size());
	}
	
	@Test
	public void testarGetAFD() throws FormaisException {
		ExprRegular exp = new ExprRegular("(ab)*|c?d");
		AFD afd = exp.getAFD();
		
		// Alfabeto deve ser o informado na expressão
		assertTrue(afd.getAlfabeto().containsAll(Arrays.asList('a', 'b', 'c', 'd')));
		assertEquals(4, afd.getAlfabeto().size());
		
		// Checar estados criados
		assertTrue(afd.getEstados().containsAll(Arrays.asList("q0", "q1", "q2", "q3", "q4")));
		assertEquals(5, afd.getEstados().size());
		
		// Checar estados finais
		assertEquals(3, afd.getEstadosFinais().size());
		
		// Checar estado inicial
		assertEquals("q0", afd.getEstadoInicial());
		
		// Checar transições criadas
		assertEquals(6, afd.getTransicoes().size());
		// TODO: testar transições de fato
	}
	
	@Test
	public void testarGetAFD2() throws FormaisException {
		ExprRegular exp = new ExprRegular("(ab)*");
		AFD afd = exp.getAFD();
		// Alfabeto deve ser o informado na expressão
		assertTrue(afd.getAlfabeto().containsAll(Arrays.asList('a', 'b')));
		assertEquals(2, afd.getAlfabeto().size());
		
		// Checar estados criados
		assertTrue(afd.getEstados().containsAll(Arrays.asList("q0", "q1")));
		assertEquals(2, afd.getEstados().size());
		
		// Checar estados finais
		assertEquals(1, afd.getEstadosFinais().size());
		assertEquals("q0", afd.getEstadosFinais().get(0));
		
		// Checar estado inicial
		assertEquals("q0", afd.getEstadoInicial());
		
		// Checar transições criadas
		assertEquals(2, afd.getTransicoes().size());
		assertEquals(Arrays.asList("q1"), afd.transicao("q0", 'a'));
		assertEquals(Arrays.asList("q0"), afd.transicao("q1", 'b'));
		assertEquals(2, afd.getTransicoes().size());
	}
	
	@Test
	public void testarGetAFD3() throws FormaisException {
		ExprRegular exp = new ExprRegular("(ab*|cb?a)*");
		AFD afd = exp.getAFD();
		// Alfabeto deve ser o informado na expressão
		assertTrue(afd.getAlfabeto().containsAll(Arrays.asList('a', 'b', 'c')));
		assertEquals(3, afd.getAlfabeto().size());
		
		// Checar estados criados
		assertTrue(afd.getEstados().containsAll(Arrays.asList("q0", "q1", "q2", "q3")));
		assertEquals(4, afd.getEstados().size());
		
		// Checar estados finais
		assertEquals(2, afd.getEstadosFinais().size());
		assertEquals("q0", afd.getEstadosFinais().get(0));
		
		// Checar estado inicial
		assertEquals("q0", afd.getEstadoInicial());
		
		// Checar transições criadas
		assertEquals(8, afd.getTransicoes().size());
	}
}

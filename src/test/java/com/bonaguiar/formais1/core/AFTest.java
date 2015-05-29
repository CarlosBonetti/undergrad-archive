package com.bonaguiar.formais1.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bonaguiar.formais1.core.exception.FormaisException;

public class AFTest {

	@Test
	public void testarInicializacaoVazia() {
		AF af = new AF(new Alfabeto("abc"));
		assertEquals(3, af.getAlfabeto().size());
		assertTrue(af.getAlfabeto().contains('c'));
	}
	
	@Test
	public void testarAddEstado() {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q1", false);
		assertEquals("q1", af.getEstados().get(0));
		assertEquals(1, af.getEstados().size());
		assertEquals(0, af.getEstadosFinais().size());
		
		af.addEstado("q0", true);
		assertEquals("q0", af.getEstados().get(1));
		assertEquals(2, af.getEstados().size());
		assertEquals(1, af.getEstadosFinais().size());
	}
	
	@Test
	public void testarSetEstadoInicial() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q1", true);
		af.setEstadoInicial("q1");
		assertEquals("q1", af.getEstadoInicial());
	}
	
	@Test(expected=FormaisException.class)
	public void testarSetEstadoInicialNaoExistente() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.setEstadoInicial("q2");
	}
	
	@Test
	public void testarAddTransicao() throws FormaisException {
		AF af = new AF(new Alfabeto("012"));
		af.addEstado("q0", false);
		af.addEstado("q1", true);
		af.addTransicao("q0", '1', "q1");
		assertEquals("q0", af.getTransicoes().get(0).estadoOrigem);
		assertEquals(new Character('1'), af.getTransicoes().get(0).simboloTransicao);
		assertEquals("q1", af.getTransicoes().get(0).estadoDestino);
		assertEquals(1, af.getTransicoes().size());
	}
	
	@Test(expected=FormaisException.class)
	public void testarAddTransicaoComEstadoOrigemNaoPertencenteAoAF() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.addTransicao("q4", 'a', "q1");
	}
	
	@Test(expected=FormaisException.class)
	public void testarAddTransicaoComSimboloNaoPertencenteAoAF() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.addTransicao("q1", 'x', "q0");
	}
	
	@Test(expected=FormaisException.class)
	public void testarAddTransicaoComEstadoDestinoNaoPertencenteAoAF() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.addTransicao("q1", 'a', "q2");
	}

}

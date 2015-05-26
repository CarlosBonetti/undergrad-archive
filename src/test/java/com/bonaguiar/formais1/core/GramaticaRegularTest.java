package com.bonaguiar.formais1.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.bonaguiar.formais1.core.exception.FormaisException;

public class GramaticaRegularTest {
	public GramaticaRegular g1;

	@Test
	public void testarInicializacaoCorreta() throws FormaisException {
		GramaticaRegular g = new GramaticaRegular(new Alfabeto('S', 'A'), new Alfabeto('a', 'b', 'c'), 'S');	
		
		assertEquals(new Character('S'), g.getS());
		assertTrue(g.getVn().contains('S'));
		assertTrue(g.getVn().contains('A'));
		assertEquals(2, g.getVn().size());
		
		assertTrue(g.getVt().contains('a'));
		assertTrue(g.getVt().contains('b'));
		assertTrue(g.getVt().contains('c'));
		assertEquals(3, g.getVt().size());
	}
	
	@Test(expected=FormaisException.class)
	public void testarInicializacaoComVnMinisculo() throws FormaisException {
		new GramaticaRegular(new Alfabeto('S', 'a'), new Alfabeto('a', 'b', 'c'), 'S');
	}
	
	@Test(expected=FormaisException.class)
	public void testarInicializacaoComVtMaiusculo() throws FormaisException {
		new GramaticaRegular(new Alfabeto('S', 'A'), new Alfabeto('a', 'B', 'c'), 'S');
	}
	
	@Test(expected=FormaisException.class)
	public void testarInicializacaoComSNaoPertencenteAVn() throws FormaisException {
		new GramaticaRegular(new Alfabeto('S', 'A'), new Alfabeto('a', 'b'), 'U');
	}
	
	@Before
	public void setup() throws FormaisException {
		g1 = new GramaticaRegular(new Alfabeto('S', 'A'), new Alfabeto('a', 'b', 'c'), 'S');
	}

	@Test
	public void testarAddProducaoCorreta() throws FormaisException {
		g1.addProducao('S', "aS");
		assertEquals("aS", g1.getProducoes().get('S').get(0));
		assertEquals(1, g1.getProducoes().get('S').size());
		
		g1.addProducao('S', "a");
		assertEquals("aS", g1.getProducoes().get('S').get(0));
		assertEquals("a", g1.getProducoes().get('S').get(1));
		assertEquals(2, g1.getProducoes().get('S').size());
		
		g1.addProducao('A', "b");
		assertEquals("b", g1.getProducoes().get('A').get(0));
		assertEquals(1, g1.getProducoes().get('A').size());
	}
	
	@Test(expected=FormaisException.class)
	public void testarAddProducaoComProdutorInexistente() throws FormaisException {
		g1.addProducao('F', "aS");
	}
	
	@Test(expected=FormaisException.class)
	public void testarAddProducaoIncorreta() throws FormaisException {
		g1.addProducao('S', "abc");
	}
		
	@Test(expected=FormaisException.class)
	public void testarAddProducaoComSimboloTerminalInexistente() throws FormaisException {
		g1.addProducao('S', "jB");
	}
	
	@Test(expected=FormaisException.class)
	public void testarAddProducaoComSimboloTerminalInexistente2() throws FormaisException {
		g1.addProducao('S', "k");
	}
	
	@Test(expected=FormaisException.class)
	public void testarAddProducaoComSimboloNaoTerminalInexistente() throws FormaisException {
		g1.addProducao('S', "aC");
	}
	
	@Test
	public void testarGetProducoesDeSimbolo() throws FormaisException {
		assertEquals(0, g1.getProducoes('S').size());
		g1.addProducao('S', "aS");
		assertEquals(1, g1.getProducoes('S').size());
		assertEquals("aS", g1.getProducoes('S').get(0));
		
		g1.addProducao('A', "b");
		g1.addProducao('S', "a");
		assertEquals(2, g1.getProducoes('S').size());
		assertEquals("a", g1.getProducoes('S').get(1));
	}
	
	@Test
	public void testarLimparProducoes() throws FormaisException {
		g1.addProducao('S', "aS");
		g1.limparProducoes();
		assertEquals(0, g1.getProducoes('S').size());
	}

}

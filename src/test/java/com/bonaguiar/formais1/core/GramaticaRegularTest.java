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
		assertEquals("aS", g1.getProducoes().get('S'));
		assertEquals(1, g1.getProducoes().size());
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
}

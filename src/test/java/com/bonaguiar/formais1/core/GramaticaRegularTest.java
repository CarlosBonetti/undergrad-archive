package com.bonaguiar.formais1.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.bonaguiar.formais1.core.exception.FormaisException;

public class GramaticaRegularTest {
	public GramaticaRegular g1;

	@Before
	public void setup() throws FormaisException {
		g1 = new GramaticaRegular(new Alfabeto('S', 'A'), new Alfabeto('a', 'b', 'c'), 'S');
	}
	
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

//	@Test
//	public void testarExisteEpisilon() throws FormaisException {
//		g1.addProducao('S', Alfabeto.EPSILON.toString());
//		assertTrue("Epsilon deveria existir", g1.existeEpsilon('S'));
//		g1.addProducao('A', "aA");
//		assertTrue("Epsilon NÃO deveria existir",!g1.existeEpsilon('A'));
//	}


	@Test
	public void testarGRparaAF() throws FormaisException {
		g1.limparProducoes();
		g1.addProducao('S', "aS");
		g1.addProducao('S', "bA");
		g1.addProducao('A', "bA");
		g1.addProducao('A', "c");
		AF af = g1.getAutomatoFinito();
		
		//testando criacao do objeto
		assertSame("Objeto não é AF", AF.class, g1.getAutomatoFinito().getClass());
		assertNotNull("Objeto é null", af);

		//testando simbolos terminais como transicoes
		assertEquals("tamanho de simbolos de transição invalidos", g1.getVt().size(), af.getAlfabeto().size());
		for (Character character : g1.getVt()) {
			assertTrue("simbolos invalidos", g1.getVt().contains(character));
		}

		//testando simbolos nao-terminais como estados
		//+1 pq eh criado um novo estado final.
		assertEquals("numero de Estados invalidos", g1.getVn().size() + 1, af.getEstados().size());
		assertTrue("Simbolo inexistente - S", g1.getVn().contains("S".charAt(0)));
		assertTrue("Simbolo inexistente - A", g1.getVn().contains("A".charAt(0)));
		assertFalse("Simbolo existente - C", g1.getVn().contains("C".charAt(0)));
		assertFalse("Simbolo existente - Δ", g1.getVn().contains("Δ".charAt(0)));
		
		//testando criacao dos addTransicoes
		assertEquals(4, af.getTransicoes().size());
		//TODO validar criacao de transicoes
		for (Transicao trans : af.getTransicoes()) {
			System.out.println(trans.estadoOrigem);
			System.out.println(trans.simboloTransicao);
			System.out.println(trans.estadoDestino);
			System.out.println("-------------------");
		}
	}

}

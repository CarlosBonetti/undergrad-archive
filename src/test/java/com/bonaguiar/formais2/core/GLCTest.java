package com.bonaguiar.formais2.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.bonaguiar.formais2.core.GLC.Producao;

public class GLCTest {

	@Test
	public void criarGLCProducao() {
		GLC.Producao p = new GLC.Producao("a");
		assertEquals(Arrays.asList("a"), p);

		p = new GLC.Producao("ab");
		assertEquals(Arrays.asList("ab"), p);

		p = new GLC.Producao("A B abc");
		assertEquals(Arrays.asList("A", "B", "abc"), p);
	}

	@Test
	public void producaoToString() {
		Producao p = new GLC.Producao("A B abc");
		assertEquals("A B abc", p.toString());

		p = new GLC.Producao("Z");
		assertEquals("Z", p.toString());

		p = new GLC.Producao("&");
		assertEquals("&", p.toString());
	}

	@Test
	public void addProducao() {
		GLC glc = new GLC();

		glc.addProducao("S", new GLC.Producao("abc X Y m"));
		glc.addProducao("S", new GLC.Producao("a S"));

		assertEquals(1, glc.producoes.size());
		assertEquals(Arrays.asList(new Producao("abc X Y m"), new Producao("a S")), glc.producoes.get("S"));

		glc.addProducao("A", "a M Fe");

		assertEquals(2, glc.producoes.size());
		assertEquals(Arrays.asList(new Producao("abc X Y m"), new Producao("a S")), glc.producoes.get("S"));
		assertEquals(Arrays.asList(new Producao("a M Fe")), glc.producoes.get("A"));
	}

}

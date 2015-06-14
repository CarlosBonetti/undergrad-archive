package com.bonaguiar.formais1.core.expr;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class SimoneTest {
	
	private Nodo<Character> d;
	private Nodo<Character> c;
	private Nodo<Character> b;
	private Nodo<Character> a;
	private Nodo<Character> concat2;
	private Nodo<Character> concat1;
	private Nodo<Character> opcional;
	private Nodo<Character> fechamento;
	private Nodo<Character> root;

	@Before
	public void setup() {
		// (ab)*|c?d
		root = new Nodo<Character>('|');
		fechamento = new Nodo<Character>('*');
		opcional = new Nodo<Character>('?');
		concat1 = new Nodo<Character>('.');
		concat2 = new Nodo<Character>('.');
		a = new Nodo<Character>('a');
		b = new Nodo<Character>('b');
		c = new Nodo<Character>('c');
		d = new Nodo<Character>('d');		
		root.setEsq(fechamento);
		fechamento.setEsq(concat1);
		concat1.setEsq(a);
		concat1.setDir(b);
		root.setDir(concat2);
		concat2.setDir(d);
		concat2.setEsq(opcional);
		opcional.setEsq(c);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testarDescerESubir() {		
		root.costurar(Simone.FIM_DA_COSTURA);		
		
		Set<Nodo<Character>> alc = new HashSet<Nodo<Character>>();
		alc = Simone.descer(root, alc);
		assertEquals(4, alc.size());
		assertTrue(alc.containsAll(Arrays.asList(a, c, d, Simone.FIM_DA_COSTURA)));
		
		alc = new HashSet<Nodo<Character>>();
		alc = Simone.subir(concat1, alc);
		assertEquals(1, alc.size());
		assertTrue(alc.containsAll(Arrays.asList(b)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testarObterComposicao() {
		root.costurar(Simone.FIM_DA_COSTURA);
		
		Set<Nodo<Character>> alc = Simone.obterComposicao(root);
		alc = Simone.descer(root, alc);
		assertEquals(4, alc.size());
		assertTrue(alc.containsAll(Arrays.asList(a, c, d, Simone.FIM_DA_COSTURA)));
		
		alc = Simone.obterComposicao(b);
		assertEquals(2, alc.size());
		assertTrue(alc.containsAll(Arrays.asList(a, Simone.FIM_DA_COSTURA)));
	}
	
}

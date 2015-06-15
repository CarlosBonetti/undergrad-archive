package com.bonaguiar.formais1.core.expr;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.bonaguiar.formais1.core.expr.Simone.Composicao;

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
		
		Composicao alc = new Composicao();
		alc = Simone.descer(root, alc);
		assertEquals(4, alc.size());
		assertTrue(alc.containsAll(Arrays.asList(a, c, d, Simone.FIM_DA_COSTURA)));
		
		alc = new Composicao();
		alc = Simone.subir(concat1, alc);
		assertEquals(1, alc.size());
		assertTrue(alc.containsAll(Arrays.asList(b)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testarObterComposicao() {
		root.costurar(Simone.FIM_DA_COSTURA);
		
		Composicao alc = Simone.obterComposicao(root);
		alc = Simone.descer(root, alc);
		assertEquals(4, alc.size());
		assertTrue(alc.containsAll(Arrays.asList(a, c, d, Simone.FIM_DA_COSTURA)));
		
		alc = Simone.obterComposicao(b);
		assertEquals(2, alc.size());
		assertTrue(alc.containsAll(Arrays.asList(a, Simone.FIM_DA_COSTURA)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testarObterComposicaoDeComposicao() {
		root.costurar(Simone.FIM_DA_COSTURA);
		
		Composicao origem = new Composicao();
		origem.add(a);
		origem.add(c);
		
		Composicao alc = Simone.obterComposicao(origem);
		assertTrue(alc.containsAll(Arrays.asList(b, d)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testarComposicaoInicializacao() {
		Composicao comp = new Composicao();
		comp.add(a);
		comp.add(b);
		comp.add(c);
		assertTrue(comp.containsAll(Arrays.asList(a, b, c)));
	}
	
	@Test
	public void testarComposicaoEhFinal() {
		Composicao comp = new Simone.Composicao();
		comp.add(a);
		assertFalse(comp.ehFinal());
		comp.add(Simone.FIM_DA_COSTURA);
		assertTrue(comp.ehFinal());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testarComposicaoTransicao() {
		Composicao comp = new Composicao();
		comp.add(a);
		comp.add(b);
		comp.add(Simone.FIM_DA_COSTURA);
		assertTrue(comp.transicao('a').containsAll(Arrays.asList(a)));
		assertTrue(comp.transicao('b').containsAll(Arrays.asList(b)));
		assertTrue(comp.transicao('d').isEmpty());
	}
	
	@Test
	public void testarComposicaoToString() {
		this.root.costurar(Simone.FIM_DA_COSTURA);
		Composicao comp = new Composicao();
		assertEquals("[]", comp.toString());
		comp.add(b);
		assertEquals("[2b]", comp.toString());		
		comp.add(a);
		comp.add(c);
		assertEquals("[1a, 2b, 3c]", comp.toString());		
		comp.add(Simone.FIM_DA_COSTURA);
		assertEquals("[1a, 2b, 3c, λ]", comp.toString());
	}
}

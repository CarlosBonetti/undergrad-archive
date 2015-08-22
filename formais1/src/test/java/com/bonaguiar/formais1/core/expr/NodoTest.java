package com.bonaguiar.formais1.core.expr;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bonaguiar.formais1.core.expr.Nodo;

public class NodoTest {

	@Test
	public void testContentInitialization() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		assertEquals(new Integer(2), node.getConteudo());
	}
	
	@Test
	public void testarTemEsq() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		Nodo<Integer> child = new Nodo<Integer>(-1);
		assertEquals(false, node.temEsq());
		node.setEsq(child);
		assertEquals(true, node.temEsq());
	}
	
	@Test
	public void testTemDir() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		Nodo<Integer> child = new Nodo<Integer>(-1);
		assertEquals(false, node.temDir());
		node.setDir(child);
		assertEquals(true, node.temDir());
	}
	
	@Test
	public void testarEhFolha() {
		Nodo<Double> node = new Nodo<Double>(2.0);
		assertEquals(node.ehFolha(), true);		
		
		Nodo<Double> child = new Nodo<Double>(-1.0);		
		node.setEsq(child);
		assertEquals(node.ehFolha(), false);
		
		node.setEsq(null);
		node.setDir(null);		
		
		assertEquals(node.ehFolha(), true);
		node.setDir(child);
		assertEquals(node.ehFolha(), false);
		node.setEsq(child);
		assertEquals(node.ehFolha(), false);
	}
	
	@Test
	public void testarCosturar() {
		// (ab)*|c?d
		Nodo<Character> root = new Nodo<Character>('|');
		Nodo<Character> fechamento = new Nodo<Character>('*');
		Nodo<Character> opcional = new Nodo<Character>('?');
		Nodo<Character> concat1 = new Nodo<Character>('.');
		Nodo<Character> concat2 = new Nodo<Character>('.');
		Nodo<Character> a = new Nodo<Character>('a');
		Nodo<Character> b = new Nodo<Character>('b');
		Nodo<Character> c = new Nodo<Character>('c');
		Nodo<Character> d = new Nodo<Character>('d');		
		root.setEsq(fechamento);
		fechamento.setEsq(concat1);
		concat1.setEsq(a);
		concat1.setDir(b);
		root.setDir(concat2);
		concat2.setDir(d);
		concat2.setEsq(opcional);
		opcional.setEsq(c);
		
		root.costurar(Simone.FIM_DA_COSTURA);
		assertEquals(null, root.getCostura());
		assertEquals(root, fechamento.getCostura());
		assertEquals(null, concat1.getCostura());
		assertEquals(concat1, a.getCostura());
		assertEquals(fechamento, b.getCostura());
		assertEquals(null, concat2.getCostura());
		assertEquals(concat2, opcional.getCostura());
		assertEquals(opcional, c.getCostura());
		assertEquals(Simone.FIM_DA_COSTURA, d.getCostura());
		
		// Numeração das folhas
		assertEquals(1, a.getId());
		assertEquals(2, b.getId());
		assertEquals(3, c.getId());
		assertEquals(4, d.getId());
	}
	
	@Test
	public void testarToString() {
		Nodo<Character> nodo = new Nodo<Character>('b');
		assertEquals("b", nodo.toString());
		nodo.setId(3);
		assertEquals("3b", nodo.toString());
	}
}

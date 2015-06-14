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
	public void testHasRightChild() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		Nodo<Integer> child = new Nodo<Integer>(-1);
		assertEquals(false, node.temDir());
		node.setDir(child);
		assertEquals(true, node.temDir());
	}
	
	@Test
	public void testIsLeaf() {
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
}

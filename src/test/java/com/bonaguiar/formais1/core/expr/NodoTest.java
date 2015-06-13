package com.bonaguiar.formais1.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class NodoTest {

	@Test
	public void testContentInitialization() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		assertEquals(new Integer(2), node.getConteudo());
	}
	
	@Test
	public void testHasLeftChild() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		Nodo<Integer> child = new Nodo<Integer>(-1);
		assertEquals(false, node.hasLeftChild());
		node.setLeft(child);
		assertEquals(true, node.hasLeftChild());
	}
	
	@Test
	public void testHasRightChild() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		Nodo<Integer> child = new Nodo<Integer>(-1);
		assertEquals(false, node.hasRightChild());
		node.setRight(child);
		assertEquals(true, node.hasRightChild());
	}
	
	@Test
	public void testClearLeft() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		Nodo<Integer> child = new Nodo<Integer>(-1);
		node.setLeft(child);
		assertEquals(child, node.getLeft());
		node.clearLeft();
		assertEquals(null, node.getLeft());
	}
	
	@Test
	public void testClearRight() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		Nodo<Integer> child = new Nodo<Integer>(-1);
		node.setRight(child);
		assertEquals(child, node.getRight());
		node.clearRight();
		assertEquals(null, node.getRight());
	}
	
	@Test
	public void testClear() {
		Nodo<Integer> node = new Nodo<Integer>(2);
		Nodo<Integer> child1 = new Nodo<Integer>(-1);
		Nodo<Integer> child2 = new Nodo<Integer>(1);
		
		node.setRight(child1);
		node.setLeft(child2);
		
		node.clear();
		assertEquals(null, node.getRight());
		assertEquals(null, node.getLeft());		
	} 

	@Test
	public void testIsLeaf() {
		Nodo<Double> node = new Nodo<Double>(2.0);
		assertEquals(node.isLeaf(), true);		
		
		Nodo<Double> child = new Nodo<Double>(-1.0);		
		node.setLeft(child);
		assertEquals(node.isLeaf(), false);
		
		node.clear();
		assertEquals(node.isLeaf(), true);
		node.setRight(child);
		assertEquals(node.isLeaf(), false);
		node.setLeft(child);
		assertEquals(node.isLeaf(), false);
	}
}

package com.bonaguiar.formais1.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class BinaryNodeTest {

	@Test
	public void testBlankInitialization() {
		BinaryNode<Integer> node = new BinaryNode<Integer>();
		assertEquals(null, node.getContent());
	}
	
	@Test
	public void testContentInitialization() {
		BinaryNode<Integer> node = new BinaryNode<Integer>(2);
		assertEquals(new Integer(2), node.getContent());
	}
	
	@Test
	public void testHasLeftChild() {
		BinaryNode<Integer> node = new BinaryNode<Integer>(2);
		BinaryNode<Integer> child = new BinaryNode<Integer>(-1);
		assertEquals(false, node.hasLeftChild());
		node.setLeft(child);
		assertEquals(true, node.hasLeftChild());
	}
	
	@Test
	public void testHasRightChild() {
		BinaryNode<Integer> node = new BinaryNode<Integer>(2);
		BinaryNode<Integer> child = new BinaryNode<Integer>(-1);
		assertEquals(false, node.hasRightChild());
		node.setRight(child);
		assertEquals(true, node.hasRightChild());
	}
	
	@Test
	public void testClearLeft() {
		BinaryNode<Integer> node = new BinaryNode<Integer>(2);
		BinaryNode<Integer> child = new BinaryNode<Integer>(-1);
		node.setLeft(child);
		assertEquals(child, node.getLeft());
		node.clearLeft();
		assertEquals(null, node.getLeft());
	}
	
	@Test
	public void testClearRight() {
		BinaryNode<Integer> node = new BinaryNode<Integer>(2);
		BinaryNode<Integer> child = new BinaryNode<Integer>(-1);
		node.setRight(child);
		assertEquals(child, node.getRight());
		node.clearRight();
		assertEquals(null, node.getRight());
	}
	
	@Test
	public void testClear() {
		BinaryNode<Integer> node = new BinaryNode<Integer>(2);
		BinaryNode<Integer> child1 = new BinaryNode<Integer>(-1);
		BinaryNode<Integer> child2 = new BinaryNode<Integer>(1);
		
		node.setRight(child1);
		node.setLeft(child2);
		
		node.clear();
		assertEquals(null, node.getRight());
		assertEquals(null, node.getLeft());		
	} 

	@Test
	public void testIsLeaf() {
		BinaryNode<Double> node = new BinaryNode<Double>(2.0);
		assertEquals(node.isLeaf(), true);		
		
		BinaryNode<Double> child = new BinaryNode<Double>(-1.0);		
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

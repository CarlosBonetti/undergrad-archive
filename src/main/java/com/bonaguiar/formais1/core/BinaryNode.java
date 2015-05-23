package com.bonaguiar.formais1.core;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BinaryNode<T> {
	private T content;
	private BinaryNode<T> left;
	private BinaryNode<T> right;
	
	public BinaryNode() {}
	
	public BinaryNode(T content) {
		this.content = content;
	}
	
	/**
	 * Return whether the node has a left child
	 */
	public Boolean hasLeftChild() {
		return this.left != null;
	}
	
	/**
	 * Return whether the node has a right child
	 */
	public Boolean hasRightChild() {
		return this.right != null;
	}
	
	/**
	 * Remove the left child
	 */
	public void clearLeft() {
		this.left = null;
	}
	
	/**
	 * Remove the right child
	 */
	public void clearRight() {
		this.right = null;
	}
	
	/**
	 * Remove both children
	 */
	public void clear() {
		this.clearLeft();
		this.clearRight();
	}
	
	/**
	 * Return whether the node is a leaf
	 */
	public Boolean isLeaf() {
		return !this.hasLeftChild() && !this.hasRightChild();
	}
	
}

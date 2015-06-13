package com.bonaguiar.formais1.core;

import lombok.Getter;
import lombok.Setter;

/**
 * Implementação clássica de um Nodo Binário (de uma árvore binária)
 *
 * @param <T> Tipo do conteúdo
 */
@Setter
@Getter
public class Nodo<T> {
	private T conteudo;
	private Nodo<T> left;
	private Nodo<T> right;
	
	public Nodo(T conteudo) {
		this.conteudo = conteudo;
	}
	
	/**
	 * Checa se o nodo tem um filho à esquerda
	 */
	public Boolean hasLeftChild() {
		return this.left != null;
	}
	
	/**
	 * Checa se o nodo tem um filho à direita
	 */
	public Boolean hasRightChild() {
		return this.right != null;
	}
	
	/**
	 * Remove o filho à esquerda
	 */
	public void clearLeft() {
		this.left = null;
	}
	
	/**
	 * Remove o filho à direita
	 */
	public void clearRight() {
		this.right = null;
	}
	
	/**
	 * Remove ambos os filhos do nodo
	 */
	public void clear() {
		this.clearLeft();
		this.clearRight();
	}
	
	/**
	 * Checa se o nodo é folha (não possui filhos)
	 */
	public Boolean isLeaf() {
		return !this.hasLeftChild() && !this.hasRightChild();
	}
	
}

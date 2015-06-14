package com.bonaguiar.formais1.core.expr;

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
	private Nodo<T> esq;
	private Nodo<T> dir;
	
	public Nodo(T conteudo) {
		this.conteudo = conteudo;
	}
	
	/**
	 * Checa se o nodo tem um filho à esquerda
	 */
	public Boolean temEsq() {
		return this.esq != null;
	}
	
	/**
	 * Checa se o nodo tem um filho à direita
	 */
	public Boolean temDir() {
		return this.dir != null;
	}	

	/**
	 * Checa se o nodo é folha (não possui filhos)
	 */
	public Boolean ehFolha() {
		return !this.temEsq() && !this.temDir();
	}
	
}

package com.bonaguiar.formais1.core.expr;

import java.util.Stack;

import lombok.Getter;
import lombok.Setter;

/**
 * Implementação clássica de um Nodo Binário (de uma árvore binária)
 *
 * @param <T> Tipo do conteúdo
 */
public class Nodo<T> {
	@Setter
	@Getter
	private T conteudo;
	
	@Setter
	@Getter
	private Nodo<T> esq;
	
	@Setter
	@Getter
	private Nodo<T> dir;
	
	@Getter
	private Nodo<T> costura;
	
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
	
	/**
	 * Prepara a costura da árvore para executar um caminhamento inorder posteriormente
	 * Após chamar este método, cada nodo terá a referência do próximo nodo "seguindo pela costura"
	 * A referência é mantida no atributo 'costura'
	 */
	public void costurar() {
		Stack<Nodo<T>> stack = new Stack<Nodo<T>>();
		stack.push(null); // O null marca o fim da costura
		this.costurar(stack);
	}
	
	/**
	 * Método recursivo de costura. Usado pelo `costurar()`
	 * @param stack
	 */
	private void costurar(Stack<Nodo<T>> stack) {
		// Se for folha, cria a costura
		if (this.ehFolha()) {
			this.costura = stack.pop();
			return;
		}
		
		stack.push(this);
		
		// Não é folha, então visite o filho à esquerda
		this.getEsq().costurar(stack);
		
		// Se tiver filho à direita, visite-o, senão crie a costura
		if (this.temDir()) {
			this.getDir().costurar(stack);
		} else {
			this.costura = stack.pop();
		}
	}
}

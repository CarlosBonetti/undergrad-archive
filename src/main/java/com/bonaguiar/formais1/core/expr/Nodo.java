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
	private int id;
	
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
	 * @param fimDaCostura Nodo que marcará o fim da costura
	 */
	public void costurar(Nodo<T> fimDaCostura) {
		Stack<Nodo<T>> stack = new Stack<Nodo<T>>();
		stack.push(fimDaCostura);
		this.costurar(stack, 0);
	}
	
	/**
	 * Método recursivo de costura. Usado pelo `costurar()`
	 * @param stack
	 */
	private int costurar(Stack<Nodo<T>> stack, int id) {
		// Se for folha, cria a costura e associa o id
		if (this.ehFolha()) {
			this.costura = stack.pop();
			this.id = ++id;
			return id;
		}
		
		stack.push(this);
		
		// Não é folha, então visite o filho à esquerda
		id = this.getEsq().costurar(stack, id);
		
		// Se tiver filho à direita, visite-o, senão crie a costura
		if (this.temDir()) {
			id = this.getDir().costurar(stack, id);
		} else {
			this.costura = stack.pop();
		}
		
		return id;
	}
	
	@Override
	public String toString() {
		if (this.getId() != 0) {
			return this.getId() + this.getConteudo().toString();
		}
		
		return this.getConteudo().toString();
	}
}

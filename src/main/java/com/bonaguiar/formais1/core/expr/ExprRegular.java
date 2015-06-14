package com.bonaguiar.formais1.core.expr;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.exception.FormaisException;

import lombok.Getter;

/**
 * Expressão regular 
 */
public class ExprRegular {
	
	/**
	 * Expressão propriamente dita 
	 */
	@Getter
	public final String expr;
	
	/**
	 * Árvore sintática da expressão regular, gerada no momento de construção
	 */
	@Getter
	public final Nodo<Character> tree;
	
	/**
	 * Cria uma nova expressão regular
	 * @param expr
	 * @throws FormaisException 
	 */
	public ExprRegular(String expr) throws FormaisException {
		this.expr = expr;
		this.tree = ERParser.parse(this.expr);
		this.tree.costurar(Simone.FIM_DA_COSTURA);
	}
	
	/**
	 * Extrai o alfabeto da expressão regular
	 * O alfabeto da ER é o conjunto de caracteres que a expressão pode gerar
	 * Exemplo: O alfabeto da expressão (ab)*|c?d é [a, b, c, d]
	 * @return
	 */
	public Alfabeto extrairAlfabeto() {
		Alfabeto alfabeto = new Alfabeto();

		for (Character c : expr.toCharArray()) {
			if (ERParser.CARACTERES.contains(c)) {
				alfabeto.add(c);
			}
		}
		
		return alfabeto;
	}
	
	public void getAFD() {
		// TODO
	}
	
}

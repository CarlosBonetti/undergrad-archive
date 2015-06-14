package com.bonaguiar.formais1.core.expr;

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
		this.tree.costurar();
	}
	
}

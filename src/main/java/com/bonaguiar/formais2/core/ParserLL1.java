package com.bonaguiar.formais2.core;

/**
 * Um analisador sintático que usa o método Top-Down Preditivo LL(1)
 */
public class ParserLL1 {

	/**
	 * Gramática base do analisador sintático
	 */
	private GLC glc;

	/**
	 * Tabela de Parsing do analisador sintático
	 */
	private ParseTable tp;

	/**
	 * Cria um novo analisador sintático para a gramática especificada
	 * A gramática deve ser LL(1) para gerar um analisador determinístico
	 *
	 * @param glc
	 * @throws IllegalArgumentException
	 */
	public ParserLL1(GLC glc) throws IllegalArgumentException {
		this.glc = glc;
		this.tp = new ParseTable(this.glc);
	}

	// TODO
}

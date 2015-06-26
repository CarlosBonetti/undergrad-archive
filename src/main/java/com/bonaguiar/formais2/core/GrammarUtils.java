package com.bonaguiar.formais2.core;

public class GrammarUtils {
	public static final Character EPSILON = '&';

	/**
	 * Checa se o símbolo é um terminal, segundo a convenção adotada para este projeto
	 *
	 * @param simbolo
	 * @return
	 */
	public static Boolean ehTerminal(String simbolo) {
		return !ehNaoTerminal(simbolo);
	}

	/**
	 * Checa se o símbolo é um não terminal, segundo a convenção adotada para este projeto
	 *
	 * @param simbolo
	 * @return
	 */
	public static Boolean ehNaoTerminal(String simbolo) {
		return Character.isUpperCase(simbolo.charAt(0));
	}
}

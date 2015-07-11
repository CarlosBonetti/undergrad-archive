package com.bonaguiar.formais2.core;

import com.bonaguiar.formais2.core.GLC.FormaSentencial;

public class GrammarUtils {
	public static final Character EPSILON = '&';

	public static final Character END_OF_SENTENCE = '$';

	public static final FormaSentencial PRODUCAO_VAZIA = new FormaSentencial(EPSILON.toString());

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
		return Character.isLetter(simbolo.charAt(0)) && Character.isUpperCase(simbolo.charAt(0));
	}
}

package com.bonaguiar.formais1.core;

import java.util.HashSet;

/**
 * Um conjunto qualquer de caracteres
 * Só uma implementação de Set<Character>
 */
public class Alfabeto extends HashSet<Character> {
	private static final long serialVersionUID = -2777252438918979238L;
	
	/**
	 * Caracter que deve ser considerado como o epsolon
	 */
	public static final Character EPSOLON = '&';

	/**
	 * Cria um novo alfabeto a partir da lista de caracteres informada
	 * @param caracteres Lista de caracteres do alfabeto
	 */
	public Alfabeto(Character[] caracteres) {
		for(char c : caracteres) {
			super.add(c);
		}
	}

	/**
	 * Cria um novo alfabeto com os caracteres passados como parâmetro
	 * @param caracteres Lista de caracteres do alfabeto
	 */
	public Alfabeto(char... caracteres) {
		for(char c : caracteres) {
			super.add(c);
		}
	}
	
	/**
	 * Cria um novo alfabeto a partir uma string
	 * @param caracteres String contendo os caracteres do alfabeto. Cada caracter da string é considerado como um símbolo
	 * novo do alfabeto
	 */
	public Alfabeto(String caracteres) {
		this(caracteres.toCharArray());
	}
	
	/**
	 * Checa se o alfabeto contém o símbolo informado
	 * Por definição, epsolon pertence a todo alfabeto
	 */
	@Override
	public boolean contains(Object o) {
		return super.contains(o) || o == EPSOLON;
	}

}

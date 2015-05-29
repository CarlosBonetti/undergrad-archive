package com.bonaguiar.formais1.core;

import java.util.HashSet;

/**
 * Um conjunto qualquer de caracteres
 * Só uma implementação de Set<Character>
 */
public class Alfabeto extends HashSet<Character> {
	private static final long serialVersionUID = -2777252438918979238L;

	/**
	 * Lista de caracteres do alfabeto
	 * @param caracteres
	 */
	public Alfabeto(Character[] caracteres) {
		for(char c : caracteres) {
			super.add(c);
		}
	}

	/**
	 * Lista de caracteres do alfabeto
	 * @param caracteres
	 */
	public Alfabeto(char... caracteres) {
		for(char c : caracteres) {
			super.add(c);
		}
	}
	
	/**
	 * String contendo os caracteres do alfabeto. Cada caracter da string é considerado como um símbolo
	 * novo do alfabeto
	 * @param caracteres
	 */
	public Alfabeto(String caracteres) {
		this(caracteres.toCharArray());
	}

}

package com.bonaguiar.formais1.core;

import java.util.HashSet;

/**
 * Um conjunto qualquer de caracteres
 * Só uma implementação de Set<Character>
 */
public class Alfabeto extends HashSet<Character> {
	private static final long serialVersionUID = -2777252438918979238L;

	public Alfabeto(Character[] caracteres) {
		for(char c : caracteres) {
			super.add(c);
		}
	}

	public Alfabeto(char... caracteres) {
		for(char c : caracteres) {
			super.add(c);
		}
	}

}

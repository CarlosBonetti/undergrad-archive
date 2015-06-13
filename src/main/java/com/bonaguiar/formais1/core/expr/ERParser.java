package com.bonaguiar.formais1.core.expr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.bonaguiar.formais1.core.exception.FormaisException;

/**
 * Implementação manual (e provavelmente extremamente ineficiente) de um parser de expressões regulares
 * Responsável por criar a árvore sintática de uma expressão regular
 */
public class ERParser {

	/**
	 * Conjunto de caracteres válidos (que podem aparecer na expressão regular)
	 */
	public static final Set<Character> CARACTERES = new HashSet<Character>(Arrays.asList(
		'&',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	));
	
	/**
	 * Retorna uma árvore binária que representa a expressão regular informada
	 * @param expr
	 * @return
	 * @throws FormaisException 
	 */
	public static Nodo<Character> parse(String expr) throws FormaisException {
		if (expr == null || expr.isEmpty()) {
			return new Nodo<Character>(null);
		}
		
		if (expr.length() == 1) {
			return parseChar(expr.charAt(0));
		}
		
		// Se expressão for da forma "(...)", retorna o parse da expressão interna
		if (expr.startsWith("(") && expr.endsWith(")")) {
			return parse(expr.substring(1, expr.length() - 1));
		}
		
		// Procuramos pela operação de mais alto nível, seguindo a precedência dos operadores
		int pos;
		
		// Se possuir '|'
		pos = posOperador(expr, '|');
		if (pos != -1) {
			Nodo<Character> nodo = new Nodo<Character>('|');
			nodo.setEsq(parse(expr.substring(0, pos)));
			nodo.setDir(parse(expr.substring(pos + 1)));
			return nodo;
		}
		
		// Se possuir concatenação
		pos = posConcat(expr);
		if (pos != 0) {
			Nodo<Character> nodo = new Nodo<Character>('.');
			nodo.setEsq(parse(expr.substring(0, pos)));
			nodo.setDir(parse(expr.substring(pos)));
			return nodo;
		}
		
		if (expr.endsWith("*")) {
			Nodo<Character> nodo = new Nodo<Character>('*');
			nodo.setEsq(parse(expr.substring(0, expr.length() - 1)));
			return nodo;
		}
		
		if (expr.endsWith("?")) {
			Nodo<Character> nodo = new Nodo<Character>('?');
			nodo.setEsq(parse(expr.substring(0, expr.length() - 1)));
			return nodo;
		}
		
		throw new FormaisException("Expressão regular má formada: '" + expr + "'");
	}
	
	public static Nodo<Character> parseChar(char c) throws FormaisException {
		if (!CARACTERES.contains(c)) {
			throw new FormaisException("Caractere '" + c + "' não é válido como alfabeto para expressões regulares");
		}
		return new Nodo<Character>(c);
	}
	
	/**
	 * Retorna em qual posição da expressão encontra-se o operador especificado, sem "entrar" em parênteses
	 * Ou seja, sub-expressões dentro de parênteses não são consideradas pela busca
	 * Retorna -1 caso o caracter não tenha sido encontrado
	 * @param expr
	 * @param op
	 * @return A primeira posição encontrada do operador op ou -1 caso não encontrado
	 */
	public static int posOperador(String expr, Character op) {
		int nivel = 0; // Nível atual de parêntese
		int i = 0;
		for (Character e : expr.toCharArray()) {
			if (e.equals('(')) {
				nivel++;
			} else if (e.equals(')')) {
				nivel--;
			} else if (e.equals(op) && nivel == 0) {
				return i;
			}
			i++;
		}
		
		return -1;
	}
	
	/**
	 * Retorna a posição na qual a expressão pode ser divida seguindo o operador de concatenação
	 * Exemplo: "(a|c)*bc" -> 6. Indicando que 0-5 representa a primeira parte da concatenação "(a|c)*" e 6-end
	 * representa a segunda parte da concatenação "bc"
	 * @param expr
	 * @return A posição da divisão ou 0 se não existe concatenação na expressão
	 */
	public static int posConcat(String expr) {
		int nivel = 0; // Nível atual de parêntese
		char[] arr = expr.toCharArray();
		
		for (int i = 0; i < expr.length() - 1; i++) {
			if (arr[i] == '(') {
				nivel++;
			} else if (arr[i] == ')') {
				nivel--;
			} 
			
			if (nivel == 0 && arr[i+1] != '*' && arr[i+1] != '?') {
				return i + 1;
			}
		}
		
		return 0;
	}
}

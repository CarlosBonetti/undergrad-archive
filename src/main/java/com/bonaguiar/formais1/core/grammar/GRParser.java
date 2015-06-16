package com.bonaguiar.formais1.core.grammar;

import java.util.ArrayList;
import java.util.List;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.exception.FormaisException;

import lombok.AllArgsConstructor;

/**
 * Responsável por criar um objeto GramaticaRegular a partir de um texto com produções
 */
public class GRParser {

	/**
	 * Interpreta uma linha e retorna a produção correspondente
	 * Uma linha deve ser da forma "S -> aS | bA ... | a"
	 * @param line
	 * @return
	 * @throws FormaisException 
	 */
	public static Producao parseLine(String line) throws FormaisException {
		line = line.replace(" ", ""); // Remove todos os espaços das produções
		String[] parts = line.split("->");
		
		if (parts.length != 2) {
			throw new FormaisException("Erro na linha '" + line
					+ "'. Cada linha deve ter exatamente um conjunto de produções do tipo 'S -> aS | ... | b'");
		}
		
		if (parts[0].length() != 1) {
			throw new FormaisException("Linha '" + line + "' deve ter exatamente um produtor válido");
		}
		
		Producao p = new Producao(parts[0].charAt(0));
		
		String[] prods = parts[1].split("\\|");
		if (prods.length == 0) {
			throw new FormaisException("Linha '" + line + "' deve ter ao menos uma produção válida");
		}

		for (String newProd : prods) {
			if (!newProd.isEmpty()) {
				p.producoes.add(newProd);
			}
		}
		
		return p;
	}
	
	/**
	 * Cria uma nova gramática regular dado um texto multilinha contendo produções de gramática
	 * Exemplo:   S -> aA | b
	 * 						B -> b | aS
	 * @throws FormaisException 
	 */
	public static GramaticaRegular parse(String text) throws FormaisException {
		Character S = 'S';
		Alfabeto Vn = new Alfabeto();
		Alfabeto Vt = new Alfabeto();
		
		String[] lines = text.split("\n");
		
		ArrayList<Producao> producoes = new ArrayList<Producao>();
		for (String line : lines) {
			Producao producao = parseLine(line);
			producoes.add(producao);
			
			Vn.add(producao.produtor);
			for (String p : producao.producoes) {
				Vt.add(p.charAt(0));
			}
		}
		
		S = producoes.get(0).produtor;		
		GramaticaRegular gr = new GramaticaRegular(Vn, Vt, S);
		
		for (Producao p : producoes) {
			for (String destino : p.producoes) {
				gr.addProducao(p.produtor, destino);
			}
		}
		
		return gr;
	}
	
	@AllArgsConstructor
	public static class Producao {
		Character produtor;
		List<String> producoes;
		
		public Producao(Character produtor) {
			this.produtor = produtor;
			this.producoes = new ArrayList<String>();
		}
	}
}

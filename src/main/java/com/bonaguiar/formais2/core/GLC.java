package com.bonaguiar.formais2.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;

/**
 * Gramática Livre de Contexto
 */
public class GLC implements Serializable {
	private static final long serialVersionUID = 785464899552263860L;

	/**
	 * Produções da gramática
	 */
	@Getter
	protected HashMap<String, List<Producao>> producoes = new HashMap<String, List<Producao>>();

	/**
	 * Adiciona uma nova produção à gramática
	 *
	 * @param produtor Lado esquerdo da produção. 'S' do exemplo: S -> ab A B
	 * @param producao Produção. Lado direito da produção. 'ab A B' do exemplo anterior
	 */
	public void addProducao(String produtor, String producao) {
		this.addProducao(produtor, new Producao(producao));
	}

	/**
	 * Adiciona uma nova produção à gramática
	 *
	 * @param produtor Lado esquerdo da produção. 'S' do exemplo: S -> ab A B
	 * @param producao Produção. Lado direito da produção. 'ab A B' do exemplo anterior
	 */
	public void addProducao(String produtor, Producao producao) {
		List<Producao> lista;

		if (producoes.containsKey(produtor)) {
			lista = producoes.get(produtor);
		} else {
			lista = new ArrayList<Producao>();
			producoes.put(produtor, lista);
		}

		lista.add(producao);
	}

	/**
	 * Produção livre de contexto
	 * Representa o lado direito de uma produção.
	 * Exemplo: em 'S -> a B C | abc Ce Fe', existem dois objetos Producao, 'a B C' e 'abc Ce Fe' com três partes
	 * cada um (um terminal e dois não terminais)
	 */
	public static class Producao extends ArrayList<String> {
		private static final long serialVersionUID = -2032770137692974596L;

		/**
		 * Cria uma nova produção para gramáticas livres de contexto
		 *
		 * @param producao Lado direito de uma produção, com as partes separadas por espaço. Exemplo: 'a T1 T2'
		 */
		public Producao(String producao) {
			String[] parts = producao.split(" ");
			for (String part : parts) {
				this.add(part);
			}
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (String part : this) {
				builder.append(part);
				builder.append(" ");
			}
			return builder.toString().trim();
		}
	}
}

package com.bonaguiar.formais2.core;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	protected Map<String, List<FormaSentencial>> producoes = new LinkedHashMap<String, List<FormaSentencial>>();

	/**
	 * Símbolo inicial da gramática
	 */
	@Getter
	protected String simboloInicial;

	/**
	 * A String com o conjunto de produções original que deu origem a esta
	 * gramática
	 */
	@Getter
	protected String raw;

	/**
	 * Guarda o conjunto first dos símbolos não terminais desta gramática em um
	 * hash do tipo 'símbolo não terminal => conjunto first'
	 */
	protected Map<String, Set<String>> firstSet = new LinkedHashMap<String, Set<String>>();

	/**
	 * Guarda o conjunto follow dos símbolos não terminais desta gramática
	 */
	protected Map<String, Set<String>> followSet = new LinkedHashMap<String, Set<String>>();

	/**
	 * Cria uma nova Gramática Livre de Contexto baseada no conjunto de
	 * produções A primeira produção fornecida é considerada símbolo inicial
	 *
	 * @param producoes
	 *            Conjunto de produções. Cada produção deve estar em uma linha.
	 *            Exemplo: E -> T E1 E1 -> + T E1 | & T -> F T1 T1 -> * F T1 | &
	 *            F -> ( E ) | id
	 * @throws Exception
	 */
	public GLC(String producoes) throws Exception {
		this.raw = producoes;

		String[] lines = producoes.split("\n|\r\n");
		for (String line : lines) {
			this.addProducoes(line);
		}

		this.simboloInicial = this.producoes.keySet().iterator().next();
	}

	/**
	 * Construtor vazio. Usado pelos testes unitários para
	 * "ir criando a gramática aos poucos". Protegido para não ser usado
	 * externamente
	 */
	protected GLC() {
	}

	/**
	 * Adiciona um novo conjunto de produções à gramática.
	 *
	 * @param line
	 *            Uma linha do conjunto de produções da gramática. Exemplo: 'E
	 *            -> E + T | E - T | T' irá adicionar três novas produções
	 *            associadas ao não terminal 'E'
	 * @throws Exception
	 */
	protected void addProducoes(String line) throws Exception {
		String[] parts = line.split("->");

		if (parts.length != 2) {
			throw new ParseException(
					"Produção mal formada: "
							+ line
							+ ". Produções devem seguir o padrao 'E -> E + T | .. | id'",
					0);
		}

		String produtor = parts[0].trim();
		String[] producoes = parts[1].trim().split("\\|");

		for (String producao : producoes) {
			this.addProducao(produtor, producao);
		}
	}

	/**
	 * Adiciona uma nova produção à gramática
	 *
	 * @param produtor
	 *            Lado esquerdo da produção. 'S' do exemplo: S -> ab A B
	 * @param producao
	 *            Produção. Lado direito da produção. 'ab A B' do exemplo
	 *            anterior
	 */
	protected void addProducao(String produtor, String producao) {
		this.addProducao(produtor, new FormaSentencial(producao.trim()));
	}

	/**
	 * Adiciona uma nova produção à gramática
	 *
	 * @param produtor
	 *            Lado esquerdo da produção. 'S' do exemplo: S -> ab A B
	 * @param formaSentencial
	 *            Produção. Lado direito da produção. 'ab A B' do exemplo
	 *            anterior
	 */
	protected void addProducao(String produtor, FormaSentencial formaSentencial) {
		List<FormaSentencial> lista;

		if (producoes.containsKey(produtor)) {
			lista = producoes.get(produtor);
		} else {
			lista = new ArrayList<FormaSentencial>();
			producoes.put(produtor, lista);
		}

		lista.add(formaSentencial);
	}

	public String getTodaGramatica() {
		String gramatica = "";
		for (String chave : producoes.keySet()) {
			gramatica += chave + " -> ";
			for (FormaSentencial forma : producoes.get(chave)) {
				for (String producao : forma) {
					gramatica += producao + " ";
				}
				gramatica += "| ";
			}
			gramatica = gramatica.substring(0, gramatica.length() - 2) + "\n";
		}
		return gramatica;
	}

	// ===================================================================================================
	// First

	/**
	 * Retorna um hash com todos os conjuntos 'first' da gramática O hash
	 * retornado possui os símbolos não terminais da gramática como chave e um
	 * conjunto de símbolos first associados a este não terminal
	 *
	 * @return
	 */
	public Map<String, Set<String>> getFirstSet() {
		for (String naoTerminal : this.producoes.keySet()) {
			if (!this.firstSet.containsKey(naoTerminal)) {
				this.firstSet.put(naoTerminal, first(naoTerminal));
			}
		}

		return this.firstSet;
	}

	/**
	 * Retorna o firstSet da forma sentencial parâmetro
	 *
	 * @param formaSentencial
	 * @return
	 */
	protected Set<String> first(FormaSentencial formaSentencial) {
		Set<String> set = new HashSet<String>();

		for (String simbolo : formaSentencial) {
			set.remove(GrammarUtils.EPSILON.toString());
			Set<String> f = first(simbolo);
			set.addAll(f);

			if (!f.contains(GrammarUtils.EPSILON.toString())) {
				break;
			}
		}

		return set;
	}

	/**
	 * Retorna o firstSet do símbolo
	 *
	 * @param simbolo
	 * @return
	 */
	protected Set<String> first(String simbolo) {
		Set<String> set = new HashSet<String>();

		if (GrammarUtils.ehTerminal(simbolo)) {
			// First de um terminal é o próprio terminal
			set.add(simbolo);
		} else {
			if (this.firstSet.containsKey(simbolo)) {
				// Se for um símbolo não terminal já calculado, simplesmente
				// retorna o conjunto previamente criado
				set.addAll(this.firstSet.get(simbolo));
			} else {
				// Calcula o first de cada produção
				for (FormaSentencial formaSentencial : this.producoes
						.get(simbolo)) {
					set.addAll(first(formaSentencial));
				}

				// Salva o firstSet recém calculado do terminal para evitar
				// retrabalho
				this.firstSet.put(simbolo, set);
			}
		}

		return set;
	}

	// ===================================================================================================
	// Follow

	public Map<String, Set<String>> getFollowSet() {
		// TODO
		return this.followSet;
	}

	// ===================================================================================================
	/**
	 * Forma sentencial de uma gramática livre de contexto Representa o lado
	 * direito de uma produção. Exemplo: em 'S -> a B C | abc Ce Fe', existem
	 * dois objetos FormaSentencial, 'a B C' e 'abc Ce Fe' com três partes cada
	 * um (um terminal e dois não terminais)
	 */
	public static class FormaSentencial extends ArrayList<String> {
		private static final long serialVersionUID = -2032770137692974596L;

		/**
		 * Cria uma nova forma sentencial para gramáticas livres de contexto
		 *
		 * @param producao
		 *            Lado direito de uma produção, com as partes separadas por
		 *            espaço. Exemplo: 'a T1 T2'
		 */
		public FormaSentencial(String producao) {
			if (producao.isEmpty()) {
				throw new IllegalArgumentException(
						"Produção não pode ser vazia");
			}

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

	// ===================================================================================================

	/**
	 * Identificar se a GLC possui recursão a esquerda, qual seu tipo e quais
	 * ñ-terminais são estes
	 *
	 */

	/**
	 * Retorna uma lista com os ñ-teminais que possuem recursão a esquerda
	 * direta
	 * 
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getRecursaoEsquerdaDireta() {
		ArrayList<String> recEsqDireta = new ArrayList<String>();
		for (String chave : producoes.keySet()) {
			if (temRecursaoEsquerdaDireta(chave)) {
				recEsqDireta.add(chave);
			}
		}
		return recEsqDireta;
	}

	/**
	 * Verifica se a producao possui recursao esquerda direta
	 * @param producao
	 * @return
	 */
	public boolean temRecursaoEsquerdaDireta(String producao) {
		for (FormaSentencial forma : producoes.get(producao)) {
			if (producao.equals(forma.get(0))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retorna uma lista com os ñ-teminais que possuem recursão a esquerda
	 * indireta
	 * 
	 * @return
	 * @throws Exception
	 */
	public void getRecursaoEsquerdaIndireta() throws Exception {
		// TODO
	}

}

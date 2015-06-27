package com.bonaguiar.formais2.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tabela de Parsing
 */
public class ParseTable {

	/**
	 * Hash Map com os valores das células da tabela
	 */
	protected Map<String, Map<String, Integer>> hash = new HashMap<String, Map<String, Integer>>();

	/**
	 * Gramática correspondente a esta Tabela de Parsing
	 */
	protected GLC glc;

	/**
	 * Cria uma nova tabela de parsing para a gramática parâmetro
	 *
	 * @throws IllegalArgumentException
	 */
	public ParseTable(GLC glc) throws IllegalArgumentException {
		this.glc = glc;
		this.construirTabela();
	}

	/**
	 * (Re)Constrói a tabela de parsing
	 *
	 * @throws IllegalArgumentException
	 */
	protected void construirTabela() throws IllegalArgumentException {
		// 1) Para todo A -> α ∈ P
		int i = 0;
		for (String A : glc.getProducoes().keySet()) {
			for (GLC.FormaSentencial alfa : glc.getProducoes().get(A)) {
				// 2) Para todo a ∈ First(α), exceto ε,
				// coloque o número da produção A -> α em TP(A,a)
				Set<String> alfaFirst = glc.first(alfa);

				for (String a : alfaFirst) {
					if (a.equals(GrammarUtils.EPSILON.toString())) {
						continue;
					}

					set(A, a, i);
				}

				// 3) Se ε ∈ first (α)
				// coloque o número da produção A -> α
				// em TP(A,b), para todo b ∈ follow(A)
				if (alfaFirst.contains(GrammarUtils.EPSILON.toString())) {
					for (String b : glc.follow(A)) {
						set(A, b, i);
					}
				}

				i++;
			}
		}
	}

	/**
	 * Seta o valor da posição na tabela
	 *
	 * @param naoTerminal
	 * @param terminal
	 * @param numero
	 * @throws IllegalArgumentException
	 */
	public void set(String naoTerminal, String terminal, Integer numero) throws IllegalArgumentException {
		if (this.hash.isEmpty()) {
			for (String nt : glc.getNaoTerminais()) {
				hash.put(nt, new HashMap<String, Integer>());
			}
		}

		if (this.hash.get(naoTerminal).containsKey(terminal)) {
			throw new IllegalArgumentException("Detectado não determinismo na construção da Tabela de Parsing: tp(" + naoTerminal + ", " + terminal + ")");
		}

		this.hash.get(naoTerminal).put(terminal, numero);
	}

	/**
	 * Retorna o número da produção associado à posição especificada na tabela
	 * 
	 * @param naoTerminal
	 * @param terminal
	 * @return
	 */
	public Integer get(String naoTerminal, String terminal) {
		return this.hash.get(naoTerminal).get(terminal);
	}
}

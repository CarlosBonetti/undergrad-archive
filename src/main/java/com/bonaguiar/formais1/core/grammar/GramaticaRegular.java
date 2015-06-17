package com.bonaguiar.formais1.core.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.automata.AF;
import com.bonaguiar.formais1.core.exception.FormaisException;

public class GramaticaRegular implements Serializable {
	private static final long serialVersionUID = -7299347814328494949L;

	/**
	 * Conjunto de símbolos não terminais
	 */
	@Getter
	protected Alfabeto Vn;

	/**
	 * Conjunto de símbolos terminais
	 */
	@Getter
	protected Alfabeto Vt;

	/**
	 * Símbolo inicial (pertencente à Vt)
	 */
	@Getter
	protected Character S;

	/**
	 * Produções da gramática
	 */
	@Getter
	protected HashMap<Character, List<String>> producoes;

	/**
	 * Flag que diz se a gramática aceita epsilon ou não
	 * Adicione uma epsilon-produção à gramática para ativar esta flag
	 */
	@Getter
	protected Boolean aceitaEpsilon = Boolean.FALSE;

	/**
	 * Cria uma nova gramática regular
	 *
	 * @param Vn Conjunto de caracteres não terminais (devem ser maiúsculos)
	 * @param Vt Conjunto de caracteres terminais (devem ser minúsculos)
	 * @param S Símbolo inicial da gramática (deve pertencer a Vn)
	 * @throws Exception
	 */
	public GramaticaRegular(Alfabeto Vn, Alfabeto Vt, Character S) throws FormaisException {
		this.setVn(Vn);
		this.setVt(Vt);
		this.setSimboloInicial(S);
		this.limparProducoes();
	}

	/**
	 * Seta o conjunto de símbolos não terminais da gramática
	 *
	 * @param Vn
	 * @throws FormaisException
	 */
	protected void setVn(Alfabeto Vn) throws FormaisException {
		for (Character c : Vn) {
			if (!Character.isUpperCase(c)) {
				throw new FormaisException("Símbolo não-terminal `" + c + "` deve ser maiúsculo.");
			}
		}
		this.Vn = Vn;
	}

	/**
	 * Seta o conjunto de caracteres terminais da gramática
	 *
	 * @param Vt
	 * @throws FormaisException
	 */
	protected void setVt(Alfabeto Vt) throws FormaisException {
		for (Character c : Vt) {
			if (!Character.isLowerCase(c)) {
				throw new FormaisException("Símbolo terminal `" + c + "` deve ser minúsculo.");
			}
		}
		this.Vt = Vt;
	}

	/**
	 * Seta o símbolo inicial da gramática
	 *
	 * @param S Novo símbolo inicial da gramática
	 * @throws FormaisException Lançado caso S não pertença a Vn
	 */
	public void setSimboloInicial(char S) throws FormaisException {
		if (!this.Vn.contains(S)) {
			throw new FormaisException("Símbolo inicial `" + S + "` não pertence a Vn");
		}

		this.S = S;
	}

	/**
	 * Remove todas as produções da gramática
	 */
	public void limparProducoes() {
		this.producoes = new HashMap<Character, List<String>>();
		for (Character sn : this.Vn) {
			this.producoes.put(sn, new ArrayList<String>());
		}
	}

	/**
	 * Adiciona uma produção à gramática
	 *
	 * @param produtor Símbolo que gera a sentença (deve pertencer a Vn)
	 * @param producao Sentença gerada (deve ser do formato aA ou a e símbolos devem pertencer à gramática)
	 * @throws FormaisException
	 */
	public void addProducao(char produtor, String producao) throws FormaisException {
		if (!this.Vn.contains(produtor)) {
			throw new FormaisException("Símbolo produtor `" + produtor + "` não pertence " + "ao conjunto de símbolos não terminais da gramática");
		}

		// Produções devem ter 1 ou 2 caracteres
		if (producao.length() != 1 && producao.length() != 2) {
			throw new FormaisException("Produções de uma gramática regular devem ter apenas os " + "formatos S->aB ou S -> a");
		}

		// Se for uma epsolon transição válida, ativa o aceitaEpsilon da gramática e encerra (não adiciona às produções)
		if (producao.charAt(0) == Alfabeto.EPSILON) {
			if (produtor != this.getS() || producao.length() != 1) {
				throw new FormaisException("Epsilon só é permitido na produção inicial de uma gramática regular e no formato S -> " + Alfabeto.EPSILON);
			}
			this.aceitaEpsilon = Boolean.TRUE;
			return;
		}

		// Primeiro caracter da produção é obrigatório e deve ser um símbolo terminal
		if (!this.Vt.contains(producao.charAt(0))) {
			throw new FormaisException("Caracter `" + producao.charAt(0) + "` não é um símbolo terminal " + "válido desta gramática");
		}

		if (producao.length() == 2 && !this.Vn.contains(producao.charAt(1))) {
			throw new FormaisException("Caracter `" + producao.charAt(1) + "` não é um símbolo não terminal " + "válido desta gramática");
		}

		this.producoes.get(produtor).add(producao);
	}

	/**
	 * Retorna todas as produções do símbolo
	 *
	 * @param produtor
	 * @return
	 * @throws FormaisException
	 */
	public List<String> getProducoes(char produtor) throws FormaisException {
		if (!this.Vn.contains(produtor)) {
			throw new FormaisException("Símbolo produtor `" + produtor + "` não pertence " + "ao conjunto de símbolos não terminais da gramática");
		}

		return this.producoes.get(produtor);
	}

	public boolean possuiEpsilon(char produtor) throws FormaisException {
		for (String string : this.getProducoes(produtor)) {
			if (string.startsWith(Alfabeto.EPSILON.toString(), 0)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retorna um automato finito
	 * Teorema 3.2
	 * Novo estado final criado representado por "Δ"
	 *
	 * @return AF
	 * @throws FormaisException
	 */
	public AF getAutomatoFinito() throws FormaisException {
		// criado novo estado de aceitação
		final String estadoFinal = "Δ";

		AF af = new AF(this.getVt());
		for (Character charVn : this.getVn()) {
			af.addEstado(charVn.toString(), false);
		}
		af.setEstadoInicial(this.getS().toString());
		af.addEstado(estadoFinal, true);

		// adiciona estado inicial também como final se epsilon existir como produção
		if (this.aceitaEpsilon) {
			af.getEstadosFinais().add(af.getEstadoInicial());
		}

		Iterator<Character> it = this.getVn().iterator();
		while (it.hasNext()) {
			Character afEstado = it.next();
			if (this.getProducoes(afEstado) != null) {
				for (String producao : this.getProducoes(afEstado)) {
					if (producao.length() == 1) {
						af.addTransicao(afEstado.toString(), producao.charAt(0), estadoFinal);
					} else {
						af.addTransicao(afEstado.toString(), producao.charAt(0), producao.substring(1, 2));
					}
				}
			}
		}

		return af;
	}
}

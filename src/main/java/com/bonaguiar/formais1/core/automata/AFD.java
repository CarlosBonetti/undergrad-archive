package com.bonaguiar.formais1.core.automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.exception.FormaisException;

/**
 * Autômato finito determinístico
 * Mesma implementação de AF, mas garante que todas as produções sejam determinísticas
 *
 * @author carlosbonetti
 *
 */
public class AFD extends AF {

	/**
	 * Cria um novo autômato finito determinístico
	 *
	 * @param alfabeto Alfabeto de aceitação do autômato
	 */
	public AFD(Alfabeto alfabeto) {
		super(alfabeto);
	}

	/**
	 * Adiciona uma transição ao autômato.
	 * Deve ser determinística, caso contrária uma FormaisException é lançada
	 *
	 * @param estadoPartida Nome do estado de partida (deve pertencer ao autômato)
	 * @param caracter Caracter de transição (deve pertencer ao alfabeto do autômato)
	 * @param estadoChegada Nome do estado de chegada (deve pertencer ao autômato)
	 * @throws FormaisException
	 */
	@Override
	public void addTransicao(String estadoPartida, Character caracter, String estadoChegada) throws FormaisException {
		// Transições ambíguas não são permitidas em AF determinísticos
		List<String> transicoes = this.transicao(estadoPartida, caracter);
		if (!transicoes.isEmpty()) {
			Transicao t = new Transicao(estadoPartida, caracter, estadoChegada);
			Transicao t2 = new Transicao(estadoPartida, caracter, transicoes.get(0));
			throw new FormaisException("A transição " + t.toString() + " não é determinística neste AFD, pois já foi definida" + " como " + t2.toString());
		}

		super.addTransicao(estadoPartida, caracter, estadoChegada);
	}

	// =====================================================================================
	// Minimização + helper methods

	/**
	 * Cria e retorna um autômato mínimo equivalente ao AFD
	 *
	 * @return
	 * @throws FormaisException
	 */
	public AFMin getAFMin() throws FormaisException {
		AFMin min = new AFMin(this.alfabeto);

		// Mantemos somente estados alcançáveis e vivos
		Set<String> alc = this.getEstadosAlcancaveis();
		Set<String> vivos = this.getEstadosVivos();
		min.estados.addAll(this.getEstados());
		min.estados.retainAll(alc);
		min.estados.retainAll(vivos);

		// Mantemos os estados finais alcancáveis (finais sempre são vivos por definição)
		min.estadosFinais.addAll(this.getEstadosFinais());
		min.estadosFinais.retainAll(alc);

		// Adicionamos somente transições que se refiram a estados vivos e alcancáveis
		for (Transicao t : this.transicoes) {
			if (alc.contains(t.estadoOrigem) && vivos.contains(t.estadoOrigem) && alc.contains(t.estadoDestino) && vivos.contains(t.estadoDestino)) {
				min.transicoes.add(t);
			}
		}

		// Criamos as classes de equivalência iniciais
		ClasseEq finais = new ClasseEq(min.estadosFinais);
		ClasseEq naoFinais = new ClasseEq(min.estados);
		naoFinais.removeAll(finais);

		List<ClasseEq> classes = new ArrayList<ClasseEq>();
		classes.add(finais);
		classes.add(naoFinais);

		List<ClasseEq> novasClasses = new ArrayList<ClasseEq>();
		while (true) {
			novasClasses = new ArrayList<ClasseEq>();

			for (ClasseEq ce : classes) {
				novasClasses.addAll(this.splitCE(ce, classes));
			}

			if (novasClasses.size() == classes.size()) {
				break;
			} else {
				classes = novasClasses;
			}
		}

		// Seta o estado inicial
		min.setEstadoInicial(this.getEstadoInicial());

		// Para cada classe de equivalência, elege um estado representante e modifica as ocorrências
		// das transições dos demais estados para este representante
		for (ClasseEq ce : classes) {
			if (ce.size() > 1) {
				Collections.sort(ce);
				String representante = ce.get(0);
				if (ce.contains(min.getEstadoInicial())) {
					representante = min.getEstadoInicial();
				}

				for (Transicao t : new ArrayList<Transicao>(min.transicoes)) {
					if (!t.estadoOrigem.equals(representante) && ce.contains(t.estadoOrigem)) {
						min.transicoes.remove(t);
					}

					if (ce.contains(t.estadoDestino)) {
						t.estadoDestino = representante;
					}
				}

				// Por fim, remove os estados não representantes
				ce.remove(representante);
				min.estados.removeAll(ce);
				min.estadosFinais.removeAll(ce);
			}
		}

		return min;
	}

	/**
	 * Retorna a classe de equivalência a que o estado leva ao encontrar o caracter c,
	 * pesquisando na lista de classes especificada
	 *
	 * @param c
	 * @param classes
	 * @throws FormaisException
	 */
	protected ClasseEq transicaoCE(String estado, Character c, List<ClasseEq> classes) throws FormaisException {
		for (ClasseEq classe : classes) {
			List<String> t = this.transicao(estado, c);
			if (!t.isEmpty() && classe.containsAll(t)) {
				return classe;
			}
		}

		return null;
	}

	/**
	 * Checa se os estados levam ao mesmo Conjunto de Equivalência dado o contexto atual de classes
	 *
	 * @param q1
	 * @param q2
	 * @param classes
	 * @return
	 * @throws FormaisException
	 */
	protected Boolean equivalenteCE(String q1, String q2, List<ClasseEq> classes) throws FormaisException {
		for (Character c : this.getAlfabeto()) {
			ClasseEq t1 = transicaoCE(q1, c, classes);
			ClasseEq t2 = transicaoCE(q2, c, classes);
			if (t1 != t2) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Qubra a classe de equivalência em novas classes de equivalência dado o contexto atual de CEs
	 *
	 * @param classe
	 * @param classes
	 * @return
	 * @throws FormaisException
	 */
	protected List<ClasseEq> splitCE(ClasseEq classe, List<ClasseEq> classes) throws FormaisException {
		List<ClasseEq> nova = new ArrayList<ClasseEq>();

		if (classe.isEmpty()) {
			return nova; // TODO: caso de teste
		}

		nova.add(new ClasseEq(classe.subList(0, 1)));
		for (int j = 1; j < classe.size(); j++) {
			boolean novaClasse = true;
			String q = classe.get(j);
			for (ClasseEq compare : nova) {
				if (equivalenteCE(q, compare.get(0), classes)) {
					compare.add(q);
					novaClasse = false;
					break;
				}
			}

			if (novaClasse) {
				nova.add(new ClasseEq(Arrays.asList(q)));
			}
		}

		return nova;
	}

	/**
	 * Classe de equivalência
	 */
	static class ClasseEq extends ArrayList<String> {
		private static final long serialVersionUID = 8590345019459690523L;

		public ClasseEq() {
		}

		/**
		 * Constrói uma nova classe de equivalência. Usada no processo de minimização
		 *
		 * @param estados
		 */
		public ClasseEq(List<String> estados) {
			super(estados);
		}

		/**
		 * Transforma os nomes dos estados para um nome único da classe de equivalência
		 */
		@Override
		public String toString() {
			Collections.sort(this);
			String nome = "[";
			int i = 0;
			for (String es : this) {
				i++;
				nome += es + (i != this.size() ? ", " : "");
			}
			nome += "]";
			return nome;
		}
	}
}

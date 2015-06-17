package com.bonaguiar.formais1.core.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.automata.AFD;
import com.bonaguiar.formais1.core.exception.FormaisException;
import com.bonaguiar.formais1.core.expr.Simone.Composicao;

/**
 * Expressão regular
 */
public class ExprRegular implements Serializable {
	private static final long serialVersionUID = 2864470087671266395L;

	/**
	 * Expressão propriamente dita
	 */
	@Getter
	public final String expr;

	/**
	 * Árvore sintática da expressão regular, gerada no momento de construção
	 */
	@Getter
	public final Nodo<Character> tree;

	/**
	 * Cria uma nova expressão regular
	 *
	 * @param expr
	 * @throws FormaisException
	 */
	public ExprRegular(String expr) throws FormaisException {
		this.expr = expr;
		this.tree = ERParser.parse(this.expr);
		this.tree.costurar(Simone.FIM_DA_COSTURA);
	}

	/**
	 * Extrai o alfabeto da expressão regular
	 * O alfabeto da ER é o conjunto de caracteres que a expressão pode gerar
	 * Exemplo: O alfabeto da expressão (ab)*|c?d é [a, b, c, d]
	 *
	 * @return
	 */
	public Alfabeto extrairAlfabeto() {
		Alfabeto alfabeto = new Alfabeto();

		for (Character c : expr.toCharArray()) {
			if (ERParser.CARACTERES.contains(c)) {
				alfabeto.add(c);
			}
		}

		return alfabeto;
	}

	/**
	 * Retorna o Autômado Finito Determinístico equivalente a esta expressão regular
	 * O AFD é gerado através do método de Simone e pode ser mínimo ou não
	 *
	 * @return
	 * @throws FormaisException
	 */
	public AFD getAFD() throws FormaisException {
		AFD afd = new AFD(this.extrairAlfabeto());

		Simone.Composicao rootComp = Simone.obterComposicao(this.tree);

		// Mapeia as composições para os estados associados
		HashMap<String, String> hash = new HashMap<String, String>();
		ArrayList<Simone.Composicao> novasComposicoes = new ArrayList<Simone.Composicao>();

		novasComposicoes.add(rootComp);
		afd.addEstado("q0", rootComp.ehFinal());
		afd.setEstadoInicial("q0");
		hash.put(rootComp.toString(), "q0");

		int i = 1;
		while (!novasComposicoes.isEmpty()) {
			Composicao comp = novasComposicoes.remove(0);
			String estadoOrigem = hash.get(comp.toString());

			for (Character c : afd.getAlfabeto()) {
				// Obtém a composição referente à transição com o caracter c
				Composicao transComp = comp.transicao(c);

				if (transComp.isEmpty()) {
					continue;
				}

				String estadoDestino;

				// Checa os nodos alcançados a partir de transComp, se já existir no hash, então
				// é um estado equivalente, senão é um novo estado
				Composicao novaComp = Simone.obterComposicao(transComp);
				if (hash.containsKey(novaComp.toString())) {
					// Composição já existe (estado equivalente)
					estadoDestino = hash.get(novaComp.toString());
				} else {
					// Nova composição = novo estado
					estadoDestino = "q" + (i++);
					afd.addEstado(estadoDestino, novaComp.ehFinal());
					novasComposicoes.add(novaComp);
					hash.put(novaComp.toString(), estadoDestino);
				}

				afd.addTransicao(estadoOrigem, c, estadoDestino);
			}
		}

		return afd;
	}

}

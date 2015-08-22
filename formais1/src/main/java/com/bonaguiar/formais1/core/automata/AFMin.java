package com.bonaguiar.formais1.core.automata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.exception.FormaisException;

/**
 * Autômato Finito Mínimo
 * Apesar de não ser instanciável externamente, implementa métodos específicos para autômatos
 * mínimos, como por exemplo, comparação (com outros AFMin)
 */
public class AFMin extends AFD {

	/**
	 * Cria um novo autômato mínimo
	 * Esse método é interno para o package, ou seja, o usuário não pode criar uma instância desta classe
	 * Ela deve ser gerada através do método de minimização de outras classes AF
	 *
	 * @param alfabeto
	 */
	protected AFMin(Alfabeto alfabeto) {
		super(alfabeto);
	}

	/**
	 * Checa se o Autômato atual é igual ao AFMin passado como parâmetro
	 *
	 * @param o
	 * @return
	 * @throws FormaisException
	 */
	public boolean equals(AFMin o) throws FormaisException {
		if (this.alfabeto.containsAll(o.alfabeto) && o.alfabeto.containsAll(this.alfabeto)) {
			// alfabetos iguais
		} else {
			return false;
		}

		if (this.estados.size() != o.estados.size()) {
			return false;
		}

		if (this.estadosFinais.size() != o.estadosFinais.size()) {
			return false;
		}

		if (this.transicoes.size() != o.transicoes.size()) {
			return false;
		}

		if (this.estadoInicial == null && o.estadoInicial == null) {
			return true;
		}

		// Guarda as equivalências de estado
		// estado deste AF -> estado do outro AF
		HashMap<String, String> eq = new HashMap<String, String>();
		eq.put(this.estadoInicial, o.estadoInicial);

		ArrayList<String> novos = new ArrayList<String>();
		novos.add(this.estadoInicial);

		while (!novos.isEmpty()) {
			String q = novos.remove(0);

			for (Character c : this.alfabeto.sorted()) {
				List<String> t = this.transicao(q, c);
				List<String> to = o.transicao(eq.get(q), c);

				if (t.size() != to.size()) {
					return false;
				}

				if (t.isEmpty() && to.isEmpty()) {
					continue;
				}

				String qd = t.get(0);
				String qdo = to.get(0);
				if (eq.containsKey(qd)) {
					if (!eq.get(qd).equals(qdo)) {
						return false;
					}
				} else {
					eq.put(qd, qdo);
					novos.add(qd);
				}
			}
		}

		// Checar se transições são aquivalente
		return true;
	}
}

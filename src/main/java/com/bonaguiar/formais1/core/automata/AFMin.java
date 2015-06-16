package com.bonaguiar.formais1.core.automata;

import com.bonaguiar.formais1.core.Alfabeto;

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
	 * @param alfabeto
	 */
	protected AFMin(Alfabeto alfabeto) {
		super(alfabeto);
	}

	/**
	 * Checa se o Autômato atual é igual ao AFMin passado como parâmetro
	 * @param o
	 * @return
	 */
	public boolean equals(AFMin o) {
		// TODO
		return super.equals(o);
	}
}

package com.bonaguiar.formais1.core;

import java.util.List;

import com.bonaguiar.formais1.core.exception.FormaisException;

/**
 * Autômato finito determinístico
 * Mesma implementação de AF, mas garante que todas as produções sejam determinísticas
 * @author carlosbonetti
 *
 */
public class AFD extends AF {
	
	/**
	 * Cria um novo autômato finito determinístico
	 * @param alfabeto Alfabeto de aceitação do autômato
	 */
	public AFD(Alfabeto alfabeto) {
		super(alfabeto);
	}
	
	/**
	 * Adiciona uma transição ao autômato.
	 * Deve ser determinística, caso contrária uma FormaisException é lançada
	 * @param estadoPartida Nome do estado de partida (deve pertencer ao autômato)
	 * @param caracter Caracter de transição (deve pertencer ao alfabeto do autômato)
	 * @param estadoChegada Nome do estado de chegada (deve pertencer ao autômato)
	 * @throws FormaisException 
	 */
	@Override
	public void addTransicao(String estadoPartida, Character caracter,
			String estadoChegada) throws FormaisException {
		// Epsolon transições não são permitidas em AF determinísticos
		if (caracter == Alfabeto.EPSOLON) {
			throw new FormaisException("Epsolon transição não é uma transição "
					+ "determinística válida para este AFD");
		}
		
		// Transições ambíguas não são permitidas em AF determinísticos
		List<String> transicoes = this.transicao(estadoPartida, caracter);
		if (!transicoes.isEmpty()) {
			Transicao t = new Transicao(estadoPartida, caracter, estadoChegada);
			Transicao t2 = new Transicao(estadoPartida, caracter, transicoes.get(0));
			throw new FormaisException("A transição " + t.toString()
					+ " não é determinística neste AFD, pois já foi definida"
					+ " como " + t2.toString());
		}
		
		super.addTransicao(estadoPartida, caracter, estadoChegada);
	}
	
}

package com.bonaguiar.formais1.core;

import lombok.AllArgsConstructor;

/**
 * Transição de estado de AF
 */
@AllArgsConstructor
public class Transicao {
	public String estadoOrigem;
	public Character simboloTransicao;
	public String estadoDestino;
	
	public String toString() {
		return "(" + estadoOrigem + ", " + simboloTransicao + ") -> " + estadoDestino;
	}
}
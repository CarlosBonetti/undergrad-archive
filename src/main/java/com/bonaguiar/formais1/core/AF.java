package com.bonaguiar.formais1.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bonaguiar.formais1.core.exception.FormaisException;

import lombok.Getter;

/**
 * Autômato finito
 */
public class AF {
	
	/**
	 * Alfabeto de entrada do autômato
	 */
	@Getter
	protected Alfabeto alfabeto;
	
	/**
	 * Conjunto de estados do autômato finito
	 */
	@Getter
	protected List<String> estados;
	
	/**
	 * Nome do estado inicial do AF
	 */
	@Getter
	protected String estadoInicial;
	
	/**
	 * Lista de estados finais do AF
	 */
	@Getter
	protected List<String> estadosFinais;
	
	/**
	 * Lista de transições do AF
	 */
	@Getter 
	protected List<Transicao> transicoes;
	
	/**
	 * Constroi um novo AF vazio
	 * @param alfabeto Alfabeto de aceitação do AF
	 */
	public AF(Alfabeto alfabeto) {
		this.alfabeto = alfabeto;
		this.estados = new ArrayList<String>();
		this.estadosFinais = new ArrayList<String>();
		this.transicoes = new ArrayList<Transicao>();
	}
	
	/**
	 * Adiciona um estado ao autômato
	 * @param nome Nome do novo estado (deve ser único no contexto do autômato)
	 * @param ehFinal Se o estado é final ou não
	 */
	public void addEstado(String nome, Boolean ehFinal) {
		this.estados.add(nome);
		
		if (ehFinal) {
			this.estadosFinais.add(nome);
		}
	}
	
	/**
	 * Seta o estado inicial do autômato
	 * @param nomeEstado Nome do novo estado inicial (deve pertencer ao autômato)
	 * @throws FormaisException 
	 */
	public void setEstadoInicial(String nomeEstado) throws FormaisException {
		if (!this.estados.contains(nomeEstado)) {
			throw new FormaisException("Estado `" + nomeEstado + "` não pertence ao AF");
		}
		this.estadoInicial = nomeEstado;
	}
	
	/**
	 * Adiciona uma transição ao autômato
	 * @param estadoPartida Nome do estado de partida (deve pertencer ao autômato)
	 * @param caracter Caracter de transição (deve pertencer ao alfabeto do autômato)
	 * @param estadoChegada Nome do estado de chegada (deve pertencer ao autômato)
	 * @throws FormaisException 
	 */
	public void addTransicao(String estadoPartida, Character caracter, String estadoChegada) throws FormaisException {
		if (!this.estados.contains(estadoPartida)) {
			throw new FormaisException("Estado `" + estadoPartida + "` não pertence ao AF");
		}
		if (!this.alfabeto.contains(caracter)) {
			throw new FormaisException("Caracter `" + caracter + "` não pertence ao alfabeto de AF");
		}		
		if (!this.estados.contains(estadoChegada)) {
			throw new FormaisException("Estado `" + estadoChegada + "` não pertence ao AF");
		}		
		
		this.transicoes.add(new Transicao(estadoPartida, caracter, estadoChegada));
	}
	
}

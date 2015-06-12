package com.bonaguiar.formais1.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	 * Nome do estado de erro padrão
	 * Uma transição de um estado qualquer a um estado de erro sempre existe com cada caracter do alfabeto
	 * caso nenhuma outra transição tenha sido definida
	 */
	public static final String ESTADO_ERRO = "qerr";
	
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
	 * Checa se o estado pertence ao autômato
	 * @param estado
	 * @return
	 */
	public Boolean contemEstado(String estado) {
		return this.getEstados().contains(estado);
	}
	
	/**
	 * Seta o estado inicial do autômato
	 * @param nomeEstado Nome do novo estado inicial (deve pertencer ao autômato)
	 * @throws FormaisException 
	 */
	public void setEstadoInicial(String nomeEstado) throws FormaisException {
		if (!this.contemEstado(nomeEstado)) {
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
		if (!this.contemEstado(estadoPartida)) {
			throw new FormaisException("Estado `" + estadoPartida + "` não pertence ao AF");
		}
		if (!this.alfabeto.contains(caracter)) {
			throw new FormaisException("Caracter `" + caracter + "` não pertence ao alfabeto de AF");
		}		
		if (!this.contemEstado(estadoChegada)) {
			throw new FormaisException("Estado `" + estadoChegada + "` não pertence ao AF");
		}		
		// TODO checar se transição já existe?
		this.transicoes.add(new Transicao(estadoPartida, caracter, estadoChegada));
	}
	
	/**
	 * Função de transição
	 * Retorna quais são os novos estados do AF ao consumir o caracter a partir do estadoOrigem
	 * Lança uma exception se o estado ou o símbolo passados como argumentos não pertençam ao AF
	 * @param estadoOrigem
	 * @param caracter
	 * @return Lista de estados alcançáveis
	 * @throws FormaisException 
	 */
	public List<String> transicao(String estadoOrigem, Character caracter) throws FormaisException {
		if (!this.contemEstado(estadoOrigem)) {
			throw new FormaisException("Estado `" + estadoOrigem + "` não pertence ao AF");
		} 
		if (!this.alfabeto.contains(caracter)) {
			throw new FormaisException("Estado `" + estadoOrigem + "` não pertence ao AF");
		} 
		
		List<String> estados = new ArrayList<String>();
		for(Transicao t : this.transicoes) {
			if (t.estadoOrigem.equals(estadoOrigem) && t.simboloTransicao.equals(caracter)) {
				estados.add(t.estadoDestino);
			}
		}
		return estados;
	}
	
	/**
	 * Checa se o estado é final
	 * Lança uma exception caso o estado parâmetro não pertença ao AF
	 * @param estado
	 * @return
	 * @throws FormaisException 
	 */
	public Boolean ehFinal(String estado) throws FormaisException {
		if (!this.contemEstado(estado)) {
			throw new FormaisException("Estado `" + estado + "` não pertence ao AF");
		}
		return this.estadosFinais.contains(estado);
	}
	
	/**
	 * Retorna o conjunto de estados alcançáveis deste AF
	 * Estados alcancáveis são aqueles que podem ser acessados através de transições a partir do estado inicial
	 * @return
	 * @throws FormaisException
	 */
	public Set<String> getEstadosAlcancaveis() throws FormaisException {
		Set<String> alc = new HashSet<String>();
		List<String> check = new ArrayList<String>();
		
		if (this.getEstadoInicial() != null) {
			check.add(this.getEstadoInicial());
		}
		
		while(!check.isEmpty()) {
			String estado = check.remove(0);
			alc.add(estado);
			
			for (Character c : this.getAlfabeto()) {
				for (String novoEstado : this.transicao(estado, c)) {
					if (!check.contains(novoEstado) && !alc.contains(novoEstado)) {
						check.add(novoEstado);
					}
				}
			}
		}		
		
		return alc;
	}
	
	/**
	 * Retorna o conjunto de estados vivos deste AF
	 * Estados vivos são aqueles que podem levar a um estado final através de transições deste AF
	 * @return
	 */
	public Set<String> getEstadosVivos() {
		Set<String> vivos = new HashSet<String>(); // Guarda os estados vivos alcançados

		Set<String> novos = new HashSet<String>(); // Guarda os novos estados vivos que devem ser considerados na próxima iteração
		novos.addAll(this.getEstadosFinais()); // Estados finais são vivos por definição
		
		while(!novos.isEmpty()) {
			vivos.addAll(novos);
			
			for(String estado : new HashSet<String>(novos)) {
				novos.remove(estado);
				for (Transicao t : this.getTransicoes()) {
					if (t.estadoDestino.equals(estado) && !vivos.contains(t.estadoOrigem)) {
						novos.add(t.estadoOrigem);
					}
				}
			}
		}
		
		return vivos;
	}
	
	/**
	 * Retorna o complemento deste AF
	 * @return
	 * @throws FormaisException 
	 */
	public AF getComplemento() throws FormaisException {
		AF comp = new AF(this.alfabeto); // Alfabeto do complemento é o mesmo
		comp.estados.addAll(this.getEstados()); // Conjunto de estados do complemento é o mesmo
		comp.setEstadoInicial(this.getEstadoInicial()); // Estado inicial do complemento é o mesmo
		comp.transicoes.addAll(this.getTransicoes()); // Transições do complemento são as mesmas
		
		// Completamos o autômato
		// Cada transição não definida levará um estado de erro
		// TODO: se for criada transição ao estado de erro e posteriormente uma transição for definida		
		for(String estado : this.getEstados()) {
			for(Character caracter : this.getAlfabeto()) {
				if (this.transicao(estado, caracter).isEmpty()) {
					// Se ainda não possuir ESTADO_ERRO, adiciona como estado final:
					if (!comp.contemEstado(ESTADO_ERRO)) {
						comp.addEstado(ESTADO_ERRO, false);
						comp.estadosFinais.add(ESTADO_ERRO); // Estado de erro sempre é final no complemento
					}
					
					comp.addTransicao(estado, caracter, ESTADO_ERRO);
				}
			}
		}
		
		// Estados finais se tornam não-finais. Estados não-finais se tornam finais
		for (String estado : this.getEstados()) {
			if (!this.ehFinal(estado)) {
				comp.estadosFinais.add(estado);
			}
		}
		
		return comp;
	}
		
	// =====================================================================================
	// Determinização + helper methods
	
	/**
	 * Retorna uma versão do AF determinizada, ou seja, sem transição não-determinísticas
	 * @return
	 * @throws FormaisException 
	 */
	public AFD determinizar() throws FormaisException {
		AFD afd = new AFD(this.alfabeto);
		
		String estadoInicial = this.getEstadoInicial();		
		if (estadoInicial == null) {
			throw new FormaisException("AF deve ter um estado inicial para determinizá-lo");
		}
		AF.MesclaDeEstados mesclaInicial = new AF.MesclaDeEstados(this.getEstadoInicial());
		String novoEstadoInicial = mesclaInicial.toString();
		afd.addEstado(novoEstadoInicial, mesclaInicial.ehFinal());
		afd.setEstadoInicial(novoEstadoInicial);		
		
		List<AF.MesclaDeEstados> novasMesclas = new ArrayList<AF.MesclaDeEstados>();
		novasMesclas.add(mesclaInicial);
		
		while(!novasMesclas.isEmpty()) {
			AF.MesclaDeEstados mesclaAtual = novasMesclas.remove(0);
			
			for (Character simbolo : this.getAlfabeto()) {
				AF.MesclaDeEstados mesclaDestino = mesclaAtual.transicao(simbolo);
				String novoDestino = mesclaDestino.toString();
				
				if (!afd.contemEstado(novoDestino)) {
					afd.addEstado(novoDestino, mesclaDestino.ehFinal());
					novasMesclas.add(mesclaDestino);
				}
				
				afd.addTransicao(mesclaAtual.toString(), simbolo, novoDestino);
			}
		}
		
		return afd;
	}
	
	/**
	 * Estrutura que representa uma mescla de estados, usado no processo de determinização 
	 * Uma mescla de estados geralmente é representada por '[q0, q3, q4]', por exemplo, e
	 * se trata da união dos respectivos estados, durante o processo de determinização
	 */
	protected class MesclaDeEstados {
		@Getter
		protected List<String> estados;
		
		public MesclaDeEstados(List<String> estados) {
			this.estados = estados;
			Collections.sort(this.estados);
		}
		
		public MesclaDeEstados(String estado) {
			this(Arrays.asList(estado));
		}
		
		/**
		 * Transforma os nomes dos estados para o padrão 'mesclado'
		 * Exemplo: transforma 'q0' para '[q0]', transforma {q5, q1, A, q0} para '[A, q0, q1, q5]'
		 * @return
		 */
		public String toString() {
			String nome = "[";
			int i = 0;
			for(String es : estados) {
				i++;
				nome += es + (i != estados.size() ? ", " : "");
			}		
			nome += "]";
			return nome;
		}
		
		/**
		 * Retorna a mescla de estados resultante da união de todas as transições da mescla de estados atual 
		 * dado o caracter de transição.
		 * Lança uma exception se o estado ou o símbolo passados como argumentos não pertençam ao AF
		 * @param caracter
		 * @return
		 * @throws FormaisException 
		 */
		public MesclaDeEstados transicao(Character caracter) throws FormaisException {
			// Adicionamos a um set para não contar estados duplicados
			Set<String> destinos = new HashSet<String>();			
			for(String estado : this.estados) {
				destinos.addAll(AF.this.transicao(estado, caracter));
			}			
			
			// Convertemos de volta para list para criar a MesclaDeEstados
			List<String> destinosList = new ArrayList<String>();
			destinosList.addAll(destinos);
			return AF.this.new MesclaDeEstados(destinosList);
		}
		
		/**
		 * Checa se a mescla de estados é final.
		 * Uma mescla é final se qualquer um de seus estados originais for final
		 * @return
		 * @throws FormaisException 
		 */
		public Boolean ehFinal() throws FormaisException {
			for (String estado : this.getEstados()) {
				if (AF.this.ehFinal(estado)) {
					return true;
				}
			}
			return false;
		}
	}
	
}

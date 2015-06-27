package com.bonaguiar.formais1.core.automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.exception.FormaisException;

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
	 *
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
	 *
	 * @param nome Nome do novo estado (deve ser único no contexto do autômato)
	 * @param ehFinal Se o estado é final ou não
	 * @throws FormaisException
	 */
	public void addEstado(String nome, Boolean ehFinal) throws FormaisException {
		if (this.contemEstado(nome)) {
			throw new FormaisException("Estado '" + nome + "' já existe no autômato");
		}

		this.estados.add(nome);

		if (ehFinal) {
			this.estadosFinais.add(nome);
		}
	}

	/**
	 * Checa se o estado pertence ao autômato
	 *
	 * @param estado
	 * @return
	 */
	public Boolean contemEstado(String estado) {
		return this.getEstados().contains(estado);
	}

	/**
	 * Seta o estado inicial do autômato
	 *
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
	 *
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
	 *
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
		for (Transicao t : this.transicoes) {
			if (t.estadoOrigem.equals(estadoOrigem) && t.simboloTransicao.equals(caracter)) {
				estados.add(t.estadoDestino);
			}
		}
		return estados;
	}

	/**
	 * Checa se o estado é final
	 * Lança uma exception caso o estado parâmetro não pertença ao AF
	 *
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
	 *
	 * @return
	 * @throws FormaisException
	 */
	public Set<String> getEstadosAlcancaveis() throws FormaisException {
		Set<String> alc = new HashSet<String>();
		List<String> check = new ArrayList<String>();

		if (this.getEstadoInicial() != null) {
			check.add(this.getEstadoInicial());
		}

		while (!check.isEmpty()) {
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
	 *
	 * @return
	 */
	public Set<String> getEstadosVivos() {
		Set<String> vivos = new HashSet<String>(); // Guarda os estados vivos alcançados

		Set<String> novos = new HashSet<String>(); // Guarda os novos estados vivos que devem ser considerados na próxima iteração
		novos.addAll(this.getEstadosFinais()); // Estados finais são vivos por definição

		while (!novos.isEmpty()) {
			vivos.addAll(novos);

			for (String estado : new HashSet<String>(novos)) {
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
	 *
	 * @return
	 * @throws FormaisException
	 */
	public AF getComplemento() throws FormaisException {
		// TODO tornar complemento uma opção de AFD somente
		AF comp = new AF(this.alfabeto); // Alfabeto do complemento é o mesmo
		comp.estados.addAll(this.getEstados()); // Conjunto de estados do complemento é o mesmo
		comp.setEstadoInicial(this.getEstadoInicial()); // Estado inicial do complemento é o mesmo
		comp.transicoes.addAll(this.getTransicoes()); // Transições do complemento são as mesmas

		// Completamos o autômato
		// Cada transição não definida levará um estado de erro
		// TODO: se for criada transição ao estado de erro e posteriormente uma transição for definida
		for (String estado : this.getEstados()) {
			for (Character caracter : this.getAlfabeto()) {
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

	/**
	 * Retorna um novo autômato que é a intersecção do AF atual com o parâmetro
	 *
	 * @param af2
	 * @return
	 * @throws FormaisException
	 */
	public AF intersectar(AF af2) throws FormaisException {
		if (!this.alfabeto.equals(af2.alfabeto)) {
			throw new FormaisException("Alfabeto dos AF devem ser iguais para intersecção. Alfabeto 1: " + this.alfabeto.toString() + ". Alfabeto 2: " + af2.alfabeto.toString());
		}

		AF novo = new AF(this.alfabeto);

		// Estados da intersecção é produto cartesiano dos estados
		for (String estado1 : this.getEstados()) {
			for (String estado2 : af2.getEstados()) {
				novo.addEstado("[" + estado1 + ", " + estado2 + "]", false);
			}
		}

		// Estados finais da intersecção é o produto cartesiano dos estados finais originais
		for (String estado1 : this.getEstadosFinais()) {
			for (String estado2 : af2.getEstadosFinais()) {
				novo.estadosFinais.add("[" + estado1 + ", " + estado2 + "]");
			}
		}

		// TODO: checar se estado inicial existe?

		// Estado inicial da intersecção é a combinação dos estados iniciais originais
		novo.setEstadoInicial("[" + this.getEstadoInicial() + ", " + af2.getEstadoInicial() + "]");

		// Transições devem ser da forma ([q0, q1], a) -> ((q0, a), (q1, a))
		// TODO: testar transições não definidas
		for (Transicao t1 : this.getTransicoes()) {
			for (Transicao t2 : af2.getTransicoes()) {
				if (t1.simboloTransicao.equals(t2.simboloTransicao)) {
					novo.addTransicao("[" + t1.estadoOrigem + ", " + t2.estadoOrigem + "]", t1.simboloTransicao, "[" + t1.estadoDestino + ", " + t2.estadoDestino + "]");
				}
			}
		}

		return novo;
	}

	// =====================================================================================
	// Determinização + helper methods

	/**
	 * Retorna uma versão do AF determinizada, ou seja, sem transição não-determinísticas
	 *
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

		while (!novasMesclas.isEmpty()) {
			AF.MesclaDeEstados mesclaAtual = novasMesclas.remove(0);

			for (Character simbolo : this.getAlfabeto()) {
				AF.MesclaDeEstados mesclaDestino = mesclaAtual.transicao(simbolo);

				// Se for transição vazia, desconsidera
				if (mesclaDestino.getEstados().isEmpty()) {
					// TODO: teste unitário para este caso
					continue;
				}

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
		 *
		 * @return
		 */
		@Override
		public String toString() {
			String nome = "[";
			int i = 0;
			for (String es : estados) {
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
		 *
		 * @param caracter
		 * @return
		 * @throws FormaisException
		 */
		public MesclaDeEstados transicao(Character caracter) throws FormaisException {
			// Adicionamos a um set para não contar estados duplicados
			Set<String> destinos = new HashSet<String>();
			for (String estado : this.estados) {
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
		 *
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

	/**
	 * Consome a palavra e retorna se ela foi aceita pelo autômato ou não
	 * Uma palavra é aceita se AF se encontrar em um estado final após a palvra ser completamente consumida
	 *
	 * @param palavra
	 * @return
	 * @throws FormaisException
	 */
	public Boolean run(String palavra) throws FormaisException {
		return this.run(palavra, this.getEstadoInicial());
	}

	/**
	 * Consome a palavra e retorna se ela foi aceita pelo autômato ou não, partindo do estado inicial passado
	 * como parâmetro
	 * Uma palavra é aceita se AF se encontrar em um estado final após a palvra ser completamente consumida
	 *
	 * @param palavra
	 * @return
	 * @throws FormaisException
	 */
	protected Boolean run(String palavra, String estadoInicial) throws FormaisException {
		String estadoAtual = estadoInicial;
		String resto = palavra; // Guarda a parte da palavra que ainda não foi consumida

		if (palavra == null) {
			palavra = "";
		}

		for (Character c : palavra.toCharArray()) {
			resto = resto.substring(1);

			List<String> destinos = this.transicao(estadoAtual, c);
			// Se não houver transição, a palavra é rejeitada
			if (destinos.isEmpty()) {
				return false;
			}

			// Se só houver uma transição, siga-a
			if (destinos.size() == 1) {
				estadoAtual = destinos.get(0);
			} else {
				// Se houver mais de uma transição, segue todas de forma não determinística
				for (String q : destinos) {
					// Se algum caminho retornar verdadeiro, a palavra é aceita
					if (this.run(resto, q)) {
						return true;
					}
				}
				// Se chegar neste ponto, nenhum caminho retornou verdadeiro, e podemos rejeitar a palavra
				return false;
			}
		}

		// Se o estado final for de aceitação, a palavra é aceita pelo AF
		return this.ehFinal(estadoAtual);
	}

	/**
	 * Procura por subexpressões do texto aceitas por este AF
	 * Retorna um hash com os índices de início de subexpressões que deram match como chave e o tamanho do match como valor
	 *
	 * @param text
	 * @return
	 * @throws FormaisException
	 */
	public HashMap<Integer, Integer> textSearch(String text) throws FormaisException {
		HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();

		String estadoAtual = this.getEstadoInicial();
		int lastPos = 0; // Posição de início do último possível match
		int tamanho = 0; // Tamanho atual do possível match

		for (int i = 0; i < text.length(); ) {
			
			List<String> destinos = null;
			try {
				char a = text.charAt(i);
				destinos = this.transicao(estadoAtual, text.charAt(i));
			} catch (FormaisException e) {
				lastPos = i;
				tamanho = 0;
				if (estadoAtual == this.getEstadoInicial()) {
					lastPos = ++i;
				}
				estadoAtual = this.getEstadoInicial();
				continue;
			}

			if (destinos.isEmpty()) {
				lastPos = i;
				tamanho = 0;
				if (estadoAtual == this.getEstadoInicial()) {
					lastPos = ++i;
				}
				estadoAtual = this.getEstadoInicial();
				continue;
			} else {
				estadoAtual = destinos.get(0);
				tamanho++;

				if (this.ehFinal(estadoAtual)) {
					hash.put(lastPos, tamanho);
				}
			}
			for (String string : destinos) {
				System.out.println(string);
			}
			i++;
		}

		return hash;
	}
	
	public void testeBusca(String texto){
		for (Character character : alfabeto) {
			
		}
	}
}

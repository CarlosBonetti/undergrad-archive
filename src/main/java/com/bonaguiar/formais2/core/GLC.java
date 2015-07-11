package com.bonaguiar.formais2.core;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

/**
 * Gramática Livre de Contexto
 */
public class GLC implements Serializable {
	private static final long serialVersionUID = 785464899552263860L;

	/**
	 * Produções da gramática
	 */
	@Getter
	protected Map<String, List<FormaSentencial>> producoes = new LinkedHashMap<String, List<FormaSentencial>>();

	/**
	 * Símbolo inicial da gramática
	 */
	@Getter
	protected String simboloInicial;

	/**
	 * Salva os símbolos não terminais da gramática
	 */
	protected Set<String> naoTerminais = new HashSet<String>();

	/**
	 * Salva os símbolos terminais da gramática
	 */
	protected Set<String> terminais = new HashSet<String>();

	/**
	 * A String com o conjunto de produções original que deu origem a esta
	 * gramática
	 */
	@Getter
	protected String raw;

	/**
	 * Guarda o conjunto first dos símbolos não terminais desta gramática em um
	 * hash do tipo 'símbolo não terminal => conjunto first'
	 */
	protected Map<String, Set<String>> firstSet = new LinkedHashMap<String, Set<String>>();

	/**
	 * Guarda o conjunto follow dos símbolos não terminais desta gramática
	 */
	protected Map<String, Set<String>> followSet = new LinkedHashMap<String, Set<String>>();

	/**
	 * Guarda uma lista com cópias das produções da gramática com índices
	 * correspondentes
	 */
	protected ArrayList<FormaSentencial> lista = new ArrayList<FormaSentencial>();

	/**
	 * Cria uma nova Gramática Livre de Contexto baseada no conjunto de
	 * produções A primeira produção fornecida é considerada símbolo inicial
	 *
	 * @param producoes
	 *            Conjunto de produções. Cada produção deve estar em uma linha.
	 *            Exemplo: E -> T E1 E1 -> + T E1 | & T -> F T1 T1 -> * F T1 | &
	 *            F -> ( E ) | id
	 * @throws ParseException
	 */
	public GLC(String producoes) throws ParseException {
		this.raw = producoes;

		String[] lines = producoes.trim().split("\n|\r\n");
		for (String line : lines) {
			this.addProducoes(line);
		}

		this.simboloInicial = this.producoes.keySet().iterator().next();
	}

	/**
	 * Construtor vazio. Usado pelos testes unitários para
	 * "ir criando a gramática aos poucos". Protegido para não ser usado
	 * externamente
	 */
	protected GLC() {
	}

	/**
	 * Adiciona um novo conjunto de produções à gramática.
	 *
	 * @param line
	 *            Uma linha do conjunto de produções da gramática. Exemplo: 'E
	 *            -> E + T | E - T | T' irá adicionar três novas produções
	 *            associadas ao não terminal 'E'
	 * @throws ParseException
	 */
	protected void addProducoes(String line) throws ParseException {
		String[] parts = line.split("->");

		if (parts.length != 2) {
			throw new ParseException(
					"Produção mal formada: "
							+ line
							+ ". Produções devem seguir o padrao 'E -> E + T | .. | id'",
					0);
		}

		String produtor = parts[0].trim();
		String[] producoes = parts[1].trim().split("\\|");

		for (String producao : producoes) {
			this.addProducao(produtor, producao);
		}
	}

	/**
	 * Adiciona uma nova produção à gramática
	 *
	 * @param produtor
	 *            Lado esquerdo da produção. 'S' do exemplo: S -> ab A B
	 * @param producao
	 *            Produção. Lado direito da produção. 'ab A B' do exemplo
	 *            anterior
	 */
	protected void addProducao(String produtor, String producao) {
		this.addProducao(produtor, new FormaSentencial(producao.trim()));
	}

	/**
	 * Adiciona uma nova produção à gramática
	 *
	 * @param produtor
	 *            Lado esquerdo da produção. 'S' do exemplo: S -> ab A B
	 * @param formaSentencial
	 *            Produção. Lado direito da produção. 'ab A B' do exemplo
	 *            anterior
	 */
	protected void addProducao(String produtor, FormaSentencial formaSentencial) {
		List<FormaSentencial> lista;

		if (producoes.containsKey(produtor)) {
			lista = producoes.get(produtor);
		} else {
			lista = new ArrayList<FormaSentencial>();
			producoes.put(produtor, lista);
		}

		lista.add(formaSentencial);
	}

	/**
	 * Retorna um lista ordenada com as produções da gramática, de forma que
	 * cada produção possui um índice correspondente
	 *
	 * @return
	 */
	public List<FormaSentencial> getListaProducoes() {
		if (this.lista.isEmpty()) {
			for (String produtor : this.producoes.keySet()) {
				lista.addAll(this.producoes.get(produtor));
			}
		}

		return lista;
	}

	/**
	 * Retorna o conjunto de símbolos não terminais da gramática
	 *
	 * @return
	 */
	public Set<String> getNaoTerminais() {
		if (this.naoTerminais.isEmpty()) {
			this.naoTerminais.addAll(this.producoes.keySet());
		}

		return this.naoTerminais;
	}

	/**
	 * Retorna o conjunto de símbolos terminais da gramática
	 *
	 * @return
	 */
	public Set<String> getTerminais() {
		if (this.terminais.isEmpty()) {
			for (FormaSentencial producao : this.getListaProducoes()) {
				for (String simbolo : producao) {
					if (GrammarUtils.ehTerminal(simbolo)) {
						this.terminais.add(simbolo);
					}
				}
			}
			// Remove o epsilon
			this.terminais.remove(GrammarUtils.EPSILON.toString());
		}

		return this.terminais;
	}

	/**
	 * Retorna as produções do símbolo não terminal
	 *
	 * @param simbolo
	 * @return
	 */
	public List<FormaSentencial> getProducoes(String simbolo) {
		if (!this.getNaoTerminais().contains(simbolo)) {
			throw new IllegalArgumentException("Símbolo '" + simbolo + "' não pertence ao conjunto de não terminais da gramática");
		}

		return this.producoes.get(simbolo);
	}

	// ===================================================================================================
	// First

	/**
	 * Retorna um hash com todos os conjuntos 'first' da gramática
	 * O hash retornado possui os símbolos não terminais da gramática como
	 * chave e um conjunto de símbolos first associados a este não terminal
	 *
	 * @return
	 */
	public Map<String, Set<String>> getFirstSet() {
		for (String naoTerminal : this.producoes.keySet()) {
			if (!this.firstSet.containsKey(naoTerminal)) {
				this.firstSet.put(naoTerminal, first(naoTerminal));
			}
		}

		return this.firstSet;
	}

	/**
	 * Retorna o firstSet da forma sentencial parâmetro
	 *
	 * @param formaSentencial
	 * @return
	 */
	protected Set<String> first(FormaSentencial formaSentencial) {
		Set<String> set = new HashSet<String>();

		for (String simbolo : formaSentencial) {
			set.remove(GrammarUtils.EPSILON.toString());
			Set<String> f = first(simbolo);
			set.addAll(f);

			if (!f.contains(GrammarUtils.EPSILON.toString())) {
				break;
			}
		}

		return set;
	}

	/**
	 * Retorna o firstSet do símbolo
	 *
	 * @param simbolo
	 * @return
	 */
	protected Set<String> first(String simbolo) {
		Set<String> set = new HashSet<String>();

		if (GrammarUtils.ehTerminal(simbolo)) {
			// First de um terminal é o próprio terminal
			set.add(simbolo);
			return set;
		}

		if (this.firstSet.containsKey(simbolo)) {
			// Se for um símbolo não terminal já calculado, simplesmente
			// retorna o conjunto previamente criado
			set.addAll(this.firstSet.get(simbolo));
		} else {
			// Seta o firstSet do não-terminal para um conjunto vazio, indicando que ele está sendo calculado
			this.firstSet.put(simbolo, set);

			// Calcula o first de cada produção
			for (FormaSentencial formaSentencial : this.producoes.get(simbolo)) {
				// Se produção começar com o próprio símbolo, é uma recursão à esquerda
				// Nesse caso, ignoramos esta produção. Exemplo: E -> E + T
				if (formaSentencial.get(0).equals(simbolo)) {
					continue;
				}

				set.addAll(first(formaSentencial));
			}

			// Salva o firstSet recém calculado do terminal para evitar
			// retrabalho
			this.firstSet.put(simbolo, set);
		}

		return set;
	}

	// ===================================================================================================
	// Follow

	/**
	 * Retorna um hash com os conjuntos follow de cada não terminal da gramática
	 *
	 * @return
	 */
	public Map<String, Set<String>> getFollowSet() {
		// Só calculamos o follow na primeira chamada
		if (this.followSet.isEmpty()) {

			// 1 – Se A é o símbolo inicial da gramática
			// $ ∈ Follow(A)
			for (String naoTerminal : this.producoes.keySet()) {
				this.followSet.put(naoTerminal, new HashSet<String>());

				if (this.getSimboloInicial().equals(naoTerminal)) {
					this.followSet.get(naoTerminal).add(
							GrammarUtils.END_OF_SENTENCE.toString());
				}
			}

			// 2 – Se A -> αBβ ∈ P ∧ β ≠ ε
			// adicione first(β) em Follow(B)
			for (FormaSentencial producao : this.getListaProducoes()) {
				for (int i = producao.size() - 2; i >= 0; i--) {
					String B = producao.get(i);
					if (!GrammarUtils.ehNaoTerminal(B)) {
						continue;
					}

					FormaSentencial Beta = new FormaSentencial();
					Beta.addAll(producao.subList(i + 1, producao.size()));
					Set<String> firstBeta = first(Beta);
					firstBeta.remove(GrammarUtils.EPSILON.toString());
					this.followSet.get(B).addAll(firstBeta);
				}
			}

			// 3 – Se A -> αB (ou A -> αBβ, onde ε ∈ First(β)) ∈ P
			// -> adicione Follow(A) em Follow(B)
			boolean modificado = true;
			while (modificado) {
				modificado = false;
				for (String produtor : this.producoes.keySet()) {
					for (FormaSentencial producao : this.producoes
							.get(produtor)) {
						for (int i = producao.size() - 1; i >= 0; i--) {
							String B = producao.get(i);
							if (!GrammarUtils.ehNaoTerminal(B)) {
								continue;
							}
							FormaSentencial Beta = new FormaSentencial();
							Beta.addAll(producao.subList(i + 1, producao.size()));
							Set<String> firstBeta = first(Beta);

							if (firstBeta.contains(GrammarUtils.EPSILON
									.toString()) || firstBeta.isEmpty()) {
								modificado = this.followSet.get(B).addAll(
										this.followSet.get(produtor))
										|| modificado;
							}
						}
					}
				}
			}
		}

		return this.followSet;
	}

	/**
	 * Retorna o conjunto follow do não terminal parâmetro
	 *
	 * @param simbolo
	 * @return
	 */
	protected Set<String> follow(String simbolo) {
		return this.getFollowSet().get(simbolo);
	}

	// ===================================================================================================
	// Conflitos first/follow (terceira condição)

	/**
	 * Retorna o conjunto de símbolos não terminais da gramática que possuem conflitos first/follow
	 * Também conhecido por "terceira condição da forma LL"
	 * Um não terminal A possui conflito first/follow se A ⇒* ε e First(A) ∩ Follow(A) = ϕ
	 *
	 * @return
	 */
	public Set<String> getConflitosFF() {
		Set<String> conflitos = new HashSet<String>();
		for (String nt : this.getNaoTerminais()) {
			Set<String> first = this.first(nt);
			if (first.contains(GrammarUtils.EPSILON.toString())) {
				Set<String> follow = this.follow(nt);
				Set<String> intersection = new HashSet<String>(first);
				intersection.removeAll(follow);
				if (!first.isEmpty()) { // Intersecção não vazia = conflito
					conflitos.add(nt);
				}
			}
		}
		return conflitos;
	}

	// ===================================================================================================

	/**
	 * Forma sentencial de uma gramática livre de contexto Representa o lado
	 * direito de uma produção. Exemplo: em 'S -> a B C | abc Ce Fe', existem
	 * dois objetos FormaSentencial, 'a B C' e 'abc Ce Fe' com três partes cada
	 * um (um terminal e dois não terminais)
	 */
	public static class FormaSentencial extends ArrayList<String> {
		private static final long serialVersionUID = -2032770137692974596L;

		/**
		 * Cria uma nova forma sentencial para gramáticas livres de contexto
		 *
		 * @param producao
		 *            Lado direito de uma produção, com as partes separadas por
		 *            espaço. Exemplo: 'a T1 T2'
		 */
		public FormaSentencial(String producao) {
			if (producao.isEmpty()) {
				throw new IllegalArgumentException(
						"Produção não pode ser vazia");
			}

			String[] parts = producao.split(" ");
			for (String part : parts) {
				this.add(part);
			}
		}

		public FormaSentencial() {
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (String part : this) {
				builder.append(part);
				builder.append(" ");
			}
			return builder.toString().trim();
		}
	}

	// ===================================================================================================

	/**
	 * Identificar se a GLC possui recursão a esquerda, qual seu tipo e quais
	 * ñ-terminais são estes
	 *
	 */

	/**
	 * Retorna uma lista com os ñ-teminais que possuem recursão a esquerda
	 * direta
	 *
	 * @return Set<String> ñ-terminais
	 */
	public Set<String> getRecursaoEsquerdaDireta() {
		Set<String> recEsqDireta = new HashSet<String>();
		for (String chave : producoes.keySet()) {
			if (temRecursaoEsquerdaDireta(chave)) {
				recEsqDireta.add(chave);
			}
		}
		return recEsqDireta;
	}

	/**
	 * Verifica se a produção possui recursão esquerda direta
	 *
	 * @param producao
	 * @return
	 */
	private boolean temRecursaoEsquerdaDireta(String producao) {
		for (FormaSentencial forma : producoes.get(producao)) {
			if (producao.equals(forma.get(0))) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Retorna uma lista com os ñ-teminais que possuem recursão a esquerda
	 * indireta
	 * @return Set<String> ñ-terminais
	 */
	public Set<String> getRecursaoEsquerdaIndireta() {
		Set<String> recEsqIndireta = new HashSet<String>();
		for (String chave : producoes.keySet()) {
			if (temRecursaoEsquerdaIndireta(chave)) {
				recEsqIndireta.add(chave);
			}
		}
		return recEsqIndireta;
	}
	
	/**
	 * Metodo faz uma busca recursiva para encontrar se alguma producao deriva a chave de entrada  
	 * @param chave producao inicial a ser analisado se possue recursão a esquerda
	 * @param producao	producao que é derivada da producão inicial direta ou indiretamente
	 * @return boolean
	 */
	private boolean producaoIniciaCom(String chave, String producao){
		try{
			for (FormaSentencial forma : getProducoes().get(producao)) {
				for (String simbolo: forma) {
					if (GrammarUtils.ehNaoTerminal(simbolo)) {
						if (simbolo.equals(chave)) {
							return true;
						}
						if (producaoIniciaCom(chave, simbolo)) {
							return true;
						}
						
						//caso o nao-terminal derive EPSILON continua analisando mesma formaSentencial
						//caso contrario pula para próxima formaSentencial
						if (!getFirstSet().get(simbolo).contains(GrammarUtils.EPSILON.toString())) {
							break;
						}
					}else break;
				}
			}
		} catch (StackOverflowError e){
//			System.err.println(chave + " <chave - producao> " + producao + "\n");
			return false;
		}
		return false;
	}
	
	/**
	 * Retorna um verdade se encontrar alguma derivação do simbolo analisado
	 * @param producao Simbolo não terminal a ser aalisado
	 * @return
	 * @throws ParseException
	 */
	private boolean temRecursaoEsquerdaIndireta(String producao)  {
		for (FormaSentencial forma : getProducoes().get(producao)) {
			for (String simbolo: forma) {
				if (GrammarUtils.ehNaoTerminal(simbolo)) {
					if (forma.get(0).equals(producao) && forma.get(0).equals(simbolo) ) {
						continue;
					}
					if (producaoIniciaCom(producao, simbolo)) {
						return true;
					}
					
					//caso o nao-terminal derive EPSILON continua analisando mesma formaSentencial
					//caso contrario pula para próxima formaSentencial
					if (!getFirstSet().get(simbolo).contains(GrammarUtils.EPSILON.toString())) {
						break;
					}
				}else break;
			}
		}
		return false;
	}
	
	// ===================================================================================================

	/**
	 * Identificar se a GLC esta fatorada , qual seu tipo e quais
	 * ñ-terminais são estes
	 *
	 */

	/**
	 * Retorna uma lista com os ñ-terminais que possuem ñ-determinismo direto(os ñ fatorados)
	 */
	public Set<String> getFatoracaoDireta() {
		Set<String> naoFatoradaDireta = new HashSet<String>();
		for (String chave : producoes.keySet()) {
			if (temNaoDeterminismoDireto(chave)) {
				naoFatoradaDireta.add(chave);
			}
		}
		return naoFatoradaDireta;
	}

	/**
	 * verifica se a produção não esta fatorado diretamente
	 */
	protected boolean temNaoDeterminismoDireto(String producao) {
		Set<String> terminaisDerivados = new HashSet<String>();
		for (FormaSentencial forma : producoes.get(producao)) {
			// Eh necessario ser nao terminal ??????
			if (GrammarUtils.ehTerminal(forma.get(0))) {

				// ao add em Set caso o terminal jah exista retorna false
				if (!terminaisDerivados.add(forma.get(0))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Retorna uma lista com os ñ-terminais que possuem ñ_determinismo indireto
	 *
	 * @return
	 */
	public Set<String> getFatoracaoIndireta() {
		Set<String> naoFatoradaDireta = new HashSet<String>();
		for (String chave : producoes.keySet()) {
			try {
				if (temNaoDeterminismoIndireta(chave, getProducoesDerivadas(chave, new HashSet<String>()))) {
					naoFatoradaDireta.add(chave);
				}
			} catch (Exception e) {
				// captura excecao gerado pelo getProducoesDerivadas() -
				naoFatoradaDireta.add(chave);
				// System.err.println(e.getMessage());
				// return naoFatoradaDireta;
			}
		}
		return naoFatoradaDireta;
	}

	/**
	 * Retorna uma lista com os ñ-terminais derivados de uma produção
	 *
	 * @param producao
	 * @param naoTerminaisDerivados
	 * @return
	 * @throws Exception
	 */
	protected Set<String> getProducoesDerivadas(String producao, Set<String> naoTerminaisDerivados) throws Exception {
		// hash auxiliar de não-terminais
		Set<String> aux = new HashSet<String>();
		for (FormaSentencial forma : producoes.get(producao)) {
			if (GrammarUtils.ehNaoTerminal(forma.get(0))) {
				aux.add(forma.get(0));
			}
		}

		for (String string : aux) {
			if (naoTerminaisDerivados.isEmpty()) {
				naoTerminaisDerivados.addAll(getProducoesDerivadas(string, aux));
			} else
				// caso derive um mesmo terminal por outro caminho gera exceção
				if (naoTerminaisDerivados.contains(string)) {
					throw new Exception();
				}

				else if (!naoTerminaisDerivados.contains(string)) {
					naoTerminaisDerivados.addAll(getProducoesDerivadas(string, naoTerminaisDerivados));
				}
		}
		return naoTerminaisDerivados;
	}

	/**
	 * Verificação se uma produção e suas derivadas derivam o mesmo terminal
	 *
	 * @param producao
	 * @param producoesDerivadas
	 * @return
	 */
	protected boolean temNaoDeterminismoIndireta(String producao, Set<String> producoesDerivadas) {

		for (FormaSentencial forma : this.producoes.get(producao)) {
			for (String producaoDerivada : producoesDerivadas) {
				for (String firstProdDerivada : this.getFirstSet().get(producaoDerivada)) {
					if (forma.get(0).contains(firstProdDerivada)) {
						return true;
					}
				}
				Set<String> f = new HashSet<String>();
				f.addAll(producoesDerivadas);
				f.remove(producaoDerivada);
				if (!f.isEmpty()) {
					return temNaoDeterminismoIndireta(producaoDerivada, f);
				}

			}
		}
		return false;
	}
}

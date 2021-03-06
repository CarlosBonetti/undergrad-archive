package com.bonaguiar.formais2.core;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
		this.naoTerminais = this.naoTerminais == null ? new HashSet<String>() : this.naoTerminais;
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
			this.firstSet.put(naoTerminal, new HashSet<String>());
		}

		boolean alterou = true;
		while (alterou) {
			alterou = false;
			for (String naoTerminal : this.producoes.keySet()) {
				Set<String> oldFirst = this.firstSet.get(naoTerminal);
				Set<String> newFirst = calcFirst(naoTerminal);
				this.firstSet.put(naoTerminal, newFirst);

				alterou = alterou || oldFirst.size() != newFirst.size();
			}
		}

		return this.firstSet;
	}

	/**
	 * Retorna o firstSet do símbolo
	 *
	 * @param simbolo
	 * @return
	 */
	public Set<String> first(String simbolo) {
		if (GrammarUtils.ehTerminal(simbolo)) {
			// First de um terminal é o próprio terminal
			Set<String> set = new HashSet<String>();
			set.add(simbolo);
			return set;
		}

		if (this.firstSet.containsKey(simbolo)) {
			// Se for um símbolo não terminal já calculado, simplesmente
			// retorna o conjunto previamente criado
			return this.firstSet.get(simbolo);
		}

		// Senão, calcula todos e retorna
		return this.getFirstSet().get(simbolo);
	}

	/**
	 * Retorna o firstSet da forma sentencial parâmetro
	 *
	 * @param formaSentencial
	 * @return
	 */
	public Set<String> first(FormaSentencial formaSentencial) {
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
	 * Calcula o first do símbolo não terminal parâmetro com base em suas produções e
	 * o first dos outros não terminais salvos neste momento.
	 * Este método não calcula o first de forma total, ele é chamado diversas vezes até
	 * que o first de todos os símbolos estejam completos. Use getFirstSet() ou first(simbolo)
	 * para obter os conjuntos first totais
	 *
	 * @param simbolo
	 * @return
	 */
	private Set<String> calcFirst(String simbolo) {
		Set<String> set = new HashSet<String>();

		// Calcula o first de cada produção
		for (FormaSentencial formaSentencial : this.producoes.get(simbolo)) {
			set.addAll(first(formaSentencial));
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
				intersection.retainAll(follow);
				if (!intersection.isEmpty()) { // Intersecção não vazia = conflito
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
	 *
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
	 *
	 * @param chave producao inicial a ser analisado se possue recursão a esquerda
	 * @param producao producao que é derivada da producão inicial direta ou indiretamente
	 * @return boolean
	 */
	private boolean producaoIniciaCom(String chave, String producao) {
		try {
			for (FormaSentencial forma : getProducoes().get(producao)) {
				for (String simbolo : forma) {
					if (GrammarUtils.ehNaoTerminal(simbolo)) {
						if (simbolo.equals(chave)) {
							return true;
						}
						if (producaoIniciaCom(chave, simbolo)) {
							return true;
						}

						// caso o nao-terminal derive EPSILON continua analisando mesma formaSentencial
						// caso contrario pula para próxima formaSentencial
						if (!getFirstSet().get(simbolo).contains(GrammarUtils.EPSILON.toString())) {
							break;
						}
					} else {
						break;
					}
				}
			}
		} catch (StackOverflowError e) {
			// System.err.println(chave + " <chave - producao> " + producao + "\n");
			return false;
		}
		return false;
	}

	/**
	 * Retorna um verdade se encontrar alguma derivação do simbolo analisado
	 *
	 * @param producao Simbolo não terminal a ser aalisado
	 * @return
	 * @throws ParseException
	 */
	private boolean temRecursaoEsquerdaIndireta(String producao) {
		for (FormaSentencial forma : getProducoes().get(producao)) {
			for (String simbolo : forma) {
				if (GrammarUtils.ehNaoTerminal(simbolo)) {
					if (forma.get(0).equals(producao) && forma.get(0).equals(simbolo)) {
						if (getFirstSet().get(simbolo).contains(GrammarUtils.EPSILON.toString())) {
							continue;
						} else {
							break;
						}
					}
					if (producaoIniciaCom(producao, simbolo)) {
						return true;
					}

					// caso o nao-terminal derive EPSILON continua analisando mesma formaSentencial
					// caso contrario pula para próxima formaSentencial
					if (!getFirstSet().get(simbolo).contains(GrammarUtils.EPSILON.toString())) {
						break;
					}
				} else {
					break;
				}
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
			// ao add em Set caso o terminal jah exista retorna false
			if (!terminaisDerivados.add(forma.get(0))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retorna uma lista com os ñ-terminais que possuem ñ-determinismo indireto(os ñ fatorados)
	 *
	 * @return
	 */
	public Set<String> getFatoracaoIndireta() {
		Set<String> naoFatoradaDireta = new HashSet<String>();
		for (String chave : producoes.keySet()) {
			if (contemIndeterminismo(getFatoracaoIndireta(chave))) {
				naoFatoradaDireta.add(chave);
			}
		}
		return naoFatoradaDireta;
	}

	/**
	 * Retorna todas os firsts, terminais ou nao, da setenca direta
	 *
	 * @param producao
	 * @return
	 */
	protected LinkedList<FormaSentencial> getFatoracaoIndireta(String producao) {
		LinkedList<FormaSentencial> derivacoesDiretas = new LinkedList<FormaSentencial>();
		// percorre formaSentencial da producao
		for (FormaSentencial forma : getProducoes().get(producao)) {
			// auxiliar para gerar uma forma sentencial
			String aux = "";
			// auxiliar para saber o tamanho da sentenca
			int tamForma = forma.size();

			for (String simbolo : forma) {
				// se simbolo gera recursao
				if (forma.get(0).equals(producao) && forma.get(0).equals(simbolo)) {
					// se simbolo gerar recursao e derivar '&' continua senão vai para próxima forma
					if (getFirstSet().get(simbolo).contains(GrammarUtils.EPSILON.toString())) {
						aux += simbolo + " ";
						continue;
					} else {
						break;
					}
				}
				if (GrammarUtils.ehTerminal(simbolo)) {
					derivacoesDiretas.add(new FormaSentencial(aux + simbolo));
					break;

				} else if (GrammarUtils.ehNaoTerminal(simbolo)) {
					aux += simbolo + " ";
					// se producao nao derivar '&' termina loop
					if (!getFirstSet().get(simbolo).contains(GrammarUtils.EPSILON.toString())) {
						derivacoesDiretas.add(new FormaSentencial(aux));
						break;
					}
					// se não existir mais simbolos na atual FormaSentencial aux eh adicionado a listagem
					if ((--tamForma) == 0) {
						derivacoesDiretas.add(new FormaSentencial(aux));
					}
				}
			}
		}
		return derivacoesDiretas;
	}

	/**
	 * Percorre a lista verificando se existem casos onde dois termos ou suas derivacoes possuem o mesmo valor
	 *
	 * @param lista
	 * @return
	 */
	protected boolean contemIndeterminismo(LinkedList<FormaSentencial> lista) {
		// percorre a lista
		for (int i = 0; i < lista.size(); i++) {
			// auxiliar para saber o tamanho da sentenca
			int tamForma = lista.get(i).size();
			for (String simbolo : lista.get(i)) {
				--tamForma;
				// se for terminal, faz validacoes diretas
				if (GrammarUtils.ehTerminal(simbolo)) {
					// percorre a lista sem valores de atual e anterores i
					for (int j = i + 1; j < lista.size(); j++) {
						for (String simbolo2 : lista.get(j)) {
							if (GrammarUtils.ehTerminal(simbolo2)) {
								// verifica se duas sentencas geram o mesmo terminal
								if (simbolo.equals(simbolo2)) {
									// faz verificacao se eh o ultimo simbolo da FormaSentencial
									if (lista.get(i).get(0).equals(lista.get(j).get(0))) {
										continue;
									} else {
										return true;
									}
								}
							} else if (GrammarUtils.ehNaoTerminal(simbolo2)) {
								// se simbolo que eh terminal existir nas derivacoes de simbolo2(ñ-terminal) retorna true
								if (getFirstSet().get(simbolo2).contains(simbolo)) {
									return true;
								}
							}
						}
					}
				} else if (GrammarUtils.ehNaoTerminal(simbolo)) {
					// percorre a lista sem valores de atual e anterores i
					for (int j = i + 1; j < lista.size(); j++) {
						for (String simbolo2 : lista.get(j)) {
							if (GrammarUtils.ehTerminal(simbolo2)) {
								// se simbolo que eh ñ-terminal ter em suas derivacoes de simbolo2(terminal)
								if (getFirstSet().get(simbolo).contains(simbolo2)) {
									// se simbolo2 for diferente de '&' ou for ultimo simbolo da formaSentencial retorna true
									if (!simbolo2.equals(GrammarUtils.EPSILON.toString()) || (tamForma) == 0) {
										return true;
									} else {
										continue;
									}
								}
							} else if (GrammarUtils.ehNaoTerminal(simbolo2)) {
								// verifica de existem firsts semelhantes
								if (firstsComFirsts(simbolo, simbolo2, (tamForma) > 0)) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Verifica se duas producoes possuem firsts semelhantes
	 * Excluindo da verificao Epsilon caso producao não for ultimo valor da Forma sentencial
	 *
	 * @param producao1
	 * @param producao2
	 * @param desconsiderarEpsilon
	 * @return
	 */
	private boolean firstsComFirsts(String producao1, String producao2, boolean desconsiderarEpsilon) {

		for (String firstProducao : getFirstSet().get(producao2)) {
			if (desconsiderarEpsilon) {
				continue;
			}
			if (getFirstSet().get(producao1).contains(firstProducao)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean ehLL1() {
		if (!(this.getRecursaoEsquerdaDireta().isEmpty()
				&& this.getRecursaoEsquerdaIndireta()
				.isEmpty())) {
			return false;
		}
		if (!(this.getFatoracaoDireta().isEmpty()
				&& this.getFatoracaoIndireta()
				.isEmpty())) {
			return false;
		}
		if (!(this.getConflitosFF().isEmpty())){
			return false;
		}
		
		return true;
	}
}

package com.bonaguiar.formais2.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bonaguiar.formais2.core.GLC.FormaSentencial;

/**
 * Um gerador de parser para gramáticas livres de contexto
 * que usa a técnica Descendente Recursivo
 */
public class ParserGenerator {

	private static final Exception Exception = null;
	/**
	 * Gramática Base do Parser
	 */
	protected GLC glc;

	/**
	 * Cria um novo gerador de parser para a gramática
	 *
	 * @throws java.lang.Exception
	 */
	public ParserGenerator(GLC glc) throws java.lang.Exception {
		if (!glc.ehLL1()) {
			throw Exception;
		}
		this.glc = glc;
	}

	/**
	 * Retorna um novo Parser para a gramática
	 *
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JavaParser getParser() throws Exception {
		Template parserTemplate = this.getParserTemplate();

		parserTemplate = parserTemplate.replace("$(inicial)", this.glc.getSimboloInicial());
		parserTemplate = parserTemplate.replace("$(metodos)", this.getMetodos());
		return new JavaParser(parserTemplate.toString());
	}

	public String getMetodos() {
		String result = "";
		for (String nt : this.glc.getNaoTerminais()) {
			Template metodo = this.getMetodoTemplate();
			metodo = metodo.replace("$(nome)", nt);
			metodo = metodo.replace("$(corpo)", this.getCorpo(nt));
			result += metodo.toString();
		}

		return result;
	}

	protected String getCorpo(String nt) {
		String result = "";
		List<FormaSentencial> producoes = this.glc.getProducoes(nt);
		boolean first = true;
		for (FormaSentencial fs : producoes) {
			if (fs.equals(GrammarUtils.PRODUCAO_VAZIA)) {
				continue;
			}

			if (!first) {
				result += "else ";
			}

			result += this.getIfProducao(fs, 0, nt);
			first = false;
		}

		// Caso seja uma produção do tipo A -> &, e A possui somente essa produção, não adiciona nada
		if (producoes.size() == 1 && producoes.get(0).equals(GrammarUtils.PRODUCAO_VAZIA)) {
			return result;
		}

		Template ultimoElseTemplate = this.getElseTemplate();
		if (producoes.contains(GrammarUtils.PRODUCAO_VAZIA)) {
			ultimoElseTemplate = ultimoElseTemplate.removeLine("$(body)");
		} else {
			String exc = "error(new HashSet<String>(Arrays.asList("+formatoArraysAsList(this.glc.getFirstSet().get(nt))+")));";
			ultimoElseTemplate = ultimoElseTemplate.replace("$(body)", exc);
		}

		result += ultimoElseTemplate.toString();

		return result;
	}
	private String formatoArraysAsList(Collection<String> c){
		String aux ="";
		for (String f : c) {
			aux += String.format("\"%s\", ", f);
		}
		// Removendo o último ", " inserido:
		aux = aux.isEmpty() ? aux : aux.substring(0, aux.length() - 2);
		return aux;
	}
	
	protected String getIfProducao(FormaSentencial formaSentencial, int index, String produtor) {
		Template ifTemplate = this.getIfTemplate();
		String simboloAtual = formaSentencial.get(index);
		boolean terminal = false;
		Set<String> lista = new HashSet<String>();
		if (GrammarUtils.ehTerminal(simboloAtual)) {
			terminal = true;
			// Cria if
			ifTemplate = ifTemplate.replace("$(condition)", "sym.equals(\"" + simboloAtual + "\")");
			ifTemplate = ifTemplate.replace("$(body)", "alex();");
		} else {
			FormaSentencial subFS = new FormaSentencial();
			subFS.addAll(formaSentencial.subList(index, formaSentencial.size()));
			lista = this.glc.first(subFS);
			if (lista.contains(GrammarUtils.EPSILON.toString())) {
				lista.addAll(this.glc.follow(produtor));
			}

			ifTemplate = ifTemplate.replace("$(condition)", this.getInOp(lista));
			ifTemplate = ifTemplate.replace("$(body)", simboloAtual + "();");
		}

		// se não for o último símbolo da produção, chama recursivamente este método
		// criando os ifs aninhados
		if (index < formaSentencial.size() - 1) {
			ifTemplate = ifTemplate.replace("$(post-body)", this.getIfProducao(formaSentencial, index + 1, produtor));
		} else {
			ifTemplate = ifTemplate.removeLine("$(post-body)");
		}

		// Se não for o primeiro símbolo da produção, adiciona um else levando a erro
		if (index != 0) {
			ifTemplate.add("else {");
			if (terminal) {
				ifTemplate.add("	error(new HashSet<String>(Arrays.asList(\""+simboloAtual+"\")));");
			} else{
				ifTemplate.add("	error(new HashSet<String>(Arrays.asList("+formatoArraysAsList(lista)+ ")));");
			}
			ifTemplate.add("}");
		}
		new HashSet<String>(Arrays.asList("+aux+ "));
		return ifTemplate.toString();
	}
	
	
	protected String getInOp(Collection<String> c) {
		Template template = new Template();
		template.add("Arrays.asList($(lista)).contains(sym)");

		template = template.replace("$(lista)", formatoArraysAsList(c));
		return template.toString().trim();
	}

	protected Template getIfTemplate() {
		Template template = new Template();
		template.add("if ($(condition)) {");
		template.add("	$(body)");
		template.add("	$(post-body)");
		template.add("}");
		return template;
	}

	protected Template getElseTemplate() {
		Template template = new Template();
		template.add("else {");
		template.add("	$(body)");
		template.add("}");
		return template;
	}

	protected Template getMetodoTemplate() {
		Template template = new Template();
		template.add("public void $(nome)() throws Exception {");
		template.add("	sequence += \"$(nome) \";");
		template.add("	$(corpo)");
		template.add("}");
		template.add("");
		return template;
	}

	protected Template getParserTemplate() throws IOException {
		return new Template(Files.readAllLines(Paths.get("./src/main/templates/parser.template"), Charset.defaultCharset()));
	}

	/**
	 * Lista de linhas (String), representando um arquivo de texto com tags especiais para serem processadas
	 * em tempo de execução, como por exemplo, a tag $(inicial)
	 */
	public static class Template extends ArrayList<String> {
		private static final long serialVersionUID = -9031772507297147620L;

		public Template() {
		}

		public Template(List<String> list) {
			super(list);
		}

		/**
		 * Altera toda a sequência de caracteres encontrada pelo replacement, mantendo
		 * níveis de tab encontrados
		 *
		 * @param target
		 * @param replacement
		 * @return
		 */
		public Template replace(String target, String replacement) {
			Template novo = new Template();
			for (String line : this) {
				String newLine = line.replace(target, replacement);

				// Se a linha foi alterada, fazemos uns cambalacho pra deixar os níveis de tab
				// corretos mesmo para replacements com multilinhas
				if (!newLine.equals(line)) {
					int lastTab = -1;
					for (Character c : line.toCharArray()) {
						if (c == '\t') {
							lastTab++;
						} else {
							break;
						}
					}

					if (lastTab != -1) {
						String tabs = line.substring(0, lastTab + 1);
						newLine = newLine.replace("\n", "\n" + tabs);
					}
				}

				novo.add(newLine);
			}
			return novo;
		}

		/**
		 * Remove a linha que contém o target
		 *
		 * @param target
		 * @return
		 */
		public Template removeLine(String target) {
			Template novo = new Template();
			for (String line : this) {
				if (!line.contains(target)) {
					novo.add(line);
				}
			}
			return novo;
		}

		@Override
		public String toString() {
			String result = "";
			for (String line : this) {
				result += line + System.lineSeparator();
			}
			return result;
		}
	}
}

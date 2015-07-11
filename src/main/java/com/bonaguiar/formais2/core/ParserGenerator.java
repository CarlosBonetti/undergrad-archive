package com.bonaguiar.formais2.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.bonaguiar.formais2.core.GLC.FormaSentencial;

/**
 * Um gerador de parser para gramáticas livres de contexto
 * que usa a técnica Descendente Recursivo
 */
public class ParserGenerator {

	/**
	 * Gramática Base do Parser
	 */
	protected GLC glc;

	/**
	 * Cria um novo gerador de parser para a gramática
	 */
	public ParserGenerator(GLC glc) {
		// TODO: verificar se é LL(1)
		this.glc = glc;
	}

	public String getParser() throws IOException {
		Template parserTemplate = this.getParserTemplate();

		parserTemplate = parserTemplate.replace("$(inicial)", this.glc.getSimboloInicial());
		parserTemplate = parserTemplate.replace("$(metodos)", this.getMetodos());
		return parserTemplate.toString();
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

			result += this.getIfProducao(fs, 0);
			first = false;
		}

		Template ultimoElseTemplate = this.getElseTemplate();
		if (producoes.contains(GrammarUtils.PRODUCAO_VAZIA)) {
			ultimoElseTemplate = ultimoElseTemplate.replace("$(body)", "");
		} else {
			String exc = "throw new ParseException(x, 0);";
			ultimoElseTemplate = ultimoElseTemplate.replace("$(body)", exc);
		}

		result += ultimoElseTemplate.toString();

		return result;
	}

	protected String getIfProducao(FormaSentencial formaSentencial, int index) {
		Template ifTemplate = this.getIfTemplate();
		String simboloAtual = formaSentencial.get(index);

		// TODO: se for não terminal, fazer aquilo...
		ifTemplate = ifTemplate.replace("$(condition)", "x == '" + simboloAtual + "'");

		ifTemplate = ifTemplate.replace("$(body)", "TODO");
		return ifTemplate.toString();
	}

	protected Template getIfTemplate() {
		Template template = new Template();
		template.add("if ($(condition)) {");
		template.add("	$(body)");
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
		template.add("public static void $(nome)(x) {");
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

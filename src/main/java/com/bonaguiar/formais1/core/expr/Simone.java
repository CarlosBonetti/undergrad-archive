package com.bonaguiar.formais1.core.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Encapsula rotinas do método de simone para gerar AF a partir de uma Expressão Regular
 */
public class Simone {
	public static final Nodo<Character> FIM_DA_COSTURA = new Nodo<Character>('λ');
	
	/**
	 * Obtém a Composição de nodos alcançados a partir do nodo parâmetro, de acordo com o algoritmo de Simone
	 * @return
	 */
	public static Composicao obterComposicao(Nodo<Character> nodo) {
		Composicao comp = new Composicao();
		
		if (nodo.ehFolha()) {
			comp = subir(nodo.getCostura(), comp);
		} else {
			comp = descer(nodo, comp);
		}
		
		return comp;
	}
	
	/**
	 * Obtém a Composição de nodos alcançados a partir da composição parâmetro
	 * @param comp
	 * @return
	 */
	public static Composicao obterComposicao(Composicao comp) {
		Composicao alc = new Composicao();		
		for (Nodo<Character> nodo : comp) {
			alc.addAll(obterComposicao(nodo));
		}		
		return alc;
	}
	
	/**
	 * Executa a rotina "descer" do método de Simone no nodo especificado, recursivamente
	 * Retorna o conjunto de nodos alcançados
	 * @param nodo
	 * @param alcancados Lista de nodos alcançados até o momento
	 */
	public static Composicao descer(Nodo<Character> nodo, Composicao alcancados) {
		switch(nodo.getConteudo()) {
		case '|':
			// Descer em ambos os filhos
			descer(nodo.getEsq(), alcancados);
			descer(nodo.getDir(), alcancados);
			break;
			
		case '*':
		case '?':
			// Desce e sobe
			descer(nodo.getEsq(), alcancados);
			subir(nodo.getCostura(), alcancados);
			break;
			
		case '.':
			// Desce à esquerda
			descer(nodo.getEsq(), alcancados);
			break;
			
		default:
			// Adicionar folha à composição
			alcancados.add(nodo);
			break;
		}
		
		return alcancados;
	}
	
	/**
	 * Executa a rotina "subir" do método de Simone no nodo especificado, recursivamente
	 * Retorna o conjunto de nodos alcançados
	 * @param nodo
	 * @param alcancados Lista de nodos alcançados até o momento
	 */
	public static Composicao subir(Nodo<Character> nodo, Composicao alcancados) {
		switch(nodo.getConteudo()) {
		case '|':
			// Seguir a costura do filho mais à direita
			Nodo<Character> dir = nodo;
			while (dir.temDir()) {
				dir = dir.getDir();
			}
			subir(dir.getCostura(), alcancados);			
			break;
			
		case '*':
			descer(nodo.getEsq(), alcancados);
			subir(nodo.getCostura(), alcancados);
			break;
			
		case '?':
			// Subir
			subir(nodo.getCostura(), alcancados);
			break;
			
		case '.':
			// Desce à direita
			descer(nodo.getDir(), alcancados);
			break;
			
		case 'λ':
			// Fim da árvore
			alcancados.add(FIM_DA_COSTURA);
			break;
			
		default:
			// Nunca alcançado
			break;
		}
		
		return alcancados;
	}
	
	public static class Composicao extends HashSet<Nodo<Character>> {
		private static final long serialVersionUID = 1567218310588925719L;
		
		/**
		 * Checa se esta composição está associada a um estado final
		 * Verdadeiro caso o Nodo de "fim de costura" pertença à composição
		 * @return
		 */
		public Boolean ehFinal() {
			return this.contains(Simone.FIM_DA_COSTURA);
		}
		
		/**
		 * Obtém uma nova composição do estado alcançado ao aplicar uma transição com o caractere c à composição atual
		 * Exemplo: Se a composição atual é composta por 1a, 3a, 2b, 4c, a transição com o caractere 'a' retornará uma nova composição
		 * composta pro 1a, 3a
		 * @param c
		 * @return
		 */
		public Composicao transicao(Character c) {
			Composicao comp = new Composicao();
			for (Nodo<Character> nodo : this) {
				if (nodo.getConteudo().equals(c)) {
					comp.add(nodo);
				}
			}		
			
			return comp;
		}
		
		@Override
		public String toString() {
			List<Nodo<Character>> list = new ArrayList<Nodo<Character>>();
			list.addAll(this);
			Collections.sort(list);
			return list.toString();
		}
	}
}

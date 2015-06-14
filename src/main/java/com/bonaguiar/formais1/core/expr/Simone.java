package com.bonaguiar.formais1.core.expr;

import java.util.HashSet;
import java.util.Set;

/**
 * Encapsula rotinas do método de simone para gerar AF a partir de uma Expressão Regular
 */
public class Simone {
	public static final Nodo<Character> FIM_DA_COSTURA = new Nodo<Character>('λ');
	
	/**
	 * Obtém a lista de nodos alcançados a partir do nodo parâmetro, de acordo com o algoritmo de Simone
	 * @return
	 */
	public static Set<Nodo<Character>> obterComposicao(Nodo<Character> nodo) {
		Set<Nodo<Character>> alc = new HashSet<Nodo<Character>>();
		
		if (nodo.ehFolha()) {
			alc = subir(nodo.getCostura(), alc);
		} else {
			alc = descer(nodo, alc);
		}
		
		return alc;
	}
	
	/**
	 * Executa a rotina "descer" do método de Simone no nodo especificado, recursivamente
	 * Retorna o conjunto de nodos alcançados
	 * @param nodo
	 * @param alcancados Lista de nodos alcançados até o momento
	 */
	public static Set<Nodo<Character>> descer(Nodo<Character> nodo, Set<Nodo<Character>> alcancados) {
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
	public static Set<Nodo<Character>> subir(Nodo<Character> nodo, Set<Nodo<Character>> alcancados) {
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
}

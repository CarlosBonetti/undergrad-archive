package com.bonaguiar.formais1.core;

import com.bonaguiar.formais1.core.exception.FormaisException;

import lombok.Getter;

public class GramaticaRegular {
	/**
	 * Conjunto de símbolos não terminais
	 */
	@Getter
	protected Alfabeto Vn;
	
	/**
	 * Conjunto de símbolos terminais
	 */
	@Getter
	protected Alfabeto Vt;
	
	/**
	 * Símbolo inicial (pertencente à Vt)
	 */
	@Getter
	protected Character S;
	
	/**
	 * Cria uma nova gramática regular
	 * @param Vn Conjunto de caracteres não terminais (devem ser maiúsculos)
	 * @param Vt Conjunto de caracteres terminais (devem ser minúsculos)
	 * @param S Símbolo inicial da gramática (deve pertencer a Vn)
	 * @throws Exception 
	 */
	public GramaticaRegular(Alfabeto Vn, Alfabeto Vt, Character S) throws FormaisException {
		this.setVn(Vn);
		this.setVt(Vt);
		this.setSimboloInicial(S);
	}
	
	/**
	 * Seta o conjunto de símbolos não terminais da gramática
	 * @param Vn
	 * @throws FormaisException
	 */
	protected void setVn(Alfabeto Vn) throws FormaisException {
		for(Character c : Vn) {
			if (!Character.isUpperCase(c)) {
				throw new FormaisException("Símbolo não-terminal `" + c + "` deve ser maiúsculo.");
			}
		}
		this.Vn = Vn;
	}
	
	/**
	 * Seta o conjunto de caracteres terminais da gramática
	 * @param Vt
	 * @throws FormaisException
	 */
	protected void setVt(Alfabeto Vt) throws FormaisException {
		for(Character c : Vt) {
			if (!Character.isLowerCase(c)) {
				throw new FormaisException("Símbolo terminal `" + c + "` deve ser minúsculo.");
			}
		}
		this.Vt = Vt;
	}

	/**
	 * Seta o símbolo inicial da gramática
	 * @param S Novo símbolo inicial da gramática
	 * @throws FormaisException Lançado caso S não pertença a Vn
	 */
	public void setSimboloInicial(char S) throws FormaisException {
		if (!this.Vn.contains(S)) {
			throw new FormaisException("Símbolo inicial `" + S + "` não pertence a Vn");
		}
		
		this.S = S;
	}
	
	public void addProducao() {
		// TODO
	}

}

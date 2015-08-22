package com.bonaguiar.formais1.persistence;

import java.io.IOException;
import java.util.HashMap;

import com.bonaguiar.formais1.core.expr.ExprRegular;

public class ERBase extends FileBase<HashMap<String, ExprRegular>> {

	public ERBase() {
		super("expressoes.db");
	}

	/**
	 * Retorna um hash com todas as ER das base
	 */
	@Override
	public HashMap<String, ExprRegular> get() {
		try {
			return super.get();
		} catch (Exception e) {
			return new HashMap<String, ExprRegular>();
		}
	}

	/**
	 * Salva o novo hash de ERs na base, sobrescrevendo a antiga
	 */
	@Override
	public void save(HashMap<String, ExprRegular> obj) throws IOException {
		super.save(obj);
	}
}

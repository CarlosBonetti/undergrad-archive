package com.bonaguiar.formais2.persistence;

import java.io.IOException;
import java.util.HashMap;

import com.bonaguiar.formais2.core.GLC;

public class GLCBase extends FileBase<HashMap<String, GLC>> {

	public GLCBase() {
		super("glc.db");
	}

	/**
	 * Retorna um hash com todas as GLC das base
	 */
	@Override
	public HashMap<String, GLC> get() {
		try {
			return super.get();
		} catch (Exception e) {
			return new HashMap<String, GLC>();
		}
	}

	/**
	 * Salva o novo hash de Gramáticas na base, sobrescrevendo a antiga
	 */
	@Override
	public void save(HashMap<String, GLC> obj) throws IOException {
		super.save(obj);
	}

}

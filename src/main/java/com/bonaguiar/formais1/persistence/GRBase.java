package com.bonaguiar.formais1.persistence;

import java.io.IOException;
import java.util.HashMap;

import com.bonaguiar.formais1.core.grammar.GramaticaRegular;

public class GRBase extends FileBase<HashMap<String, GramaticaRegular>> {

	public GRBase() {
		super("gramaticas.db");
	}

	/**
	 * Retorna um hash com todas as GR das base
	 */
	@Override
	public HashMap<String, GramaticaRegular> get() {
		try {
			return super.get();
		} catch (Exception e) {
			return new HashMap<String, GramaticaRegular>();
		}
	}

	/**
	 * Salva o novo hash de Gramáticas na base, sobrescrevendo a antiga
	 */
	@Override
	public void save(HashMap<String, GramaticaRegular> obj) throws IOException {
		super.save(obj);
	}

}

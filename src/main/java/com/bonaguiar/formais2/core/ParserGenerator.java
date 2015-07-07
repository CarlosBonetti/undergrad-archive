package com.bonaguiar.formais2.core;

import java.io.File;
import java.io.IOException;

import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;

/**
 * Um gerador de parser para gramáticas livres de contexto
 * que usa a técnica Descendente Recursivo
 */
public class ParserGenerator {

	/**
	 * Cria um novo gerador de parser para a gramática
	 *
	 * @param glc
	 * @throws IOException
	 * @throws JClassAlreadyExistsException
	 */
	public ParserGenerator(GLC glc) throws IOException, JClassAlreadyExistsException {
		// http://stackoverflow.com/questions/121324/a-java-api-to-generate-java-source-files#136010
		// https://github.com/phax/jcodemodel

		JCodeModel model = new JCodeModel();
		JDefinedClass dc = model._class("Bar");
		JMethod m = dc.method(JMod.PUBLIC | JMod.STATIC, void.class, "main");
		m.param(String[].class, "args");

		File file = new File("./parsers/");
		file.mkdirs();
		model.build(file);
	}
}

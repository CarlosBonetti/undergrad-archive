package com.bonaguiar.formais2.core;

import java.io.IOException;

import org.junit.Test;

import com.helger.jcodemodel.JClassAlreadyExistsException;

public class ParserGeneratorTest {

	@Test
	public void test() throws IOException, JClassAlreadyExistsException {
		GLC glc = new GLC();
		ParserGenerator generator = new ParserGenerator(glc);
	}

}

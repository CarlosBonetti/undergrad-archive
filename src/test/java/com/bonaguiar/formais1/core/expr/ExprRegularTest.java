package com.bonaguiar.formais1.core.expr;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bonaguiar.formais1.core.exception.FormaisException;

public class ExprRegularTest {

	@Test
	public void testarCriacao() throws FormaisException {
		ExprRegular expr1 = new ExprRegular("(ab)*");
		assertEquals("(ab)*", expr1.getExpr());
	}
	
	@Test
	public void testarArvoreSintatica() throws FormaisException {
		ExprRegular expr1 = new ExprRegular("(ab)*(cc)*");
		assertEquals(new Character('.'), expr1.getTree().getConteudo());
		assertEquals(new Character('*'), expr1.getTree().getEsq().getConteudo());
		assertEquals(new Character('*'), expr1.getTree().getDir().getConteudo());
		assertEquals(new Character('.'), expr1.getTree().getEsq().getEsq().getConteudo());
		assertEquals(null, expr1.getTree().getEsq().getDir());
		assertEquals(null, expr1.getTree().getDir().getDir());		
		assertEquals(new Character('a'), expr1.getTree().getEsq().getEsq().getEsq().getConteudo());
		assertEquals(new Character('b'), expr1.getTree().getEsq().getEsq().getDir().getConteudo());
		assertEquals(new Character('c'), expr1.getTree().getDir().getEsq().getEsq().getConteudo());
		assertEquals(new Character('c'), expr1.getTree().getDir().getEsq().getDir().getConteudo());
		
		// Deve estar costurada
		assertEquals(expr1.getTree(), expr1.getTree().getEsq().getCostura());
	}

}

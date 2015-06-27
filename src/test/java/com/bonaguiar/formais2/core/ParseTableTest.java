package com.bonaguiar.formais2.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ParseTableTest {

	/**
	 * GLC de expressões aritméticas com recursão à esquerda
	 */
	private GLC glc1;

	/**
	 * GLC das expressões aritméticas sem RE (LL1)
	 */
	private GLC glc2;

	@Before
	public void setUp() throws Exception {
		// E -> E + T | E - T | T\n" + "T -> T * F | T / F | F\n" + "F -> ( E ) | id
		this.glc1 = new GLC("E -> E + T | E - T | T \n" + "T -> T * F | T / F | F \n" + "F -> ( E ) | id");
		this.glc2 = new GLC("E -> T E' \n" + "E' -> + T E' | & \n" + "T -> F T' \n" + "T' -> * F T' | & \n" + "F -> ( E ) | id");
	}

	@Test
	public void testarTabelaConstruida() {
		ParseTable table = new ParseTable(this.glc2);
		assertEquals("{id=0, (=0}", table.hash.get("E").toString());
		assertEquals("{$=2, +=1, )=2}", table.hash.get("E'").toString());
		assertEquals("{id=3, (=3}", table.hash.get("T").toString());
		assertEquals("{$=5, *=4, +=5, )=5}", table.hash.get("T'").toString());
		assertEquals("{id=7, (=6}", table.hash.get("F").toString());
	}

	@Test
	public void testarConstrucaoTabelaNaoDeterministica() {
		// new ParseTable(this.glc1);
		// TODO: deve jogar exception dizendo que possui recursão (impossível calcular first)
	}

	@Test
	public void testarGet() {
		ParseTable table = new ParseTable(this.glc2);
		assertEquals(new Integer(0), table.get("E", "id"));
		assertEquals(new Integer(0), table.get("E", "("));
		assertEquals(null, table.get("E", "$"));
	}
}

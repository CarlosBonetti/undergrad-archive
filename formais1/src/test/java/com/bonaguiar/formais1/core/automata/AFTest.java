package com.bonaguiar.formais1.core.automata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.bonaguiar.formais1.core.Alfabeto;
import com.bonaguiar.formais1.core.automata.AF.MesclaDeEstados;
import com.bonaguiar.formais1.core.exception.FormaisException;
import com.bonaguiar.formais1.core.expr.ExprRegular;

public class AFTest {

	@Test
	public void testarInicializacaoVazia() {
		AF af = new AF(new Alfabeto("abc"));
		assertEquals(3, af.getAlfabeto().size());
		assertTrue(af.getAlfabeto().contains('c'));
	}

	@Test
	public void testarAddEstado() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q1", false);
		assertEquals("q1", af.getEstados().get(0));
		assertEquals(1, af.getEstados().size());
		assertEquals(0, af.getEstadosFinais().size());

		af.addEstado("q0", true);
		assertEquals("q0", af.getEstados().get(1));
		assertEquals(2, af.getEstados().size());
		assertEquals(1, af.getEstadosFinais().size());
	}

	@Test(expected = FormaisException.class)
	public void testarAddEstadoDuplicado() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q0", false);
	}

	@Test
	public void testarContemEstado() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q4", false);
		af.addEstado("A", true);
		assertEquals(true, af.contemEstado("q4"));
		assertEquals(true, af.contemEstado("A"));
		assertEquals(false, af.contemEstado("q0"));
		assertEquals(false, af.contemEstado("B"));
	}

	@Test
	public void testarSetEstadoInicial() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q1", true);
		af.setEstadoInicial("q1");
		assertEquals("q1", af.getEstadoInicial());
	}

	@Test(expected = FormaisException.class)
	public void testarSetEstadoInicialNaoExistente() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.setEstadoInicial("q2");
	}

	@Test
	public void testarAddTransicao() throws FormaisException {
		AF af = new AF(new Alfabeto("012"));
		af.addEstado("q0", false);
		af.addEstado("q1", true);
		af.addTransicao("q0", '1', "q1");
		assertEquals("q0", af.getTransicoes().get(0).estadoOrigem);
		assertEquals(new Character('1'), af.getTransicoes().get(0).simboloTransicao);
		assertEquals("q1", af.getTransicoes().get(0).estadoDestino);
		assertEquals(1, af.getTransicoes().size());
	}

	@Test(expected = FormaisException.class)
	public void testarAddTransicaoComEstadoOrigemNaoPertencenteAoAF() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.addTransicao("q4", 'a', "q1");
	}

	@Test(expected = FormaisException.class)
	public void testarAddTransicaoComSimboloNaoPertencenteAoAF() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.addTransicao("q1", 'x', "q0");
	}

	@Test(expected = FormaisException.class)
	public void testarAddEpsolonTransicao() throws FormaisException {
		AF afd1 = new AF(new Alfabeto("abcde"));
		afd1.addEstado("q0", true);
		afd1.addEstado("q1", true);
		afd1.addTransicao("q0", Alfabeto.EPSILON, "q1");
	}

	@Test(expected = FormaisException.class)
	public void testarAddTransicaoComEstadoDestinoNaoPertencenteAoAF() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.addTransicao("q1", 'a', "q2");
	}

	@Test
	public void testarTransicao() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);

		// Uma transição simples (q0, a) -> q1
		af.addTransicao("q0", 'a', "q1");
		assertEquals("q1", af.transicao("q0", 'a').get(0));
		assertEquals(1, af.transicao("q0", 'a').size());

		// Outra transição simples (q1, b) -> q0
		af.addTransicao("q1", 'b', "q0");
		assertEquals(0, af.transicao("q1", 'a').size());
		assertEquals(1, af.transicao("q1", 'b').size());

		// Segunda transição a partir de q0 (q0, c) -> q0
		af.addTransicao("q0", 'c', "q0");
		assertEquals("q0", af.transicao("q0", 'c').get(0));
		assertEquals(1, af.transicao("q0", 'a').size());

		// Add transição não determinística (q0, a) -> {q1, q0}
		af.addTransicao("q0", 'a', "q0");
		assertEquals("q1", af.transicao("q0", 'a').get(0));
		assertEquals("q0", af.transicao("q0", 'a').get(1));
		assertEquals(2, af.transicao("q0", 'a').size());
	}

	@Test(expected = FormaisException.class)
	public void testarTransicaoComEstadoNaoPertencenteAoAF() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.transicao("q8", 'a');
	}

	@Test(expected = FormaisException.class)
	public void testarTransicaoComSimboloNaoPertencenteAoAF() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.transicao("q0", 'x');
	}

	@Test
	public void testarEhFinal() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		assertEquals(true, af.ehFinal("q0"));
		assertEquals(false, af.ehFinal("q1"));
	}

	@Test(expected = FormaisException.class)
	public void testarEhFinalComEstadoInexistente() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.ehFinal("q5");
	}

	@Test
	public void testarGetEstadosAlcancaveis() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.addEstado("q2", false);
		af.addEstado("q3", false);
		af.addEstado("q4", false);
		af.addEstado("q5", false);
		af.setEstadoInicial("q0");

		// Estado inicial sempre é alcancável
		assertTrue(af.getEstadosAlcancaveis().contains("q0"));

		af.addTransicao("q0", 'a', "q1");
		af.addTransicao("q0", 'a', "q2");
		af.addTransicao("q0", 'c', "q3");
		af.addTransicao("q1", 'b', "q2");
		af.addTransicao("q2", 'c', "q4");

		Set<String> alc = af.getEstadosAlcancaveis();
		assertTrue(alc.contains("q0"));
		assertTrue(alc.contains("q1"));
		assertTrue(alc.contains("q2"));
		assertTrue(alc.contains("q3"));
		assertTrue(alc.contains("q4"));
		assertFalse(alc.contains("q5"));

		af.addTransicao("q2", 'b', "q5");
		alc = af.getEstadosAlcancaveis();
		assertTrue(alc.contains("q5"));
	}

	@Test
	public void testarGetEstadosVivos() throws FormaisException {
		AF af = new AF(new Alfabeto("abc"));
		af.addEstado("q0", false);
		af.addEstado("q1", false);
		af.addEstado("q2", false);
		af.addEstado("q3", false);
		af.addEstado("q4", true);
		af.addEstado("q5", true);
		af.setEstadoInicial("q0");

		// Estados finais são vivos por definição
		Set<String> vivos = af.getEstadosVivos();
		assertEquals(2, vivos.size());
		assertTrue(vivos.contains("q4"));
		assertTrue(vivos.contains("q5"));

		// Adicionando transições para os estados finais e checando se eles se tornam vivos
		af.addTransicao("q2", 'b', "q4");
		af.addTransicao("q2", 'b', "q2"); // Transição não determinística pra testar se algoritmo finaliza
		af.addTransicao("q0", 'a', "q4");
		af.addTransicao("q1", 'c', "q5");
		vivos = af.getEstadosVivos();
		assertEquals(5, vivos.size());
		assertTrue(vivos.contains("q4"));
		assertTrue(vivos.contains("q5"));
		assertTrue(vivos.contains("q2"));
		assertTrue(vivos.contains("q0"));
		assertTrue(vivos.contains("q1"));
	}

	@Test
	public void testarGetComplemento() throws FormaisException {
		AF af = new AF(new Alfabeto("ab"));
		af.addEstado("q0", false);
		af.addEstado("q1", false);
		af.addEstado("q2", true);
		af.setEstadoInicial("q0");
		af.addTransicao("q0", 'b', "q0");
		af.addTransicao("q0", 'a', "q1");
		af.addTransicao("q1", 'b', "q1");
		af.addTransicao("q1", 'a', "q2");
		af.addTransicao("q2", 'a', "q2");
		af.addTransicao("q2", 'b', "q2");

		AF comp = af.getComplemento();

		// Complemento deve ter o mesmo alfabeto
		assertEquals(af.getAlfabeto(), comp.getAlfabeto());

		// Complemento deve ter o mesmo conjunto de estados
		assertEquals(af.getEstados(), comp.getEstados());

		// Complemento deve ter o mesmo estado inicial
		assertEquals(af.getEstadoInicial(), comp.getEstadoInicial());

		// Complemento deve ter as mesmas transições
		assertEquals(af.getTransicoes(), comp.getTransicoes());

		// Estados finais se tornam não-finais. Estados não-finais se tornam finais
		assertEquals(Arrays.asList("q0", "q1"), comp.getEstadosFinais());
	}

	@Test
	public void testarGetComplementoDeAutomatoIncompleto() throws FormaisException {
		AF af = new AF(new Alfabeto("ab")); // (ab)* onde não é permitido 'aa'
		af.addEstado("q0", true);
		af.addEstado("q1", true);
		af.setEstadoInicial("q0");
		af.addTransicao("q0", 'b', "q0");
		af.addTransicao("q0", 'a', "q1");
		af.addTransicao("q1", 'b', "q0");

		AF comp = af.getComplemento();

		// Complemento deve ter o estado de erro
		assertTrue(comp.estados.contains(AF.ESTADO_ERRO));

		// Estado de erro deve ser final
		assertEquals(Arrays.asList(AF.ESTADO_ERRO), comp.getEstadosFinais());

		// Transições de erro devem ter sido criadas
		assertEquals(Arrays.asList(AF.ESTADO_ERRO), comp.transicao("q1", 'a'));
	}

	@Test
	public void testarIntersectar() throws FormaisException {
		AF af = new AF(new Alfabeto("ab"));
		AF af2 = new AF(new Alfabeto("ab"));

		af.addEstado("q0", true);
		af.addEstado("q1", false);
		af.setEstadoInicial("q0");
		af.addTransicao("q0", 'a', "q1");
		af.addTransicao("q0", 'b', "q1");
		af.addTransicao("q1", 'a', "q0");
		af.addTransicao("q1", 'b', "q0");

		af2.addEstado("q3", false);
		af2.addEstado("q4", true);
		af2.setEstadoInicial("q3");
		af2.addTransicao("q3", 'a', "q3");
		af2.addTransicao("q3", 'b', "q4");
		af2.addTransicao("q4", 'b', "q4");

		AF inter = af.intersectar(af2);

		// Alfabeto da intersecção deve ser o mesmo
		assertEquals(af.getAlfabeto(), inter.getAlfabeto());

		// Estados da intersecção deve ser o produto cartesiano dos conjuntos dos estados originais
		assertEquals(Arrays.asList("[q0, q3]", "[q0, q4]", "[q1, q3]", "[q1, q4]"), inter.getEstados());

		// Estados finais deve ser o produto cartesiano dos conjuntos dos estados finais originais
		assertEquals(Arrays.asList("[q0, q4]"), inter.getEstadosFinais());

		// Estado inicial é a combinação dos estados iniciais originais
		assertEquals("[q0, q3]", inter.getEstadoInicial());

		// Transições devem ser da forma ([q0, q1], a) -> ((q0, a), (q1, a))
		assertEquals(Arrays.asList("[q1, q3]"), inter.transicao("[q0, q3]", 'a'));
		assertEquals(Arrays.asList("[q1, q4]"), inter.transicao("[q0, q3]", 'b'));
		assertEquals(Arrays.asList(), inter.transicao("[q0, q4]", 'a'));
		assertEquals(Arrays.asList("[q1, q4]"), inter.transicao("[q0, q4]", 'b'));
		assertEquals(Arrays.asList("[q0, q3]"), inter.transicao("[q1, q3]", 'a'));
		assertEquals(Arrays.asList("[q0, q4]"), inter.transicao("[q1, q3]", 'b'));
		assertEquals(Arrays.asList(), inter.transicao("[q1, q4]", 'a'));
		assertEquals(Arrays.asList("[q0, q4]"), inter.transicao("[q1, q4]", 'b'));
	}

	@Test(expected = FormaisException.class)
	public void testarIntersectarComAlfabetosDiferentes() throws FormaisException {
		AF af = new AF(new Alfabeto("ab"));
		AF af2 = new AF(new Alfabeto("abc"));
		af.intersectar(af2);
	}

	// ======================================================================================
	// Testes de determinização

	@Test
	public void testarMesclaDeEstadosToString() {
		AF af = new AF(new Alfabeto("abc"));
		List<String> list = new ArrayList<String>();
		AF.MesclaDeEstados mescla = af.new MesclaDeEstados(list);
		assertEquals("[]", mescla.toString());

		list.add("q4");
		mescla = af.new MesclaDeEstados(list);
		assertEquals("[q4]", mescla.toString());

		list.add("q1");
		mescla = af.new MesclaDeEstados(list);
		assertEquals("[q1, q4]", mescla.toString());

		list.add("A");
		mescla = af.new MesclaDeEstados(list);
		assertEquals("[A, q1, q4]", mescla.toString());

		list = new ArrayList<String>();
		list.add("[[q0]]");
		mescla = af.new MesclaDeEstados(list);
		assertEquals("[q0]", mescla.toString());

		list = new ArrayList<String>();
		list.add("[[[[q0]]]]");
		mescla = af.new MesclaDeEstados(list);
		assertEquals("[q0]", mescla.toString());
	}

	@Test
	public void testarDeterminizacao() throws FormaisException {
		AF af = new AF(new Alfabeto("01"));
		af.addEstado("q0", false);
		af.addEstado("q1", false);
		af.addEstado("q2", false);
		af.addEstado("q3", true);
		af.setEstadoInicial("q0");
		af.addTransicao("q0", '0', "q0");
		af.addTransicao("q0", '1', "q0");
		af.addTransicao("q0", '0', "q1");
		af.addTransicao("q0", '1', "q2");
		af.addTransicao("q1", '0', "q3");
		af.addTransicao("q2", '1', "q3");
		af.addTransicao("q3", '0', "q3");
		af.addTransicao("q3", '1', "q3");

		testarAlfabeto(af);
		testarEstadoInicial(af);
		testarMesclaDeEstadosTransicao(af);
		testarEstadosCriados(af);
		testarTransicoesCriadas(af);
		testarMesclaDeEstadosEhFinal(af);
		testarEstadosFinais(af);
	}

	@Test
	public void testarDeterminizacaoSemEstadoInicial() {
		AF af = new AF(new Alfabeto("01"));
		try {
			af.determinizar();
			fail("Determinização de AF sem estado inicial definido deve falhar");
		} catch (FormaisException e) {
		}
	}

	public void testarAlfabeto(AF af) throws FormaisException {
		// Alfabeto deve ser sempre o mesmo
		assertEquals(af.getAlfabeto(), af.determinizar().getAlfabeto());
	}

	public void testarEstadoInicial(AF af) throws FormaisException {
		AFD afd = af.determinizar();
		assertEquals("[q0]", afd.getEstadoInicial());
		assertEquals(false, afd.ehFinal("[q0]"));
	}

	public void testarMesclaDeEstadosTransicao(AF af) throws FormaisException {
		AF.MesclaDeEstados mescla = af.new MesclaDeEstados("q1");
		assertEquals(Arrays.asList("q3"), mescla.transicao('0').getEstados());

		mescla = af.new MesclaDeEstados("q0");
		assertEquals(Arrays.asList("q0", "q1"), mescla.transicao('0').getEstados());

		mescla = af.new MesclaDeEstados(Arrays.asList("q0", "q1"));
		assertEquals(Arrays.asList("q0", "q1", "q3"), mescla.transicao('0').getEstados());

		mescla = af.new MesclaDeEstados(Arrays.asList("q0", "q1"));
		assertEquals(Arrays.asList("q0", "q2"), mescla.transicao('1').getEstados());

		mescla = af.new MesclaDeEstados(Arrays.asList("q0", "q1", "q3"));
		assertEquals(Arrays.asList("q0", "q1", "q3"), mescla.transicao('0').getEstados());
	}

	public void testarEstadosCriados(AF af) throws FormaisException {
		AFD afd = af.determinizar();
		List<String> estados = afd.getEstados();
		assertEquals(5, estados.size());
		assertTrue(estados.contains("[q0]"));
		assertTrue(estados.contains("[q0, q1]"));
		assertTrue(estados.contains("[q0, q2]"));
		assertTrue(estados.contains("[q0, q1, q3]"));
		assertTrue(estados.contains("[q0, q2, q3]"));
	}

	public void testarTransicoesCriadas(AF af) throws FormaisException {
		AFD afd = af.determinizar();
		assertEquals(Arrays.asList("[q0, q1]"), afd.transicao("[q0]", '0'));
		assertEquals(Arrays.asList("[q0, q2]"), afd.transicao("[q0]", '1'));

		assertEquals(Arrays.asList("[q0, q1, q3]"), afd.transicao("[q0, q1]", '0'));
		assertEquals(Arrays.asList("[q0, q2]"), afd.transicao("[q0, q1]", '1'));

		assertEquals(Arrays.asList("[q0, q1]"), afd.transicao("[q0, q2]", '0'));
		assertEquals(Arrays.asList("[q0, q2, q3]"), afd.transicao("[q0, q2]", '1'));

		assertEquals(Arrays.asList("[q0, q1, q3]"), afd.transicao("[q0, q1, q3]", '0'));
		assertEquals(Arrays.asList("[q0, q2, q3]"), afd.transicao("[q0, q1, q3]", '1'));

		assertEquals(Arrays.asList("[q0, q1, q3]"), afd.transicao("[q0, q2, q3]", '0'));
		assertEquals(Arrays.asList("[q0, q2, q3]"), afd.transicao("[q0, q2, q3]", '1'));

		assertEquals(10, afd.getTransicoes().size());
	}

	public void testarMesclaDeEstadosEhFinal(AF af) throws FormaisException {
		MesclaDeEstados mescla = af.new MesclaDeEstados(Arrays.asList("q0"));
		assertEquals(false, mescla.ehFinal());

		mescla = af.new MesclaDeEstados(Arrays.asList("q0", "q1", "q2"));
		assertEquals(false, mescla.ehFinal());

		mescla = af.new MesclaDeEstados(Arrays.asList("q0", "q1", "q2", "q3"));
		assertEquals(true, mescla.ehFinal());

		mescla = af.new MesclaDeEstados(Arrays.asList("q3"));
		assertEquals(true, mescla.ehFinal());
	}

	public void testarEstadosFinais(AF af) throws FormaisException {
		AFD afd = af.determinizar();
		List<String> finais = afd.getEstadosFinais();
		assertEquals(2, finais.size());
		assertEquals(true, finais.contains("[q0, q1, q3]"));
		assertEquals(true, finais.contains("[q0, q2, q3]"));
	}

	@Test
	public void testarRunDeterministico() throws FormaisException {
		AF af = new AF(new Alfabeto("ab")); // (ab)* onde não é permitido 'aa'
		af.addEstado("q0", true);
		af.addEstado("q1", true);
		af.setEstadoInicial("q0");
		af.addTransicao("q0", 'b', "q0");
		af.addTransicao("q0", 'a', "q1");
		af.addTransicao("q1", 'b', "q0");

		assertTrue(af.run("abbbbababa"));
		assertFalse(af.run("aa"));
		assertFalse(af.run("abaa"));
		assertFalse(af.run("abbbbabbbabbbbabbbbaab"));
		assertTrue(af.run(""));
		assertTrue(af.run(null));
	}

	@Test
	public void testarRunNaoDeterministico() throws FormaisException {
		AF af = new AF(new Alfabeto("ab")); // (ab)* que termine com aa
		af.addEstado("q0", false);
		af.addEstado("q1", true);
		af.setEstadoInicial("q0");
		af.addTransicao("q0", 'a', "q0");
		af.addTransicao("q0", 'b', "q0");
		af.addTransicao("q0", 'a', "q1");

		assertTrue(af.run("aa"));
		assertFalse(af.run("babbaab"));
		assertTrue(af.run("bbaababababbbabbbbabaaaaaabbabbabaa"));
		assertTrue(af.run("bbaababababbbabbbbabaaaaaabbabbabaaaaaaaaa"));
		assertFalse(af.run(""));
		assertFalse(af.run(null));
	}

	@Test
	public void testarTextMatch() throws FormaisException {
		AFD af = (new ExprRegular("(abc)*")).getAFD();
		HashMap<Integer, Integer> hash = af.textSearch("aabc abcabc_abc");

		assertTrue(hash.containsKey(1));
		assertTrue(hash.containsValue(3));

		assertTrue(hash.containsKey(5));
		assertTrue(hash.containsValue(6));

		assertTrue(hash.containsKey(12));
		assertTrue(hash.containsValue(3));

		hash = af.textSearch("O albabeto abcabc começa com os 3 caracteres abc... e depois do abc é seguido por def...");
		assertTrue(hash.containsKey(11));
		assertTrue(hash.containsValue(6));

		assertTrue(hash.containsKey(45));
		assertTrue(hash.containsValue(3));

		assertTrue(hash.containsKey(64));
		assertTrue(hash.containsValue(3));
	}
}

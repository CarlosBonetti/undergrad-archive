# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../src/Jogo'
require_relative '../src/Jogador'

class JogoTest < Test::Unit::TestCase

  def setup
    @jogo = Jogo.new
    @jogo.criar_jogador 'Napoleão'
    @jogo.criar_jogador 'César'
    @jogo.disable_log

    @napoleao = @jogo.jogadores[0]
    @cesar = @jogo.jogadores[1]

    @jogo_sem_jogadores = Jogo.new

    @alexandre = Usuario.new 'alexandre', '0grand3'
    @leonidas = Usuario.new 'leonidas', 'raul'
  end

  def test_criar_jogador
    @jogo_sem_jogadores.criar_jogador 'Napoleão'
    assert_equal 1, @jogo_sem_jogadores.jogadores.size
    assert_equal 'Napoleão', @jogo_sem_jogadores.jogadores[0].nome

    @jogo_sem_jogadores.criar_jogador 'César'
    assert_equal 2, @jogo_sem_jogadores.jogadores.size
    assert_equal 'César', @jogo_sem_jogadores.jogadores[1].nome
  end

  def test_adicionar_usuario
    @jogo_sem_jogadores.adicionar_usuario @alexandre
    assert_equal 1, @jogo_sem_jogadores.jogadores.size
    assert_equal 'alexandre', @jogo_sem_jogadores.jogadores[0].nome

    @jogo_sem_jogadores.adicionar_usuario @leonidas
    assert_equal 2, @jogo_sem_jogadores.jogadores.size
    assert_equal 'leonidas', @jogo_sem_jogadores.jogadores[1].nome
  end

  def test_iniciar_sem_jogadores
    assert_raise MinimoDeJogadoresException do
      @jogo_sem_jogadores.iniciar
    end
  end

  def test_criar_jogador_depois_de_iniciada_a_partida
    @jogo.iniciar

    assert_raise NovoJogadorException do
      @jogo.criar_jogador 'Colombo'
    end
  end

  def test_iniciar
    @jogo.iniciar

    # Testar se os mapas foram criados:
    assert_equal false, @jogo.mapa.campos.empty?
    assert_equal false, @jogo.mapa.cidades.empty?

    # Testa se existe jogador atual
    assert_equal true, @jogo.jogadores.include?(@jogo.jogador_atual)

    # Testa se foram atribuídas cidades aos jogadores
    @jogo.jogadores.each do |jogador|
      assert_equal 1, jogador.cidades.size
    end
  end

  def test_passar_a_vez
    @jogo.iniciar
    primeiro_jogador = @jogo.jogador_atual
    assert_equal 1, @jogo.turno

    @jogo.passar_a_vez
    segundo_jogador = @jogo.jogador_atual
    assert_equal 2, @jogo.turno
    assert_not_equal primeiro_jogador, segundo_jogador

    @jogo.passar_a_vez
    assert_equal primeiro_jogador, @jogo.jogador_atual
  end

  def test_remover_jogador_perdedor_ao_passar_a_vez
    @jogo.iniciar

    paris = @napoleao.cidades[0]
    @napoleao.cidades.clear
    @napoleao.tropas.clear

    assert_equal 2, @jogo.jogadores.size
    @jogo.jogadores.size.times do
      @jogo.passar_a_vez
    end
    assert_equal 1, @jogo.jogadores.size
    assert_equal false, @jogo.jogadores.include?(@napoleao)
  end

  def test_terminou?
    @jogo.iniciar
    assert_equal false, @jogo.terminou?
    @jogo.jogadores.delete @napoleao
    assert_equal true, @jogo.terminou?
  end

  def testar_inclusao_de_jogadores_no_log_batalha
    assert_equal true, @jogo.log.jogadores.include?(@napoleao)
    assert_equal true, @jogo.log.jogadores.include?(@cesar)
  end

  def testar_configuracao_do_log_batalha_no_final_da_partida
    @jogo.iniciar
    @napoleao.cidades.clear
    @napoleao.tropas.clear
    @jogo.passar_a_vez
    @jogo.passar_a_vez
    assert_equal true, @jogo.terminou?
    assert_equal @cesar, @jogo.log.vencedor
    assert_equal true, @jogo.log.turnos <= 2 && @jogo.log.turnos > 0
  end

end

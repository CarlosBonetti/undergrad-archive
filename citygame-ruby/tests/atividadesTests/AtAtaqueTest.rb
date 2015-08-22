# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../../src/Jogador'
require_relative '../../src/Cidade'
require_relative '../../src/atividades/AtAtaque'
require_relative '../../src/atividades/AtAtaqueRural'

class AtAtaqueTest < Test::Unit::TestCase

  def setup
    @jogador_atacante = Jogador.new 1, "Napoleão"
    @jogador_defensor = Jogador.new 2, "Alexandre"

    @cidade = Cidade.new 1

    @atacante = Tropa.new @jogador_atacante, 10, @cidade
    @defensora = Tropa.new @jogador_defensor, 20, @cidade

    @ataque = AtAtaqueRural.new @cidade, @atacante, @defensora
  end

  def testar_se_o_numero_de_soldados_diminui_para_ambos_os_lados
    n_atacantes = @atacante.tamanho
    n_defensores = @defensora.tamanho

    @ataque.executar 1
    assert @atacante.tamanho < n_atacantes
    assert @defensora.tamanho < n_defensores
  end

  def testar_se_o_ataque_eh_encerrado_se_uma_tropa_for_aniquilada
    i = 1
    last_result = false
    while @atacante.tamanho >= 1 and @defensora.tamanho >= 1 do
      last_result = @ataque.executar i
      i += 1
    end

    assert last_result
  end

  def testar_se_o_ataque_eh_encerrado_se_tropa_atacante_fugir
    @atacante.ocupar_local nil
    assert @ataque.executar(1)
  end

  def testar_se_o_ataque_eh_encerrado_se_tropa_defensora_fugir
    @defensora.ocupar_local nil
    assert @ataque.executar(1)
  end

  def testar_se_tropa_aniquilada_eh_excluida_ao_final_da_batalha
    i = 1
    while !@ataque.executar(i) do
      i += 1
    end

    assert_equal 1, @ataque.local.tropas.size
  end

  def testar_se_tropa_concatenada_eh_adicionada_a_tropa_da_batalha
    nova_tropa_atacante = Tropa.new @jogador_atacante, 50, @cidade
    assert_equal 60, @ataque.tropa_atacante.tamanho

    nova_tropa_defensora = Tropa.new @jogador_defensor, 100, @cidade
    assert_equal 120, @ataque.tropa_defensora.tamanho
  end

end

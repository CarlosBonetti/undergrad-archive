# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../../src/Jogador'
require_relative '../../src/Cidade'
require_relative '../../src/atividades/AtAtaqueUrbano'

class AtAtaqueUrbanoTest < Test::Unit::TestCase

  def setup
    @jogador_atacante = Jogador.new 1, "Napoleão"
    @jogador_defensor = Jogador.new 2, "Alexandre"

    @cidade = Cidade.new 1

    @atacante = Tropa.new @jogador_atacante, 10, @cidade
    @defensora = Tropa.new @jogador_defensor, 10, @cidade

    @ataque = AtAtaqueUrbano.new @cidade, @atacante, @defensora
  end

  def testar_se_tropa_defensora_tem_bonus_de_defesa
    assert_equal @atacante.tamanho, @defensora.tamanho
    @ataque.executar 1
    assert @atacante.tamanho < @defensora.tamanho

    i = 1
    while !@ataque.executar(i) do
      i += 1
    end

    assert_equal 0, @atacante.tamanho
    assert_not_equal 0, @defensora.tamanho
    assert_equal 1, @cidade.tropas.size
    assert_equal @defensora, @cidade.tropas[0]
  end

end

# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../../src/Jogador'
require_relative '../../src/Local'
require_relative '../../src/atividades/AtAtaqueRural'

class AtAtaqueRuralTest < Test::Unit::TestCase

  def setup
    @jogador_atacante = Jogador.new 1, "Napoleão"
    @jogador_defensor = Jogador.new 2, "Alexandre"

    @local = Local.new 1

    @atacante = Tropa.new @jogador_atacante, 50, @local
    @defensora = Tropa.new @jogador_defensor, 50, @local

    @ataque = AtAtaqueRural.new @local, @atacante, @defensora
  end

  def testar_se_batalha_eh_justa
    # Como o tamanho das tropas é igual, a força delas deve ser equivalente

    assert_equal @atacante.tamanho, @defensora.tamanho
    @ataque.executar 1
    assert_equal @atacante.tamanho, @defensora.tamanho

    i = 1
    while !@ataque.executar(i) do
      i += 1
    end

    assert_equal 0, @defensora.tamanho
    assert_equal 0, @atacante.tamanho
  end

end

# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../../src/Jogador'
require_relative '../../src/Local'
require_relative '../../src/Cidade'
require_relative '../../src/atividades/AtConquista'

class AtConquistaTest < Test::Unit::TestCase

  def setup
    @jogador = Jogador.new 1, "Napoleão"
    @cidade = Cidade.new 1
    @tropa = Tropa.new @jogador, 10, @cidade
    @at = AtConquista.new @tropa, @cidade
  end

  def testar_conquista
    AtConquista::TURNOS_CONQUISTA.times do |i|
      if i == AtConquista::TURNOS_CONQUISTA - 1 then
        assert @at.executar(i), "Falhou em terminar a atividade"
      else
        assert !@at.executar(i), "Falhou em postergar a atividade"
      end
    end

    assert @jogador.cidades.include?(@cidade)
    assert_equal @jogador, @cidade.jogador
  end

  def testar_se_conquista_eh_cancelada_ao_mover_a_tropa
    @at.executar(1)
    novo_local = Local.new 2
    @tropa.movimentar @tropa.tamanho, novo_local
    assert @at.executar(2)
    assert_not_equal @jogador, @cidade.jogador
  end

  def testar_se_conquista_eh_postergada_em_batalha
    jogador_inimigo = Jogador.new 2, "Alexandre"
    tropa_inimiga = Tropa.new jogador_inimigo, 10, @cidade
    turnos_restantes_anterior = @at.turnos_restantes
    @at.executar(1)
    turnos_restantes_posterior = @at.turnos_restantes
    assert_equal turnos_restantes_anterior, turnos_restantes_posterior

    # Retirar exército inimigo
    tropa_inimiga.ocupar_local nil

    @at.executar(2)
    turnos_restantes_posterior = @at.turnos_restantes
    assert_equal turnos_restantes_anterior - 1, turnos_restantes_posterior
  end

end

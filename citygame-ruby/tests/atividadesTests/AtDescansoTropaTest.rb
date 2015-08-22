# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../../src/Jogador'
require_relative '../../src/Local'
require_relative '../../src/Tropa'
require_relative '../../src/atividades/AtDescansoTropa'

class AtDescansoTropaTest < Test::Unit::TestCase

  def setup
    @jogador = Jogador.new 1, "Napoleão"
    @local = Local.new 1
    @tropa = Tropa.new @jogador, 10, nil
    @at = AtDescansoTropa.new @tropa
  end

  def testar_regeneracao_de_stamina
    Tropa::MAX_STAMINA.times do |i|
      @tropa.movimentar 10, @local
    end

    assert_equal true, @tropa.cansada?
    @at.executar 1
    assert_equal false, @tropa.cansada?
  end

  def testar_exclusao_imediada_da_atividade
    assert_equal true, @at.executar(1)
  end

end

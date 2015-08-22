# -*- encoding : utf-8 -*-

require_relative 'Atividade'

# Atividade Descanso Tropa: regenera stamina de uma tropa
class AtDescansoTropa < Atividade
  attr_reader :tropa

  def initialize tropa
    @tropa = tropa
  end

  def executar turno_atual
    @tropa.regenerar_stamina 1
    return true
  end

end

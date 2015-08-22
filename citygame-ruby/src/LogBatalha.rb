# -*- encoding : utf-8 -*-

require_relative 'Usuario'

class LogBatalha
  attr_accessor :jogadores, :turnos, :vencedor

  def initialize
    @jogadores = []
  end

  # Adiciona um jogador ao log
  # @param [Usuario]
  def adicionar_jogador jogador
    @jogadores.push jogador
  end

end

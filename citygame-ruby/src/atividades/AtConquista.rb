# -*- encoding : utf-8 -*-

require_relative 'Atividade'

# Atividade conquista
# A atividade é criada no momento de ocupação de uma tropa em uma cidade abandonada. Após N turnos, a cidade é considerada conquistada e passa a ser uma colônia do jogador que possui a tropa
class AtConquista < Atividade
  attr_reader :turnos_restantes

  TURNOS_CONQUISTA = 3 # Número de turnos necessários para a tropa conquistar a cidade

  def initialize tropa, cidade
    @tropa = tropa
    @cidade = cidade
    @turnos_restantes = TURNOS_CONQUISTA
  end

  def executar turno_atual
    if @tropa.nil? or @tropa.tamanho < 1 or @tropa.local != @cidade or @cidade.jogador == @tropa.jogador then
      # Cancela a conquista se a tropa não existe OU não está mais na cidade OU a cidade já pertence ao jogador
      return true
    end

    # Conta como um turno de conquista se não estiverem ocorrendo batalhas na cidade
    @turnos_restantes -= 1 if @cidade.tropas.size == 1

    if @turnos_restantes <= 0 then # Cidade conquistada!
      conquistar()
      return true
    end

    return false
  end

protected

  def conquistar
    @tropa.jogador.atribuir_cidade @cidade
  end

end

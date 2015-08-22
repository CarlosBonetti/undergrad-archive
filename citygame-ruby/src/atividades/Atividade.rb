# -*- encoding : utf-8 -*-

# Uma atividade representa uma ação executada toda vez ao passar de turno
class Atividade

  # O método executar é chamado uma vez para cada objeto atividade da fila de atividades quando o turno é passado
  # Caso o método retorne true a atividade é considerada encerrada e retirada da fila de atividades
  # Caso o método retorne falso a atividade não foi terminada e o objeto é mantido na fila de atividades para executar novamente no próximo turno
  def executar turno_atual
    return true
  end

end

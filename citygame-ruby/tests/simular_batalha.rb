# -*- encoding : utf-8 -*-

# Utilizada para simulação de batalhas
# Permite configurar uma batalha com número de soldados e taxa de tecnologia para ambos os lados e visualizar as perdas resultantes em cada rodada
# Exemplo de uso: ruby tests/simular_batalha 30 3.5 40 2
# => Simula uma batalha entre uma tropa com 30 soldados e 3.5 de tecnologia contra uma tropa com 40 soldados e 2 de tecnologia

require_relative '../src/Jogador'
require_relative '../src/Local'

class SimulacaoBatalha
  attr_reader :local, :tropa1, :tropa2

  def initialize tam_tropa1, tec_tropa1, tam_tropa2, tec_tropa2
    jogador1 = Jogador.new 1, "Jogador 1"
    jogador1.tecnologia = tec_tropa1.to_f

    jogador2 = Jogador.new 2, "Jogador 2"
    jogador2.tecnologia = tec_tropa2.to_f

    @local = Local.new 1

    @tropa1 = Tropa.new jogador1, tam_tropa1.to_i, @local
    @tropa2 = Tropa.new jogador2, tam_tropa2.to_i, @local
    @turno = 0
  end

  # Executa uma rodada da batalha
  def executar_turno
    @turno += 1
    @tropa2.jogador.executar_atividades @turno
  end

  # Imprime um cabeçalho para as estatísticas da batalha
  def imprimir_cabecalho
    print "             Jog1 / Jog2"
    puts ""
    print "Tecnologia: "
    print '%.3f' % @tropa1.jogador.tecnologia.to_f
    print "  / "
    print '%.3f' % @tropa2.jogador.tecnologia.to_f
    puts ""
  end

  # Imprime estatísticas atuais da batalha
  def imprimir_status
    print "Turno " + '%4d' % @turno + ': '
    print '%4d' % @tropa1.tamanho
    print "  / "
    print '%4d' % @tropa2.tamanho
    puts ""
  end

  # Retorna verdadeiro caso a batalha tenha terminado
  def terminou?
    @tropa1.tamanho < 1 or @tropa2.tamanho < 1
  end

end

if ARGV.size != 4 then
  r = Random.new
  ARGV[0] = r.rand(1...42)
  ARGV[1] = r.rand(1...42)
  ARGV[2] = r.rand(1...42)
  ARGV[3] = r.rand(1...42)
# elsif ARGV.size != 4
#  abort "Número de argumentos inválidos"
end

batalha = SimulacaoBatalha.new ARGV[0], ARGV[1], ARGV[2], ARGV[3]
batalha.imprimir_cabecalho
batalha.imprimir_status

while !batalha.terminou?
  batalha.executar_turno
  batalha.imprimir_status
end

# -*- encoding : utf-8 -*-

require_relative 'exceptions'
require_relative 'Local'
require_relative 'Tropa'

class Cidade < Local
  attr_reader :g_tropas, :g_tecnologia
  attr_accessor :jogador
  attr_reader :status

  STATUS_PROTEGIDA  = 1
  STATUS_SOB_ATAQUE = 2
  STATUS_SOB_CERCO  = 3

  def initialize id
    super id
    @g_tropas = 10
    @g_tecnologia = 0
    @jogador = nil
    @status = STATUS_PROTEGIDA
  end

  def is_cidade
    return true
  end

  # Balanceia os recursos gerados pela cidade
  # Parâmetros: tropas (inteiro), tecnologia (inteiro)
  # A soma de tropas e tecnologia não deve ultrapassar 10. Se isso acontecer, um CitygameException é lançado
  def balancear_recursos tropas, tecnologia
    raise SomaDeRecursosException, 'A soma das tropas e tecnologia não deve ser maior que 10' if tropas + tecnologia > 10

    @g_tropas = tropas
    @g_tecnologia = tecnologia
  end

  def gerar_tropas
    if @status != STATUS_SOB_CERCO && @jogador != nil
      nova_tropa = Tropa.new(@jogador, @g_tropas, self)
    end
  end

  # Retorna a taxa de tecnologia gerada pela cidade
  # @return [Numeric]
  def taxa_tecnologia
    return @g_tecnologia if @status != STATUS_SOB_CERCO && @jogador != nil
    return 0
  end

  # Retorna verdadeiro se nenhum jogador está em posse da cidade
  # @return [Boolean]
  def abandonada?
    return @jogador == nil
  end

end

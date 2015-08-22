# -*- encoding : utf-8 -*-

class Jogador
  attr_reader :id, :nome
  attr_reader :cidades, :tropas
  attr_reader :fila_de_atividades
  attr_accessor :tecnologia

  alias_method :username, :nome

  def initialize id, nome
    @id = id
    @nome = nome
    @tecnologia = 1
    @cidades = []
    @tropas = []
    @fila_de_atividades = []
  end

  def gerar_recursos
    tec = 0
    @cidades.each do |cidade|
      cidade.gerar_tropas()
      tec += cidade.taxa_tecnologia()
    end
    tec /= 100.0
    @tecnologia = tecnologia*(1 + tec)
  end

  # Atribui uma cidade ao jogador, adicionando na lista de cidades conquistadas e alterando a referência do jogador no objeto cidade
  # @param [Cidade]
  # @return [Boolean] true se a cidade foi atribuída ou false se a cidade já pertencia ao jogador
  def atribuir_cidade cidade
    return false if @cidades.include? cidade
    if cidade.jogador then
      atual_proprietario = cidade.jogador
      atual_proprietario.cidades.delete cidade
    end
    atual_proprietario = self
    cidade.jogador = atual_proprietario
    @cidades.push cidade
    return true
  end

  # Adiciona uma nova atividade à fila do jogador
  # @param [Atividade]
  def adicionar_atividade atividade
    @fila_de_atividades.push atividade
  end

  # Executa todas as atividades da fila do jogador e exclui da fila as atividades terminadas
  def executar_atividades turno_atual
    @fila_de_atividades.delete_if do |atividade|
      atividade.executar turno_atual
    end
  end

  # Checa se o jogador perdeu o jogo (caso não possua mais nenhuma cidade sob seu domínio E não tenha mais nenhuma tropa viva)
  # @return [Boolean]
  def perdeu?
    return @cidades.empty? && @tropas.empty?
  end

end

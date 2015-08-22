# -*- encoding : utf-8 -*-

require_relative 'Local'
require_relative 'Jogador'
require_relative 'atividades/AtConquista'
require_relative 'atividades/AtDescansoTropa'

class Tropa
  attr_reader :tamanho, :jogador, :stamina
  attr_accessor :local

  MAX_STAMINA = 1

  # @param [Jogador] jogador
  # @param [Fixnum] tamanho
  # @param [Local] local
  def initialize jogador, tamanho, local
    @jogador = jogador
    @tamanho = tamanho
    @stamina = MAX_STAMINA
    jogador.tropas.push self
    ocupar_local local
  end

  def forca
    return (@jogador.tecnologia ** 3) * @tamanho
  end

  # Movimenta uma quantidade de soldados da tropa para um novo local adjacente. Retorna verdadeiro em caso de sucesso e falso caso o movimento não seja permitido em função de falta de stamina
  # @param [Fixnum] n_soldados Número de soldados a serem movidos
  # @param [Local] local_novo
  # @return [Boolean]
  def movimentar n_soldados, local_novo
    return false if cansada?
    tropa_em_movimento = separar n_soldados
    tropa_em_movimento.ocupar_local local_novo
    tropa_em_movimento.stamina -= 1
    @jogador.adicionar_atividade AtDescansoTropa.new(tropa_em_movimento)
    return true
  end

  # Concatena a tropa com outra, aumentando o número de soldados da tropa do objeto atual e reduzindo a 0 o tamanho da tropa parâmetro
  # @param [Tropa] tropa
  def concatenar tropa
    raise ArgumentError, 'Não é possível concatenar tropas de diferentes jogadores' unless @jogador.eql? tropa.jogador

    @tamanho += tropa.tamanho
    tropa.tamanho = 0
    @jogador.tropas.delete tropa # Remove a referência da tropa concatenada do jogador
    return self
  end

  # Reduz o número de soldados na tropa.
  # @param [Integer] n_soldados: Número de soldados a serem reduzidos da tropa atual
  def aniquilar n_soldados
    raise ArgumentError, "Impossível aniquilar um número de soldados negativo" if n_soldados < 0

    @tamanho -= n_soldados
    @tamanho = 0 if @tamanho < 1
    @jogador.tropas.delete self if @tamanho == 0
  end

  # Faz a tropa ocupar um novo local
  # @param [Local] local: novo local a ser ocupado
  def ocupar_local local
    # Caso esteja tentando ocupar o mesmo local, pára
    return if @local == local

    # Sai do local atual:
    @local.desocupar self unless @local.nil?

    # Cria a referência para o novo local
    @local = local

    # Ocupa o novo local
    @local.ocupar self unless @local.nil?

    # Começa uma conquista se o local é uma cidade
    if !@local.nil? and @local.is_cidade and @local.jogador != @jogador
      @jogador.adicionar_atividade AtConquista.new(self, @local)
    end
  end

  # Retorna verdadeiro se a tropa está cansada (sem stamina)
  # @return [Boolean]
  def cansada?
    return @stamina <= 0
  end

  # Regenera a stamina da tropa
  # @param [Integer] add : stamina a ser adicionada para a tropa
  def regenerar_stamina add = 1
    raise ArgumentError, "Stamina deve ser positiva" if add < 0
    @stamina += add
    @stamina = MAX_STAMINA if @stamina > MAX_STAMINA
  end

protected

  def tamanho=(n)
    @tamanho = n
  end

  def stamina=(n)
    @stamina = n
  end

private

  # Retorna um novo objeto Tropa com o tamanho estipulado. Caso o tamanho de separação seja igual ao tamanho da tropa, a própria tropa é retornada
  # @param [Fixnum] n_soldados representa a quantidade de soldados que serão separados da tropa inicial
  # @return [Tropa] Uma nova tropa com valores de ataque atualizados
  def separar n_soldados
    return self if n_soldados == @tamanho

    if n_soldados < 1 || n_soldados > @tamanho then
      raise NumeroDeTropasException, "Número de soldados fora do intervalo permitido [1, #{@tamanho}]"
    end

    @tamanho -= n_soldados
    tropa_separada = Tropa.new(@jogador, n_soldados, nil)
    return tropa_separada
  end

end

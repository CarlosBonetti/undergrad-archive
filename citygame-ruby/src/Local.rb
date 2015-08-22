# -*- encoding : utf-8 -*-

require_relative 'Tropa'
require_relative 'Jogador'
require_relative 'atividades/AtAtaqueRural'
require_relative 'atividades/AtAtaqueUrbano'

class Local

  attr_reader :id, :tropas

  # @param [Fixnum] id identificador do Local
  def initialize id
    @id = id
    @tropas = []
  end

  def is_cidade
    return false
  end

  # Adiciona a tropa ao local atual, concatenando com tropas amigas
  # @param [Tropa] tropa
  def ocupar tropa
    # Checa se existem tropas amigas no local, se sim, mescla as tropas
    concatenada = checar_concatenacao tropa

    if concatenada.nil? then
      @tropas.push tropa
      checar_batalhas tropa
    else
      checar_batalhas concatenada
    end
  end

  # Retira a tropa do local atual
  # @param [Tropa] tropa
  def desocupar tropa
    @tropas.delete tropa
  end

  # Elimina as tropas nulas ou de tamanho 0 do local
  def limpar_os_mortos
    @tropas.each do |tropa|
      @tropas.delete tropa if tropa.nil? or tropa.tamanho < 1
    end
  end

  # Retorna a tropa do jogador presente no local, ou nil se não existem tropas do jogador no local
  # @param [Jogador]
  # @return [Tropa]
  def get_tropa_jogador jogador
    tropa_jogador = nil

    @tropas.each do |tropa|
      if tropa.jogador == jogador then
        tropa_jogador = tropa
        break
      end
    end

    return tropa_jogador
  end

protected

  # Checa se a nova tropa pode ser concatenada com alguma tropa amiga presente no local
  # @param [Tropa] tropa_a_concatenar : Nova tropa a ser concatenada
  # @return [Tropa] Retorna a tropa na qual a nova tropa foi concatenada ou nil se não houve concatenação
  def checar_concatenacao tropa_a_concatenar
    @tropas.each do |outra_tropa|
      if outra_tropa.jogador == tropa_a_concatenar.jogador and outra_tropa != tropa_a_concatenar
        outra_tropa.concatenar(tropa_a_concatenar)
        return outra_tropa
      end
    end

    return nil
  end

  # Checa se há tropas inimigas no local. Caso existam, gera as batalhas das tropas, duas a duas
  # @param [Tropa] tropa_atacante : Ultima tropa a entrar na local, responsável por iniciar o ataque
  def checar_batalhas tropa_atacante
    @tropas.each do |tropa|
      if !tropa.nil? and tropa != tropa_atacante
        if is_cidade then
          tropa_atacante.jogador.adicionar_atividade AtAtaqueUrbano.new(self, tropa_atacante, tropa)
        else
          tropa_atacante.jogador.adicionar_atividade AtAtaqueRural.new(self, tropa_atacante, tropa)
        end
      end
    end
  end

end

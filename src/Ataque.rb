# -*- encoding : utf-8 -*-

require_relative 'Local'
require_relative 'Tropa'

# Classe abstract para uso do design pattern Template.
class Ataque

  attr_reader :local
  attr_reader :tropa_atacante, :tropa_defensora
  attr_reader :forca_do_atacante, :forca_do_defensor

  PHI = (1 + Math.sqrt(5))/2

  # @param [Local] local onde esta sendo realizada a batalha
  # @param [Tropa] tropa_atacante
  # @param [Tropa] tropa_defensora
  def initialize local, tropa_atacante, tropa_defensora
    @local = local
    @tropa_atacante = tropa_atacante
    @tropa_defensora = tropa_defensora
    @forca_do_atacante = 0
    @forca_do_defensor = 0
  end

  # @return [FalseClass, TrueClass] Executa batalha e retorna false se nenhuma tropa foi derrotada.
  def executar
    @forca_do_atacante.atualizar_forca
    @forca_do_defensor.atualizar_forca

    @forca_do_atacante = @tropa_atacante.forca
    @forca_do_defensor = calcula_forca_de_defesa

    @tropa_atacante.atualizar_valores_pos_batalha calculo_de_perda @tropa_defensora
    @tropa_defensora.atualizar_valores_pos_batalha calculo_de_perda @tropa_atacante

    @local.limpar_os_mortos
    return terminou?
  end


  private

  def calcula_forca_de_defesa
  end

  # @param [Fixnum] forca_inimiga
  # @return [Fixnum] valor inteiro representando o decrescimo na tropa inimiga
  def calculo_de_perda forca_inimiga
    if (@forca_do_atacante >= forca_do_defensor)
      d = @forca_do_atacante - @forca_do_defensor
    else
      d = @forca_do_defensor - @forca_do_atacante
    end
    #interferência quadrática
    ((forca_inimiga/d)**2).floor
  end


  # @return [FalseClass,TrueClass] retorna true se batalha acabou
  def terminou?
    @tropa_atacante.tamanho < 1 or @tropa_defensora.tamanho < 1 or @tropa_atacante.local != @tropa_defensora.local
  end

end

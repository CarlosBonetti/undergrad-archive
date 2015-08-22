# -*- encoding : utf-8 -*-

require_relative 'AtAtaque'

# Ataque na cidade, ou seja, urbano
# Design Pattern: Template
class AtAtaqueUrbano < AtAtaque


  # @param [Local] local onde esta sendo realizada a batalha
  # @param [Tropa] tropa_atacante
  # @param [Tropa] tropa_defensora
  def initialize local, tropa_atacante, tropa_defensora
    super local, tropa_atacante, tropa_defensora
  end

  # Tropa que defende na cidade recebe um adicional de 10*tecnologia
  def calcula_forca_de_defesa
    @tropa_defensora.jogador.tecnologia*10 + @tropa_defensora.forca
  end

end

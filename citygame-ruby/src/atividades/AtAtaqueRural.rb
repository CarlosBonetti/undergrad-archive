# -*- encoding : utf-8 -*-

require_relative 'AtAtaque'

# Ataque no campo, ou seja, rural.
# Design Pattern: Template
class AtAtaqueRural < AtAtaque


  # @param [Local] local onde esta sendo realizada a batalha
  # @param [Tropa] tropa_atacante
  # @param [Tropa] tropa_defensora
  def initialize local, tropa_atacante, tropa_defensora
    super local, tropa_atacante, tropa_defensora
  end

  #Tropa de defesa não recebe adicional de força em area rural
  def calcula_forca_de_defesa
    @tropa_defensora.forca
  end

end

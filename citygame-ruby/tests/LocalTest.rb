# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../src/Local'
require_relative '../src/Jogador'
require_relative '../src/Tropa'

class LocalTest < Test::Unit::TestCase

  def setup
    @jogador = Jogador.new 1, "Napoleão"
    @local = Local.new 1

    @tropa1 = Tropa.new @jogador, 10, nil
    @tropa2 = Tropa.new @jogador, 15, nil

    @jogador_inimigo = Jogador.new 2, "Alexandre"
    @tropa_inimiga = Tropa.new @jogador_inimigo, 7, nil
  end

  def testar_se_local_eh_cidade
    assert_equal false, @local.is_cidade
  end

  def testar_se_ocupar_adiciona_tropa_na_lista_de_tropas
    @local.ocupar @tropa1
    assert_equal @tropa1, @local.tropas[0]
    assert_equal 1, @local.tropas.size
  end

  def testar_se_ocupar_concatena_tropas_amigas
    @local.ocupar @tropa1
    @local.ocupar @tropa2

    assert_equal @tropa1.tamanho + @tropa2.tamanho, @local.tropas[0].tamanho
    assert_equal 1, @local.tropas.size
    assert_equal @jogador, @local.tropas[0].jogador
  end

  def testar_se_ocupar_cria_batalhas_entre_tropas_inimigas
    @local.ocupar @tropa1
    @local.ocupar @tropa_inimiga

    assert_equal 2, @local.tropas.size
    at_ataque = @jogador_inimigo.fila_de_atividades[0]

    assert_equal @tropa1, at_ataque.tropa_defensora
    assert_equal @tropa_inimiga, at_ataque.tropa_atacante
    assert_equal @local, at_ataque.local
  end

  def testar_se_batalha_criada_eh_rural
    @local.ocupar @tropa1
    @local.ocupar @tropa_inimiga
    at_ataque = @jogador_inimigo.fila_de_atividades[0]

    assert at_ataque.kind_of?(AtAtaqueRural)
  end

  def testar_desocupar
    @local.ocupar @tropa1
    @local.desocupar @tropa1
    assert_equal 0, @local.tropas.size
  end

  def testar_desocupar_de_tropas_inexistentes
    @local.ocupar @tropa1
    @local.desocupar @tropa2

    assert_equal 1, @local.tropas.size
    assert_equal @tropa1, @local.tropas[0]
  end

  def testar_limpar_os_mortos
    tropa_fantasma = Tropa.new @jogador, 0, nil
    @local.ocupar tropa_fantasma

    assert_equal 1, @local.tropas.size
    @local.limpar_os_mortos
    assert_equal 0, @local.tropas.size
  end

  def testar_get_tropa_jogador
    @local.ocupar @tropa_inimiga
    @local.ocupar @tropa1

    tropa = @local.get_tropa_jogador @jogador
    assert_equal @tropa1, tropa
  end

  def testar_get_tropa_inexistente_de_jogador
    tropa = @local.get_tropa_jogador @jogador
    assert_equal nil, tropa
  end

end

# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../src/Tropa'
require_relative '../src/Cidade'
require_relative '../src/Local'

class TropaTest < Test::Unit::TestCase

  def setup
    @jogador = Jogador.new 1, "Napoleão"
    @cidade = Cidade.new 1
    @tropa = Tropa.new @jogador, 10, @cidade

    @jogador_inimigo = Jogador.new 2, "Alexandre"
    @tropa_inimiga = Tropa.new @jogador_inimigo, 10, @cidade

    @local2 = Local.new 2
  end

  def testar_ocupacao_da_tropa_no_momento_de_criacao
    nova_cidade = Cidade.new 3
    tropa = Tropa.new @jogador, 3, nova_cidade
    assert_equal nova_cidade, tropa.local
    assert_equal 1, nova_cidade.tropas.size
    assert_equal tropa, nova_cidade.tropas[0]
  end

  def testar_associacao_de_tropa_ao_jogador_no_momento_de_criacao
    assert_equal true, @jogador.tropas.include?(@tropa)
    tropa = Tropa.new @jogador, 3, Cidade.new(2)
    assert_equal true, @jogador.tropas.include?(tropa)
    assert_equal 2, @jogador.tropas.size
  end

  def testar_forca
    assert @tropa.forca == @tropa.tamanho * @jogador.tecnologia
  end

  def testar_movimentar_toda_a_tropa
    @tropa.movimentar @tropa.tamanho, @local2

    assert !@cidade.tropas.include?(@tropa) # Deve ter saído da cidade de origem
    assert @local2.tropas.include?(@tropa) # Deve ter chegado na cidade de destino
    assert_equal @local2, @tropa.local
  end

  def testar_movimentar_tropas_inexistentes
    assert_raise NumeroDeTropasException do
      @tropa.movimentar 20000, @local2
    end

    assert_equal @cidade, @tropa.local
  end

  def testar_movimentar_parte_da_tropa
    @tropa.movimentar 2, @local2
    assert_equal 2, @local2.tropas[0].tamanho
    assert_equal 8, @cidade.tropas[0].tamanho
  end

  def testar_associacao_de_tropa_ao_jogador_ao_separar_tropa
    assert_equal 1, @jogador.tropas.size
    @tropa.movimentar 2, @local2
    assert_equal 2, @jogador.tropas.size
  end

  def testar_concatenar_tropas_de_diferentes_jogadores
    assert_raise ArgumentError do
      @tropa.concatenar @tropa_inimiga
    end
  end

  def testar_concatenar_tropas
    tropa2 = Tropa.new @jogador, 4, @cidade
    @tropa.concatenar tropa2
    assert_equal 14, @tropa.tamanho
    assert_equal 0, tropa2.tamanho
  end

  def testar_se_referencia_da_tropa_concatenada_eh_removida_do_jogador
    tropa2 = Tropa.new @jogador, 4, @cidade
    @tropa.concatenar tropa2
    assert_equal 1, @jogador.tropas.size
    assert_equal false, @jogador.tropas.include?(tropa2)
    assert_equal true, @jogador.tropas.include?(@tropa)
  end

  def testar_aniquilar
    @tropa.aniquilar 2
    assert_equal 8, @tropa.tamanho
    @tropa.aniquilar 100
    assert_equal 0, @tropa.tamanho

    # Aniquilar um valor negativo:
    assert_raise ArgumentError do
      @tropa.aniquilar -10
    end
  end

  def testar_se_referencia_da_tropa_aniquilada_eh_removida_do_jogador
    @tropa.aniquilar @tropa.tamanho
    assert_equal false, @jogador.tropas.include?(@tropa)
  end

  def testar_concatenar_tropas_ao_mover
    tropa2 = Tropa.new @jogador, 7, nil
    tropa2.movimentar 3, @cidade
    assert_equal 13, @tropa.tamanho # Deve ter concatenado com @tropa
    tropa2.regenerar_stamina
    tropa2.movimentar 4, @cidade
    assert_equal 17, @tropa.tamanho # Deve ter concatenado com @tropa
    assert_equal 0, tropa2.tamanho # Tropa original deve ter sido 'diluída'
  end

  def testar_regenerar_stamina
    Tropa::MAX_STAMINA.times do |i|
      @tropa.movimentar 10, @cidade
    end

    assert_equal true, @tropa.cansada?
    @tropa.regenerar_stamina
    assert_equal false, @tropa.cansada?
    @tropa.regenerar_stamina 1000
    assert_equal Tropa::MAX_STAMINA, @tropa.stamina
  end

  def testar_regenerar_stamina_negativa
    stamina_anterior = @tropa.stamina

    assert_raise ArgumentError do
      @tropa.regenerar_stamina -10
    end

    assert_equal stamina_anterior, @tropa.stamina
  end

  def testar_consumo_de_stamina_ao_mover_tropa_inteira
    stamina_anterior = @tropa.stamina
    @tropa.movimentar @tropa.tamanho, @cidade
    assert_equal stamina_anterior - 1, @tropa.stamina
    assert_equal @cidade, @tropa.local
  end

  def testar_consumo_de_stamina_ao_separar_tropas
    stamina_anterior = @tropa.stamina
    @tropa.movimentar 5, @local2
    assert_equal stamina_anterior, @tropa.stamina
    assert_equal stamina_anterior - 1, @local2.tropas[0].stamina
  end

  def testar_cansada
    Tropa::MAX_STAMINA.times do |i|
      @tropa.movimentar @tropa.tamanho, @local2
    end

    assert_equal true, @tropa.cansada?
  end

  def testar_se_atividade_de_descanso_eh_criada
    @tropa.movimentar @tropa.tamanho, @local2
    assert_equal true, @tropa.jogador.fila_de_atividades[1].kind_of?(AtDescansoTropa) # fila_de_atividades[0] contém uma instância de AtConquista
  end

  def testar_descanso_da_tropa
    Tropa::MAX_STAMINA.times do |i|
      @tropa.movimentar @tropa.tamanho, @local2
    end

    assert_equal true, @tropa.cansada?
    @jogador.executar_atividades 1
    assert_equal false, @tropa.cansada?
  end

end

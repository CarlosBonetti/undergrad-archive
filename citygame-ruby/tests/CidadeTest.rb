# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../src/Cidade'
require_relative '../src/Jogador'

class CidadeTest < Test::Unit::TestCase

  def setup
    @cidade = Cidade.new 1
    @jogador = Jogador.new 1, "Napoleão"
    @jogador.atribuir_cidade @cidade
  end

  def testar_se_local_eh_cidade
    assert_equal true, @cidade.is_cidade
  end

  def testar_balancear_recursos
    @cidade.balancear_recursos 3, 7
    assert_equal 3, @cidade.g_tropas
    assert_equal 7, @cidade.g_tecnologia
  end

  def testar_balancear_recursos_invalidos
    assert_raise SomaDeRecursosException do
      @cidade.balancear_recursos 100, 100
    end
  end

  def testar_taxa_tecnologia
    assert_equal 0, @cidade.taxa_tecnologia
    @cidade.balancear_recursos 4, 6
    assert_equal 6, @cidade.taxa_tecnologia
  end

  def testar_gerar_tropas
    @cidade.balancear_recursos 9, 1
    @cidade.gerar_tropas

    assert_equal 1, @cidade.tropas.size
    assert_equal 9, @cidade.tropas[0].tamanho
    assert_equal @jogador, @cidade.tropas[0].jogador
  end

  def testar_abandonada
    assert_equal false, @cidade.abandonada?

    cidade_fantasma = Cidade.new 2
    assert_equal true, cidade_fantasma.abandonada?
  end

end

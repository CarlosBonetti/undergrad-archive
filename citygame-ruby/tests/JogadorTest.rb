# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../src/Jogador'
require_relative '../src/Cidade'
require_relative '../src/Tropa'
require_relative '../src/atividades/Atividade'

class AtTest < Atividade
  def executar turno
    return true
  end
end

class AtInfinitaTest < Atividade
  def executar turno
    return false
  end
end

class JogadorTest < Test::Unit::TestCase

  def setup
    @jogador = Jogador.new 1, "Napoleão"
    @cesar = Jogador.new 2, "César"
    @cidade = Cidade.new 1
  end

  def testar_atribuir_cidade
    @jogador.atribuir_cidade @cidade
    assert @jogador.cidades.include?(@cidade)
    assert_equal @jogador, @cidade.jogador
  end

  def testear_se_atribuicao_de_cidade_remove_a_cidade_do_antigo_proprietario
    @jogador.atribuir_cidade @cidade
    assert_equal true, @jogador.cidades.include?(@cidade)
    @cesar.atribuir_cidade @cidade
    assert_equal false, @jogador.cidades.include?(@cidade)
  end

  def testar_executar_atividades
    at_test = AtTest.new
    @jogador.adicionar_atividade at_test
    assert @jogador.fila_de_atividades.include?(at_test)

    @jogador.executar_atividades(1)
    assert_equal 0, @jogador.fila_de_atividades.size
  end

  # Caso específico que causava erro ao deletar item dentro de um each block
  # http://stackoverflow.com/questions/3260686
  def testar_duas_atividades
    @jogador.adicionar_atividade AtTest.new
    @jogador.adicionar_atividade AtTest.new

    @jogador.executar_atividades 1
    assert_equal 0, @jogador.fila_de_atividades.size
  end

  def testar_multiplas_atividades
    at1 = AtTest.new
    at2 = AtInfinitaTest.new
    at3 = AtTest.new
    at4 = AtInfinitaTest.new

    @jogador.adicionar_atividade at1
    @jogador.adicionar_atividade at2
    @jogador.adicionar_atividade at3
    @jogador.adicionar_atividade at4

    assert_equal 4, @jogador.fila_de_atividades.size  # Testa se mantém o número de atividades

    # Testa se mantém a ordem das atividades
    assert_equal at1, @jogador.fila_de_atividades[0]
    assert_equal at2, @jogador.fila_de_atividades[1]
    assert_equal at3, @jogador.fila_de_atividades[2]
    assert_equal at4, @jogador.fila_de_atividades[3]

    @jogador.executar_atividades 1

    # Testa se excluiu as atividades terminadas e manteve as infinitas
    assert_equal 2, @jogador.fila_de_atividades.size
    assert_equal at2, @jogador.fila_de_atividades[0]
    assert_equal at4, @jogador.fila_de_atividades[1]
  end

   def testar_gerar_recursos
    @jogador.atribuir_cidade @cidade
    @cidade.balancear_recursos 5, 5
    @jogador.gerar_recursos

    assert_equal 1.05, @jogador.tecnologia
    assert_equal 5, @cidade.tropas[0].tamanho
    assert_equal @jogador, @cidade.tropas[0].jogador

    @jogador.gerar_recursos

    assert_equal 1.1025, @jogador.tecnologia
    assert_equal 10, @cidade.tropas[0].tamanho
    assert_equal @jogador, @cidade.tropas[0].jogador
    assert_equal 1, @cidade.tropas.size
  end

  def testar_perdeu?
    assert_equal true, @jogador.perdeu?
    @jogador.atribuir_cidade @cidade
    assert_equal false, @jogador.perdeu?
    @jogador.cidades.clear
    assert_equal true, @jogador.perdeu?
    Tropa.new @jogador, 10, @cidade
    assert_equal false, @jogador.perdeu?
    @jogador.tropas.clear
    assert_equal true, @jogador.perdeu?
  end

end

# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../src/Mapa'
require_relative '../src/Jogador'
require_relative '../src/Local'
require_relative '../src/Cidade'

class MapaTest < Test::Unit::TestCase

  def setup
    @mapa = Mapa.new
    @mapa.criar_locais

    @napoleao = Jogador.new 1, 'Napoleão'
    @cesar    = Jogador.new 2, 'Júlio César'
  end

  def test_criar_locais
    @mapa.criar_locais
    assert_not_empty @mapa.matriz
    assert_not_empty @mapa.campos
    assert_not_empty @mapa.cidades
  end

  def test_direcao_valida
    assert_equal true, @mapa.direcao_valida?(Mapa::NORTE)
    assert_equal true, @mapa.direcao_valida?(Mapa::SUL)
    assert_equal true, @mapa.direcao_valida?(Mapa::LESTE)
    assert_equal true, @mapa.direcao_valida?(Mapa::OESTE)
    assert_equal false, @mapa.direcao_valida?(-1)
    assert_equal false, @mapa.direcao_valida?(1000)
  end

  def test_str_direcao
    assert_equal Mapa::NORTE, @mapa.str_direcao('nOrTe')
    assert_equal Mapa::SUL, @mapa.str_direcao('SUL')
    assert_equal Mapa::LESTE, @mapa.str_direcao('leste')
    assert_equal Mapa::OESTE, @mapa.str_direcao('Oeste')
    assert_equal nil, @mapa.str_direcao('aspodkas')
  end

  def test_atribuir_cidade_ao_jogador
    assert_empty @napoleao.cidades
    @mapa.atribuir_cidade_ao_jogador @napoleao
    assert_not_empty @napoleao.cidades
    assert_equal @napoleao, @napoleao.cidades[0].jogador
  end

  def test_get_local_by_id
    assert_equal true, @mapa.get_local_by_id(1).kind_of?(Local)
    assert_equal nil, @mapa.get_local_by_id(910238108109)
  end

end

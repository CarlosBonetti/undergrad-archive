# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../src/LogBatalha'
require_relative '../src/Usuario'

class LogBatalhaTest < Test::Unit::TestCase

  def setup
    @log = LogBatalha.new
    @jogador = Jogador.new 1, 'Napoleão'
  end

  def testar_adicionar_jogador
    assert_equal 0, @log.jogadores.size
    @log.adicionar_jogador @jogador
    assert_equal 1, @log.jogadores.size
  end

end

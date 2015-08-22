# -*- encoding : utf-8 -*-

require 'test/unit'
require_relative '../src/Usuario'

class UsuarioTest < Test::Unit::TestCase

  def testar_validacao_de_username
    assert_equal true, Usuario.valid_username?('napoleao')
    assert_equal true, Usuario.valid_username?('Napoleao')
    assert_equal true, Usuario.valid_username?('Napoleao123')
    assert_equal true, Usuario.valid_username?('napoleao_bonaparte')
    assert_equal true, Usuario.valid_username?('nap')
    assert_equal false, Usuario.valid_username?('na')
    assert_equal false, Usuario.valid_username?('n')
    assert_equal false, Usuario.valid_username?('')
    assert_equal false, Usuario.valid_username?('napoleão')
    assert_equal false, Usuario.valid_username?('napoleao bonaparte')
    assert_equal false, Usuario.valid_username?('napo!')
    assert_equal false, Usuario.valid_username?('nome_com_mais_de_30_caracteres_para_teste_de_tamanho')
  end

  def testar_criacao_com_username_invalido
    assert_raise ArgumentError do
      Usuario.new 'Napoleão1', 'r31d4guerr4'
    end
    Usuario.new 'napoleao', 'r31d4guerr4'
  end

  def testar_registro_com_username_invalido
    assert_raise ArgumentError do
      Usuario.register 'Napoleão1', 'r31d4guerr4', 'r31d4guerr4'
    end
    Usuario.register 'napoleao', 'r31d4guerr4', 'r31d4guerr4'
  end

  def testar_validacao_de_password
    assert_equal true, Usuario.valid_password?('1234')
    assert_equal true, Usuario.valid_password?('r31d4guerr4')
    assert_equal true, Usuario.valid_password?('caracteres estranhos !!! @,')
    assert_equal false, Usuario.valid_password?('')
    assert_equal false, Usuario.valid_password?('1')
    assert_equal false, Usuario.valid_password?('12')
    assert_equal false, Usuario.valid_password?('123')
    assert_equal false, Usuario.valid_password?('senha com mais de 30 caracteres para teste de tamanho')
  end

  def testar_registro_com_senha_invalida
    assert_raise ArgumentError do
      Usuario.register 'napoleao', '12', '12'
    end
    Usuario.register 'napoleao', 'r31d4guerr4', 'r31d4guerr4'
  end

  def testar_registro_com_senha_e_confirmacao_diferentes
    assert_raise ArgumentError do
      Usuario.register 'napoleao', '1234', '12345'
    end
    Usuario.register 'napoleao', '1234', '1234'
  end

  def testar_password
    user = Usuario.register 'cesar', 'daiacesaroqueehdecesar', 'daiacesaroqueehdecesar'
    assert_equal 'daiacesaroqueehdecesar', user.password
  end

  def testar_registro
    user = Usuario.register 'leonidas', 'raul', 'raul'
    assert_equal 'leonidas', user.username
    assert_equal false, user.password.nil?
  end

end

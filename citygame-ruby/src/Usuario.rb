# -*- encoding : utf-8 -*-

require 'digest'
require_relative 'LogBatalha'

class Usuario
  attr_accessor :username, :password
  attr_accessor :batalhas

  def initialize username, password
    raise ArgumentError, "Nome de usuário deve conter 3-30 caraceres (somente letras, números e _)" unless Usuario.valid_username?(username)

    raise ArgumentError, "Senha deve conter 4-30 caracteres" unless Usuario.valid_password?(password)

    @username = username
    @password = password
    @batalhas = []
  end

  # Cria um novo usuário, pronto para ser cadastrado no banco de dados
  # @return [Usuario]
  def self.register username, password, password_confirm
    user = Usuario.new username, password
    raise ArgumentError, "A senha e sua confirmação não são idênticas" unless password == password_confirm

    return user
  end

  def self.valid_username? username
    !username.match(/^[a-zA-Z0-9_]{3,30}$/).nil?
  end

  def self.valid_password? password
    !password.match(/^.{4,30}$/).nil?
  end

end

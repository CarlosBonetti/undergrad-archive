# -*- encoding : utf-8 -*-

require_relative 'Persistence'
require_relative '../exceptions'

class DAOUsuario
	include Persistence

	# Cadastra usuario na tabela usuarios
	# @param [String] username
	# @param [String] password
	def create username, password
		begin
		    CONNECTION.exec( "INSERT INTO usuarios (username, password) values ('#{username}', '#{password}')" )
		rescue PG::UniqueViolation
			raise UsernameJaExistente, 'Username não disponível para cadastro'
		end
	end

	# Procura usuario na tabela usuarios
	# @param [String] username
	# @param [String] password
	# @return [Usuario] usuario
	def read username, password
		resultado =  CONNECTION.exec( "SELECT * FROM usuarios where username = '#{username}' and password = '#{password}'" )
		unless resultado.values.empty?
			return Usuario.new username, password
		end

		return false
	end

	# Deleta o usuário que tenha o username informado
	# @param [String] username
	def delete username
		res = CONNECTION.exec("DELETE FROM usuarios where username = '#{username}'")
	end

end

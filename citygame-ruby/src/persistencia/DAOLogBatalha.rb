# -*- encoding : utf-8 -*-

require_relative 'Persistence'
require_relative '../LogBatalha'
require_relative '../Usuario'

class DAOLogBatalha
	include Persistence

	# Cadastra usuario na tabela usuarios
	# @param [LogBatalha] log_batalha
	def create log_batalha
		vencedor_username = log_batalha.vencedor.username
		turnos = log_batalha.turnos
		id = gerador_de_id
		jogadores = log_batalha.jogadores

		# Preenche tabela batalhas
		CONNECTION.exec( "INSERT INTO batalhas (id,turnos, vencedor) values (#{id}, #{turnos}, '#{vencedor_username}')" )

		# Preenche tabela batalha_usuario (dependência de id com batalhas)
		jogadores.each do |usuario|
			username = usuario.username
			CONNECTION.exec( "INSERT INTO batalha_usuario (username, batalha_id) values ('#{username}', #{id})")
		end
	end

	# Procura usuario na tabela usuarios
	# @param [string] username
	# @return [LogBatalha[]] batalhas
	def read_batalhas_vencidas_por username
	  # Busca todas batalhas em que o vencedor foi o usuario passado como parâmetro
		query_batalhas = CONNECTION.exec( "SELECT * FROM batalhas where vencedor = '#{username}'" )
		batalhas = []

		query_batalhas.each do |resultado_tabela_batalhas|
			vencedor = nil # Vencedor da batalha
			jogadores = []

			# Busca username (Usuario) na tabela batalha_usuario com o id da batalha
			query_username_jogadores = CONNECTION.exec( "SELECT username FROM batalha_usuario where batalha_id = #{resultado_tabela_batalhas['id']}" )
			query_username_jogadores.each do |resultado_tabela_batalha_usuario|
				# Busca password (Usuario) da tabela usuarios
				query_password = CONNECTION.exec( "SELECT password FROM usuarios where username = '#{resultado_tabela_batalha_usuario['username']}' " )
				query_password.each do |resultado_tabela_usuarios|
					user = Usuario.new resultado_tabela_batalha_usuario['username'], resultado_tabela_usuarios['password']
					jogadores.push user
					vencedor = user if user.username == username
				end
				query_password.clear
			end


			batalha = LogBatalha.new
			batalha.jogadores = jogadores
			batalha.turnos = resultado_tabela_batalhas["turnos"].to_i
			batalha.vencedor = vencedor

			batalhas.push batalha

			query_username_jogadores.clear
		end
		query_batalhas.clear
		return batalhas
	end

	private

	# Gera um ID (Primary Key) válido
	# @return [Fixnum] id
	def gerador_de_id
		id = 0

		result = CONNECTION.exec( "SELECT max(id) FROM batalhas" )
		result.each do |row|
			id = row['max'].to_i
		end
		result.clear
		return (id + 1)
	end


end

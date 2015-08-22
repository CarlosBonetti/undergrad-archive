# -*- encoding : utf-8 -*-
require_relative 'Modo'
require_relative 'ModoPartida'
require_relative '../../src/persistencia/DAOUsuario'
require_relative '../../src/persistencia/DAOLogBatalha'

class ModoNovoJogo < Modo

  def initialize jogo
    super jogo
    @comandos = ['exit', 'help', 'registrar', 'login', 'status', 'iniciar', 'vitorias']
  end

  def prefixo
    return 'new-game ~> '
  end

  def help
    puts "  Opções de criação de novo jogo:"
    puts "    * help                 - Exibe este texto de ajuda"
    puts "    * exit                 - Encerra o jogo"
    puts "    * registrar            - Cria um novo jogador e o adiciona na base de dados"
    puts "    * login                - Loga o jogador e o adiciona na partida (username e senha necessários)"
    puts "    * status               - Mostra os jogadores atuais"
    puts "    * iniciar              - Inicia a partida com os jogadores atuais, iniciando o modo 'partida'"
    puts "    * vitorias [username]  - Mostra as batalhas vencidas pelo jogador"
    puts ""
  end

  def registrar
    username = ask 'username: '
    senha = ask_encrypted 'senha: '
    senha_confirm = ask_encrypted 'confirmação de senha: '

    begin
      user = Usuario.register username, senha, senha_confirm
    rescue ArgumentError => e
      warning_msg e.to_s
      return
    end

    begin
      dao = DAOUsuario.new
      dao.create user.username, user.password
    rescue UsernameJaExistente => e
      warning_msg e.to_s
      return
    end

    success_msg "Usuário #{username} cadastrado! Efetue login para entrar na partida"
  end

  def login
    username = ask 'username: '
    senha = ask_encrypted 'senha: '

    dao = DAOUsuario.new
    user = dao.read username, senha

    if !user then
      warning_msg "Usuário não existe ou a senha não confere"
      return
    end

    @jogo.adicionar_usuario user
    success_msg "Usuário #{user.username} adicionado na partida!"
  end

  def status
    if @jogo.jogadores.size == 0 then
      puts "[Nenhum jogador criado]"
      return
    end

    puts "[Jogadores atualmente cadastrados]"
    @jogo.jogadores.each do |jogador|
      puts "  ##{jogador.id} #{jogador.nome}"
    end
    puts ""
  end

  def iniciar
    begin
      @jogo.iniciar
    rescue CitygameException => e
      error_msg e.to_s
      return
    end

    modo_partida = ModoPartida.new(@jogo)
    success_msg "Partida iniciada!"
    return modo_partida
  end

  def vitorias username
    dao = DAOLogBatalha.new
    batalhas = dao.read_batalhas_vencidas_por username

    return if batalhas.empty?

    puts "Batalhas vencidas por #{username}"
    batalhas.each do |batalha|
      print "   #{batalha.turnos} turnos. Jogadores: "
      nomes = batalha.jogadores.map { |j| j.username }
      print nomes.join(', ')
      puts
    end
  end

end

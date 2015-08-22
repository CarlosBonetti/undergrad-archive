# -*- encoding : utf-8 -*-

require_relative 'Terminal'

# Um modo encapsula uma série de comandos disponíveis em dado momento do jogo
class Modo
  include Terminal
  attr_reader :ativo, :jogo, :comandos

  def initialize jogo
    @comandos = []
    @ativo = true
    @jogo = jogo
  end

  # String impressa antes de cada chamada de comando do jogador
  # @return [String]
  def prefixo
    return ' ~> '
  end

  # Processo para o auto completar dos comandos disponíveis
  # @return [Proc]
  def completion_proc
    return proc { |s| comandos.grep(/^#{s}/) }
  end

  # Submete um comando
  # @param [Hash] Hash com {:command => 'nome do comando requisitado', :options => 'array com os argumentos'}
  # @return [Modo] Retorna um novo modo de jogo
  def submeter_comando command_hash
    if !validar_comando(command_hash[:command]) then
      error_msg "'#{command_hash[:command]}' não é um comando válido... Digite 'help' caso esteja perdido!"
      return self
    end

    begin
      if command_hash[:options].empty?
        r = self.send(command_hash[:command])
      else
        r = self.send(command_hash[:command], *command_hash[:options])
      end
    rescue ArgumentError => e
      error_msg "Número de argumentos inválido. Digite 'help' para ver o número correto de argumentos do comando"
    rescue => e
      warning_msg e.inspect
    end

    return r if r.kind_of? Modo
    return self
  end

private

  # Checa se o comando é válido dentro do contexto do modo atual
  # @return [Boolean]
  def validar_comando comando
    return false if !@comandos.include?(comando)
    return false if comando.empty?

    return true
  end

public

  # ========================================================
  # Comandos padrões

  # Encerra o jogo e termina a aplicação
  def exit
    c = confirm_msg "Encerrar o jogo, perdendo todas as informações?"
    @ativo = !c
  end

end

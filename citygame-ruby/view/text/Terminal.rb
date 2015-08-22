# -*- encoding : utf-8 -*-

# Funções de input/output padrões do terminal
module Terminal

  # Imprime uma mensagem padrão de confirmação
  # @param [String] Mensagem apresentada
  # @return [Boolean] True em caso de confirmação
  def confirm_msg msg
    print msg.colorize(:yellow), " (s, n) "
    c = gets
    c = c.strip.downcase
    return c == 's'
  end

  # Imprime uma mensagem padrão de sucesso
  # @param [String] Mensagem apresentada
  def success_msg msg
    puts msg.colorize :green
  end

  # Imprime uma mensagem padrão de alerta
  # @param [String] Mensagem apresentada
  def warning_msg msg
    puts msg.colorize :yellow
  end

  # Imprime uma mensagem padrão de erro
  # @param [String] Mensagem apresentada
  def error_msg msg
    puts msg.colorize :red
  end

  # Limpa o terminal
  def clear
    system 'clear' unless system 'cls'
  end

  # Faz uma pergunta ao usuário, esperando pela resposta
  # @param [string] msg: mensagem a ser digitada antes do input
  # @return [string]
  def ask msg
    print msg
    gets.chomp
  end

  # Faz uma pergunta ao usuário, esperando pela resposta e não mostrando-a na tela
  # @param [string] msg: mensagem a ser digitada antes do input
  # @return [string]
  def ask_encrypted msg
    system 'stty -echo'
    r = ask msg
    system 'stty echo'
    puts
    return r
  end

end

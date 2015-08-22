# -*- encoding : utf-8 -*-

# Utilizada para interpretar os comandos digitados na linha de comando
class CommandParser

  # Interpreta um comando digitado pelo jogador
  # Os comandos são da forma `nome_do_comando argumento1, argumento2, argumento3, ...`
  # @param [String] String digitada
  # @return [Hash] { :command => 'nome do comando digitado', :options => 'array dos argumentos' }
  def parse string
    pieces = string.split ' ', 2
    command = pieces[0]

    if pieces[1] then
      options = pieces[1].split ','
    else
      options = []
    end

    options = options.map { |opt| opt.strip }

    return {
      :command => command,
      :options => options
    }
  end

end

# -*- encoding : utf-8 -*-

require_relative 'TropaTest.rb'
require_relative 'JogadorTest.rb'
require_relative 'LocalTest.rb'
require_relative 'CidadeTest.rb'
require_relative 'MapaTest.rb'
require_relative 'JogoTest.rb'
require_relative 'UsuarioTest.rb'
require_relative 'LogBatalhaTest.rb'

# Testes das atividades
require_relative 'atividadesTests/AtConquistaTest.rb'
require_relative 'atividadesTests/AtAtaqueTest.rb'
require_relative 'atividadesTests/AtAtaqueRuralTest.rb'
require_relative 'atividadesTests/AtAtaqueUrbanoTest.rb'
require_relative 'atividadesTests/AtDescansoTropaTest.rb'

# Testes de persistência
# Utilize a flag -- db para rodar os testes de persistência
# Ex.: ruby AllTests.rb -- db
if ARGV.size > 0 && ARGV.include?('db') then
  require_relative 'DAOUsuarioTest.rb'
  require_relative 'DAOLogBatalhaTest.rb'
end

# -*- encoding : utf-8 -*-

# Exceção padrão da biblioteca. Todas as demais exceções são derivadas desta
class CitygameException < Exception; end

# Exception lançada quando direção não pertence a {LESTE, SUL, OESTE, NORTE}
class DirecaoException < CitygameException; end

# Exception lançada quando local não existe ou não pertence ao jogador
class LocalException < CitygameException; end

# Exception lançada quando não há pelo menos dois jogadores para iniciar partida
class MinimoDeJogadoresException < CitygameException; end

# Exception lançada quando houver tentativa de criar novos jogadores no meio de uma partida!
class NovoJogadorException  < CitygameException; end

# Exception lançada quando número de tropas está fora do intervalo [minimo, maximo]
class NumeroDeTropasException < CitygameException; end

# Exception lançada quando a soma das tropas e tecnologia for maior que 10.
class SomaDeRecursosException < CitygameException; end

# Exception lançada quando uma tropa está sem stamina para executar uma ação.
class SemStaminaException < CitygameException; end

# Exception lançada quando há duplicata de username (PRIMARY KEY).
class UsernameJaExistente < CitygameException; end

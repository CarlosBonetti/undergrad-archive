# -*- encoding : utf-8 -*-
require_relative '../../src/Jogo'
require_relative 'Modo'

class ModoPartida < Modo

  def initialize jogo
    super jogo
    @comandos = ['exit', 'help', 'passar', 'status', 'map', 'balancear', 'move']
  end

  def prefixo
    s = "turno#{@jogo.turno} @ "
    s <<= "#{@jogo.jogador_atual.nome}".colorize cor_jogador(@jogo.jogador_atual)
    s <<= ' ~> '
    return s
  end

  def help
    puts "  Opções de partida:"
    puts "   * help                                     - Exibe este texto de ajuda"
    puts "   * exit                                     - Encerra o jogo"
    puts "   * passar                                   - Passa a vez"
    puts "   * status                                   - Mostra informações do jogador e recursos"
    puts "   * map                                      - Exibe o mapa"
    puts "   * balancear [ID_CIDADE], [TROPAS], [TECNO] - Altera o balanceamento de tropas e tecnologia produzidas por uma cidade"
    puts "   * move [ID_FONTE], [N_SOLDADOS], [DIRECAO] - Move a quantidade de soldados de um local na direção definida. DIRECAO pode ser {NORTE, SUL, LESTE, OESTE}"
    puts ""
  end

  def passar
    @jogo.passar_a_vez
    if @jogo.terminou?
      vencedor = @jogo.jogadores.pop
      puts "Parabéns #{vencedor.nome}! Você venceu."
      @ativo = false
    else
      clear()
    end
  end

  def status
    puts "Tecnologia atual: #{@jogo.jogador_atual.tecnologia}"
    puts "Recursos:"
    @jogo.jogador_atual.cidades.each do |cidade|
      puts "  cidade##{cidade.id} ~> soldados: +#{cidade.g_tropas} | tecnologia: +#{cidade.g_tecnologia}"
    end
  end

  def map
    # Imprimir legenda do mapa
    s = "\nLegenda:  "
    s <<= "{ }" + " => Cidade   "
    s <<= "[ ]" + " => Campo\n"

    for jogador in @jogo.jogadores
      nome_do_jogador = "%-8s" % jogador.nome
      s <<= "          #{nome_do_jogador} => " + "X".colorize(cor_jogador(jogador)) + "\n"
    end
    s <<= "\n"

    # Imprimir o mapa
    matriz = @jogo.mapa.matriz
    tamanho_dos_locais = 14  # Espaço, em caracteres, ocupado no mapa por cada local
    for i in 0...matriz.size
      for j in 0...matriz[0].size
        local = "%02d" % matriz[i][j].id

        # Para cada jogador que controlar uma tropa neste
        # território, será apresentado um X com a sua cor
        # ou o tamanho da tropa caso:
        #   1. ele seja o da vez
        #   2. o jogador da vez possua tropas no local

        visivel = !matriz[i][j].get_tropa_jogador(@jogo.jogador_atual).nil? # Caso o jogador atual tenha tropas no local, ele pode ver o tamanho de todas as demais tropas (visivel é true neste caso)

        for jogador in @jogo.jogadores
          tropa_local = matriz[i][j].get_tropa_jogador jogador
          if tropa_local
            tamanho_dos_locais += 14
            if jogador == @jogo.jogador_atual or visivel
              tamanho = tropa_local.tamanho.to_s
              local <<= ", " + tamanho.colorize(cor_jogador(jogador))
            else
              local <<= ", " + "X".colorize(cor_jogador(jogador))
            end
          end
        end
        if matriz[i][j].is_cidade
          tamanho_dos_locais += 14*2
          cor_da_cidade = :default
          cor_da_cidade = cor_jogador(matriz[i][j].jogador) if matriz[i][j].jogador          
          local = (local << "}".colorize(cor_da_cidade)).prepend "{".colorize(cor_da_cidade)  # Cidades são representadas com {}
        else
          local = (local << "]").prepend "["  # Campos são representados com []
        end
        s <<= local.center(tamanho_dos_locais, '-')
        tamanho_dos_locais = 14
      end
      s <<= "\n"
      # Duvido você compreender a próxima linha!
      s <<= ("%#{(tamanho_dos_locais/2.0).floor}c%#{(tamanho_dos_locais/2.0).ceil}c" % ['|', ' ']) * matriz[0].size
      s <<= "\n"
    end
    s.slice! (-tamanho_dos_locais*(matriz[0].size)..-1)
    s <<= "\n"
    puts s
  end

  def balancear id_cidade, tropas, tecnologia
    id_cidade = id_cidade.to_i
    tropas = tropas.to_i
    tecnologia = tecnologia.to_i

    begin
      @jogo.balancear_recursos id_cidade, tropas, tecnologia
    rescue CitygameException => e
      error_msg e.to_s
      return
    end

    success_msg "Quantidade de recursos produzidos alterada"
  end

  def move id_fonte, n_soldados, direcao
    id_fonte = id_fonte.to_i
    n_soldados = n_soldados.to_i
    direcao = @jogo.direcao direcao

    begin
      destino = @jogo.movimentar_tropas id_fonte, n_soldados, direcao
    rescue CitygameException => e
      error_msg e.to_s
      return
    end

    success_msg "Tropa movida para o local #{destino.id}"
  end

  # Retorna a cor do jogador passado
  # @return [int] Inteiro que representa uma cor
  def cor_jogador jogador
    return String.colors[jogador.id + 3]
  end

end

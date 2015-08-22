# -*- encoding : utf-8 -*-

require_relative 'exceptions'

class Mapa
  attr_reader :campos, :cidades, :matriz

  # Constantes
  LESTE = 3
  SUL = 6
  OESTE = 9
  NORTE = 12
  QUANTIDADE_DE_CIDADES = 5
  QUANTIDADE_DE_CAMPOS = 31

  # Cria um novo mapa aleatório
  def initialize
    @campos = []
    @cidades = []
    @matriz = [[]]
  end

  # Checa se o id da direção é uma direção válida
  # @param [Integer] Constante que representa uma direção
  # @return [Boolean]
  def direcao_valida? direcao_const
    direcao_const == LESTE or direcao_const == SUL or direcao_const == OESTE or direcao_const == NORTE
  end

  # Recebe uma string {NORTE, SUL, LESTE, OESTE} e retorna um inteiro que representa a direção informada
  # @param [String] Um dentre {NORTE, SUL, LESTE, OESTE}
  # @return [Integer] Int que representa a direção ou nil em caso de direção não reconhecida
  def str_direcao str_dir
    direcao = nil
    
    case str_dir.upcase
      when 'NORTE'
        direcao = NORTE
      when 'SUL'
        direcao = SUL
      when 'LESTE'
        direcao = LESTE
      when 'OESTE'
        direcao = OESTE
    end

    return direcao
  end

  def criar_locais
    # Criar locais
    n_loc = QUANTIDADE_DE_CIDADES + QUANTIDADE_DE_CAMPOS
    raise LocalException, 'Não existem locais suficientes para iniciar o jogo' if (n_loc**(0.5))%(n_loc**(0.5)).floor != 0
    loc = Array.new(n_loc)
    for i in 0...QUANTIDADE_DE_CIDADES
      loc[i] = Cidade.new(i)
      @cidades.push loc[i]
    end
    for i in 0...QUANTIDADE_DE_CAMPOS
      j = (QUANTIDADE_DE_CIDADES + i)
      loc[j] = Local.new(j)
      @campos.push loc[j]
    end

    # Misturar locais
    for i in 0...n_loc
      destino_randomico = rand(n_loc)
      loc[i], loc[destino_randomico] = loc[destino_randomico], loc[i]
    end

    # Exemplo de mapa criado:
    #      local(04)--local(08)--local(10)-- ... --local(56)
    #         |          |           |                 |
    #      local(01)--local(03)--local(02)-- ... --local(34)
    #         |          |           |     ..           |
    #         :          :           :        ..        :
    #         |          |           |           ..     |
    #      local(07)--local(65)--local(79)-- ... --local(81)
    k = 0
    largura = n_loc**(0.5)
    comprimento = largura
    mapa = Array.new(comprimento) { |linha| linha = Array.new(largura) }
    for i in 0...comprimento
      for j in 0...largura
        mapa[i][j] = loc[k]
        k = k + 1
      end
    end
    
    @matriz = mapa
  end

  # Atribui uma cidade aleatório do mapa ao jogador
  # @param [Jogador]
  def atribuir_cidade_ao_jogador jogador
    begin
      rand_city = @cidades[rand(@cidades.size)]
    end while (!rand_city.abandonada?) # Se a cidade já foi atribuída a alguém, atribui outra cidade

    jogador.atribuir_cidade rand_city
  end

  # Retorna o local com id igual ao parâmetro ou nil caso não exista
  # @param [Integer]
  # @return [Local]
  def get_local_by_id id
    local_do_id = nil

    @matriz.each_index do |i|
      @matriz[i].each_index do |j|
        if @matriz[i][j].id == id then
          local_do_id = @matriz[i][j]
          break
        end
      end
    end

    return local_do_id
  end

  # Retorna o local ajancente à fonte e ligada nela na direção especificada
  # @param [Local] fonte : local fonte
  # @param [Integer] direcao : direção a ser pesquisada
  # @return [Local] Retorna o local ou nil caso não existam locais na direção especificada
  def get_local_adjacente fonte, direcao
    adjacente = nil
    
    @matriz.each_index do |i|
      @matriz[i].each_index do |j|
        if @matriz[i][j].id == fonte.id then
          case direcao
            when NORTE
              adjacente = @matriz[i-1][j] if @matriz[i-1][j]
            when SUL
              adjacente = @matriz[i+1][j] if @matriz[i+1][j]
            when LESTE
              adjacente = @matriz[i][j+1] if @matriz[i][j+1]
            when OESTE
              adjacente = @matriz[i][j-1] if @matriz[i][j-1]
          end
          break
        end
      end
    end
    
    return adjacente
  end

end

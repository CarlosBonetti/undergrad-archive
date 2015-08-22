# formais1

[![Build Status](https://magnum.travis-ci.com/CarlosBonetti/formais1.svg?token=TncWKXR1N9y1CQNXWyip)](https://magnum.travis-ci.com/CarlosBonetti/formais1)

Trabalho 1 de Linguagens Formais e Compiladores

# I – Definição:

Elaborar uma aplicação para manipular LR representadas por GR, AF e ER, que
envolva as seguintes operações/funcionalidades:

1. Edição, leitura e gravação de GR e ER.
2. Conversão de GR para AF e de ER para AF (usando De Simone) .
3. Determinização, Minimização, Complementação e Intersecção de AF.
4. Dadas uma GR G e uma ER E, verificar se G ≡ E;
5. busca de padrões (representados por ER) em um dado texto.

# II - Observações:

1. Apresentar os AF através de tabelas de transição.
2. Representar os estados de um AF por letras maiúsculas. Identificar o estado
inicial por “->” e os finais por “*”.
3. Os símbolos do alfabeto devem ter tamanho 1 e podem ser limitados a letras
minúsculas e dígitos.
4. Usar & para representar épsilon.
5. Todos os AF´s envolvidos em uma operação/verificação devem ser
visualizáveis e disponíveis para outras operações.
6. As ER devem seguir o padrão usado em aula (Ex.: a\*(b?c|d)\*).
7. AS GR devem seguir o padrão S->aB|a e devem ser editáveis na forma textual.
8. Além da corretude, serão avaliados aspectos de usabilidade e robustez.
9. O trabalho deverá ser feito em duplas.
10. A linguagem de programação é de livre escolha (porém deve ser dominada
pelos 2 membros da equipe).
11. Caso sejam usados algoritmos diferentes dos usados em aula, eles devem ser
documentados e exemplificados no relatório.
12. O trabalho deve ser encaminhado por e-mail, até 15/06, em um único
arquivo zipado, contendo relatório (descrevendo a implementação e sua
utilização), fonte (documentado), executável e testes. Usar como nome do
arquivo o nome dos componentes da equipe (ex. JoaoF_MariaG.zip).

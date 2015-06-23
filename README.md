# Linguagens Formais e Compiladores - Trabalho II

## I – Definição:

Elaborar uma aplicação, com interface gráfica para manipular GLC, envolvendo as seguintes
verificações/operações:

1. Ler, Editar e Salvar GLC;
2. Verificar se G é LL(1):

	2.1. Possui Recursão a Esquerda? Em caso positivo identificar o tipo de recursão e os
	não-terminais recursivos;	
	2.2. Esta Fatorada? Em caso negativo identificar o tipo da não-fatoração e os nãoterminais
	não-fatorados;
	2.3. Para todo A ∈ Vn | A ⇒* ε, First(A) ∩ Follow(A) = ϕ?
	
3. Gerar o PARSER Descendente Recursivo de G (caso G seja LL(1));
4. Efetuar análise sintática (reconhecimento de sentenças) usando o Parser gerado.

## II - Observações:

1. Representar as GLC de forma textual, seguindo o padrão dos exemplos abaixo:

```
a) E -> E + T | E - T | T
T -> T * F | T / F | F
F -> ( E ) | id
```

```
b) E -> T E1
E1 -> + T E1 | &
 T -> F T1
T1 -> * F T1 | &
F -> ( E ) | id
```

2. deixar um espaço em branco entre os símbolos do lado direito.
3. Representar não-terminais por letra maiúscula (seguida de 0 ou + dígitos).
4. Representar terminais com um ou mais caracteres contíguos (quaisquer caracteres, exceto
letras maiúsculas).
5. Usar & para representar épsilon.
6. Apresentar os resultados intermediários obtidos (First, Follow, Parser e sequências de
ativações realizadas durante a análise de uma sentença).
7. O trabalho deverá ser feito em duplas.
8. A linguagem de programação é de livre escolha (porém deve ser dominada pelos 2
membros da equipe).
9. O trabalho deve ser encaminhado por e-mail, até 13/07, em um único arquivo zipado,
contendo relatório (descrevendo a implementação), fonte (documentado), executável e testes.
Usar como nome do arquivo o nome dos componentes da equipe (ex. JoaoF-MariaG.zip).
10. Além da corretude, serão avaliados aspectos de usabilidade e robustez da aplicação.
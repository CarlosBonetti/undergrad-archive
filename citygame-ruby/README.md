# citygame-ruby

Jogo de estratégia baseado em turnos.

### Rodando o jogo

Utilize `ruby view/text/init.rb` para rodar o jogo em modo texto.

### Dependências

* Ruby >= 1.9
* [colorize](https://github.com/fazibear/colorize)

[Bundler](http://bundler.io/) pode ser utilizado para a resolução de dependências: `bundle install`.

### Autores

* [Bruno Souza da Silva](https://github.com/brunosouzasilva)
* [Carlos Bonetti](https://github.com/CarlosBonetti/citygame-ruby)
* [Lucas Tonussi](https://github.com/tonussi)
* [Thiago Rose](https://github.com/thisenrose)

### Rodando os testes unitários

`$ ruby tests/AllTests.rb` para rodar todos os testes da aplicação.

`$ ruby tests/AllTests.rb -- db` para rodar todos os testes da aplicação + os testes de persistência (é necessária a configuração da base de dados).

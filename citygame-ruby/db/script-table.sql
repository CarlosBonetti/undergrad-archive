-- Script: Tabelas
-- Learn: http://www.postgresql.org/docs/current/static/sql-createtable.html

CREATE TABLE usuarios (
    username VARCHAR(30) PRIMARY KEY,
    password VARCHAR(30)
);

CREATE TABLE batalhas (
    id INT PRIMARY KEY,
    turnos INT,
    vencedor VARCHAR(30),
    FOREIGN KEY (vencedor) REFERENCES usuarios(username)
);

CREATE TABLE batalha_usuario (
    username VARCHAR(30),
    batalha_id INT,
    PRIMARY KEY (username, batalha_id),
    FOREIGN KEY (batalha_id) REFERENCES batalhas(id),
    FOREIGN KEY (username) REFERENCES usuarios(username)
);

delete from usuarios;
delete from batalha_usuario;
delete from batalhas;

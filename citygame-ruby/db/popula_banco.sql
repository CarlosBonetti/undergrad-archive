DELETE FROM batalha_usuario;
DELETE FROM batalhas;
DELETE FROM usuarios;

INSERT INTO usuarios  VALUES ('thiago','thiago');
INSERT INTO usuarios  VALUES ('carlos','carlos');
INSERT INTO usuarios  VALUES ('bruno','bruno');
INSERT INTO usuarios  VALUES ('lucas','lucas');
INSERT INTO usuarios  VALUES ('vilain','vilain');
INSERT INTO usuarios  VALUES ('napoleao','reidaguerra');
INSERT INTO usuarios  VALUES ('luiz_iv','sanguenoolho');


INSERT INTO batalhas (id, turnos, vencedor) VALUES (1, 41, 'thiago');
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('thiago',1);
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('carlos',1);

INSERT INTO batalhas (id, turnos, vencedor) VALUES (2, 12, 'carlos');
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('napoleao',2);
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('carlos',2);


INSERT INTO batalhas (id, turnos, vencedor) VALUES (3, 32, 'bruno');
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('bruno',3);
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('luiz_iv',3);


INSERT INTO batalhas (id, turnos, vencedor) VALUES (4, 22, 'thiago');
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('thiago',4);
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('lucas',4);

INSERT INTO batalhas (id, turnos, vencedor) VALUES (5, 22, 'lucas');
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('bruno',5);
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('lucas',5);

INSERT INTO batalhas (id, turnos, vencedor) VALUES (6, 22, 'vilain');
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('vilain',6);
INSERT INTO batalha_usuario (username, batalha_id) VALUES ('lucas',6);
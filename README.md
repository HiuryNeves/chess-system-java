# Chess System

Um jogo de xadrez para desktop escrito em Java. O mecanismo de regras existente é exibido por meio de uma interface Swing, portanto, ele roda com um JDK padrão e não possui dependências externas.

Executar

A partir da pasta do projeto:

javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)
java -cp out application.ChessApplication

Clique em uma de suas peças e, em seguida, em uma casa destacada. O ponto de entrada original do terminal permanece disponível em application.Program.


Arquitetura

boardgame: tabuleiro, posições e peças genéricas.
chess: regras específicas do xadrez, validação de jogadas, xeque, xeque-mate, roque, en passant e promoção.
application: a interface de usuário do terminal e a nova interface de usuário Swing.
A GUI delega deliberadamente as regras ao ChessMatch; ela não decide se uma jogada é válida.

Traduzido com a versão gratuita do tradutor - DeepL.com

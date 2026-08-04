# Chess System

A desktop chess game written in Java. The existing rules engine is displayed through a Swing interface, so it runs with a standard JDK and has no external dependencies.

## Run

From the project folder:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)
java -cp out application.ChessApplication
```

Click one of your pieces and then a highlighted square. The original terminal entry point remains available in `application.Program`.

## Architecture

- `boardgame`: generic board, positions, and pieces.
- `chess`: chess-specific rules, move validation, check, checkmate, castling, en passant, and promotion.
- `application`: the terminal UI and the new Swing user interface.

The GUI deliberately delegates rules to `ChessMatch`; it does not decide whether a move is legal.

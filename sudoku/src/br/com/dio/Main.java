package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;
import br.com.dio.util.BoardTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {

        final var positions = Stream.of(args).collect(Collectors.toMap(
                k -> k.split(";")[0],
                v -> v.split(";")[1]
        ));
        var option = -1;
        while (true){
            System.out.println("Selecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            option = scanner.nextInt();

            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione uma das opções do menu.");
            }
        }

    }

    private static void startGame(Map<String,  String> positions) {
        if (nonNull(board)){
            System.out.println("O jogo já foi iniciado.");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int i=0; i < BOARD_LIMIT; i++){
            spaces.add(new ArrayList<>());
            for (int j=0; j < BOARD_LIMIT; j++){
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println("O jogo está pronto para começar.");
    }

    private static void inputNumber() {

        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Informe a coluna para inserir o número:");
        var col = runUntilGetValidNumber(0, 8);

        System.out.println("Informe a linha para inserir o número:");
        var row = runUntilGetValidNumber(0, 8);

        System.out.printf("Informe o número a ser inserido em [%s,%s]:\n", col, row);
        var value = runUntilGetValidNumber(1, 9);

        if(!board.changeValue(col, row, value)){
            System.out.printf("A posição [%s,%s] não é alterável\n", col, row);
        }
    }

    private static void removeNumber() {

        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Informe a coluna para remover o número:");
        var col = runUntilGetValidNumber(0, 8);

        System.out.println("Informe a linha para remover o número:");
        var row = runUntilGetValidNumber(0, 8);

        if(!board.clearValue(col, row)){
            System.out.printf("A posição [%s,%s] não é alterável.\n", col, row);
        }
    }

    private static void showCurrentGame() {

        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        var args = new Object[81];
        var argPos = 0;

        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col: board.getSpaces()) {
                args[argPos ++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
                
            }
        }

        System.out.println("Seu jogo atualmente: ");
        System.out.printf((BoardTemplate.BOARD_TEMPLATE) + "%n", args);

    }

    private static void showGameStatus() {

        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("O status atual do seu jogo é: " + board.getStatus().getLabel());

        if(board.hasErrors()){
            System.out.println("O jogo contém erros.");
        }
        System.out.println("O jogo não contém erros.");
    }

    private static void clearGame() {

        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Tem certeza que quer reiniciar o jogo? Digite Sim ou Não");
        var confirm = scanner.next();
        while(!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")){
            System.out.println("Opção inválida, digite 'sim' ou 'não'.");
            confirm = scanner.next();
        }

        if (confirm.equalsIgnoreCase("sim")){
            board.reset();
        }

    }

    private static void finishGame() {

        if (isNull(board)){
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        if (board.gameIsFinished()){
            System.out.println("Parabéns, você venceu!");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()){
            System.out.println("O jogo contém erros, verifique seu tabuleiro.");
        } else {
            System.out.println("Seu tabuleiro não está completo.");
        }

    }

    private static int runUntilGetValidNumber(final int min, final int max){

        var current = scanner.nextInt();
        while (current < min || current > max){
            System.out.printf("Informe um número entre %s e %s\n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }
}
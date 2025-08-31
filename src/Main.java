import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Board;
import model.Space;
import util.BoardTemplate;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {

        final var positions = Stream.of(args)
                .collect(Collectors.toMap(key -> key.split(";")[0],
                        value -> value.split(":")[1]));

        var menuOption = -1;

        while (true) {
            System.out.println("Selecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - Limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            menuOption = scanner.nextInt();

            switch (menuOption) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione uma das opções do menu");
            }

        }

    }

    private static void startGame(final Map<String, String> positions) {
        if(nonNull(board)) {
            System.out.println("O jogo já foi iniciado.");
            return;
        }

        List<List<Space>> spacesList = new ArrayList<>();

        for(int i = 0; i < BOARD_LIMIT; i++) {
            spacesList.add(new ArrayList<>());
            for(int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = positions.get("%s,%s".formatted(i, j));

                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);

                var currentSpace = new Space(expected, fixed);
                spacesList.get(i).add(currentSpace);
            }
        }
        
        board = new Board(spacesList);
        System.out.println("O jogo está pronto para começar!");
    }

    private static void inputNumber() {
        if(isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Informe a coluna em que o número será inserido:");
        var column = runUntilGetValidNumber(0,8);
        System.out.println("Informe a linha em que o número será inserido:");
        var row = runUntilGetValidNumber(0,8);
        System.out.printf("Informe o número que será inserido na posição [%s,%S]\n", column, row);
        var spaceValue =  runUntilGetValidNumber(1,9);

        if(!board.changeValue(column, row, spaceValue)) {
            System.out.printf("A posição [%s,%s] já possui um valor fixo!\n", column, row);
        }
    }

    private static void removeNumber() {
        if(isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Informe a coluna em que o número será inserido:");
        var column = runUntilGetValidNumber(0,8);
        System.out.println("Informe a linha em que o número será inserido:");
        var row = runUntilGetValidNumber(0,8);
        System.out.printf("Informe o número que será removido na posição [%s,%S]\n", column, row);

        if(!board.clearSpace(column, row)) {
            System.out.printf("A posição [%s,%s] possui um valor fixo!\n", column, row);
        }
    }

    private static void showCurrentGame() {
        if(isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        var boardSpaces = 81;
        var args = new Object[boardSpaces];
        var argPosition = 0;

        for(int i = 0; i < BOARD_LIMIT; i++) {
            for(var column: board.getSpaces()) {
                args[argPosition ++] = " " + (isNull(column.get(i).getActual()) ? " " : column.get(i).getActual());
            }
        }

        System.out.println("Seu jogo se encontra assim:");
        System.out.printf((BoardTemplate.BOARD_TEMPLATE) + "\n", args);

    }

    private static void showGameStatus() {
        if(isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.printf("O status do jogo atualmente é %s\n", board.getStatus().getLabel());
        
        if(board.hasErrors()) {
            System.out.println("O jogo contém erros!");
        } else {
            System.out.println("O jogo não contém erros!");
        }
    }

    private static void clearGame() {
        if(isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo?");
        var confirm = scanner.next();

        while (!confirm.equalsIgnoreCase("sim") || !confirm.equalsIgnoreCase("não")) {
            System.out.println("Responda 'SIM' ou 'NÃO'.");
            confirm = scanner.next();
        }

        if (confirm.equalsIgnoreCase("sim")) {
            board.reset();
        }
    }

    private static void finishGame() {
       if(isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        if(board.gameIsFinished()) {
            System.out.println("Parabéns! Você concluiu o jogo!");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo ainda contém erros! Verifique e ajuste");
        } else {
            System.out.println("Seu jogo ainda possui espaços vazios!");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        var current =  scanner.nextInt();

        while(current < min || current > max) {
            System.out.printf("Informe um número entre %s e %s.\n", min, max);
            current = scanner.nextInt();
        }

        return current;
    }
}
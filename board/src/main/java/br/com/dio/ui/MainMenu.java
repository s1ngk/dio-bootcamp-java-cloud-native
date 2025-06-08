package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.INITIAL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.PENDING;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao gerenciador de boards.\n");
        System.out.println("Escolha uma opção:");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar um board novo.");
            System.out.println("2 - Selecionar um board já existente.");
            System.out.println("3 - Excluir um dos boards.");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Informe o nome do board a ser criado.");
        entity.setName(scanner.next());

        System.out.println("Deseja acrescentar colunas além das padrões? \n Se sim, informe a quantidade de colunas extras, se não digite 0.");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome para a coluna inicial do seu board:");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome para a coluna de tarefas pendente:");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
            
        }

        System.out.println("Informe o nome para a coluna final:");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, PENDING, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome para a coluna de cancelamento:");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, PENDING, additionalColumns + 1);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try (var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }

    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board a ser selecionado.");
        var id = scanner.nextLong();
        try (var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("O board %s informado não foi encontrado.", id));
        }
    }

    private void deleteBoard() throws SQLException {

        System.out.println("Informe o id do board que será deletado:");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {

            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("O board %s foi excluído com sucesso.", id);
            } else {
                System.out.printf("O board %s informado não foi encontrado.", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;

    }
}

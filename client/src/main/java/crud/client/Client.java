package crud.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	 private final String host;
	    private final int port;
	    private final Scanner scanner;
	    
	    // Conexão persistente
	    private Socket socket;
	    private PrintWriter out;
	    private BufferedReader in;

	    public Client(String host, int port) {
	        this.host = host;
	        this.port = port;
	        this.scanner = new Scanner(System.in);
	    }

	    /**
	     * Estabelece conexão com o servidor
	     */
	    private void conectar() throws IOException {
	        if (socket == null || socket.isClosed()) {
	            socket = new Socket(host, port);
	            out = new PrintWriter(socket.getOutputStream(), true);
	            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            System.out.println("Conectado ao servidor " + host + ":" + port);
	        }
	    }

	    /**
	     * Fecha a conexão com o servidor
	     */
	    private void desconectar() {
	        try {
	            if (out != null) {
	                out.println("QUIT"); // Sinal para o servidor encerrar
	            }
	            if (in != null) {
	                in.close();
	            }
	            if (out != null) {
	                out.close();
	            }
	            if (socket != null && !socket.isClosed()) {
	                socket.close();
	            }
	            System.out.println("Conexão fechada");
	        } catch (IOException e) {
	            System.err.println("Erro ao fechar conexão: " + e.getMessage());
	        }
	    }

	    /**
	     * Envia mensagem para o servidor e retorna a resposta
	     */
	    private String sendMessage(String message) throws IOException {
	        conectar(); // Garante que está conectado
	        
	        // Envia mensagem
	        out.println(message);
	        
	        // Lê resposta
	        StringBuilder response = new StringBuilder();
	        String line;
	        while ((line = in.readLine()) != null) {
	            if ("<END>".equals(line)) break;
	            response.append(line).append("\n");
	        }
	        
	        return response.toString().trim();
	    }


    /**
     * Exibe o menu principal
     */
    private void showMainMenu() {
        System.out.println("\n=== Sistema de Gestão de Aviação ===");
        System.out.println("1. Gerenciar Tripulantes");
        System.out.println("2. Gerenciar Passageiros");
        System.out.println("3. Gerenciar Aviões");
        System.out.println("4. Gerenciar Tripulação de Aviões");
        System.out.println("5. Relatórios Gerais");
        System.out.println("6. Sair");
        System.out.println("====================================");
        System.out.print("Escolha uma opção: ");
    }

    private void showTripulanteMenu() {
        System.out.println("\n=== Gerenciar Tripulantes ===");
        System.out.println("1. Inserir tripulante");
        System.out.println("2. Atualizar tripulante");
        System.out.println("3. Buscar tripulante");
        System.out.println("4. Remover tripulante");
        System.out.println("5. Listar tripulantes");
        System.out.println("6. Voltar");
        System.out.println("=============================");
        System.out.print("Escolha uma opção: ");
    }

    private void showPassageiroMenu() {
        System.out.println("\n=== Gerenciar Passageiros ===");
        System.out.println("1. Inserir passageiro");
        System.out.println("2. Atualizar passageiro");
        System.out.println("3. Buscar passageiro");
        System.out.println("4. Remover passageiro");
        System.out.println("5. Listar passageiros");
        System.out.println("6. Voltar");
        System.out.println("=============================");
        System.out.print("Escolha uma opção: ");
    }

    private void showAviaoMenu() {
        System.out.println("\n=== Gerenciar Aviões ===");
        System.out.println("1. Inserir avião");
        System.out.println("2. Atualizar avião");
        System.out.println("3. Buscar avião");
        System.out.println("4. Remover avião");
        System.out.println("5. Listar aviões");
        System.out.println("6. Voltar");
        System.out.println("========================");
        System.out.print("Escolha uma opção: ");
    }

    private void showTripulacaoMenu() {
        System.out.println("\n=== Gerenciar Tripulação ===");
        System.out.println("1. Adicionar tripulante ao avião");
        System.out.println("2. Remover tripulante do avião");
        System.out.println("3. Listar tripulantes do avião");
        System.out.println("4. Voltar");
        System.out.println("============================");
        System.out.print("Escolha uma opção: ");
    }

    /**
     * Lê entrada do usuário
     */
    private String readInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                String input = readInput(prompt);
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, digite um número válido");
            }
        }
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                String input = readInput(prompt);
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, digite um número inteiro válido");
            }
        }
    }

    // ========== OPERAÇÕES DE TRIPULANTE ==========
    private void handleTripulante() {
        while (true) {
            showTripulanteMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1": insertTripulante(); break;
                case "2": updateTripulante(); break;
                case "3": getTripulante(); break;
                case "4": deleteTripulante(); break;
                case "5": listTripulantes(); break;
                case "6": return;
                default: System.out.println("❌ Opção inválida");
            }
            pause();
        }
    }

    private void insertTripulante() {
        System.out.println("\n--- Inserir Tripulante ---");
        String cpf = readInput("CPF: ");
        String nome = readInput("Nome: ");
        String endereco = readInput("Endereço: ");
        double salario = readDouble("Salário: R$ ");
        String cargo = readInput("Cargo: ");

        if (cpf.isEmpty() || nome.isEmpty() || endereco.isEmpty() || cargo.isEmpty()) {
            System.out.println("❌ Todos os campos são obrigatórios");
            return;
        }

        String message = String.format("INSERT;TRIPULANTE;%s;%s;%s;%.2f;%s",
                cpf, nome, endereco, salario, cargo);

        try {
            String response = sendMessage(message);
            if (response.isEmpty()) {
                System.out.println("✅ Tripulante inserido com sucesso");
            } else {
                System.out.println("Resposta: " + response);
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao inserir tripulante: " + e.getMessage());
        }
    }

    private void updateTripulante() {
        System.out.println("\n--- Atualizar Tripulante ---");
        String cpf = readInput("CPF (não pode ser alterado): ");
        String nome = readInput("Novo nome: ");
        String endereco = readInput("Novo endereço: ");
        double salario = readDouble("Novo salário: R$ ");
        String cargo = readInput("Novo cargo: ");

        String message = String.format("UPDATE;TRIPULANTE;%s;%s;%s;%.2f;%s",
                cpf, nome, endereco, salario, cargo);

        try {
            String response = sendMessage(message);
            System.out.println("Resposta: " + response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao atualizar tripulante: " + e.getMessage());
        }
    }

    private void getTripulante() {
        System.out.println("\n--- Buscar Tripulante ---");
        String cpf = readInput("CPF: ");

        String message = "GET;TRIPULANTE;" + cpf;

        try {
            String response = sendMessage(message);

            if (response.startsWith("TRIPULANTE;")) {
                displayTripulante(response);
            } else {
                System.out.println("Resposta: " + response);
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao buscar tripulante: " + e.getMessage());
        }
    }

    private void deleteTripulante() {
        System.out.println("\n--- Remover Tripulante ---");
        String cpf = readInput("CPF: ");

        String message = "DELETE;TRIPULANTE;" + cpf;

        try {
            String response = sendMessage(message);
            System.out.println("Resposta: " + response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao remover tripulante: " + e.getMessage());
        }
    }

    private void listTripulantes() {
        System.out.println("\n--- Listar Tripulantes ---");

        try {
            String response = sendMessage("LIST;TRIPULANTE");
            displayTripulanteList(response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao listar tripulantes: " + e.getMessage());
        }
    }

    // ========== OPERAÇÕES DE PASSAGEIRO ==========
    private void handlePassageiro() {
        while (true) {
            showPassageiroMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1": insertPassageiro(); break;
                case "2": updatePassageiro(); break;
                case "3": getPassageiro(); break;
                case "4": deletePassageiro(); break;
                case "5": listPassageiros(); break;
                case "6": return;
                default: System.out.println("❌ Opção inválida");
            }
            pause();
        }
    }

    private void insertPassageiro() {
        System.out.println("\n--- Inserir Passageiro ---");
        String cpf = readInput("CPF: ");
        String nome = readInput("Nome: ");
        String endereco = readInput("Endereço: ");
        String poltrona = readInput("Poltrona: ");

        String message = String.format("INSERT;PASSAGEIRO;%s;%s;%s;%s",
                cpf, nome, endereco, poltrona);

        try {
            String response = sendMessage(message);
            if (response.isEmpty()) {
                System.out.println("✅ Passageiro inserido com sucesso");
            } else {
                System.out.println("Resposta: " + response);
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao inserir passageiro: " + e.getMessage());
        }
    }

    private void updatePassageiro() {
        System.out.println("\n--- Atualizar Passageiro ---");
        String cpf = readInput("CPF (não pode ser alterado): ");
        String nome = readInput("Novo nome: ");
        String endereco = readInput("Novo endereço: ");
        String poltrona = readInput("Nova poltrona: ");

        String message = String.format("UPDATE;PASSAGEIRO;%s;%s;%s;%s",
                cpf, nome, endereco, poltrona);

        try {
            String response = sendMessage(message);
            System.out.println("Resposta: " + response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao atualizar passageiro: " + e.getMessage());
        }
    }

    private void getPassageiro() {
        System.out.println("\n--- Buscar Passageiro ---");
        String cpf = readInput("CPF: ");

        String message = "GET;PASSAGEIRO;" + cpf;

        try {
            String response = sendMessage(message);

            if (response.startsWith("PASSAGEIRO;")) {
                displayPassageiro(response);
            } else {
                System.out.println("Resposta: " + response);
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao buscar passageiro: " + e.getMessage());
        }
    }

    private void deletePassageiro() {
        System.out.println("\n--- Remover Passageiro ---");
        String cpf = readInput("CPF: ");

        String message = "DELETE;PASSAGEIRO;" + cpf;

        try {
            String response = sendMessage(message);
            System.out.println("Resposta: " + response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao remover passageiro: " + e.getMessage());
        }
    }

    private void listPassageiros() {
        System.out.println("\n--- Listar Passageiros ---");

        try {
            String response = sendMessage("LIST;PASSAGEIRO");
            displayPassageiroList(response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao listar passageiros: " + e.getMessage());
        }
    }

    // ========== OPERAÇÕES DE AVIÃO ==========
    private void handleAviao() {
        while (true) {
            showAviaoMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1": insertAviao(); break;
                case "2": updateAviao(); break;
                case "3": getAviao(); break;
                case "4": deleteAviao(); break;
                case "5": listAvioes(); break;
                case "6": return;
                default: System.out.println("❌ Opção inválida");
            }
            pause();
        }
    }

    private void insertAviao() {
        System.out.println("\n--- Inserir Avião ---");
        String modelo = readInput("Modelo: ");
        int anoFabricacao = readInt("Ano de Fabricação: ");
        String marca = readInput("Marca: ");

        String message = String.format("INSERT;AVIAO;%s;%d;%s", modelo, anoFabricacao, marca);

        try {
            String response = sendMessage(message);
            if (response.isEmpty()) {
                System.out.println("✅ Avião inserido com sucesso");
            } else {
                System.out.println("Resposta: " + response);
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao inserir avião: " + e.getMessage());
        }
    }

    private void updateAviao() {
        System.out.println("\n--- Atualizar Avião ---");
        System.out.println("Dados atuais:");
        String modeloAtual = readInput("Modelo atual: ");
        int anoAtual = readInt("Ano atual: ");
        String marcaAtual = readInput("Marca atual: ");

        System.out.println("Novos dados:");
        String novoModelo = readInput("Novo modelo: ");
        int novoAno = readInt("Novo ano: ");
        String novaMarca = readInput("Nova marca: ");

        String message = String.format("UPDATE;AVIAO;%s;%d;%s;%s;%d;%s",
                modeloAtual, anoAtual, marcaAtual,
                novoModelo, novoAno, novaMarca);

        try {
            String response = sendMessage(message);
            System.out.println("Resposta: " + response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao atualizar avião: " + e.getMessage());
        }
    }

    private void getAviao() {
        System.out.println("\n--- Buscar Avião ---");
        String modelo = readInput("Modelo: ");
        int anoFabricacao = readInt("Ano de Fabricação: ");
        String marca = readInput("Marca: ");

        String message = String.format("GET;AVIAO;%s;%d;%s", modelo, anoFabricacao, marca);

        try {
            String response = sendMessage(message);
            displayAviao(response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao buscar avião: " + e.getMessage());
        }
    }

    private void deleteAviao() {
        System.out.println("\n--- Remover Avião ---");
        String modelo = readInput("Modelo: ");
        int anoFabricacao = readInt("Ano de Fabricação: ");
        String marca = readInput("Marca: ");

        String message = String.format("DELETE;AVIAO;%s;%d;%s", modelo, anoFabricacao, marca);

        try {
            String response = sendMessage(message);
            System.out.println("Resposta: " + response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao remover avião: " + e.getMessage());
        }
    }

    private void listAvioes() {
        System.out.println("\n--- Listar Aviões ---");

        try {
            String response = sendMessage("LIST;AVIAO");
            displayAviaoList(response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao listar aviões: " + e.getMessage());
        }
    }

    // ========== OPERAÇÕES DE TRIPULAÇÃO ==========
    private void handleTripulacao() {
        while (true) {
            showTripulacaoMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1": addTripulanteToAviao(); break;
                case "2": removeTripulanteFromAviao(); break;
                case "3": listTripulantesAviao(); break;
                case "4": return;
                default: System.out.println("❌ Opção inválida");
            }
            pause();
        }
    }

    private void addTripulanteToAviao() {
        System.out.println("\n--- Adicionar Tripulante ao Avião ---");
        String modelo = readInput("Modelo do avião: ");
        int anoFabricacao = readInt("Ano de fabricação: ");
        String marca = readInput("Marca do avião: ");
        String cpfTripulante = readInput("CPF do tripulante: ");

        String message = String.format("ADD_TRIPULANTE;%s;%d;%s;%s",
                modelo, anoFabricacao, marca, cpfTripulante);

        try {
            String response = sendMessage(message);
            System.out.println("Resposta: " + response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao adicionar tripulante: " + e.getMessage());
        }
    }

    private void removeTripulanteFromAviao() {
        System.out.println("\n--- Remover Tripulante do Avião ---");
        String modelo = readInput("Modelo do avião: ");
        int anoFabricacao = readInt("Ano de fabricação: ");
        String marca = readInput("Marca do avião: ");
        String cpfTripulante = readInput("CPF do tripulante: ");

        String message = String.format("REMOVE_TRIPULANTE;%s;%d;%s;%s",
                modelo, anoFabricacao, marca, cpfTripulante);

        try {
            String response = sendMessage(message);
            System.out.println("Resposta: " + response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao remover tripulante: " + e.getMessage());
        }
    }

    private void listTripulantesAviao() {
        System.out.println("\n--- Listar Tripulantes do Avião ---");
        String modelo = readInput("Modelo do avião: ");
        int anoFabricacao = readInt("Ano de fabricação: ");
        String marca = readInput("Marca do avião: ");

        String message = String.format("LIST_TRIPULANTES;%s;%d;%s", modelo, anoFabricacao, marca);

        try {
            String response = sendMessage(message);
            displayTripulanteList(response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao listar tripulantes: " + e.getMessage());
        }
    }

    // ========== RELATÓRIOS GERAIS ==========
    private void handleRelatorios() {
        System.out.println("\n--- Relatórios Gerais ---");

        try {
            String response = sendMessage("LIST;ALL");
            displayRelatorioGeral(response);
        } catch (IOException e) {
            System.err.println("❌ Erro ao gerar relatório: " + e.getMessage());
        }
    }

    // ========== MÉTODOS DE EXIBIÇÃO ==========
    private void displayTripulante(String data) {
        // Formato: TRIPULANTE;cpf;nome;endereco;salario;cargo
        String[] parts = data.split(";");
        if (parts.length >= 6) {
            System.out.println("\n✅ Tripulante encontrado:");
            System.out.println("CPF: " + parts[1]);
            System.out.println("Nome: " + parts[2]);
            System.out.println("Endereço: " + parts[3]);
            System.out.println("Salário: R$ " + parts[4]);
            System.out.println("Cargo: " + parts[5]);
        } else {
            System.out.println("Resposta: " + data);
        }
    }

    private void displayPassageiro(String data) {
        // Formato: PASSAGEIRO;cpf;nome;endereco;poltrona
        String[] parts = data.split(";");
        if (parts.length >= 5) {
            System.out.println("\n✅ Passageiro encontrado:");
            System.out.println("CPF: " + parts[1]);
            System.out.println("Nome: " + parts[2]);
            System.out.println("Endereço: " + parts[3]);
            System.out.println("Poltrona: " + parts[4]);
        } else {
            System.out.println("Resposta: " + data);
        }
    }

    private void displayAviao(String data) {
        if ("Avião não encontrado".equals(data) || "Sem aviões cadastrados".equals(data)) {
            System.out.println("Resposta: " + data);
            return;
        }

        // Formato: modelo;anoFabricacao;marca;numTripulantes;[tripulantes...]
        String[] parts = data.split(";");
        if (parts.length >= 4) {
            System.out.println("\n✅ Avião encontrado:");
            System.out.println("Modelo: " + parts[0]);
            System.out.println("Ano de Fabricação: " + parts[1]);
            System.out.println("Marca: " + parts[2]);

            try {
                int numTripulantes = Integer.parseInt(parts[3]);
                System.out.println("Tripulantes: " + numTripulantes);

                if (numTripulantes > 0) {
                    System.out.println("\nTripulação:");
                    int index = 4;
                    for (int i = 0; i < numTripulantes && index < parts.length; i++) {
                        if (index < parts.length) {
                            String tipo = parts[index++];
                            if ("TRIPULANTE".equals(tipo) && index + 4 < parts.length) {
                                System.out.printf("  %d. %s - %s (%s)\n",
                                        i + 1, parts[index + 1], parts[index + 4], parts[index]);
                                index += 5; // cpf, nome, endereco, salario, cargo
                            }
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro ao processar tripulantes");
            }
        } else {
            System.out.println("Resposta: " + data);
        }
    }

    private void displayTripulanteList(String response) {
        if ("0".equals(response)) {
            System.out.println("Nenhum tripulante cadastrado");
            return;
        }

        String[] lines = response.split("\n");
        if (lines.length > 0) {
            try {
                int count = Integer.parseInt(lines[0]);
                System.out.println("\n✅ " + count + " tripulante(s) encontrado(s):");
                System.out.println("---------------------------------------------------");

                for (int i = 1; i <= count && i < lines.length; i++) {
                    String[] dados = lines[i].split(";");
                    if (dados.length >= 6) {
                        System.out.printf("%d. CPF: %s | Nome: %s | Cargo: %s | Salário: R$ %s\n",
                                i, dados[1], dados[2], dados[5], dados[4]);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro ao processar lista de tripulantes");
            }
        }
    }

    private void displayPassageiroList(String response) {
        if ("0".equals(response)) {
            System.out.println("Nenhum passageiro cadastrado");
            return;
        }

        String[] lines = response.split("\n");
        if (lines.length > 0) {
            try {
                int count = Integer.parseInt(lines[0]);
                System.out.println("\n✅ " + count + " passageiro(s) encontrado(s):");
                System.out.println("-------------------------------------------");

                for (int i = 1; i <= count && i < lines.length; i++) {
                    String[] dados = lines[i].split(";");
                    if (dados.length >= 5) {
                        System.out.printf("%d. CPF: %s | Nome: %s | Poltrona: %s\n",
                                i, dados[1], dados[2], dados[4]);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro ao processar lista de passageiros");
            }
        }
    }

    private void displayAviaoList(String response) {
        if ("0".equals(response)) {
            System.out.println("Nenhum avião cadastrado");
            return;
        }

        String[] lines = response.split("\n");
        if (lines.length > 0) {
            try {
                int count = Integer.parseInt(lines[0]);
                System.out.println("\n✅ " + count + " avião(ões) encontrado(s):");
                System.out.println("---------------------------------------");

                for (int i = 1; i <= count && i < lines.length; i++) {
                    String[] dados = lines[i].split(";");
                    if (dados.length >= 4) {
                        System.out.printf("%d. %s %s (%s) - %s tripulante(s)\n",
                                i, dados[2], dados[0], dados[1], dados[3]);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro ao processar lista de aviões");
            }
        }
    }

    private void displayRelatorioGeral(String response) {
        System.out.println("\n=== RELATÓRIO GERAL DO SISTEMA ===");

        String[] lines = response.split("\n");
        for (String line : lines) {
            String[] parts = line.split(";");
            if (parts.length >= 2) {
                String tipo = parts[0];
                switch (tipo) {
                    case "ESTATISTICAS":
                        System.out.println("📊 " + parts[1]);
                        break;
                    case "TRIPULANTES":
                        System.out.println("\n👨‍✈️ TRIPULANTES (" + parts[1] + "):");
                        break;
                    case "PASSAGEIROS":
                        System.out.println("\n🧳 PASSAGEIROS (" + parts[1] + "):");
                        break;
                    case "AVIOES":
                        System.out.println("\n✈️ AVIÕES (" + parts[1] + "):");
                        break;
                    case "TRIPULANTE":
                        if (parts.length >= 6) {
                            System.out.printf("  • %s - %s (%s)\n", parts[2], parts[5], parts[1]);
                        }
                        break;
                    case "PASSAGEIRO":
                        if (parts.length >= 5) {
                            System.out.printf("  • %s - Poltrona %s (%s)\n", parts[2], parts[4], parts[1]);
                        }
                        break;
                    case "AVIAO":
                        if (parts.length >= 4) {
                            System.out.printf("  • %s %s (%s)\n", parts[3], parts[1], parts[2]);
                        }
                        break;
                }
            }
        }
        System.out.println("===================================");
    }

    private void pause() {
        System.out.print("\nPressione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Inicia o cliente
     */
    public void start() {
        System.out.println("Cliente do Sistema de Gestão de Aviação iniciado");
        System.out.println("Conectando ao servidor " + host + ":" + port);
        
        try {
        	 while (true) {
                 showMainMenu();
                 String option = scanner.nextLine().trim();

                 switch (option) {
                     case "1":
                         handleTripulante();
                         break;
                     case "2":
                         handlePassageiro();
                         break;
                     case "3":
                         handleAviao();
                         break;
                     case "4":
                         handleTripulacao();
                         break;
                     case "5":
                         handleRelatorios();
                         pause();
                         break;
                     case "6":
                         System.out.println("Encerrando cliente...");
                         desconectar();
                         scanner.close();
                         return;
                     default:
                         System.out.println("❌ Opção inválida");
                         pause();
                 }
             }
		} catch (Exception e) {
			desconectar();
		} 
       
    }

    public static void main(String[] args) {
        String host = "10.15.120.175";
        int port = 8080;

        // Permite especificar host e porta via argumentos
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Porta inválida. Usando porta padrão: " + port);
            }
        }

        Client client = new Client(host, port);
        client.start();
    }
}
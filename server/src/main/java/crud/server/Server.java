package crud.server;

import crud.repository.RepositoryManager;
import crud.service.MessageProcessor;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {
    private final int port;
    private final RepositoryManager repositoryManager;
    private final MessageProcessor messageProcessor;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public Server(int port) {
        this.port = port;
        this.repositoryManager = new RepositoryManager();
        this.messageProcessor = new MessageProcessor(repositoryManager);
        this.running = false;
    }

    /**
     * Inicia o servidor
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;

        log("=== Servidor de Gestão de Aviação ===");
        log("Servidor iniciado na porta " + port);
        log("Aguardando conexões...");

        // Adiciona hook para shutdown graceful
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        while (running) {
            try {
                // Aceita conexão do cliente
                Socket clientSocket = serverSocket.accept();

                // Processa cliente em thread separada para suportar múltiplos clientes
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.setDaemon(true); // Thread finaliza quando main finalizar
                clientThread.start();

            } catch (IOException e) {
                if (running) {
                    log("Erro ao aceitar conexão: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Trata a conexão de um cliente
     */
    private void handleClient(Socket clientSocket) {
        String clientInfo = clientSocket.getRemoteSocketAddress().toString();
        log("Cliente conectado: " + clientInfo);

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // Lê a mensagem do cliente
            String message = in.readLine();
            if (message != null && !message.trim().isEmpty()) {
                log("Mensagem de " + clientInfo + ": " + message);

                // Processa a mensagem
                String response = messageProcessor.processMessage(message);

                // Envia resposta
                out.println(response);

                // Log da resposta (truncado se muito longo)
                String logResponse = response.isEmpty() ? "[resposta vazia]" :
                        (response.length() > 100 ? response.substring(0, 97) + "..." : response);
                log("Resposta para " + clientInfo + ": " + logResponse);
            } else {
                log("Mensagem vazia recebida de " + clientInfo);
                out.println("Mensagem vazia");
            }

        } catch (IOException e) {
            log("Erro ao processar cliente " + clientInfo + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                log("Cliente desconectado: " + clientInfo);
            } catch (IOException e) {
                log("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    /**
     * Para o servidor
     */
    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                log("Servidor parado");

                // Exibe estatísticas finais
                log("Estatísticas finais: " + repositoryManager.getEstatsticas());
            } catch (IOException e) {
                log("Erro ao parar servidor: " + e.getMessage());
            }
        }
    }

    /**
     * Log com timestamp
     */
    private void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + timestamp + "] " + message);
    }


    public static void main(String[] args) {
        int port = 8080;
        Server server = new Server(port);

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
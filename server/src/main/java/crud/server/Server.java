package crud.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import crud.repository.RepositoryManager;
import crud.service.MessageProcessor;

public class Server {
    private final int port;
    private final RepositoryManager repositoryManager;
    private final MessageProcessor messageProcessor;

    public Server(int port) {
        this.port = port;
        this.repositoryManager = new RepositoryManager();
        this.messageProcessor = new MessageProcessor(repositoryManager);
    }


    public void iniciar() {
        try (ServerSocket server = new ServerSocket(port)) {
            server.setReuseAddress(true);
            System.out.println("Servidor iniciado na porta " + port);

            while (true) {
                System.out.println("Aguardando conexão");
                try (Socket conn = server.accept()) {
                    String clientAddress = conn.getInetAddress().getHostAddress();
                    System.out.println("Conectado com " + clientAddress);
                    processarConexao(conn);        // mantém a sessão até cliente fechar/QUIT
                }
                System.out.println("Desconectado");
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
    
    private void processarConexao(Socket conn) {
        try (
            BufferedReader readerInput = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writerOutput = new BufferedWriter(
                new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            String mensagem;
            while ((mensagem = readerInput.readLine()) != null) {
                String cmd = mensagem.trim();
                if (cmd.isEmpty()) {
                    // resposta vazia, mas sinaliza término
                    writerOutput.write("<END>");
                    writerOutput.newLine();
                    writerOutput.flush();
                    continue;
                }

                // comando para encerrar a sessão
                if ("QUIT".equalsIgnoreCase(cmd)) {
                    writerOutput.write("BYE");
                    writerOutput.newLine();
                    writerOutput.write("<END>");
                    writerOutput.newLine();
                    writerOutput.flush();
                    break; // sai do loop => fecha socket (try-with-resources)
                }

                // processamento normal
                String resposta = messageProcessor.processMessage(cmd);
                if (resposta != null && !resposta.isEmpty()) {
                    for (String linha : resposta.split("\r")) {
                        writerOutput.write(linha);
                        writerOutput.newLine();
                    }
                }
                // marca fim da resposta
                writerOutput.write("<END>");
                writerOutput.newLine();
                writerOutput.flush();
            }
        } catch (IOException e) {
            System.err.println("Erro ao processar conexão: " + e.getMessage());
        }
    }
    
    
    public static void main(String[] args) {
        int port = 8080;

        if (args.length > 1) {
            System.err.println("Muitos argumentos!");
            return;
        }
        if (args.length == 1) {
            try { port = Integer.parseInt(args[0]); }
            catch (NumberFormatException e) {
                System.err.println("Porta inválida, precisa ser um número");
            }
        }
        if (port < 1 || port > 65535) {
            System.err.println("Porta deve estar entre 1 e 65535!");
            return;
        }

        new Server(port).iniciar();
    }
 
}
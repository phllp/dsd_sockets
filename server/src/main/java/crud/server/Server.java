package crud.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
                    processarConexao(conn);
                }
                System.out.println("Desconectado");
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    private void processarConexao(Socket conn) {
        try (BufferedReader readerInput = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            BufferedWriter writerOutput = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String mensagem;
            if ((mensagem = readerInput.readLine())!= null) {
                String mensagemRetorno = messageProcessor.processMessage(mensagem);

                if(mensagemRetorno != null) {
                    writerOutput.write(mensagemRetorno);
                    writerOutput.newLine();
                    writerOutput.flush();
                } else {
                    writerOutput.newLine();
                    writerOutput.flush();
                }
            }
            writerOutput.close();
        } catch (IOException e) {
            System.err.println("Erro ao processar conexão: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 8080;

        try {
            if (args.length > 1) {
                System.err.println("Muitos argumentos!");
                return;
            }
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Porta inválida, precisa ser um número");
                }
            }


            // Validações
            if (port < 1 || port > 65535) {
                System.err.println("Porta deve estar entre 1 e 65535!");
                return;
            }

        } catch (NumberFormatException e) {
            System.err.println("Porta inválida: " + args[0]);
            return;
        }

        Server servidor = new Server(port);
        servidor.iniciar();
    }
}
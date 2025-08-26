package crud.service;

import crud.model.*;
import crud.repository.*;
import java.util.List;

/**
 * Classe para tratar as mensagens recebidas do cliente
 */
public class MessageProcessor {
    private final RepositoryManager repositoryManager;
    private final PessoaRepository pessoaRepository;
    private final AviaoRepository aviaoRepository;

    public MessageProcessor(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        this.pessoaRepository = repositoryManager.getPessoaRepository();
        this.aviaoRepository = repositoryManager.getAviaoRepository();
    }

    /**
     * Processa mensagem recebida do cliente
     * Formato geral: OPERACAO;TIPO;dados...
     */
    public String processMessage(String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return "Mensagem vazia";
            }

            String[] fields = message.trim().split(";");
            if (fields.length < 2) {
                return "Formato inválido. Use: OPERACAO;TIPO;dados...";
            }

            String operation = fields[0].toUpperCase();
            String type = fields[1].toUpperCase();

            switch (operation) {
                case "INSERT":
                    return handleInsert(type, fields);
                case "UPDATE":
                    return handleUpdate(type, fields);
                case "GET":
                    return handleGet(type, fields);
                case "DELETE":
                    return handleDelete(type, fields);
                case "LIST":
                    return handleList(type, fields);
                case "ADD_TRIPULANTE":
                    return handleAddTripulante(fields);
                case "REMOVE_TRIPULANTE":
                    return handleRemoveTripulante(fields);
                case "LIST_TRIPULANTES":
                    return handleListTripulantes(fields);
                default:
                    return "Operação não reconhecida: " + operation;
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem: " + e.getMessage());
            e.printStackTrace();
            return "Erro interno do servidor: " + e.getMessage();
        }
    }

    // ========== OPERAÇÕES INSERT ==========
    private String handleInsert(String type, String[] fields) {
        switch (type) {
            case "TRIPULANTE":
                return insertTripulante(fields);
            case "PASSAGEIRO":
                return insertPassageiro(fields);
            case "AVIAO":
                return insertAviao(fields);
            default:
                return "Tipo inválido para inserção: " + type;
        }
    }

    private String insertTripulante(String[] fields) {
        // INSERT;TRIPULANTE;cpf;nome;endereco;salario;cargo
        if (fields.length < 7) {
            return "Dados insuficientes. Formato: INSERT;TRIPULANTE;cpf;nome;endereco;salario;cargo";
        }

        try {
            Pessoa tripulante = PessoaFactory.criarPessoa("TRIPULANTE",
                    new String[]{fields[2], fields[3], fields[4], fields[5], fields[6]});
            pessoaRepository.insert(tripulante);

            System.out.println("Tripulante inserido: " + tripulante);
            return "";
        } catch (Exception e) {
            return "Erro ao inserir tripulante: " + e.getMessage();
        }
    }

    private String insertPassageiro(String[] fields) {
        // INSERT;PASSAGEIRO;cpf;nome;endereco;poltrona
        if (fields.length < 6) {
            return "Dados insuficientes. Formato: INSERT;PASSAGEIRO;cpf;nome;endereco;poltrona";
        }

        try {
            Pessoa passageiro = PessoaFactory.criarPessoa("PASSAGEIRO",
                    new String[]{fields[2], fields[3], fields[4], fields[5]});
            pessoaRepository.insert(passageiro);

            System.out.println("Passageiro inserido: " + passageiro);
            return "";
        } catch (Exception e) {
            return "Erro ao inserir passageiro: " + e.getMessage();
        }
    }

    private String insertAviao(String[] fields) {
        // INSERT;AVIAO;modelo;anoFabricacao;marca
        if (fields.length < 5) {
            return "Dados insuficientes. Formato: INSERT;AVIAO;modelo;anoFabricacao;marca";
        }

        try {
            // Ordem correta: modelo, anoFabricacao, marca
            Aviao aviao = new Aviao(fields[2], Integer.parseInt(fields[3]), fields[4]);
            aviaoRepository.insert(aviao);

            System.out.println("Avião inserido: " + aviao);
            return "";
        } catch (NumberFormatException e) {
            return "Ano de fabricação deve ser um número válido";
        } catch (Exception e) {
            return "Erro ao inserir avião: " + e.getMessage();
        }
    }

    // ========== OPERAÇÕES UPDATE ==========
    private String handleUpdate(String type, String[] fields) {
        switch (type) {
            case "TRIPULANTE":
                return updateTripulante(fields);
            case "PASSAGEIRO":
                return updatePassageiro(fields);
            case "AVIAO":
                return updateAviao(fields);
            default:
                return "Tipo inválido para atualização: " + type;
        }
    }

    private String updateTripulante(String[] fields) {
        // UPDATE;TRIPULANTE;cpf;nome;endereco;salario;cargo
        if (fields.length < 7) {
            return "Dados insuficientes para atualização de tripulante";
        }

        try {
            boolean success = pessoaRepository.update(fields[2], fields[3], fields[4], fields[5], fields[6]);
            return success ? "Tripulante atualizado com sucesso" : "Tripulante não encontrado";
        } catch (Exception e) {
            return "Erro ao atualizar tripulante: " + e.getMessage();
        }
    }

    private String updatePassageiro(String[] fields) {
        // UPDATE;PASSAGEIRO;cpf;nome;endereco;poltrona
        if (fields.length < 6) {
            return "Dados insuficientes para atualização de passageiro";
        }

        try {
            boolean success = pessoaRepository.update(fields[2], fields[3], fields[4], fields[5]);
            return success ? "Passageiro atualizado com sucesso" : "Passageiro não encontrado";
        } catch (Exception e) {
            return "Erro ao atualizar passageiro: " + e.getMessage();
        }
    }

    private String updateAviao(String[] fields) {
        // UPDATE;AVIAO;modeloAtual;anoAtual;marcaAtual;novoModelo;novoAno;novaMarca
        if (fields.length < 8) {
            return "Dados insuficientes. Formato: UPDATE;AVIAO;modeloAtual;anoAtual;marcaAtual;novoModelo;novoAno;novaMarca";
        }

        try {
            int anoAtual = Integer.parseInt(fields[3]);
            int novoAno = Integer.parseInt(fields[6]);

            boolean success = aviaoRepository.update(fields[2], anoAtual, fields[4], fields[5], novoAno, fields[7]);
            return success ? "Avião atualizado com sucesso" : "Avião não encontrado";
        } catch (NumberFormatException e) {
            return "Anos devem ser números válidos";
        } catch (Exception e) {
            return "Erro ao atualizar avião: " + e.getMessage();
        }
    }

    // ========== OPERAÇÕES GET ==========
    private String handleGet(String type, String[] fields) {
        switch (type) {
            case "TRIPULANTE":
            case "PASSAGEIRO":
                return getPessoa(fields);
            case "AVIAO":
                return getAviao(fields);
            default:
                return "Tipo inválido para busca: " + type;
        }
    }

    private String getPessoa(String[] fields) {
        // GET;TIPO;cpf
        if (fields.length < 3) {
            return "CPF não informado";
        }

        if (pessoaRepository.isEmpty()) {
            return "Sem pessoas cadastradas";
        }

        Pessoa pessoa = pessoaRepository.get(fields[2]);
        return pessoa != null ? pessoa.getTipo() + ";" + pessoa.toDataString() : "Pessoa não encontrada";
    }

    private String getAviao(String[] fields) {
        // GET;AVIAO;modelo;anoFabricacao;marca
        if (fields.length < 5) {
            return "Dados insuficientes. Formato: GET;AVIAO;modelo;anoFabricacao;marca";
        }

        if (aviaoRepository.isEmpty()) {
            return "Sem aviões cadastrados";
        }

        try {
            Aviao aviao = aviaoRepository.get(fields[2], Integer.parseInt(fields[3]), fields[4]);
            return aviao != null ? aviao.toFullDataString() : "Avião não encontrado";
        } catch (NumberFormatException e) {
            return "Ano deve ser um número válido";
        }
    }

    // ========== OPERAÇÕES DELETE ==========
    private String handleDelete(String type, String[] fields) {
        switch (type) {
            case "TRIPULANTE":
            case "PASSAGEIRO":
                return deletePessoa(fields);
            case "AVIAO":
                return deleteAviao(fields);
            default:
                return "Tipo inválido para remoção: " + type;
        }
    }

    private String deletePessoa(String[] fields) {
        // DELETE;TIPO;cpf
        if (fields.length < 3) {
            return "CPF não informado";
        }

        if (pessoaRepository.isEmpty()) {
            return "Sem pessoas cadastradas";
        }

        boolean success = pessoaRepository.delete(fields[2]);
        return success ? "Pessoa removida com sucesso" : "Pessoa não encontrada";
    }

    private String deleteAviao(String[] fields) {
        // DELETE;AVIAO;modelo;anoFabricacao;marca
        if (fields.length < 5) {
            return "Dados insuficientes para remoção de avião";
        }

        if (aviaoRepository.isEmpty()) {
            return "Sem aviões cadastrados";
        }

        try {
            boolean success = aviaoRepository.delete(fields[2], Integer.parseInt(fields[3]), fields[4]);
            return success ? "Avião removido com sucesso" : "Avião não encontrado";
        } catch (NumberFormatException e) {
            return "Ano deve ser um número válido";
        }
    }

    // ========== OPERAÇÕES LIST ==========
    private String handleList(String type, String[] fields) {
        switch (type) {
            case "TRIPULANTE":
                return listTripulantes();
            case "PASSAGEIRO":
                return listPassageiros();
            case "AVIAO":
                return listAvioes();
            case "ALL":
                return listAll();
            default:
                return "Tipo inválido para listagem: " + type;
        }
    }

    private String listTripulantes() {
        List<Pessoa> tripulantes = pessoaRepository.listByType("TRIPULANTE");
        return formatPessoaList(tripulantes);
    }

    private String listPassageiros() {
        List<Pessoa> passageiros = pessoaRepository.listByType("PASSAGEIRO");
        return formatPessoaList(passageiros);
    }

    private String listAvioes() {
        List<Aviao> avioes = aviaoRepository.list();

        if (avioes.isEmpty()) {
            return "0";
        }

        StringBuilder response = new StringBuilder();
        response.append(String.format("%02d", avioes.size())).append("\n");

        for (Aviao aviao : avioes) {
            response.append(aviao.toFullDataString()).append("\n");
        }

        return response.toString().trim();
    }

    private String listAll() {
        StringBuilder response = new StringBuilder();

        // Estatísticas gerais
        response.append("ESTATISTICAS;").append(repositoryManager.getEstatsticas()).append("\n");

        // Tripulantes
        List<Pessoa> tripulantes = pessoaRepository.listByType("TRIPULANTE");
        response.append("TRIPULANTES;").append(tripulantes.size()).append("\n");
        for (Pessoa tripulante : tripulantes) {
            response.append("TRIPULANTE;").append(tripulante.toDataString()).append("\n");
        }

        // Passageiros
        List<Pessoa> passageiros = pessoaRepository.listByType("PASSAGEIRO");
        response.append("PASSAGEIROS;").append(passageiros.size()).append("\n");
        for (Pessoa passageiro : passageiros) {
            response.append("PASSAGEIRO;").append(passageiro.toDataString()).append("\n");
        }

        // Aviões
        List<Aviao> avioes = aviaoRepository.list();
        response.append("AVIOES;").append(avioes.size()).append("\n");
        for (Aviao aviao : avioes) {
            response.append("AVIAO;").append(aviao.toFullDataString()).append("\n");
        }

        return response.toString().trim();
    }

    private String formatPessoaList(List<Pessoa> pessoas) {
        if (pessoas.isEmpty()) {
            return "0";
        }

        StringBuilder response = new StringBuilder();
        response.append(String.format("%02d", pessoas.size())).append("\n");

        for (Pessoa pessoa : pessoas) {
            response.append(pessoa.getTipo()).append(";").append(pessoa.toDataString()).append("\n");
        }

        return response.toString().trim();
    }

    // ========== OPERAÇÕES ESPECÍFICAS DE AVIÕES ==========
    private String handleAddTripulante(String[] fields) {
        // ADD_TRIPULANTE;modelo;anoFabricacao;marca;cpfTripulante
        if (fields.length < 5) {
            return "Dados insuficientes. Formato: ADD_TRIPULANTE;modelo;anoFabricacao;marca;cpfTripulante";
        }

        try {
            String modelo = fields[1];
            int anoFabricacao = Integer.parseInt(fields[2]);
            String marca = fields[3];
            String cpfTripulante = fields[4];

            // Verifica se o avião existe
            if (!aviaoRepository.exists(modelo, anoFabricacao, marca)) {
                return "Avião não encontrado";
            }

            // Verifica se a pessoa existe
            Pessoa pessoa = pessoaRepository.get(cpfTripulante);
            if (pessoa == null) {
                return "Pessoa não encontrada";
            }

//            if (!(pessoa instanceof Tripulante)) {
//                return "Apenas tripulantes podem ser adicionados ao avião";
//            }

            boolean success = aviaoRepository.adicionarTripulante(modelo, anoFabricacao, marca, pessoa);
            return success ? "Pessoa adicionado ao avião com sucesso" : "Erro ao adicionar Pessoa";

        } catch (NumberFormatException e) {
            return "Ano deve ser um número válido";
        } catch (Exception e) {
            return "Erro ao adicionar tripulante: " + e.getMessage();
        }
    }

    private String handleRemoveTripulante(String[] fields) {
        // REMOVE_TRIPULANTE;modelo;anoFabricacao;marca;cpfTripulante
        if (fields.length < 5) {
            return "Dados insuficientes. Formato: REMOVE_TRIPULANTE;modelo;anoFabricacao;marca;cpfTripulante";
        }

        try {
            String modelo = fields[1];
            int anoFabricacao = Integer.parseInt(fields[2]);
            String marca = fields[3];
            String cpfTripulante = fields[4];

            if (!aviaoRepository.exists(modelo, anoFabricacao, marca)) {
                return "Avião não encontrado";
            }

            boolean success = aviaoRepository.removerTripulante(modelo, anoFabricacao, marca, cpfTripulante);
            return success ? "Tripulante removido do avião com sucesso" : "Tripulante não encontrado no avião";

        } catch (NumberFormatException e) {
            return "Ano deve ser um número válido";
        } catch (Exception e) {
            return "Erro ao remover tripulante: " + e.getMessage();
        }
    }

    private String handleListTripulantes(String[] fields) {
        // LIST_TRIPULANTES;modelo;anoFabricacao;marca
        if (fields.length < 4) {
            return "Dados insuficientes. Formato: LIST_TRIPULANTES;modelo;anoFabricacao;marca";
        }

        try {
            String modelo = fields[1];
            int anoFabricacao = Integer.parseInt(fields[2]);
            String marca = fields[3];

            if (!aviaoRepository.exists(modelo, anoFabricacao, marca)) {
                return "Avião não encontrado";
            }

            List<Pessoa> tripulantes = aviaoRepository.listarTripulantes(modelo, anoFabricacao, marca);
            return formatPessoaList(tripulantes);

        } catch (NumberFormatException e) {
            return "Ano deve ser um número válido";
        } catch (Exception e) {
            return "Erro ao listar tripulantes: " + e.getMessage();
        }
    }
}
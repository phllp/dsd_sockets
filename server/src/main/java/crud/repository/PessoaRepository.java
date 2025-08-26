package crud.repository;

import crud.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repo para gerenciar Pessoas (Tripulantes e Passageiros)
 */
public class PessoaRepository {
    private final Map<String, Pessoa> pessoas;

    public PessoaRepository() {
        this.pessoas = new ConcurrentHashMap<>();
    }

    /**
     * Insere uma nova pessoa
     */
    public void insert(Pessoa pessoa) {
        pessoas.put(pessoa.getCpf(), pessoa);
    }

    /**
     * Atualiza uma pessoa existente baseado no tipo
     */
    public boolean update(String cpf, String nome, String endereco, String... extraParams) {
        Pessoa pessoa = pessoas.get(cpf);
        if (pessoa == null) {
            return false;
        }

        // Atualiza campos base
        pessoa.setNome(nome);
        pessoa.setEndereco(endereco);

        // Atualiza campos específicos baseado no tipo
        if (pessoa instanceof Tripulante && extraParams.length >= 2) {
            Tripulante tripulante = (Tripulante) pessoa;
            tripulante.setSalario(Double.parseDouble(extraParams[0]));
            tripulante.setCargo(extraParams[1]);
        } else if (pessoa instanceof Passageiro && extraParams.length >= 1) {
            Passageiro passageiro = (Passageiro) pessoa;
            passageiro.setPoltrona(extraParams[0]);
        }

        return true;
    }

    /**
     * Busca uma pessoa pelo CPF
     */
    public Pessoa get(String cpf) {
        return pessoas.get(cpf);
    }

    /**
     * Remove uma pessoa pelo CPF
     */
    public boolean delete(String cpf) {
        return pessoas.remove(cpf) != null;
    }

    /**
     * Lista todas as pessoas
     */
    public List<Pessoa> list() {
        return pessoas.values().stream()
                .sorted(Comparator.comparing(Pessoa::getCpf))
                .collect(Collectors.toList());
    }

    /**
     * Lista pessoas por tipo
     */
    public List<Pessoa> listByType(String tipo) {
        return pessoas.values().stream()
                .filter(p -> p.getTipo().equals(tipo.toUpperCase()))
                .sorted(Comparator.comparing(Pessoa::getCpf))
                .collect(Collectors.toList());
    }

    /**
     * Verifica se não há pessoas cadastradas
     */
    public boolean isEmpty() {
        return pessoas.isEmpty();
    }

    /**
     * Retorna a quantidade de pessoas cadastradas
     */
    public int size() {
        return pessoas.size();
    }

    /**
     * Verifica se uma pessoa existe
     */
    public boolean exists(String cpf) {
        return pessoas.containsKey(cpf);
    }
}

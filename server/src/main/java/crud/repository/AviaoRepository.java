package crud.repository;

import crud.model.Aviao;
import crud.model.Pessoa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repo para gerenciar Aviões
 */
public class AviaoRepository {
    private final Map<String, Aviao> avioes; // Chave: modelo+ano+marca

    public AviaoRepository() {
        this.avioes = new ConcurrentHashMap<>();
    }

    /**
     * Gera chave única para o avião
     */
    private String generateKey(String modelo, int anoFabricacao, String marca) {
        return modelo + "_" + anoFabricacao + "_" + marca;
    }

    private String generateKey(Aviao aviao) {
        return generateKey(aviao.getModelo(), aviao.getAnoFabricacao(), aviao.getMarca());
    }

    /**
     * Insere um novo avião
     */
    public void insert(Aviao aviao) {
        String key = generateKey(aviao);
        avioes.put(key, aviao);
    }

    /**
     * Atualiza um avião existente
     */
    public boolean update(String modelo, int anoFabricacao, String marca,
        String novoModelo, int novoAno, String novaMarca) {
        String oldKey = generateKey(modelo, anoFabricacao, marca);
        Aviao aviao = avioes.get(oldKey);

        if (aviao == null) {
            return false;
        }

        // Remove da chave antiga
        avioes.remove(oldKey);

        // Atualiza dados
        aviao.setModelo(novoModelo);
        aviao.setAnoFabricacao(novoAno);
        aviao.setMarca(novaMarca);

        // Insere com nova chave
        String newKey = generateKey(aviao);
        avioes.put(newKey, aviao);

        return true;
    }

    /**
     * Busca um avião
     */
    public Aviao get(String modelo, int anoFabricacao, String marca) {
        String key = generateKey(modelo, anoFabricacao, marca);
        return avioes.get(key);
    }

    /**
     * Remove um avião
     */
    public boolean delete(String modelo, int anoFabricacao, String marca) {
        String key = generateKey(modelo, anoFabricacao, marca);
        return avioes.remove(key) != null;
    }

    /**
     * Lista todos os aviões
     */
    public List<Aviao> list() {
        return avioes.values().stream()
                .sorted(Comparator.comparing(Aviao::getModelo)
                        .thenComparing(Aviao::getAnoFabricacao)
                        .thenComparing(Aviao::getMarca))
                .collect(Collectors.toList());
    }

    /**
     * Adiciona tripulante a um avião
     */
    public boolean adicionarTripulante(String modelo, int anoFabricacao, String marca, Pessoa tripulante) {
        Aviao aviao = get(modelo, anoFabricacao, marca);
        if (aviao != null) {
            aviao.adicionarTripulante(tripulante);
            return true;
        }
        return false;
    }

    /**
     * Remove tripulante de um avião
     */
    public boolean removerTripulante(String modelo, int anoFabricacao, String marca, String cpfTripulante) {
        Aviao aviao = get(modelo, anoFabricacao, marca);
        if (aviao != null) {
            return aviao.removerTripulante(cpfTripulante);
        }
        return false;
    }

    /**
     * Lista tripulantes de um avião
     */
    public List<Pessoa> listarTripulantes(String modelo, int anoFabricacao, String marca) {
        Aviao aviao = get(modelo, anoFabricacao, marca);
        if (aviao != null) {
            return aviao.getTripulantes();
        }
        return new ArrayList<>();
    }

    /**
     * Verifica se não há aviões cadastrados
     */
    public boolean isEmpty() {
        return avioes.isEmpty();
    }

    /**
     * Retorna a quantidade de aviões cadastrados
     */
    public int size() {
        return avioes.size();
    }

    /**
     * Verifica se um avião existe
     */
    public boolean exists(String modelo, int anoFabricacao, String marca) {
        String key = generateKey(modelo, anoFabricacao, marca);
        return avioes.containsKey(key);
    }
}

package crud.repository;

public class RepositoryManager {
    private final PessoaRepository pessoaRepository;
    private final AviaoRepository aviaoRepository;

    public RepositoryManager() {
        this.pessoaRepository = new PessoaRepository();
        this.aviaoRepository = new AviaoRepository();
    }

    public PessoaRepository getPessoaRepository() {
        return pessoaRepository;
    }

    public AviaoRepository getAviaoRepository() {
        return aviaoRepository;
    }

    /**
     * Obtém estatísticas gerais
     */
    public String getEstatsticas() {
        int totalPessoas = pessoaRepository.size();
        int totalAvioes = aviaoRepository.size();
        int tripulantes = pessoaRepository.listByType("TRIPULANTE").size();
        int passageiros = pessoaRepository.listByType("PASSAGEIRO").size();

        return String.format("Pessoas: %d (Tripulantes: %d, Passageiros: %d), Aviões: %d",
                totalPessoas, tripulantes, passageiros, totalAvioes);
    }
}
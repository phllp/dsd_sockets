package crud.model;

public class PessoaFactory {
    public static Pessoa criarPessoa(String tipo, String[] dados) {
        switch (tipo.toUpperCase()) {
            case "TRIPULANTE":
                if (dados.length >= 5) {
                    return new Tripulante(
                            dados[0], // cpf
                            dados[1], // nome
                            dados[2], // endereco
                            Double.parseDouble(dados[3]), // salario
                            dados[4]  // cargo
                    );
                }
                break;
            case "PASSAGEIRO":
                if (dados.length >= 4) {
                    return new Passageiro(
                            dados[0], // cpf
                            dados[1], // nome
                            dados[2], // endereco
                            dados[3]  // poltrona
                    );
                }
                break;
        }
        throw new IllegalArgumentException("Dados insuficientes ou tipo inválido: " + tipo);
    }
}
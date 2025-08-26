package crud.model;

public class Passageiro extends Pessoa {
    private String poltrona;

    public Passageiro(String cpf, String nome, String endereco, String poltrona) {
        super(cpf, nome, endereco);
        this.poltrona = poltrona;
    }

    // Getter específico
    public String getPoltrona() {
        return poltrona;
    }

    // Setter específico
    public void setPoltrona(String poltrona) {
        this.poltrona = poltrona;
    }

    @Override
    public String getTipo() {
        return "PASSAGEIRO";
    }

    @Override
    public String toDataString() {
        return getBaseData() + ";" + poltrona;
    }

    @Override
    public String toString() {
        return "Passageiro{" +
                "cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", poltrona='" + poltrona + '\'' +
                '}';
    }
}

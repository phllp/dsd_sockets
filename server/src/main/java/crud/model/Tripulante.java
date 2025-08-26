package crud.model;

public class Tripulante extends Pessoa {
    private double salario;
    private String cargo;

    public Tripulante(String cpf, String nome, String endereco, double salario, String cargo) {
        super(cpf, nome, endereco);
        this.salario = salario;
        this.cargo = cargo;
    }

    // Getters específicos
    public double getSalario() {
        return salario;
    }

    public String getCargo() {
        return cargo;
    }

    // Setters específicos
    public void setSalario(double salario) {
        this.salario = salario;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @Override
    public String getTipo() {
        return "TRIPULANTE";
    }

    @Override
    public String toDataString() {
        return getBaseData() + ";" + salario + ";" + cargo;
    }

    @Override
    public String toString() {
        return "Tripulante{" +
                "cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", salario=" + salario +
                ", cargo='" + cargo + '\'' +
                '}';
    }
}
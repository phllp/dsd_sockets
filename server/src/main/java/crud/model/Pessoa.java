package crud.model;

public abstract class Pessoa {
    protected String cpf;
    protected String nome;
    protected String endereco;

    public Pessoa(String cpf, String nome, String endereco) {
        this.cpf = cpf;
        this.nome = nome;
        this.endereco = endereco;
    }

    // Getters
    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    // Setters (CPF não pode ser alterado)
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    /**
     * Método abstrato para serializar dados específicos da subclasse
     */
    public abstract String toDataString();

    /**
     * Método abstrato para obter o tipo da pessoa
     */
    public abstract String getTipo();

    /**
     * Formato base para todas as pessoas: cpf;nome;endereco
     */
    protected String getBaseData() {
        return cpf + ";" + nome + ";" + endereco;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pessoa pessoa = (Pessoa) obj;
        return cpf.equals(pessoa.cpf);
    }

    @Override
    public int hashCode() {
        return cpf.hashCode();
    }
}
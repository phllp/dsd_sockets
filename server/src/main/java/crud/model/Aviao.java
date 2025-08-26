package crud.model;

public class Aviao {
    private String modelo;
    private int anoFabricacao;
    private String marca;
    private java.util.ArrayList<Pessoa> tripulantes;

    public Aviao(String modelo, int anoFabricacao, String marca) {
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.marca = marca;
        this.tripulantes = new java.util.ArrayList<>();
    }

    // Getters
    public String getModelo() {
        return modelo;
    }

    public int getAnoFabricacao() {
        return anoFabricacao;
    }

    public String getMarca() {
        return marca;
    }

    public java.util.ArrayList<Pessoa> getTripulantes() {
        return new java.util.ArrayList<>(tripulantes); // Retorna cópia para proteger encapsulamento
    }

    // Setters
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setAnoFabricacao(int anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    // Métodos para gerenciar tripulantes
    public void adicionarTripulante(Pessoa tripulante) {
        if (!tripulantes.contains(tripulante)) {
            tripulantes.add(tripulante);
        }
    }

    public boolean removerTripulante(String cpf) {
        return tripulantes.removeIf(tripulante -> tripulante.getCpf().equals(cpf));
    }

    public Pessoa getTripulantePorCpf(String cpf) {
        return tripulantes.stream()
                .filter(t -> t.getCpf().equals(cpf))
                .findFirst()
                .orElse(null);
    }

    /**
     * Serialização dos dados básicos do avião
     */
    public String toDataString() {
        return modelo + ";" + anoFabricacao + ";" + marca;
    }

    /**
     * Serialização completa incluindo tripulantes
     */
    public String toFullDataString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toDataString()).append(";").append(tripulantes.size());

        for (Pessoa tripulante : tripulantes) {
            sb.append(";").append(tripulante.getTipo()).append(";").append(tripulante.toDataString());
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Aviao aviao = (Aviao) obj;
        return modelo.equals(aviao.modelo) &&
                anoFabricacao == aviao.anoFabricacao &&
                marca.equals(aviao.marca);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(modelo, anoFabricacao, marca);
    }

    @Override
    public String toString() {
        return "Aviao{" +
                "modelo='" + modelo + '\'' +
                ", anoFabricacao=" + anoFabricacao +
                ", marca='" + marca + '\'' +
                ", tripulantes=" + tripulantes.size() +
                '}';
    }
}

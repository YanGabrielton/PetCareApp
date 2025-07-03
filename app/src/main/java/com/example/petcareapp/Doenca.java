package com.example.petcareapp;

public class Doenca {
    private int id;
    private String especie;
    private String nomeAnimal;
    private String racaAnimal;
    private String nomeDoenca;
    private String detalhesDoenca;

    public Doenca() {}

    public Doenca(int id, String especie, String nomeAnimal, String racaAnimal,
                  String nomeDoenca, String detalhesDoenca) {
        this.id = id;
        this.especie = especie;
        this.nomeAnimal = nomeAnimal;
        this.racaAnimal = racaAnimal;
        this.nomeDoenca = nomeDoenca;
        this.detalhesDoenca = detalhesDoenca;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getNomeAnimal() { return nomeAnimal; }
    public void setNomeAnimal(String nomeAnimal) { this.nomeAnimal = nomeAnimal; }
    public String getRacaAnimal() { return racaAnimal; }
    public void setRacaAnimal(String racaAnimal) { this.racaAnimal = racaAnimal; }
    public String getNomeDoenca() { return nomeDoenca; }
    public void setNomeDoenca(String nomeDoenca) { this.nomeDoenca = nomeDoenca; }
    public String getDetalhesDoenca() { return detalhesDoenca; }
    public void setDetalhesDoenca(String detalhesDoenca) { this.detalhesDoenca = detalhesDoenca; }
}
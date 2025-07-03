package com.example.petcareapp;

import static com.example.petcareapp.ConexaoMysql.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoencaDAO {

    public long inserirDoenca(Doenca doenca) throws SQLException {
        try (Connection con = conectarSync()) {
            try (var stmt = con.prepareStatement(
                    "INSERT INTO doencas (especie, nome_animal, raca_animal, nome_doenca, detalhes_doenca) " +
                            "VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, doenca.getEspecie());
                stmt.setString(2, doenca.getNomeAnimal());
                stmt.setString(3, doenca.getRacaAnimal());
                stmt.setString(4, doenca.getNomeDoenca());
                stmt.setString(5, doenca.getDetalhesDoenca());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Falha ao inserir, nenhuma linha afetada.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Falha ao inserir, nenhum ID obtido.");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
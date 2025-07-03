package com.example.petcareapp;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConexaoMysql {
    private static final String TAG = "ConexaoMysql";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://10.0.2.2:3306/projeto_pet_care"; /**/
    private static final String USUARIO = "root";
    private static final String SENHA = "";
    private static final int TIMEOUT_CONEXAO = 5;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface ConexaoCallback {
        void onSuccess(Connection conexao);
        void onFailure(String error);
    }

    public static void conectar(ConexaoCallback callback) {
        executor.execute(() -> {
            try {
                Connection conexao = conectarSync();
                callback.onSuccess(conexao);
                Log.d(TAG, "Conexão estabelecida com sucesso");
            } catch (Exception e) {
                String error = "Erro ao conectar: " + e.getMessage();
                Log.e(TAG, error);
                callback.onFailure(error);
            }
        });
    }

    public static Connection conectarSync() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        DriverManager.setLoginTimeout(TIMEOUT_CONEXAO);
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void fecharConexao(Connection conexao) {
        executor.execute(() -> {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                    Log.d(TAG, "Conexão fechada com sucesso");
                }
            } catch (SQLException e) {
                Log.e(TAG, "Erro ao fechar conexão: " + e.getMessage());
            }
        });
    }
}
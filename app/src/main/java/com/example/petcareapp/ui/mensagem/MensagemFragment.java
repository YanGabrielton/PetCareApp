package com.example.petcareapp.ui.mensagem;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petcareapp.ConexaoMysql;
import com.example.petcareapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MensagemFragment extends Fragment {

    private static final String TAG = "MensagemFragment";

    public MensagemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mensagem, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String emailUsuarioAtual = currentUser.getEmail();
            loadUserId(emailUsuarioAtual);
        } else {
            Log.d(TAG, "Usuário não autenticado.");
        }
    }

    private void loadUserId(String email) {
        ConexaoMysql.conectar(new ConexaoMysql.ConexaoCallback() {
            @Override
            public void onSuccess(Connection con) {
                try {
                    String sql = "SELECT id_login FROM login WHERE email = ?;";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, email);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String idUsuarioAtual = rs.getString("id_login");
                        Log.d(TAG, "ID do usuário: " + idUsuarioAtual);
                    } else {
                        Log.d(TAG, "Usuário não encontrado.");
                    }

                    rs.close();
                    stmt.close();
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao executar consulta: " + e.getMessage());
                } finally {
                    ConexaoMysql.fecharConexao(con);
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, error);
            }
        });
    }
}
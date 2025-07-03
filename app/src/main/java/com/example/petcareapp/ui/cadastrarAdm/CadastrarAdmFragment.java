package com.example.petcareapp.ui.cadastrarAdm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petcareapp.ConexaoMysql;
import com.example.petcareapp.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CadastrarAdmFragment extends Fragment {

    private static final String TAG = "CadastrarAdmFragment";
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cadastrar_adm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        // Inicialize aqui outros componentes e listeners
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        // Inicialize outros componentes aqui
    }

    public void cadastrarAdministrador(String nome, String email, String senha) {
        showProgress(true);

        executor.execute(() -> {
            try (Connection con = ConexaoMysql.conectarSync();
                 PreparedStatement stmt = con.prepareStatement(
                         "INSERT INTO administradores (nome, email, senha) VALUES (?, ?, ?)")) {

                stmt.setString(1, nome);
                stmt.setString(2, email);
                stmt.setString(3, senha);

                int rowsAffected = stmt.executeUpdate();

                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    if (rowsAffected > 0) {
                        showToast("Administrador cadastrado com sucesso");
                        limparFormulario();
                    } else {
                        showToast("Falha no cadastro");
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Erro ao cadastrar administrador", e);
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    showToast("Erro: " + e.getMessage());
                });
            }
        });
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void limparFormulario() {
        // Implemente a lógica para limpar os campos do formulário
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
    }
}
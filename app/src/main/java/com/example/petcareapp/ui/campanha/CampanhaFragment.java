package com.example.petcareapp.ui.campanha;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CampanhaFragment extends Fragment {

    private static final String TAG = "CampanhaFragment";
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_campanha, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadUserData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        // Inicialize outros componentes aqui conforme necessário
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showToast("Usuário não autenticado");
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            showToast("Email do usuário não disponível");
            return;
        }

        showProgress(true);

        executor.execute(() -> {
            try (Connection con = ConexaoMysql.conectarSync();
                 PreparedStatement stmt = con.prepareStatement(
                         "SELECT id_login FROM login WHERE email = ?")) {

                stmt.setString(1, userEmail);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String userId = rs.getString("id_login");
                        updateUI(userId);
                    } else {
                        showToastOnUiThread("Usuário não encontrado");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao carregar dados do usuário: " + e.getMessage(), e);
                showToastOnUiThread("Erro ao carregar dados do usuário: " + e.getMessage());
            } finally {
                hideProgressOnUiThread();
            }
        });
    }

    private void updateUI(String userId) {
        requireActivity().runOnUiThread(() -> {
            // Aqui você pode usar o userId para atualizar a UI
            // Exemplo: textView.setText("ID do usuário: " + userId);
            Log.d(TAG, "User ID: " + userId); // Apenas um exemplo de uso
        });
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void showToastOnUiThread(String message) {
        requireActivity().runOnUiThread(() -> showToast(message));
    }

    private void hideProgressOnUiThread() {
        requireActivity().runOnUiThread(() -> showProgress(false));
    }
}
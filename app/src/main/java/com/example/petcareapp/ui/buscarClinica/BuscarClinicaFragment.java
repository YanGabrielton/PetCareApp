package com.example.petcareapp.ui.buscarClinica;

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

public class BuscarClinicaFragment extends Fragment {

    private static final String TAG = "BuscarClinicaFragment";
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar_clinica, container, false);
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
        if (currentUser == null || currentUser.getEmail() == null) {
            showToast("Usuário não autenticado");
            return;
        }

        String userEmail = currentUser.getEmail();
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
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao carregar dados do usuário", e);
                requireActivity().runOnUiThread(() ->
                        showToast("Erro ao carregar dados do usuário"));
            } finally {
                requireActivity().runOnUiThread(() -> showProgress(false));
            }
        });
    }

    private void updateUI(String userId) {
        requireActivity().runOnUiThread(() -> {
            // Atualize a UI com o ID do usuário conforme necessário
            // Pode ser usado para buscar clínicas relacionadas ao usuário
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
}
package com.example.petcareapp.ui.cartaoVacina;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class CartaoVacinaFragment extends Fragment {

    private static final String TAG = "CartaoVacinaFragment";
    private TextView textViewUserInfo;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cartao_vacina, container, false);
        textViewUserInfo = view.findViewById(R.id.textViewUserInfo); // Certifique-se que o layout tem um TextView com id textViewUserInfo
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getEmail() == null) {
            showToast(R.string.user_not_authenticated);
            return;
        }

        String userEmail = currentUser.getEmail();

        executor.execute(() -> {
            try (Connection con = ConexaoMysql.conectarSync();
                 PreparedStatement stmt = con.prepareStatement(
                         "SELECT id_login FROM login WHERE email = ?")) {

                stmt.setString(1, userEmail);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String userId = rs.getString("id_login");
                        updateUserInfo(userId);
                    } else {
                        updateUserInfo(getString(R.string.user_not_found));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao carregar dados do usuário", e);
                updateUserInfo(getString(R.string.error_loading_data));
            }
        });
    }

    private void updateUserInfo(String userInfo) {
        requireActivity().runOnUiThread(() -> {
            textViewUserInfo.setText(getString(R.string.user_id_display, userInfo));
            // Adicione aqui outras atualizações de UI conforme necessário
        });
    }

    private void showToast(int messageResId) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), messageResId, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
    }
}
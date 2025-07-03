package com.example.petcareapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginSenha;
    private Button btLoginEntrar;
    private TextView telaLoginCadastrar;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        loginEmail = findViewById(R.id.loginEmail);
        loginSenha = findViewById(R.id.loginSenha);
        btLoginEntrar = findViewById(R.id.btLoginEntrar);
        telaLoginCadastrar = findViewById(R.id.telaLoginCadastrar);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void setupListeners() {
        btLoginEntrar.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String senha = loginSenha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                showSnackbar(v, 0);
            } else {
                authenticateUser(email, senha);
            }
        });

        telaLoginCadastrar.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastrarLoginActivity.class));
            finish();
        });
    }

    private void authenticateUser(String email, String senha) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            verifyUserTypeAndRedirect(user);
                        }
                    } else {
                        handleLoginError(task.getException(), findViewById(android.R.id.content));
                    }
                });
    }

    private void verifyUserTypeAndRedirect(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE);
        ConexaoMysql.conectar(new ConexaoMysql.ConexaoCallback() {
            @Override
            public void onSuccess(Connection con) {
                try {
                    PreparedStatement stmt = con.prepareStatement(
                            "SELECT tipo_user FROM login WHERE email = ?");
                    stmt.setString(1, user.getEmail());
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String userType = rs.getString("tipo_user");
                        redirectBasedOnUserType(userType);
                    }
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        showSnackbar(findViewById(android.R.id.content), 0);
                    });
                } finally {
                    ConexaoMysql.fecharConexao(con);
                }
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    showSnackbar(findViewById(android.R.id.content), 0);
                });
            }
        });
    }

    private void redirectBasedOnUserType(String userType) {
        Class<?> destination;
        switch (userType) {
            case "Tutor":
                destination = MainActivity.class;
                break;
            case "Clinica":
                destination = MainClinicaActivity.class;
                break;
            case "ADM":
                destination = MainAdmActivity.class;
                break;
            default:
                destination = MainActivity.class; // Direciona para a tela padrão
                break;
        }
        startActivity(new Intent(this, destination));
        finish();
    }

    private void handleLoginError(Exception exception, View view) {
        try {
            throw exception;
        } catch (FirebaseAuthInvalidCredentialsException e) {
            showSnackbar(view, 1);
        } catch (Exception e) {
            showSnackbar(view, 0);
        }
    }

    private void showSnackbar(View view, int messageIndex) {
        String[] messages = {
                "Ocorreu um erro ao tentar realizar o login. Tente novamente",
                "E-mail ou senha incorretos"
        };

        Snackbar.make(view, messages[messageIndex], Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.BLACK)
                .setTextColor(Color.WHITE)
                .show();
    }
}
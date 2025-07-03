package com.example.petcareapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

public class CadastrarLoginActivity extends AppCompatActivity {

    private EditText cadastrarEmail, cadastrarSenha, cadastrarConfSenha;
    private Spinner cadastrarTipoUsuario;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        setupSpinner();
    }

    private void initComponents() {
        cadastrarEmail = findViewById(R.id.cadastrarEmail);
        cadastrarSenha = findViewById(R.id.cadastrarSenha);
        cadastrarConfSenha = findViewById(R.id.cadastrarConfSenha);
        cadastrarTipoUsuario = findViewById(R.id.cadastrarTipoUsuario);

        findViewById(R.id.btLoginCadastrar).setOnClickListener(v -> registerUser());
        findViewById(R.id.telaLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void setupSpinner() {
        List<String> userTypes = Arrays.asList("Tutor", "Clínica");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cadastrarTipoUsuario.setAdapter(adapter);
    }

    private void registerUser() {
        String email = cadastrarEmail.getText().toString().trim();
        String senha = cadastrarSenha.getText().toString().trim();
        String confSenha = cadastrarConfSenha.getText().toString().trim();
        String userType = cadastrarTipoUsuario.getSelectedItem().toString();

        if (email.isEmpty() || senha.isEmpty() || confSenha.isEmpty()) {
            showSnackbar("Preencha todos os campos");
            return;
        }

        if (!senha.equals(confSenha)) {
            showSnackbar("Senha e confirmação de senha não coincidem");
            return;
        }

        if (senha.length() < 6) {
            showSnackbar("A senha deve ter pelo menos 6 caracteres");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        registerUserInDatabase(email, senha, userType);
                    } else {
                        handleRegistrationError(task.getException());
                    }
                });
    }

    private void registerUserInDatabase(String email, String senha, String userType) {
        ConexaoMysql.conectar(new ConexaoMysql.ConexaoCallback() {
            @Override
            public void onSuccess(Connection con) {
                try {
                    PreparedStatement stmt = con.prepareStatement(
                            "INSERT INTO login(email, senha, tipo_user) VALUES(?, UPPER(MD5(?)), ?)");
                    stmt.setString(1, email);
                    stmt.setString(2, senha);
                    stmt.setString(3, userType);
                    stmt.executeUpdate();
                    stmt.close();

                    runOnUiThread(() -> {
                        Toast.makeText(CadastrarLoginActivity.this,
                                "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CadastrarLoginActivity.this, LoginActivity.class));
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() ->
                            showSnackbar("Erro ao cadastrar no banco de dados"));
                } finally {
                    ConexaoMysql.fecharConexao(con);
                }
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        showSnackbar("Erro ao conectar com o banco de dadosL"));
            }
        });
    }

    private void handleRegistrationError(Exception exception) {
        if (exception == null) {
            showSnackbar("Erro desconhecido ao registrar");
            return;
        }

        try {
            throw exception;
        } catch (FirebaseAuthWeakPasswordException e) {
            showSnackbar("A senha deve ter pelo menos 6 caracteres");
        } catch (FirebaseAuthUserCollisionException e) {
            showSnackbar("E-mail já cadastrado, tente outro");
        } catch (FirebaseAuthInvalidCredentialsException e) {
            showSnackbar("Por favor, insira um e-mail válido");
        } catch (Exception e) {
            showSnackbar("Ocorreu um erro ao tentar realizar o cadastro");
        }
    }

    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.BLACK);
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
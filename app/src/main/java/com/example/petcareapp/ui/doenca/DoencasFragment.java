package com.example.petcareapp.ui.doenca;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareapp.ConexaoMysql;
import com.example.petcareapp.Doenca;
import com.example.petcareapp.R;
import com.google.android.material.button.MaterialButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DoencasFragment extends Fragment {

    private RecyclerView recyclerView;
    private DoencaAdapter adapter;
    private List<Doenca> listaDoencas = new ArrayList<>();
    private AutoCompleteTextView editEspecie, editDoenca;
    private EditText editPesquisar, editNome, editRaca, editDetalhes;
    private ProgressBar progressBar;
    private MaterialButton btnPesquisar, btnAdicionar;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private List<String> especies = new ArrayList<>();
    private List<String> doencas = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doenca, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupButtons();
        setupDropdowns();
        carregarDadosIniciais();
    }

    private void initViews(View view) {
        editPesquisar = view.findViewById(R.id.editPesquisar);
        editEspecie = view.findViewById(R.id.editEspecie);
        editNome = view.findViewById(R.id.editNome);
        editRaca = view.findViewById(R.id.editRaca);
        editDoenca = view.findViewById(R.id.editDoenca);
        editDetalhes = view.findViewById(R.id.doencasFragment_detalhes);
        recyclerView = view.findViewById(R.id.recyclerViewDoencas);
        progressBar = view.findViewById(R.id.progressBar);
        btnPesquisar = view.findViewById(R.id.btnPesquisar);
        btnAdicionar = view.findViewById(R.id.btnAdicionarTabela);



        editDetalhes.setFocusableInTouchMode(true);


        editDetalhes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Mantém o cursor no final do texto
                editDetalhes.setSelection(editDetalhes.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new DoencaAdapter(listaDoencas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupButtons() {
        btnPesquisar.setOnClickListener(v -> pesquisarDoencas());

        btnAdicionar.setOnClickListener(v -> {
            if (validarCamposCadastro()) {
                adicionarDoenca();
            }
        });
    }

    private void setupDropdowns() {
        editEspecie.setOnItemClickListener((parent, view, position, id) -> {
            String especieSelecionada = especies.get(position);
            carregarDoencasPorEspecie(especieSelecionada);

            // Limpa campos dependentes
            editNome.setText("");
            editRaca.setText("");
            editDoenca.setText("");
            editDetalhes.setText("");
        });

        editDoenca.setOnItemClickListener((parent, view, position, id) -> {
            String doencaSelecionada = doencas.get(position);
            // Não preenche automaticamente outros campos para evitar conflitos
        });
    }

    private boolean validarCamposCadastro() {
        if (TextUtils.isEmpty(editEspecie.getText())) {
            editEspecie.setError("Informe a espécie");
            return false;
        }
        if (TextUtils.isEmpty(editNome.getText())) {
            editNome.setError("Informe o nome");
            return false;
        }
        if (TextUtils.isEmpty(editRaca.getText())) {
            editRaca.setError("Informe a raça");
            return false;
        }
        if (TextUtils.isEmpty(editDoenca.getText())) {
            editDoenca.setError("Informe a doença");
            return false;
        }
        if (TextUtils.isEmpty(editDetalhes.getText())) {
            editDetalhes.setError("Informe os detalhes");
            return false;
        }
        return true;
    }

    private void carregarDadosIniciais() {
        carregarEspecies();
    }

    private void carregarEspecies() {
        showProgress(true);
        executor.execute(() -> {
            try (Connection con = ConexaoMysql.conectarSync()) {
                especies = new ArrayList<>();
                String sql = "SELECT DISTINCT especie FROM doencas";
                try (PreparedStatement stmt = con.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        especies.add(rs.getString("especie"));
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            especies
                    );
                    editEspecie.setAdapter(adapter);
                    showProgress(false);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(getContext(), "Erro ao carregar espécies: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void carregarDoencasPorEspecie(String especie) {
        showProgress(true);
        executor.execute(() -> {
            try (Connection con = ConexaoMysql.conectarSync()) {
                doencas = new ArrayList<>();
                String sql = "SELECT DISTINCT nome_doenca FROM doencas WHERE especie = ?";
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    stmt.setString(1, especie);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            doencas.add(rs.getString("nome_doenca"));
                        }
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            doencas
                    );
                    editDoenca.setAdapter(adapter);
                    showProgress(false);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(getContext(), "Erro ao carregar doenças: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void pesquisarDoencas() {
        showProgress(true);
        executor.execute(() -> {
            try (Connection con = ConexaoMysql.conectarSync()) {
                String termo = editPesquisar.getText().toString().trim();
                String especie = editEspecie.getText().toString().trim();
                String nomeAnimal = editNome.getText().toString().trim();
                String raca = editRaca.getText().toString().trim();
                String doenca = editDoenca.getText().toString().trim();

                StringBuilder sql = new StringBuilder("SELECT * FROM doencas WHERE 1=1");
                List<String> params = new ArrayList<>();

                if (!TextUtils.isEmpty(termo)) {
                    sql.append(" AND (especie LIKE ? OR nome_animal LIKE ? OR raca_animal LIKE ? OR nome_doenca LIKE ? OR detalhes_doenca LIKE ?)");
                    for (int i = 0; i < 5; i++) {
                        params.add("%" + termo + "%");
                    }
                } else {
                    if (!TextUtils.isEmpty(especie)) {
                        sql.append(" AND especie = ?");
                        params.add(especie);
                    }
                    if (!TextUtils.isEmpty(nomeAnimal)) {
                        sql.append(" AND nome_animal LIKE ?");
                        params.add("%" + nomeAnimal + "%");
                    }
                    if (!TextUtils.isEmpty(raca)) {
                        sql.append(" AND raca_animal LIKE ?");
                        params.add("%" + raca + "%");
                    }
                    if (!TextUtils.isEmpty(doenca)) {
                        sql.append(" AND nome_doenca LIKE ?");
                        params.add("%" + doenca + "%");
                    }
                }

                try (PreparedStatement stmt = con.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        stmt.setString(i + 1, params.get(i));
                    }

                    try (ResultSet rs = stmt.executeQuery()) {
                        List<Doenca> resultados = new ArrayList<>();
                        while (rs.next()) {
                            resultados.add(new Doenca(
                                    rs.getInt("id_doenca"),
                                    rs.getString("especie"),
                                    rs.getString("nome_animal"),
                                    rs.getString("raca_animal"),
                                    rs.getString("nome_doenca"),
                                    rs.getString("detalhes_doenca")
                            ));
                        }

                        requireActivity().runOnUiThread(() -> {
                            showProgress(false);
                            listaDoencas.clear();
                            listaDoencas.addAll(resultados);
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(0);

                            if (resultados.isEmpty()) {
                                Toast.makeText(getContext(), "Nenhum resultado encontrado", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(getContext(), "Erro na pesquisa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void adicionarDoenca() {
        showProgress(true);

        Doenca novaDoenca = new Doenca();
        novaDoenca.setEspecie(editEspecie.getText().toString().trim());
        novaDoenca.setNomeAnimal(editNome.getText().toString().trim());
        novaDoenca.setRacaAnimal(editRaca.getText().toString().trim());
        novaDoenca.setNomeDoenca(editDoenca.getText().toString().trim());
        novaDoenca.setDetalhesDoenca(editDetalhes.getText().toString().trim());

        executor.execute(() -> {
            try (Connection con = ConexaoMysql.conectarSync()) {
                String sql = "INSERT INTO doencas (especie, nome_animal, raca_animal, nome_doenca, detalhes_doenca) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, novaDoenca.getEspecie());
                    stmt.setString(2, novaDoenca.getNomeAnimal());
                    stmt.setString(3, novaDoenca.getRacaAnimal());
                    stmt.setString(4, novaDoenca.getNomeDoenca());
                    stmt.setString(5, novaDoenca.getDetalhesDoenca());
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            long id = rs.getLong(1);
                            requireActivity().runOnUiThread(() -> {
                                showProgress(false);
                                Toast.makeText(getContext(), "Adicionado com sucesso! ID: " + id, Toast.LENGTH_SHORT).show();
                                limparCampos();
                                pesquisarDoencas();
                            });
                        }
                    }
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(getContext(), "Erro ao adicionar doença: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void limparCampos() {
        editEspecie.setText("");
        editNome.setText("");
        editRaca.setText("");
        editDoenca.setText("");
        editDetalhes.setText("");
        editPesquisar.setText("");
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnPesquisar.setEnabled(!show);
        btnAdicionar.setEnabled(!show);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
        editDetalhes = null;
    }
}
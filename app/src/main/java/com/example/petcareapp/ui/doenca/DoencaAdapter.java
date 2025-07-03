package com.example.petcareapp.ui.doenca;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareapp.Doenca;
import com.example.petcareapp.R;

import java.util.List;

public class DoencaAdapter extends RecyclerView.Adapter<DoencaAdapter.DoencaViewHolder> {

    private final List<Doenca> doencas;

    public DoencaAdapter(List<Doenca> doencas) {
        this.doencas = doencas;
    }

    @NonNull
    @Override
    public DoencaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tabela_doenca, parent, false);
        return new DoencaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoencaViewHolder holder, int position) {
        Doenca doenca = doencas.get(position);
        holder.bind(doenca);
    }

    @Override
    public int getItemCount() {
        return doencas.size();
    }

    public void atualizarDados(List<Doenca> novasDoencas) {
        doencas.clear();
        doencas.addAll(novasDoencas);
        notifyDataSetChanged();
    }

    static class DoencaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId, tvEspecie, tvNomeAnimal, tvRaca, tvDoenca, tvDetalhes;

        public DoencaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvEspecie = itemView.findViewById(R.id.tvEspecie);
            tvNomeAnimal = itemView.findViewById(R.id.tvNomeAnimal);
            tvRaca = itemView.findViewById(R.id.tvRaca);
            tvDoenca = itemView.findViewById(R.id.tvDoenca);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhes);
        }

        public void bind(Doenca doenca) {
            tvId.setText(String.valueOf(doenca.getId()));
            tvEspecie.setText(doenca.getEspecie());
            tvNomeAnimal.setText(doenca.getNomeAnimal());
            tvRaca.setText(doenca.getRacaAnimal());
            tvDoenca.setText(doenca.getNomeDoenca());
            tvDetalhes.setText(doenca.getDetalhesDoenca());
        }
    }
}
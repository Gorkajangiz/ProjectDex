package com.example.projectdexv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class PokemonFavoritoAdapter extends RecyclerView.Adapter<PokemonFavoritoAdapter.PokemonViewHolder> {

    private List<Pokemon> pokemonList;
    private Context context;
    private OnPokemonClickListener listener;

    public interface OnPokemonClickListener {
        void onPokemonClick(Pokemon pokemon);
    }

    public PokemonFavoritoAdapter(List<Pokemon> pokemonList, Context context) {
        this.pokemonList = pokemonList;
        this.context = context;
    }

    public void setOnPokemonClickListener(OnPokemonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favoritotarjeta, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = pokemonList.get(position);

        holder.nombreTextView.setText(pokemon.getNombre());
        holder.generacionTextView.setText("Gen " + pokemon.getGeneracion());

        String tipos = pokemon.getTipoUno();
        if (pokemon.getTipoDos() != null) {
            tipos += " / " + pokemon.getTipoDos();
        }
        holder.tiposTextView.setText(tipos);

        if (pokemon.getDescripcion() != null && !pokemon.getDescripcion().isEmpty()) {
            holder.descripcionTextView.setText(pokemon.getDescripcion());
        } else {
            holder.descripcionTextView.setText("Descripción no disponible.");
        }

        cargarSpritePokemon(holder.imageView, pokemon.getNumeroPokedex());

        // Configurar el click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPokemonClick(pokemon);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    private void cargarSpritePokemon(ImageView imageView, int numeroPokedex) {
        String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + numeroPokedex + ".png";

        Picasso.get()
                .load(spriteUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(imageView);
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nombreTextView;
        TextView tiposTextView;
        TextView generacionTextView;
        TextView descripcionTextView;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fotoPokemon);
            nombreTextView = itemView.findViewById(R.id.nombrePokemon);
            tiposTextView = itemView.findViewById(R.id.tiposPokemon);
            generacionTextView = itemView.findViewById(R.id.generacionPokemon);
            descripcionTextView = itemView.findViewById(R.id.descripcionPokemon);
        }
    }
}
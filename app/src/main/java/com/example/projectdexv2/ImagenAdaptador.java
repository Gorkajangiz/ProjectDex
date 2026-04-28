package com.example.projectdexv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class ImagenAdaptador extends RecyclerView.Adapter<ImagenAdaptador.ViewHolder> {

    private ArrayList<PokemonBasico> listaPokemon;
    private Context context;
    private OnPokemonClickListener listener;

    public interface OnPokemonClickListener {
        void onPokemonClick(PokemonBasico pokemon);
    }

    public ImagenAdaptador(ArrayList<PokemonBasico> listaPokemon, Context context) {
        this.listaPokemon = listaPokemon;
        this.context = context;
        if (context instanceof OnPokemonClickListener) {
            this.listener = (OnPokemonClickListener) context;
        }
    }

    public void setOnPokemonClickListener(OnPokemonClickListener listener) {
        this.listener = listener;
    }

    public void actualizarLista(ArrayList<PokemonBasico> nuevaLista) {
        this.listaPokemon = nuevaLista;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PokemonBasico pokemon = listaPokemon.get(position);
        holder.tvNombre.setText(pokemon.getNombre());
        holder.tvNumero.setText("#" + String.format("%03d", pokemon.getNumero()));
        String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumero() + ".png";
        Glide.with(context).load(imageUrl).placeholder(R.drawable.pokeballllena).into(holder.ivPokemon);
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
        return listaPokemon.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPokemon;
        TextView tvNombre;
        TextView tvNumero;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPokemon = itemView.findViewById(R.id.ivPokemon);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvNumero = itemView.findViewById(R.id.tvNumero);
        }
    }
}
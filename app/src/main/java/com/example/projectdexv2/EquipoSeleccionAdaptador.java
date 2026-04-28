package com.example.projectdexv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EquipoSeleccionAdaptador extends RecyclerView.Adapter<EquipoSeleccionAdaptador.ViewHolder> {

    private List<Equipo> equiposList;
    private Context context;
    private OnEquipoClickListener listener;
    private int pokemonIdSeleccionado = -1;

    public interface OnEquipoClickListener {
        void onEquipoClick(Equipo equipo);
    }

    public EquipoSeleccionAdaptador(List<Equipo> equiposList, Context context) {
        this.equiposList = equiposList;
        this.context = context;
    }

    public void setOnEquipoClickListener(OnEquipoClickListener listener) {
        this.listener = listener;
    }

    public void setPokemonSeleccionado(int pokemonId) {
        this.pokemonIdSeleccionado = pokemonId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Equipo equipo = equiposList.get(position);

        String texto = equipo.getNombre() + " (" + equipo.getCantidadPokemon() + "/6)";

        if (pokemonIdSeleccionado != -1) {
            boolean enEquipo = equipo.tienePokemon(pokemonIdSeleccionado);
            boolean equipoLleno = equipo.getCantidadPokemon() >= 6;

            //Esto es la primera vez que lo hago, un elsif con doble boolean
            if (enEquipo) {
                holder.tvEquipo.setText("✓ " + texto + " (Ya en equipo)");
                holder.tvEquipo.setTextColor(context.getResources().getColor(R.color.tipo_planta));
                holder.itemView.setEnabled(false);
                holder.itemView.setAlpha(0.7f);
                holder.itemView.setOnClickListener(null);
            } else if (equipoLleno) {
                holder.tvEquipo.setText("✗ " + texto + " (Equipo lleno)");
                holder.tvEquipo.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                holder.itemView.setEnabled(false);
                holder.itemView.setAlpha(0.5f);
                holder.itemView.setOnClickListener(null);
            } else {
                holder.tvEquipo.setText("→ " + texto);
                holder.tvEquipo.setTextColor(context.getResources().getColor(android.R.color.black));
                holder.itemView.setEnabled(true);
                holder.itemView.setAlpha(1.0f);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onEquipoClick(equipo);
                        }
                    }
                });
            }
        } else {
            boolean equipoLleno = equipo.getCantidadPokemon() >= 6;

            if (equipoLleno) {
                holder.tvEquipo.setText("✗ " + texto + " (Equipo lleno)");
                //Esto esta deprecated pero funciona, no he mirado cual es la versión actualizada
                holder.tvEquipo.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                holder.itemView.setEnabled(false);
                holder.itemView.setAlpha(0.5f);
                holder.itemView.setOnClickListener(null);
            } else {
                holder.tvEquipo.setText(texto);
                holder.tvEquipo.setTextColor(context.getResources().getColor(android.R.color.black));
                holder.itemView.setEnabled(true);
                holder.itemView.setAlpha(1.0f);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onEquipoClick(equipo);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return equiposList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEquipo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEquipo = itemView.findViewById(android.R.id.text1);
        }
    }
}
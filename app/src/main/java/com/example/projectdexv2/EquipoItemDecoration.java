package com.example.projectdexv2;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class EquipoItemDecoration extends RecyclerView.ItemDecoration {
    //Este metodo lo hizo la IA intentando solucionar un error que se me atascó un montón
    //con un tema de espaciar y demás, no se si lo arreglé posteriormente porque no tiene usos,
    //por si acaso
    private final int spacing;
    public EquipoItemDecoration(int spacing) {
        this.spacing = spacing;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();
        if (position == 0) {
            outRect.left = spacing;
        }
        if (position == itemCount - 1) {
            outRect.right = spacing;
        }
        outRect.left = spacing / 2;
        outRect.right = spacing / 2;
    }
}
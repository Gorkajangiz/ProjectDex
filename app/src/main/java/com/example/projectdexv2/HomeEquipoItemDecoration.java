package com.example.projectdexv2;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class HomeEquipoItemDecoration extends RecyclerView.ItemDecoration {
    private int screenWidth;

    public HomeEquipoItemDecoration(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    //Otro metodo como el equipoItemDecoration para ajustar tamaños
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int desiredWidth = (int) (screenWidth * 0.85);
        int margin = (screenWidth - desiredWidth) / 2;
        outRect.left = margin;
        outRect.right = margin;
        outRect.top = 8;
        outRect.bottom = 8;

        view.getLayoutParams().width = desiredWidth;
    }
}
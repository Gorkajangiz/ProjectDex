package com.example.projectdexv2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private long backPressedTime;
    MiniHttpServer server = new MiniHttpServer(9999);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        try {
            server.start();
        } catch (IOException e) {
        }

        bottomNavigation = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_fragmentos, new fragmentHome()).commit();
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                resetIcons();
                if (itemId == R.id.menu_home) {
                    item.setIcon(R.drawable.homellena);
                    getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_fragmentos, new fragmentHome()).commit();
                    return true;
                }
                if (itemId == R.id.menu_favoritos) {
                    item.setIcon(R.drawable.favllena);
                    getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_fragmentos, new fragmentFavoritos()).commit();
                    return true;
                }
                if (itemId == R.id.menu_todos) {
                    item.setIcon(R.drawable.browsellena);
                    getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_fragmentos, new fragmentTodos()).commit();
                    return true;
                }
                if (itemId == R.id.menu_equipos) {
                    item.setIcon(R.drawable.pokeballllena);
                    getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_fragmentos, new fragmentEquipos()).commit();
                    return true;
                }
                return false;
            }
        });

        bottomNavigation.setSelectedItemId(R.id.menu_home);
    }

    private void resetIcons() {
        MenuItem homeItem = bottomNavigation.getMenu().findItem(R.id.menu_home);
        homeItem.setIcon(R.drawable.home);
        MenuItem favItem = bottomNavigation.getMenu().findItem(R.id.menu_favoritos);
        favItem.setIcon(R.drawable.fav);
        MenuItem todosItem = bottomNavigation.getMenu().findItem(R.id.menu_todos);
        todosItem.setIcon(R.drawable.browse);
        MenuItem equiposItem = bottomNavigation.getMenu().findItem(R.id.menu_equipos);
        equiposItem.setIcon(R.drawable.pokeball);
    }

    //Esto esta deprecated, no se por qué lo puse pero me da miedo quitarlo
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
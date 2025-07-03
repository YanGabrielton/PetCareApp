package com.example.petcareapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petcareapp.databinding.ActivityMainAdmBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainAdmActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.petcareapp.databinding.ActivityMainAdmBinding binding = ActivityMainAdmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainAdm.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Configuração da AppBar
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_perfil_adm, R.id.nav_cadastrar_adm, R.id.nav_doenca, R.id.nav_campanha, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_adm);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla o menu; isso adiciona itens à barra de ação se estiver presente.
        getMenuInflater().inflate(R.menu.main_adm, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Ação do Menu
        if (item.getItemId() == R.id.action_sair) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainAdmActivity.this, LoginActivity.class); // Correção aqui
            startActivity(intent);
            finish();
        }

        /*
        if (item.getItemId() == R.id.action_alterar_senha) {
            Intent intent = new Intent(MainActivity.this, AlterarSenhaActivity.class);
            startActivity(intent);
            finish();
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_adm);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
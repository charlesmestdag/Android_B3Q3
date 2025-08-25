package com.mestdag.gestionpointsetudiants;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private AppDatabase database;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            database = AppDatabase.getInstance(this);
            Log.d(TAG, "Base de données initialisée avec succès");
            

            
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation de la base de données", e);
            // Afficher un message d'erreur à l'utilisateur
            showErrorDialog("Erreur de base de données", "Impossible d'initialiser la base de données. L'application peut ne pas fonctionner correctement.");
        }

        try {
            // Utiliser NavHostFragment.findNavController au lieu de Navigation.findNavController
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_class_list).build();
                NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
                Log.d(TAG, "Navigation initialisée avec succès");
            } else {
                Log.e(TAG, "NavHostFragment non trouvé");
                showErrorDialog("Erreur de navigation", "Impossible d'initialiser la navigation. L'application peut ne pas fonctionner correctement.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation de la navigation", e);
            showErrorDialog("Erreur de navigation", "Erreur lors de l'initialisation de la navigation: " + e.getMessage());
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        try {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                return navController.navigateUp() || super.onSupportNavigateUp();
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la navigation", e);
        }
        return super.onSupportNavigateUp();
    }

    private void showErrorDialog(String title, String message) {
        try {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'affichage du dialogue d'erreur", e);
        }
    }
}
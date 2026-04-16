package com.example.spacecolony.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.spacecolony.R;
import com.example.spacecolony.data.DataManager;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.fragment.MedbayFragment;
import com.example.spacecolony.fragment.MissionControlFragment;
import com.example.spacecolony.fragment.QuartersFragment;
import com.example.spacecolony.fragment.SimulatorFragment;
import com.example.spacecolony.fragment.StatisticsFragment;

/**
 * Main activity — hosts all fragments via BottomNavigationView.
 * Loads saved game on startup and saves on exit.
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ── Load saved game ───────────────────────────────────────────────
        if (DataManager.hasSaveFile(this)) {
            boolean loaded = DataManager.loadGame(this);
            if (loaded) {
                Toast.makeText(this, "Colony loaded!", Toast.LENGTH_SHORT).show();
            }
        }

        // ── Colony name in toolbar ────────────────────────────────────────
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Storage.getInstance().getColonyName());
        }

        // ── Bottom navigation ─────────────────────────────────────────────
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(this::onNavItemSelected);

        // ── FAB: Recruit ──────────────────────────────────────────────────
        findViewById(R.id.fab_recruit).setOnClickListener(v -> {
            startActivity(new Intent(this, RecruitActivity.class));
        });

        // ── Start on Home (Quarters) ──────────────────────────────────────
        if (savedInstanceState == null) {
            loadFragment(new QuartersFragment());
        }
    }

    private boolean onNavItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_quarters) {
            fragment = new QuartersFragment();
        } else if (id == R.id.nav_simulator) {
            fragment = new SimulatorFragment();
        } else if (id == R.id.nav_mission) {
            fragment = new MissionControlFragment();
        } else if (id == R.id.nav_medbay) {
            fragment = new MedbayFragment();
        } else if (id == R.id.nav_stats) {
            fragment = new StatisticsFragment();
        }

        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh toolbar title in case colony name changed
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Storage.getInstance().getColonyName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Auto-save on every pause
        DataManager.saveGame(this);
    }

    @Override
    public void onBackPressed() {
        // Save before exit
        DataManager.saveGame(this);
        super.onBackPressed();
    }
}
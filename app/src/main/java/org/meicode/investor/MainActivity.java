package org.meicode.investor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.meicode.investor.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Initialize View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. Set the Toolbar
        setSupportActionBar(binding.topAppBar);

        // 3. Setup Bottom Navigation
        BottomNavigationView navView = binding.navView;

        // 4. Configure Navigation destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_stocks,
                R.id.navigation_f_o,
                R.id.navigation_mutualfunds,
                R.id.navigation_upi)
                .build();

        // 5. Initialize NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // 6. Link UI components to NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // 7. Configure Logo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 8. Setup Horizontal RecyclerView (Indices/Categories)
        setupHorizontalList();
    }

    private void setupHorizontalList() {
        // We use the ID "indicesRecyclerView" or "horizontalList" as per your XML
        RecyclerView recyclerView = binding.indicesRecyclerView;

        ArrayList<String> categories = new ArrayList<>(Arrays.asList(
                "NIFTY 50", "SENSEX", "BANK NIFTY", "ALL INDICES"
        ));

        // Set LayoutManager to Horizontal
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Set Adapter
        recyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Using your indices.xml layout
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indices, parent, false);
                return new MyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                holder.textView.setText(categories.get(position));
                // Optional: holder.valueView.setText("...");
            }

            @Override
            public int getItemCount() {
                return categories.size();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setQueryHint("Search here...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_qr) {
            // Handle QR click
            return true;
        } else if (id == R.id.action_profile) {
            // Handle Profile click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        // TextView valueView; // Uncomment if you added an ID for value in indices.xml

        public MyViewHolder(View itemView) {
            super(itemView);
            // Matches the ID in your indices.xml
            textView = itemView.findViewById(R.id.indexName);
        }
    }
}
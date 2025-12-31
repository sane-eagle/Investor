package org.meicode.investor;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat; // Fix for getColor warning
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.meicode.investor.databinding.ActivityMainBinding;
import org.meicode.investor.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Fix: Field 'livePrices' made final
    private final ArrayList<String> stockSymbols = new ArrayList<>(Arrays.asList(
            "AAPL", "TSLA", "GOOGL", "AMZN", "ALL INDICES"
    ));

    private final String[] livePrices = {"Loading...", "Loading...", "Loading...", "Loading...", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.topAppBar);

        BottomNavigationView navView = binding.navView;

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_stocks, R.id.navigation_f_o, R.id.navigation_mutualfunds, R.id.navigation_upi)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setupHorizontalList();
    }

    private void setupHorizontalList() {
        RecyclerView recyclerView = binding.indicesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Fix: Added <MyViewHolder> to the Adapter definition
        RecyclerView.Adapter<MyViewHolder> adapter = new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Fix: Explicit type argument <MyViewHolder> replaced with <>
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indices, parent, false);
                return new MyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                String name = stockSymbols.get(position);
                holder.textView.setText(name);

                if (name.equals("ALL INDICES")) {
                    holder.valueView.setText("View All →");
                    holder.textView.setTextColor(Color.parseColor("#2196F3"));
                } else {
                    holder.valueView.setText(livePrices[position]);
                }
            }

            @Override
            public int getItemCount() {
                return stockSymbols.size();
            }
        };

        recyclerView.setAdapter(adapter);

        for (int i = 0; i < 4; i++) {
            updatePriceFromServer(stockSymbols.get(i), i, adapter);
        }
    }

    // Fix: Parameterized the adapter in the method signature
    private void updatePriceFromServer(String symbol, int position, RecyclerView.Adapter<MyViewHolder> adapter) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finnhub.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FinnhubService service = retrofit.create(FinnhubService.class);

        // Ensure you have your actual API key here
        String myApiKey = BuildConfig.MAPS_API_KEY;

        service.getQuote(symbol, myApiKey).enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(@NonNull Call<StockResponse> call, @NonNull Response<StockResponse> response) {
                // Check if the server actually sent a successful code (200 OK)
                if (response.isSuccessful() && response.body() != null) {
                    double price = response.body().currentPrice;

                    // Finnhub returns 0 if the symbol is wrong or data isn't available
                    if (price == 0) {
                        livePrices[position] = "N/A";
                        Log.w("API_DEBUG", "Symbol " + symbol + " returned 0. Check if symbol is valid.");
                    } else {
                        livePrices[position] = "$" + price;
                        Log.d("API_DEBUG", "Success! " + symbol + ": $" + price);
                    }

                    // Refresh the specific item in the list
                    runOnUiThread(() -> adapter.notifyItemChanged(position));

                } else {
                    // If the server responded but with an error (like 401 or 429)
                    Log.e("API_ERROR", "Server Error Code: " + response.code());
                    Log.e("API_ERROR", "Server Message: " + response.message());

                    runOnUiThread(() -> {
                        livePrices[position] = "Error " + response.code();
                        adapter.notifyItemChanged(position);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockResponse> call, @NonNull Throwable t) {
                // This happens if there is NO internet or the URL is wrong
                Log.e("API_ERROR", "Network Failure for " + symbol + ": " + t.getMessage());

                runOnUiThread(() -> {
                    livePrices[position] = "Offline";
                    adapter.notifyItemChanged(position);
                });
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
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_qr) return true;
        if (id == R.id.action_profile) return true;
        return super.onOptionsItemSelected(item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView valueView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.indexName);
            valueView = itemView.findViewById(R.id.indexValue);
        }
    }
}
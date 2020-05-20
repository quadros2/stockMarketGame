package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TradingActivity extends AppCompatActivity {

    RequestQueue requestQueue;

    Button searchButton;

    EditText queryText;

    String localPrice;

    String localPreviousClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_hub);

        requestQueue = Volley.newRequestQueue(this);

        queryText = findViewById(R.id.searchQuery);
        queryText.setVisibility(View.VISIBLE);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setVisibility(View.VISIBLE);
        searchButton.setOnClickListener(V -> {
            getStockListing(queryText.getText().toString());
        });

    }

    private void getStockListing(String queryText) {
        String url_str = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + queryText +
                "&apikey=" + BuildConfig.ApiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_str, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            LinearLayout parent = findViewById(R.id.searchResultsContainer);
                            JSONArray results = response.getJSONArray("bestMatches");
                            for (int i = 0; i < results.length(); i++) {
                                String region = results.getJSONObject(i).get("4. region").toString();
                                String currency = results.getJSONObject(i).get("8. currency").toString();
                                if (!(currency.equals("USD")) || !(region.equals("United States"))) {
                                    continue;
                                }
                                View stockChunckInflator = getLayoutInflater().inflate(R.layout.chunk_stock_listing,
                                        parent, false);
                                TextView stockName = stockChunckInflator.findViewById(R.id.stockName);
                                stockName.setText(results.getJSONObject(i).get("2. name").toString());
                                TextView stockSymbol = stockChunckInflator.findViewById(R.id.stockSymbol);
                                stockSymbol.setText(results.getJSONObject(i).get("1. symbol").toString());
                                TextView stockPrice = stockChunckInflator.findViewById(R.id.stockValue);
                                getStockPrice(results.getJSONObject(i).get("1. symbol").toString());
                                stockPrice.setText(localPrice);
                                ImageView tickerArrow = stockChunckInflator.findViewById(R.id.tickerArrow);
                                if (Double.parseDouble(localPreviousClose) < Double.parseDouble(localPrice)) {
                                    tickerArrow.setImageResource(R.drawable.green_tick);
                                    stockPrice.setTextColor(Color.GREEN);
                                } else {
                                    tickerArrow.setImageResource(R.drawable.red_tick);
                                    stockPrice.setTextColor(Color.RED);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getStockPrice(String symbol) {
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" +
                symbol + "&apikey=" + BuildConfig.ApiKey;
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String x = response.getJSONObject("Global Quote").get("05. price").toString();
                            String y = response.getJSONObject("Global Quote").get("08. previous close").toString();
                            passPrice(x, y);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest1);
    }

    public void passPrice(String price, String previousClose) {
        localPrice = price;
        localPreviousClose = previousClose;
    }


    //onCreateOptionsMenu and onOptionsItemSelected are both methods used to create the toolbar!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater dropdownMenu = getMenuInflater();
        dropdownMenu.inflate(R.menu.menu_dropdown, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signOut) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to sign out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.create().show();
        } else if (item.getItemId() == R.id.tradingHub) {
            Intent intent = new Intent(getApplicationContext(), TradingActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.myPortfolio) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

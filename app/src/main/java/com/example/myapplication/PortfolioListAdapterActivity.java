package com.example.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PortfolioListAdapterActivity extends ArrayAdapter<Stock> {

    private Activity appContext;
    private List<Stock> stockList;
    private RequestQueue requestQueue = Volley.newRequestQueue(getContext());
    public String currentPrice;

    public PortfolioListAdapterActivity(Activity appContext, List<Stock> stockList) {
        super(appContext, R.layout.chunk_portfolio_stock, stockList);
        this.appContext = appContext;
        this.stockList = stockList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = appContext.getLayoutInflater();
        View listItems = inflater.inflate(R.layout.chunk_portfolio_stock, null, true);

        TextView stockName = listItems.findViewById(R.id.stockName);
        TextView dateAdded = listItems.findViewById(R.id.dateAdded);
        TextView priceChange = listItems.findViewById(R.id.priceChange);
        TextView percentageChange = listItems.findViewById(R.id.percentageChange);
        Button sellButton = listItems.findViewById(R.id.sellButton);

        Stock stock = stockList.get(position);

        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" +
                stock.getSymbol() + "&apikey=" + BuildConfig.ApiKey;
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            stockName.setText(stock.getName());
                            dateAdded.setText(stock.getDateAndTime());
                            currentPrice = response.getJSONObject("Global Quote").get("05. price").toString();
                            double netGains = (Double.parseDouble(currentPrice) - Double.parseDouble(stock.getPriceBought()))
                                    * Double.parseDouble(stock.getQuantity());
                            priceChange.setText("$" + netGains);
                            if (netGains < 0) {
                                priceChange.setTextColor(Color.RED);
                            } else {
                                priceChange.setTextColor(Color.GREEN);
                            }
                            String percentageC = response.getJSONObject("Global Quote").get("10. change percent").toString();
                            percentageChange.setText(percentageC);
                            if (percentageC.charAt(0) == '-') {
                                percentageChange.setTextColor(Color.RED);
                            } else {
                                percentageChange.setTextColor(Color.GREEN);
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
        requestQueue.add(jsonObjectRequest1);

        return listItems;
    }

}

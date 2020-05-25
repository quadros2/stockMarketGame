package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PortfolioListAdapterActivity extends ArrayAdapter<Stock> {

    private Activity appContext;
    private List<Stock> stockList;

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

        stockName.setText(stock.getName());
        dateAdded.setText(stock.getDateAndTime());

        return listItems;
    }
}

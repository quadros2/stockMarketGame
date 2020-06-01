package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
//A.K.A. portfolio code
public class MainActivity extends AppCompatActivity {

    List<Stock> stockList;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = firebaseFirestore.collection(
            FirebaseAuth.getInstance().getCurrentUser().getUid() + "'s stocks");

    private DocumentReference documentReference = firebaseFirestore.collection(
            FirebaseAuth.getInstance().getCurrentUser().getUid() + "'s netWorth")
            .document("Net Worth");

    ListView portfolioStocks;

    Button refreshPortfolio;

    TextView portfolioValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portfolioStocks = findViewById(R.id.stockPortfolioList);

        stockList = new ArrayList<>();

        refreshPortfolio = findViewById(R.id.refreshPortfolio);
        refreshPortfolio.setOnClickListener(V -> {
            onStart();
        });

        portfolioValue = findViewById(R.id.portfolioValue);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                double numbs = documentSnapshot.getDouble("net worth");
                portfolioValue.setText(String.valueOf(numbs));
            }
        });

        //double portfolioVal = caluclateNetGains() + Double.parseDouble(portfolioValue.getText().toString());
        //portfolioValue.setText(String.valueOf(portfolioVal));
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                stockList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Stock stock = documentSnapshot.toObject(Stock.class);
                    stockList.add(stock);
                }
                PortfolioListAdapterActivity portfolioListAdapterActivity = new PortfolioListAdapterActivity(MainActivity.this,
                        stockList);
                portfolioStocks.setAdapter(portfolioListAdapterActivity);
            }
        });
    }

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

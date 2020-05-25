package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class BuyStock extends AppCompatActivity {

    TextView stockToBuy, priceDisplay, costDisplay;

    EditText quantityDisplay;

    Button addShare, minusShare, purchaseButton;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buysellstock);
        Intent intent = getIntent();
        stockToBuy = findViewById(R.id.nameOfPurchasedStock);
        stockToBuy.setVisibility(View.VISIBLE);
        stockToBuy.setText("Stock: " + intent.getStringExtra("name") + "(" + intent.getStringExtra("symbol") + ")");

        priceDisplay = findViewById(R.id.priceOfStockToBuy);
        priceDisplay.setVisibility(View.VISIBLE);
        priceDisplay.setText(intent.getStringExtra("price"));

        quantityDisplay = findViewById(R.id.quantityShares);
        quantityDisplay.setVisibility(View.VISIBLE);
        quantityDisplay.setText("10");

        addShare = findViewById(R.id.addShare);
        addShare.setVisibility(View.VISIBLE);
        addShare.setOnClickListener(V -> {
            String getNumber = quantityDisplay.getText().toString();
            int newNumber = Integer.parseInt(getNumber) + 1;
            String setNumber = String.valueOf(newNumber);
            quantityDisplay.setText(setNumber);
        });

        minusShare = findViewById(R.id.minusShare);
        minusShare.setVisibility(View.VISIBLE);
        minusShare.setOnClickListener(V -> {
            String getNumber = quantityDisplay.getText().toString();
            int newNumber = Integer.parseInt(getNumber) - 1;
            String setNumber = String.valueOf(newNumber);
            quantityDisplay.setText(setNumber);
        });

        costDisplay = findViewById(R.id.costOfStockPurchase);
        costDisplay.setText(String.valueOf(10.0 * Double.parseDouble(priceDisplay.getText().toString())));
        quantityDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                costDisplay.setText("$" + Integer.parseInt(quantityDisplay.getText().toString()) *
                        Double.parseDouble(priceDisplay.getText().toString()));
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        purchaseButton = findViewById(R.id.purchaseButton);
        purchaseButton.setVisibility(View.VISIBLE);
        purchaseButton.setOnClickListener(v -> {
            String quantity = quantityDisplay.getText().toString();
            String priceBought  = priceDisplay.getText().toString();
            String stock = intent.getStringExtra("symbol");
            String type = "Buy";
            String name = intent.getStringExtra("name");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss a");
            LocalDateTime now = LocalDateTime.now();
            String dateAndTime = dtf.format(now);
            Stock stock1 = new Stock(dateAndTime, priceBought, type, quantity, name, stock);

            database.collection(FirebaseAuth.getInstance().getCurrentUser().getUid() + "'s stocks")
                    .document(stock + ": Bought on " + stock1.getDateAndTime()).set(stock1)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            });
        });

    }
}

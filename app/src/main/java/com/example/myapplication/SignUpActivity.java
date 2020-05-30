package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    TextView signUpSign, signUpEmailSign, signUpPasswordSign;
    EditText signUpEmailEntry, signUpPasswordEntry;
    Button createAccount;
    String email, password;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private DocumentReference netWorthRef;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        signUpSign = findViewById(R.id.signUpSign);
        signUpSign.setVisibility(View.VISIBLE);
        signUpEmailSign = findViewById(R.id.signUpEmailSign);
        signUpEmailSign.setVisibility(View.VISIBLE);
        signUpPasswordSign = findViewById(R.id.signUpPasswordSign);
        signUpPasswordSign.setVisibility(View.VISIBLE);

        signUpEmailEntry = findViewById(R.id.signUpEmailEntry);
        signUpEmailEntry.setVisibility(View.VISIBLE);
        signUpPasswordEntry = findViewById(R.id.signUpPasswordEntry);
        signUpPasswordEntry.setVisibility(View.VISIBLE);

        createAccount = findViewById(R.id.createAccountButton);
        createAccount.setVisibility(View.VISIBLE);
        createAccount.setOnClickListener(v -> {
            email = signUpEmailEntry.getText().toString();
            password = signUpPasswordEntry.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                netWorthRef = firebaseFirestore.collection(FirebaseAuth.getInstance().getCurrentUser().getUid() + "'s netWorth")
                                        .document("Net Worth");
                                createPortfolio();
                                loginUI();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    public void loginUI() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void createPortfolio() {
        Map<String, Object> netWorth = new HashMap<>();
        netWorth.put("net worth", "10000.00");
        netWorth.put("confirmed", "yes");
        netWorthRef.set(netWorth)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

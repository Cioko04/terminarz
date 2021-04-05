package com.example.terminarzg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private EditText nazwa, haslo;
    private UserProfile user;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();

        if(user != null){
            wyloguj();
        }


        setUpButtons();


    }


    private void setUpViews(){
        nazwa = findViewById(R.id.nazwa);
        haslo = findViewById(R.id.haslo);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        progressBar = findViewById(R.id.progress);
    }

    private void setUpButtons(){
        TextView zal = findViewById(R.id.zal);
        zal.setOnClickListener(v -> {
                    zaloguj(nazwa.getText().toString(), haslo.getText().toString());
                    finish();
                }
                
        );

        TextView rej = findViewById(R.id.rej);
        rej.setOnClickListener(v -> rejestruj());

        TextView kal = findViewById(R.id.kal);
        kal.setOnClickListener(v -> zobaczTerminarz());

    }

    private void wyloguj(){
        firebaseAuth.signOut();
        finish();
    }

    private void zaloguj(String nazwa, String haslo) {
        if (!nazwa.isEmpty() && !haslo.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(nazwa, haslo).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            validate();
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            Toast.makeText(MainActivity.this, "Użytkownik nie istnieje!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        }else{
            Toast.makeText(getApplicationContext(),"Podaj dane logowania!", Toast.LENGTH_SHORT).show();
        }
    }

    private void validate(){
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Uzytkownicy").document(firebaseAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(UserProfile.class);
                if(user.getCzyFryzjer()){
                    Toast.makeText(MainActivity.this, "Fryzjer zalogowany!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, FryzjerActivity.class));
                }else {
                    Toast.makeText(MainActivity.this,  "Klient zalogowany", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, KlientActivity.class));
                }
            }
        });
    }

    private void rejestruj(){
        Intent intent2 = new Intent(this, RejestracjaActivity.class);
        startActivity(intent2);
    }

    private void zobaczTerminarz(){
        Intent intent3 = new Intent(this, KalendarzActivity.class);
        startActivity(intent3);
    }
}
package com.example.terminarzg;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RejestracjaActivity extends AppCompatActivity {
    private EditText email, imie, nazwisko, haslo1, haslo2;
    private String emailS, imieS, nazwiskoS, hasloS1, hasloS2;
    private TextView zare;
    private FirebaseAuth firebaseAuth;
    private Map<String, Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja);
        getSupportActionBar().setTitle("Rejestracja");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpViews();

        firebaseAuth = FirebaseAuth.getInstance();

        zare.setOnClickListener(v -> {
            if(validate()){
                registery();
            }
        });

    }

    private void registery(){
        String user_email = email.getText().toString().trim();
        String user_haslo = haslo1.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(user_email, user_haslo).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    sendData();
                    Toast.makeText(getApplicationContext(),"Rejestracja udana!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RejestracjaActivity.this, MainActivity.class));
                }else {
                    Toast.makeText(getApplicationContext(),"Użytkownik już istnieje!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setUpViews(){
        email = findViewById(R.id.email);
        imie = findViewById(R.id.imie);
        nazwisko = findViewById(R.id.nazwisko);
        haslo1 = findViewById(R.id.haslo1);
        haslo2 = findViewById(R.id.haslo2);
        zare = findViewById(R.id.zare);
    }

    private Boolean validate(){
        Boolean result = false;

        emailS = email.getText().toString().trim();
        imieS = imie.getText().toString().trim();
        nazwiskoS = nazwisko.getText().toString().trim();
        hasloS1 = haslo1.getText().toString().trim();
        hasloS2 = haslo2.getText().toString().trim();



        if(!emailS.isEmpty() && !imieS.isEmpty() && !nazwiskoS.isEmpty() && !hasloS1.isEmpty() && !hasloS2.isEmpty()){

            if(hasloS1.equals(hasloS2)){
                result = true;

            }else Toast.makeText(getApplicationContext(),"Hasła się nie zgadzają, podaj takie same!", Toast.LENGTH_LONG).show();


        }else if(emailS.isEmpty()) Toast.makeText(getApplicationContext(),"Podaj email!", Toast.LENGTH_LONG).show();
        else if(imieS.isEmpty()) Toast.makeText(getApplicationContext(),"Podaj imię!", Toast.LENGTH_LONG).show();
        else if(nazwiskoS.isEmpty()) Toast.makeText(getApplicationContext(),"Podaj nazwisko!", Toast.LENGTH_LONG).show();
        else if(hasloS1.isEmpty()) Toast.makeText(getApplicationContext(),"Podaj haslo!", Toast.LENGTH_LONG).show();

        return result;
    }

    private void sendData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        user.put("imie", imieS);
        user.put("nazwisko", nazwiskoS);
        user.put("email", emailS);
        user.put("czyFryzjer", false);

        db.collection("Uzytkownicy").document(firebaseAuth.getUid()).set(user);

    }

}

package com.example.terminarzg;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class KlientActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private UserProfile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klient);
        firebaseAuth = FirebaseAuth.getInstance();
        upClient();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView umow = findViewById(R.id.umow);
        umow.setOnClickListener(v -> umowTermin());

        TextView ztermin = findViewById(R.id.ztermin);
        ztermin.setOnClickListener(v -> zobaczTerminy());

    }
    private void wyloguj(){
        Toast.makeText(getApplicationContext(),"Wylogowano!", Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(KlientActivity.this, MainActivity.class));
    }
    private void umowTermin(){
        Intent intent1 = new Intent(this, UmowActivity.class);
        startActivity(intent1);
    }

    private void zobaczTerminy(){
        usunDane();
        Intent intent1 = new Intent(this, KlientZobaczTerminy.class);
        startActivity(intent1);

    }
    private void usunDane(){
        KlientZobaczTerminy.docZ.clear();
        KlientZobaczTerminy.itemsZ.clear();
        KlientZobaczTerminy.docO.clear();
        KlientZobaczTerminy.itemsO.clear();
    }

    public void upClient(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Uzytkownicy").document(firebaseAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(UserProfile.class);
                getSupportActionBar().setTitle(user.getImie());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logutMenu:{
                wyloguj();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}

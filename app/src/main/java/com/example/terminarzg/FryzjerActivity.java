package com.example.terminarzg;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class FryzjerActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private UserProfile user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fryzjer);
        firebaseAuth = FirebaseAuth.getInstance();
        upInfo();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView umow = findViewById(R.id.umow);
        umow.setOnClickListener(v -> umowTermin());

        TextView ztermin = findViewById(R.id.ztermin);
        ztermin.setOnClickListener(v -> zobaczTerminy());

        TextView aterminy = findViewById(R.id.aterminy);
        aterminy.setOnClickListener(v -> akceptujTerminy());

    }


    private void umowTermin(){
        usunDane();
        Intent intent1 = new Intent(this, UmowFryzjerActivity.class);
        startActivity(intent1);
    }

    private void zobaczTerminy(){
        Intent intent1 = new Intent(this, FryzjerTerminy.class);
        startActivity(intent1);
    }

    private void akceptujTerminy() {
        usunDane();
        Intent intent1 = new Intent(this, AkceptujTermin.class);
        startActivity(intent1);
    }

    private void usunDane(){
        AkceptujTermin.docID.clear();
        AkceptujTermin.items.clear();
        FryzjerTerminy.docID.clear();
        FryzjerTerminy.items.clear();
        UmowFryzjerActivity.fryzjerzy.clear();
        UmowFryzjerActivity.fEmail.clear();
    }
    private void wyloguj(){
        Toast.makeText(getApplicationContext(),"Wylogowano!", Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(FryzjerActivity.this, MainActivity.class));
    }
    public void upInfo(){
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

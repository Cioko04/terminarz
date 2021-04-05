package com.example.terminarzg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.Query.Direction;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FryzjerTerminy extends AppCompatActivity {
    private UserProfile user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private WaitCalendar terminy;
    public static ArrayList<String> docID = new ArrayList<>();
    public static ArrayList<RecycleItem> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fryzjerterminy);
        setUpViews();
        upClient();

    }

    private void setUpViews(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.txt);
    }

    public void upClient(){
        DocumentReference docRef = db.collection("Uzytkownicy").document(firebaseAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(UserProfile.class);
                getSupportActionBar().setTitle(user.getImie());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                wyswietlTerminy();
            }
        });
    }
    private void wyswietlTerminy(){
        db.collection("Terminy").orderBy("order", Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                terminy = document.toObject(WaitCalendar.class);
                                if(terminy.getfEmail().equals(user.getEmail())) {
                                    if (terminy.getczyOk()) {
                                        docID.add(document.getId());
                                        items.add(new RecycleItem(R.drawable.ic_man_green, terminy.getwDataStr() + " " + terminy.getwCzas(), terminy.getwImie() + " " + terminy.getwNazwisko(), R.drawable.ic_delete, R.drawable.ic_null));
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Cos nie wyszlo", Toast.LENGTH_SHORT).show();
                        }
                        recycleView(items);
                    }
                });

    }


    private void recycleView(ArrayList<RecycleItem> items){

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(FryzjerTerminy.this);
        recyclerAdapter = new Adapter(items);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                db.collection("Terminy").
                        document(docID.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Usunięto termin!", Toast.LENGTH_SHORT).show();
                        docID.remove(position);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Nie udało się dodać terminu!" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                items.remove(position);
                recyclerAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onAddClick(int position) {

            }
        });

    }

}
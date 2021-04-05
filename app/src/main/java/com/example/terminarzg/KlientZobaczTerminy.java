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

public class KlientZobaczTerminy extends AppCompatActivity {
    private UserProfile user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private WaitCalendar terminy;
    public static ArrayList<String> docZ = new ArrayList<>();
    public static ArrayList<String> docO = new ArrayList<>();
    public static ArrayList<RecycleItem> itemsZ = new ArrayList<>();
    public static ArrayList<RecycleItem> itemsO = new ArrayList<>();
    private RecyclerView recyclerView, recyclerView1;
    private Adapter recyclerAdapter, recyclerAdapter1;
    private RecyclerView.LayoutManager layoutManager, layoutManager1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.klient_zobacz_terminy);
        setUpViews();
        upClient();
    }

    private void setUpViews(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.txt);
        recyclerView1 = findViewById(R.id.txt1);
    }

    public void upClient(){
        DocumentReference docRef = db.collection("Uzytkownicy").document(firebaseAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(UserProfile.class);
                getSupportActionBar().setTitle(user.getImie());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                wyswietlOczekujaceTerminy();
                wyswietlZaakcetowaneTerminy();
            }
        });
    }
    private void wyswietlOczekujaceTerminy(){
        db.collection("Terminy").orderBy("order", Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                terminy = document.toObject(WaitCalendar.class);
                                if(terminy.getUserUID().equals(firebaseUser.getUid())) {
                                    if (!terminy.getczyOk()) {
                                        docO.add(document.getId());
                                        itemsO.add(new RecycleItem(R.drawable.ic_manred, terminy.getwDataStr() + " " + terminy.getwCzas(), terminy.getwFryzjer(), R.drawable.ic_delete,R.drawable.ic_null));
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Cos nie wyszlo", Toast.LENGTH_SHORT).show();
                        }
                        recycleOczekujace(itemsO);
                    }
                });

    }
    private void wyswietlZaakcetowaneTerminy(){
        db.collection("Terminy").orderBy("order", Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                terminy = document.toObject(WaitCalendar.class);
                                if(terminy.getUserUID().equals(firebaseUser.getUid())) {
                                    if (terminy.getczyOk()) {
                                        docZ.add(document.getId());
                                        itemsZ.add(new RecycleItem(R.drawable.ic_man_green, terminy.getwDataStr() + " " + terminy.getwCzas(), terminy.getwFryzjer(), R.drawable.ic_delete,R.drawable.ic_null));
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Cos nie wyszlo", Toast.LENGTH_SHORT).show();
                        }
                        recycleZaakceptowane(itemsZ);
                    }
                });

    }

   private void recycleZaakceptowane(ArrayList<RecycleItem> itemsZ){

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(KlientZobaczTerminy.this);
        recyclerAdapter = new Adapter(itemsZ);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

       recyclerAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
           @Override
           public void onDeleteClick(int position) {
               db.collection("Terminy").
                       document(docZ.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Toast.makeText(getApplicationContext(),"Usunięto termin!", Toast.LENGTH_SHORT).show();
                       docZ.remove(position);
                   }
               })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(getApplicationContext(),"Nie udało się dodać terminu!" + e, Toast.LENGTH_SHORT).show();
                           }
                       });
               itemsZ.remove(position);
               recyclerAdapter.notifyItemRemoved(position);
           }

           @Override
           public void onAddClick(int position) {

           }
       });

    }
    private void recycleOczekujace(ArrayList<RecycleItem> itemsO){

        recyclerView1.setHasFixedSize(true);
        layoutManager1 = new LinearLayoutManager(KlientZobaczTerminy.this);
        recyclerAdapter1 = new Adapter(itemsO);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView1.setAdapter(recyclerAdapter1);

        recyclerAdapter1.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                db.collection("Terminy").
                        document(docO.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Usunięto termin!", Toast.LENGTH_SHORT).show();
                        docO.remove(position);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Nie udało się dodać terminu!" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                itemsO.remove(position);
                recyclerAdapter1.notifyItemRemoved(position);
            }

            @Override
            public void onAddClick(int position) {

            }
        });

    }

}
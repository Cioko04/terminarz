package com.example.terminarzg;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.firestore.Query.Direction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class KalendarzActivity extends AppCompatActivity {
    private int pozycja;
    private String dataStr;
    private FirebaseFirestore db;
    private Spinner spinner;
    private UserProfile user;
    private WaitCalendar terminy;
    private CalendarView calendarView;
    public static ArrayList<String> fryzjerzy = new ArrayList<>();
    public static ArrayList<String> fEmail = new ArrayList<>();
    private ArrayList<RecycleItem> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalendarz);
        getSupportActionBar().setTitle("Kalendarz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpViews();
        wFryzjera();

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        setUpViews();
        wFryzjera();
    }
    private void setUpViews(){
        spinner = findViewById(R.id.spinner);
        calendarView = findViewById(R.id.data);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.txt);

    }

    private void wFryzjera(){
        fryzjerzy.clear();
        fEmail.clear();
        CollectionReference fryzjerzyRef = db.collection("Uzytkownicy");
        Query query = fryzjerzyRef.whereEqualTo("czyFryzjer", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user = document.toObject(UserProfile.class);
                        fryzjerzy.add(user.getImie());
                        fEmail.add(user.getEmail());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(KalendarzActivity.this, android.R.layout.simple_spinner_item, fryzjerzy);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            pozycja = position;
                            wybierzDate();
                        }
                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Nie pobrano dokumentu!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
   private void wybierzDate(){
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar kalendarz = Calendar.getInstance();
            kalendarz.set(year,month,dayOfMonth);
            items.clear();
            recyclerAdapter = new Adapter(items);
            dataStr = sdf.format(kalendarz.getTime());
            wyswietlTerminy();
        });
    }

    private void wyswietlTerminy(){
        db.collection("Terminy")
                .orderBy("order", Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                terminy = document.toObject(WaitCalendar.class);
                                if(terminy.getfEmail().equals(fEmail.get(pozycja))) {
                                    if (terminy.getwDataStr().equalsIgnoreCase(dataStr)) {
                                        if (terminy.getczyOk()) {
                                            items.add(new RecycleItem(R.drawable.ic_man_green, terminy.getwCzas(),
                                                    terminy.getwImie() + " " + terminy.getwNazwisko(),
                                                    R.drawable.ic_null,R.drawable.ic_null));
                                        }
                                    }
                                }

                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Cos nie wyszlo", Toast.LENGTH_LONG).show();
                        }

                        recycleView(items);
                    }
                });

    }
    private void recycleView(ArrayList<RecycleItem> items){

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(KalendarzActivity.this);
        recyclerAdapter = new Adapter(items);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

    }

}

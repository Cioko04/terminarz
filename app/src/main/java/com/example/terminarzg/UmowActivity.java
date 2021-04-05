package com.example.terminarzg;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.firestore.Query.Direction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UmowActivity extends AppCompatActivity {
    private int h = -1 , min = -1, pozycja = -1, data = -1, order;
    private TextView wCzas;
    private UserProfile userF, user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private CalendarView wDate;
    private Spinner spinner;
    private WaitCalendar terminy;
    public static ArrayList<String> fryzjerzy = new ArrayList<>();
    public static ArrayList<String> fEmail = new ArrayList<>();
    private ArrayList<RecycleItem> items = new ArrayList<>();
    private ArrayList<String> times = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Map<String, Object> termin = new HashMap<>();
    private String czas, dataStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umow);

        setUpViews();
        upClient();
        wFryzjera();
        setUpButtons();


    }

    private void setUpButtons(){
        FloatingActionButton godzina = findViewById(R.id.godzina);
        godzina.setOnClickListener(view1 -> {
            wCzas();
        });
        FloatingActionButton zatwierdz = findViewById(R.id.zatwierdz);
        zatwierdz.setOnClickListener(view1 -> {
            if(validate()) {
                zatwierdzDane();
            }

        });
    }

    private void setUpViews(){
        wCzas = findViewById(R.id.wCzas);
        wDate = findViewById(R.id.data);
        spinner = findViewById(R.id.spinner);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.txt);
    }

    private Boolean validate(){
        for(int i=0; i<times.size();i++){
            if(times.get(i).equalsIgnoreCase(czas)){
                h = -1;
                min = -1;
                Toast.makeText(getApplicationContext(),"Wizyta na wybraną godzinę jest już umówiona!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(h == -1 || min == -1){
            Toast.makeText(getApplicationContext(),"Wybierz godzinę wizyty!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(pozycja == -1){
            Toast.makeText(getApplicationContext(),"Wybierz fryzjera do którego chcesz się umówić!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(data == -1){
            Toast.makeText(getApplicationContext(),"Wybierz datę wizyty!", Toast.LENGTH_SHORT).show();
            return false;
        }else  return true;

    }
    public void upClient(){
        DocumentReference docRef = db.collection("Uzytkownicy").document(firebaseAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(UserProfile.class);
                getSupportActionBar().setTitle(user.getImie());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });
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
                                userF = document.toObject(UserProfile.class);
                                fryzjerzy.add(userF.getImie());
                                fEmail.add(userF.getEmail());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Nie pobrano dokumentu!", Toast.LENGTH_SHORT).show();
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UmowActivity.this, android.R.layout.simple_spinner_item, fryzjerzy);
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
                    }


                });
    }

    private void wCzas(){
       SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        TimePickerDialog zegar = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                    h = hourOfDay;
                    min = minute;
                    Calendar kalendarz = Calendar.getInstance();
                    kalendarz.set(0,0,0,h,min);
                    wCzas.setText("Wybrana godzina:\n           " + sdf.format(kalendarz.getTime()));
                    czas = sdf.format(kalendarz.getTime());
            }, 24,0,true);
        zegar.updateTime(h,min);
        zegar.show();

    }

    private void wybierzDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        wDate.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            int day = dayOfMonth;
            int month1 = month+1;
            int year1 = year;
            Calendar kalendarz = Calendar.getInstance();
            kalendarz.set(year,month,dayOfMonth);
            items.clear();
            recyclerAdapter = new Adapter(items);
            data = (day*1000) + (month1*10000) + (year1*100000);
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
                                        if(terminy.getczyOk()){
                                        items.add(new RecycleItem(R.drawable.ic_man_green, terminy.getwCzas(), terminy.getwImie() + " " + terminy.getwNazwisko(), R.drawable.ic_null, R.drawable.ic_null));
                                        }
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
        layoutManager = new LinearLayoutManager(UmowActivity.this);
        recyclerAdapter = new Adapter(items);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void zatwierdzDane(){
            termin.put("wCzas", czas);
            termin.put("wFryzjer", fryzjerzy.get(pozycja));
            termin.put("userUID", firebaseUser.getUid());
            termin.put("wImie", user.getImie());
            termin.put("wNazwisko", user.getNazwisko());
            termin.put("wDataStr", dataStr);
            termin.put("order", data + h*100 + min);
            termin.put("czyOk", false);
            termin.put("fEmail", fEmail.get(pozycja));

            db.collection("Terminy").add(termin);
            Intent intent = new Intent(this, KlientActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"Dodano twój termin! ", Toast.LENGTH_SHORT).show();
            finish();
    }


}

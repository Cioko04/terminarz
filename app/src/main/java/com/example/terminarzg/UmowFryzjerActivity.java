package com.example.terminarzg;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.firestore.Query.Direction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UmowFryzjerActivity extends AppCompatActivity {
    private int h = -1 , min = -1, pozycja = -1, data = -1;
    private Spinner spinner;
    private CalendarView wDate;
    private TextView wCzas;
    private EditText imie, nazwisko;
    private String imieS, nazwiskoS, czas, dataStr;
    private UserProfile userF, user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private WaitCalendar terminy;
    public static ArrayList<String> fryzjerzy = new ArrayList<>();
    public static ArrayList<String> fEmail = new ArrayList<>();
    private Map<String, Object> termin = new HashMap<>();
    private ArrayList<RecycleItem> items = new ArrayList<>();
    private ArrayList<String> times = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umowfryzjer);
        getSupportActionBar().setTitle("Umów wiztytę");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpViews();
        wFryzjera();

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
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        spinner = findViewById(R.id.spinner);
        imie = findViewById(R.id.imie);
        nazwisko = findViewById(R.id.nazwisko);
        wCzas = findViewById(R.id.wCzas);
        wDate = findViewById(R.id.data);
        recyclerView = findViewById(R.id.txt);
    }

    private Boolean validate(){
        imieS = imie.getText().toString().trim();
        nazwiskoS = nazwisko.getText().toString().trim();
        for(int i=0; i<times.size();i++){
            if(times.get(i).equalsIgnoreCase(czas)){
                h = -1;
                min = -1;
                Toast.makeText(getApplicationContext(),"Wizyta na wybraną godzinę jest już umówiona!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(h == -1 || min == -1){
            Toast.makeText(getApplicationContext(),"Wybierz godzinę wizyty!", Toast.LENGTH_LONG).show();
            return false;
        }else if(pozycja == -1){
            Toast.makeText(getApplicationContext(),"Wybierz fryzjera do którego chcesz się umówić!", Toast.LENGTH_LONG).show();
            return false;
        }else if(data == -1){
            Toast.makeText(getApplicationContext(),"Wybierz datę wizyty!", Toast.LENGTH_LONG).show();
            return false;
        }else if(imieS.isEmpty() && nazwiskoS.isEmpty()){
            Toast.makeText(getApplicationContext(),"Podaj imie i nazwisko klienta!", Toast.LENGTH_LONG).show();
            return false;
        }else return true;
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
                } else {
                    Toast.makeText(getApplicationContext(),"Nie pobrano dokumentu!", Toast.LENGTH_LONG).show();
                }
                Spinner spinner = findViewById(R.id.spinner);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UmowFryzjerActivity.this, android.R.layout.simple_spinner_item, fryzjerzy);
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
        layoutManager = new LinearLayoutManager(UmowFryzjerActivity.this);
        recyclerAdapter = new Adapter(items);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void zatwierdzDane(){
        termin.put("wCzas", czas);
        termin.put("wFryzjer", fryzjerzy.get(pozycja));
        termin.put("userUID", firebaseUser.getUid());
        termin.put("wImie", imie.getText().toString());
        termin.put("wNazwisko", nazwisko.getText().toString());
        termin.put("wDataStr", dataStr);
        termin.put("order", data + h*100 + min);
        termin.put("czyOk", true);
        termin.put("fEmail", fEmail.get(pozycja));

        db.collection("Terminy").add(termin);

        Intent intent = new Intent(this, FryzjerActivity.class);
        startActivity(intent);

        Toast.makeText(getApplicationContext(),"Dodano twój termin!", Toast.LENGTH_SHORT).show();
        finish();

    }



}

package com.example.projetws;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.adapter.EtudiantAdapter;
import com.example.projetws.beans.Etudiant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AffichageActivity extends AppCompatActivity{
    private static final String TAG = "RecycleActivity";
    private RecyclerView recycle;
    private FloatingActionButton addItem;
    RequestQueue requestQueue;
    String loadUrl = "http://10.0.2.2/PhpProject2/ws/loadEtudiant.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage);

        recycle = findViewById(R.id.recycle);
        addItem = findViewById(R.id.addItem);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AffichageActivity.this, AddEtudiant.class));
            }
        });

        loadData();
    }

    public void loadData() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                loadUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                Collection<Etudiant> etudiants = new Gson().fromJson(response, type);
                recycle.setAdapter(new EtudiantAdapter(AffichageActivity.this, (List<Etudiant>) etudiants));
                recycle.setLayoutManager(new LinearLayoutManager(AffichageActivity.this));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);
    }
}

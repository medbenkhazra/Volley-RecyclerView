package com.example.projetws;



import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddEtudiant extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AddEtudiant";
    private CircleImageView img;
    private ImageButton remove;
    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add;
    private Button affichage;
    private Bitmap bitmap = null;
    private String link = "android.resource://com.example.projetws/drawable/avatar";
    RequestQueue requestQueue;
    String insertUrl = "http://10.0.2.2/PhpProject2/ws/createEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);

        img = findViewById(R.id.image);
        remove = findViewById(R.id.remove);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        add = findViewById(R.id.add);
        affichage = findViewById(R.id.afficher);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);
        img.setOnClickListener(this);
        remove.setOnClickListener(this);
        remove.setScaleType(ImageView.ScaleType.FIT_CENTER);
        add.setOnClickListener(this);
        affichage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.d("ok","ok");
        if(v == img) {
            ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        }
        if(v == remove) {
            link = "android.resource://com.example.projetws/drawable/avatar";
            bitmap = null;
            Glide
                    .with(getApplicationContext())
                    .load(Uri.parse(link))
                    .apply(RequestOptions.fitCenterTransform())
                    .into(img);
        }
        if (v == add) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest request = new StringRequest(Request.Method.POST,
                    insertUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response);
                    Toast.makeText(AddEtudiant.this, "Ajout avec succès", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AddEtudiant.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String sexe = "";
                    if(m.isChecked())
                        sexe = "homme";
                    else
                        sexe = "femme";
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("nom", nom.getText().toString());
                    params.put("prenom", prenom.getText().toString());
                    params.put("ville", ville.getSelectedItem().toString());
                    params.put("sexe", sexe);
                  //  params.put("img", img);
                    String stringImg = null;
                    if(bitmap != null) {
                        Log.d("mzian","bitmap machi null");

                        stringImg = getStringImage(bitmap);
                        Log.d("bitmapAch",stringImg);
                        params.put("img", stringImg);
                    }else {
                        Log.d("erreura","bitmap null");
                        params.put("img", "no");
                    }

                    return params;
                }
            };
            requestQueue.add(request);
          //  startActivity(new Intent(AddEtudiant.this, AffichageActivity.class));
        }
        if(v == affichage) {
            startActivity(new Intent(AddEtudiant.this, AffichageActivity.class));
        }


    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Glide
                    .with(getApplicationContext())
                    .load(uri)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(img);
        }
    }
}
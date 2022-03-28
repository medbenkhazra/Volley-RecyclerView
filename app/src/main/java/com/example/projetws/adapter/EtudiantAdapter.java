package com.example.projetws.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projetws.beans.Etudiant;

import com.example.projetws.R;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> {
    private static final String TAG = "EtudiantAdapter";
    private List<Etudiant> etudiants;
    private LayoutInflater inflater;
    private Context context;
    RequestQueue requestQueue;
    String deleteUrl = "http://192.168.1.107/phpvolley/ws/deleteEtudiant.php";

    public EtudiantAdapter(Context context, List<Etudiant> etudiants) {
        this.etudiants = etudiants;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.etudiant_item, parent, false);
        return new EtudiantViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        holder.id.setText(etudiants.get(position).getId()+"");
        holder.nom.setText(etudiants.get(position).getNom());
        holder.prenom.setText(etudiants.get(position).getPrenom());
        holder.ville.setText("Ville : " + etudiants.get(position).getVille());
        holder.sexe.setText("Sexe : " + etudiants.get(position).getSexe());
        if(etudiants.get(position).getImg() == null) {
            String link = "android.resource://com.example.projetws/drawable/avatar";
            Glide
                    .with(context)
                    .load(Uri.parse(link))
                    .apply(RequestOptions.fitCenterTransform())
                    .into(holder.image);
        }else {
            byte[] decodedString = Base64.decode(etudiants.get(position).getImg(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide
                    .with(context)
                    .load(decodedByte)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Choisir une option !");

                alertDialogBuilder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestQueue = Volley.newRequestQueue(context);
                        StringRequest request = new StringRequest(Request.Method.POST,
                                deleteUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, response);
                                etudiants.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Suppression avec succès", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("id", holder.id.getText().toString());
                                return params;
                            }
                        };
                        requestQueue.add(request);
                    }
                });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    public class EtudiantViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView nom, prenom, ville, sexe, id;
        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageAffich);
            nom = itemView.findViewById(R.id.nom);
            prenom = itemView.findViewById(R.id.prenom);
            ville = itemView.findViewById(R.id.ville);
            sexe = itemView.findViewById(R.id.sexe);
            id = itemView.findViewById(R.id.idE);
        }
    }
}

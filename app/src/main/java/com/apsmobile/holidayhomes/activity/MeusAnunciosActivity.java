package com.apsmobile.holidayhomes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apsmobile.holidayhomes.R;
import com.apsmobile.holidayhomes.adapter.AdapterAnuncios;
import com.apsmobile.holidayhomes.helper.FirebaseHelper;
import com.apsmobile.holidayhomes.model.Anuncio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity implements AdapterAnuncios.OnClick {

    private List<Anuncio> anuncioList = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView text_info;
    private SwipeableRecyclerView rv_anuncios;
    private AdapterAnuncios adapterAnuncios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        iniciaComponentes();

        configRv();

        configCliques();

    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperaAnuncios();
    }

    private void configCliques(){
        findViewById(R.id.ib_add).setOnClickListener(view ->
                startActivity(new Intent(this, FormAnuncioActivity.class)));
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
    }

    private void configRv(){
        rv_anuncios.setLayoutManager(new LinearLayoutManager(this));
        rv_anuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncioList, this);
        rv_anuncios.setAdapter(adapterAnuncios);

        rv_anuncios.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
            }

            @Override
            public void onSwipedRight(int position) {
                showDialogDelete(position);
            }
        });
    }

    private void showDialogDelete(int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete anúncio");
        builder.setMessage("Aperte em sim para confirmar ou não para cancelar.");
        builder.setNegativeButton("Não", ((dialogInterface, i) -> {
            dialogInterface.dismiss();
            adapterAnuncios.notifyDataSetChanged();
        }));
        builder.setPositiveButton("Sim", ((dialogInterface, i) -> {
            anuncioList.get(pos).deletar();
            adapterAnuncios.notifyItemRemoved(pos);
        }));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperaAnuncios(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios")
                .child(FirebaseHelper.getIdFirebase());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    anuncioList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Anuncio anuncio = snap.getValue(Anuncio.class);
                        anuncioList.add(anuncio);
                    }
                    text_info.setText("");
                }else {
                    text_info.setText("Nenhum anúncio cadatrado.");
                }

                progressBar.setVisibility(View.GONE);
                Collections.reverse(anuncioList);
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void iniciaComponentes() {
        TextView text_titulo = findViewById(R.id.text_titulo_anuncio);
        text_titulo.setText("Meus Anúncios");

        progressBar = findViewById(R.id.progressBar);
        text_info = findViewById(R.id.text_info);
        rv_anuncios = findViewById(R.id.rv_anuncios);
    }

    @Override
    public void OnClickListener(Anuncio anuncio) {
        Intent intent = new Intent(this, FormAnuncioActivity.class);
        intent.putExtra("anuncio", anuncio);
        startActivity(intent);
    }
}
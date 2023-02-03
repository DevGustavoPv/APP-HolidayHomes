package com.apsmobile.holidayhomes.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apsmobile.holidayhomes.R;
import com.apsmobile.holidayhomes.activity.autenticacao.LoginActivity;
import com.apsmobile.holidayhomes.adapter.AdapterAnuncios;
import com.apsmobile.holidayhomes.helper.FirebaseHelper;
import com.apsmobile.holidayhomes.model.Anuncio;
import com.apsmobile.holidayhomes.model.Filtro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterAnuncios.OnClick {

    private final int REQUEST_FILTRO = 100;

    private RecyclerView rv_anuncios;
    private TextView text_info;
    private ProgressBar progressBar;

    private List<Anuncio> anuncioList = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private ImageButton ib_menu;

    private Filtro filtro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciaComponentes();

        configRv();

        configCliques();

        recuperaAnuncios();

    }

    private void configRv() {
        rv_anuncios.setLayoutManager(new LinearLayoutManager(this));
        rv_anuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncioList, this);
        rv_anuncios.setAdapter(adapterAnuncios);
    }

    private void recuperaAnuncios() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios_publicos");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    anuncioList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Anuncio anuncio = snap.getValue(Anuncio.class);
                        anuncioList.add(anuncio);
                    }
                    text_info.setText("");
                } else {
                    text_info.setText("Nenhum anúncio cadastrado.");
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

    private void recuperaAnunciosFiltro() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios_publicos");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    anuncioList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Anuncio anuncio = snap.getValue(Anuncio.class);

                        int quarto = Integer.parseInt(anuncio.getQuarto());
                        int banheiro = Integer.parseInt(anuncio.getBanheiro());
                        int garagem = Integer.parseInt(anuncio.getGaragem());

                        if (quarto >= filtro.getQtdQuarto() &&
                                banheiro >= filtro.getQtdBanheiro() &&
                                garagem >= filtro.getQtdGaragem()) {
                            anuncioList.add(anuncio);
                        }

                    }
                }

                if (anuncioList.size() == 0) {
                    text_info.setText("Nenhum anúncio encontrado.");
                } else {
                    text_info.setText("");
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

    private void configCliques() {
        ib_menu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, ib_menu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_home, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_filtrar) {
                    Intent intent = new Intent(this, FiltrarAnunciosActivity.class);
                    intent.putExtra("filtro", filtro);
                    startActivityForResult(intent, REQUEST_FILTRO);
                } else if (menuItem.getItemId() == R.id.menu_meus_anuncios) {
                    if (FirebaseHelper.getAutenticado()) {
                        startActivity(new Intent(this, MeusAnunciosActivity.class));
                    } else {
                        showDialogLogin();
                    }
                } else {
                    if (FirebaseHelper.getAutenticado()) {
                        startActivity(new Intent(this, MinhaContaActivity.class));
                    } else {
                        showDialogLogin();
                    }
                }
                return true;
            });
            popupMenu.show();
        });
    }

    private void showDialogLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Autenticação");
        builder.setMessage("Você não está autenticado no app, deseja fazer isso agora ?");
        builder.setCancelable(false);
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Sim", (dialog, which) -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void iniciaComponentes() {
        ib_menu = findViewById(R.id.ib_menu);
        rv_anuncios = findViewById(R.id.rv_anuncios);
        text_info = findViewById(R.id.text_info);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FILTRO) {
                filtro = (Filtro) data.getSerializableExtra("filtro");
                recuperaAnunciosFiltro();
            }
        }
    }

    @Override
    public void OnClickListener(Anuncio anuncio) {
        Intent intent = new Intent(this, DetalheAnuncioActivity.class);
        intent.putExtra("anuncio", anuncio);
        startActivity(intent);
    }
}
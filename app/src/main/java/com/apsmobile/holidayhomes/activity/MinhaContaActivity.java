package com.apsmobile.holidayhomes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apsmobile.holidayhomes.R;
import com.apsmobile.holidayhomes.helper.FirebaseHelper;
import com.apsmobile.holidayhomes.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MinhaContaActivity extends AppCompatActivity {

    private EditText edit_nome;
    private EditText edit_telefone;
    private EditText edit_email;

    private ProgressBar progressBar;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta);

        iniciaComponentes();

        recuperaDados();

        configCliques();

    }

    private void recuperaDados(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);

                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados(){
        edit_nome.setText(usuario.getNome());
        edit_telefone.setText(usuario.getTelefone());
        edit_email.setText(usuario.getEmail());

        progressBar.setVisibility(View.GONE);
    }

    private void configCliques(){
        findViewById(R.id.ib_salvar).setOnClickListener(view -> validaDados());
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
        findViewById(R.id.btn_deslogar).setOnClickListener(view -> {
            FirebaseHelper.getAuth().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void validaDados(){

        String nome = edit_nome.getText().toString();
        String telefone = edit_telefone.getText().toString();

        if(!nome.isEmpty()){
            if(!telefone.isEmpty()){

                progressBar.setVisibility(View.VISIBLE);

                usuario.setNome(nome);
                usuario.setTelefone(telefone);
                usuario.salvar();

                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Perfil salvo com sucesso.", Toast.LENGTH_SHORT).show();

            }else {
                edit_telefone.requestFocus();
                edit_telefone.setError("Informe seu telefone.");
            }
        }else {
            edit_nome.requestFocus();
            edit_nome.setError("Informe seu nome.");
        }

    }

    private void iniciaComponentes(){
        TextView text_titulo = findViewById(R.id.text_titulo);
        text_titulo.setText("Perfil");

        edit_nome = findViewById(R.id.edit_nome);
        edit_telefone = findViewById(R.id.edit_telefone);
        edit_email = findViewById(R.id.edit_email);

        progressBar = findViewById(R.id.progressBar);
    }

}
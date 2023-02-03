package com.apsmobile.holidayhomes.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apsmobile.holidayhomes.R;
import com.apsmobile.holidayhomes.helper.FirebaseHelper;
import com.apsmobile.holidayhomes.model.Anuncio;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class FormAnuncioActivity extends AppCompatActivity {

    private static final int REQUEST_GALERIA = 100;

    private EditText edit_titulo;
    private EditText edit_descricao;
    private EditText edit_quarto;
    private EditText edit_banheiro;
    private EditText edit_garagem;
    private CheckBox cb_status;

    private ImageView img_anuncio;
    private String caminhoImagem;
    private Bitmap imagem;
    private ProgressBar progressBar;

    private Anuncio anuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_anuncio);

        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            anuncio = (Anuncio) bundle.getSerializable("anuncio");

            configDados();
        }

        configCliques();

    }

    private void configDados() {
        Picasso.get().load(anuncio.getUrlImagem()).into(img_anuncio);
        edit_titulo.setText(anuncio.getTitulo());
        edit_descricao.setText(anuncio.getDescricao());
        edit_quarto.setText(anuncio.getQuarto());
        edit_banheiro.setText(anuncio.getBanheiro());
        edit_garagem.setText(anuncio.getBanheiro());
        cb_status.setChecked(anuncio.isStatus());
    }

    public void verificaPermissaoGaleria(View view) {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FormAnuncioActivity.this, "Permissão Negada.", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissaoGaleria(permissionListener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});

    }

    private void showDialogPermissaoGaleria(PermissionListener listener, String[] permissoes) {
        TedPermission.with(this)
                .setPermissionListener(listener)
                .setDeniedTitle("Permissões negadas.")
                .setDeniedMessage("Você negou as permissões para acessar a galeria do dispositivo, deseja permitir ?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    private void configCliques() {
        findViewById(R.id.ib_salvar).setOnClickListener(view -> validaDados());
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
    }

    private void validaDados() {

        String titulo = edit_titulo.getText().toString();
        String descricao = edit_descricao.getText().toString();
        String quartos = edit_quarto.getText().toString();
        String banheiros = edit_banheiro.getText().toString();
        String garagem = edit_garagem.getText().toString();

        if (!titulo.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (!quartos.isEmpty()) {
                    if (!banheiros.isEmpty()) {
                        if (!garagem.isEmpty()) {

                            if (anuncio == null) anuncio = new Anuncio();
                            anuncio.setIdUsuario(FirebaseHelper.getIdFirebase());
                            anuncio.setTitulo(titulo);
                            anuncio.setDescricao(descricao);
                            anuncio.setQuarto(quartos);
                            anuncio.setBanheiro(banheiros);
                            anuncio.setGaragem(garagem);
                            anuncio.setStatus(cb_status.isChecked());

                            if (caminhoImagem != null) {
                                salvarImagemAnuncio();
                            } else {
                                if (anuncio.getUrlImagem() != null) {
                                    anuncio.salvar();
                                } else {
                                    Toast.makeText(this, "Selecione uma imagem para o anúncio.", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            edit_garagem.requestFocus();
                            edit_garagem.setError("Informação obrigatória.");
                        }
                    } else {
                        edit_banheiro.requestFocus();
                        edit_banheiro.setError("Informação obrigatória.");
                    }
                } else {
                    edit_quarto.requestFocus();
                    edit_quarto.setError("Informação obrigatória.");
                }
            } else {
                edit_descricao.requestFocus();
                edit_descricao.setError("Informe uma descrição.");
            }
        } else {
            edit_titulo.requestFocus();
            edit_titulo.setError("Informe um título.");
        }

    }

    private void salvarImagemAnuncio() {

        progressBar.setVisibility(View.VISIBLE);

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("anuncios")
                .child(anuncio.getId() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {

            String urlImagem = task.getResult().toString();
            anuncio.setUrlImagem(urlImagem);

            anuncio.salvar();

            finish();

        })).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void iniciaComponentes() {
        TextView text_titulo = findViewById(R.id.text_titulo);
        text_titulo.setText("Form anúncio");

        edit_titulo = findViewById(R.id.edit_titulo);
        edit_descricao = findViewById(R.id.edit_descricao);
        edit_quarto = findViewById(R.id.edit_quarto);
        edit_banheiro = findViewById(R.id.edit_banheiro);
        edit_garagem = findViewById(R.id.edit_garagem);
        cb_status = findViewById(R.id.cb_status);
        img_anuncio = findViewById(R.id.img_anuncio);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALERIA) {

                Uri localImagemSelecionada = data.getData();
                caminhoImagem = localImagemSelecionada.toString();

                if (Build.VERSION.SDK_INT < 28) {
                    try {
                        imagem = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), localImagemSelecionada);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localImagemSelecionada);
                    try {
                        imagem = ImageDecoder.decodeBitmap(source);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                img_anuncio.setImageBitmap(imagem);

            }
        }

    }
}
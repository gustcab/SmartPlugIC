package smartplug.smartplug.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import smartplug.smartplug.DAO.ConfiguracaoFirebase;
import smartplug.smartplug.R;
import smartplug.smartplug.entidades.Aparelhos;

import static smartplug.smartplug.R.layout.activity_cadastro_aparelhos;

public class CadastroAparelhos extends AppCompatActivity {

    private Button btnGravar;
    private EditText edtNome, edtIp;
    private Aparelhos aparelhos;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_cadastro_aparelhos);

        edtNome = findViewById(R.id.edtNomeProduto);
        edtIp = findViewById(R.id.edtValorProduto);
        btnGravar = findViewById(R.id.btnGravar);

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aparelhos = new Aparelhos();
                aparelhos.setNome(edtNome.getText().toString());
                aparelhos.setIp(edtIp.getText().toString());
                aparelhos.setStatus("Desativado");
                aparelhos.setPower("OFF");
                aparelhos.setCorrente(0.0);
                aparelhos.setTensao(0.0);
                aparelhos.setPotencia(0.0);
                aparelhos.setPotenciaRela(0.0);
                aparelhos.setPotenciaAlter(0.0);
                aparelhos.setFatorPotencia(0.0);

                salvarProduto(aparelhos);
                limpaCampos();

                voltarHome();

            }
        });

    }


    private boolean salvarProduto (Aparelhos aparelhos){

        try{

            firebase = ConfiguracaoFirebase.getFirebase().child("addaparelho");
            firebase.child(aparelhos.getNome()).setValue(aparelhos);
            Toast.makeText(CadastroAparelhos.this, "Aparelho cadastrado com sucesso", Toast.LENGTH_LONG).show();

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void limpaCampos(){

        edtNome.setText("");
        edtIp.setText("");
    }

    private void voltarHome(){
        Intent intent = new Intent(CadastroAparelhos.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

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
import smartplug.smartplug.entidades.Produtos;

import static smartplug.smartplug.R.layout.activity_cadastro_produtos;

public class CadastroProdutos extends AppCompatActivity {

    private Button btnGravar, btnVoltarTelaInicial;
    private EditText edtNome, edtIp;
    private Produtos produtos;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_cadastro_produtos);

        edtNome = (EditText) findViewById(R.id.edtNomeProduto);
        edtIp = (EditText) findViewById(R.id.edtValorProduto);
        btnGravar = (Button) findViewById(R.id.btnGravar);
       // btnVoltarTelaInicial = (Button) findViewById(R.id.btnVoltarTelaInicial);

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                produtos = new Produtos();
                produtos.setNome(edtNome.getText().toString());
                produtos.setIp(edtIp.getText().toString());
                produtos.setStatus("Desativado");
                produtos.setPower("OFF");
                produtos.setCorrente(0.0);
                produtos.setTensao(0.0);
                produtos.setPotencia(0.0);

                salvarProduto(produtos);
                limpaCampos();

            }
        });

    }


    private boolean salvarProduto (Produtos produtos){

        try{

            firebase = ConfiguracaoFirebase.getFirebase().child("addaparelho");
            firebase.child(produtos.getNome()).setValue(produtos);
            Toast.makeText(CadastroProdutos.this, "Aparelho cadastrado com sucesso", Toast.LENGTH_LONG).show();

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
        Intent intent = new Intent(CadastroProdutos.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

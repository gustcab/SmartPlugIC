package smartplug.smartplug.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import smartplug.smartplug.Adapter.ProdutosAdapter;
import smartplug.smartplug.DAO.ConexaoDispositivo;
import smartplug.smartplug.DAO.ConfiguracaoFirebase;
import smartplug.smartplug.R;
import smartplug.smartplug.entidades.Produtos;

import static android.support.constraint.Constraints.TAG;

public class ProdutosActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<Produtos> adapter;
    private ArrayList<Produtos> produtos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerProdutos;
    private AlertDialog alerta;
    private Produtos excluiAparelho,alteraStatus;
    private DatabaseReference statusDispositivo;
    private String status = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        produtos = new ArrayList<>();

        listView = (ListView) findViewById(R.id.listViewConectados);
        adapter = new ProdutosAdapter(this, produtos);

        listView.setAdapter(adapter);

        firebase = ConfiguracaoFirebase.getFirebase().child("addaparelho");


       valueEventListenerProdutos = new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               produtos.clear();

               for(DataSnapshot dados: dataSnapshot.getChildren())
               {
                   Produtos produtosNovos = dados.getValue(Produtos.class);

                   produtos.add(produtosNovos);
               }

               adapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       };

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


              alteraStatus = adapter.getItem(position);

               //cria o gerador do Alert Dialog

               AlertDialog.Builder builder = new AlertDialog.Builder(ProdutosActivity.this);

               // Define Titulo
               builder.setTitle("Processos");

               //define uma mensagem
               builder.setMessage("Selecione a ação desejada");

               //define botão sim
               builder.setPositiveButton("Alterar Status do aparelho", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       firebase = ConfiguracaoFirebase.getFirebase().child("addaparelho");

                       if(getStatus()) {
                           firebase.child(alteraStatus.getNome()).child("status").setValue("Desativado");
                           Toast.makeText(ProdutosActivity.this,"Aparelho Desativado!", Toast.LENGTH_LONG).show();

                       }else
                       {
                           firebase.child(alteraStatus.getNome()).child("status").setValue("Ativado");
                           Toast.makeText(ProdutosActivity.this,"Aparelho Ativado!", Toast.LENGTH_LONG).show();
                       }

                       Toast.makeText(ProdutosActivity.this,"Alteração efetuada!", Toast.LENGTH_LONG).show();

                   }
               });


               builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       Toast.makeText(ProdutosActivity.this,"Processo Cancelado", Toast.LENGTH_LONG).show();
                   }
               });

               //Criar o alert Diaglog
               alerta = builder.create();

               //exibe alertdialog
               alerta.show();
           }

           private boolean getStatus(){

               statusDispositivo = ConexaoDispositivo.PegaDados(alteraStatus.getNome(),"status");
               statusDispositivo.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       status = dataSnapshot.getValue(String.class);

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {
                       Log.w(TAG, "onCancelled", databaseError.toException());
                   }
               });

               return status.equals("Ativado");

           }
       });


    }

    private void voltarTelaInicial(){
        Intent intent = new Intent(ProdutosActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerProdutos);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerProdutos);
    }
}

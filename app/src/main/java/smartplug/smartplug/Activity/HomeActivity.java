package smartplug.smartplug.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import smartplug.smartplug.DAO.ConexaoDispositivo;
import smartplug.smartplug.DAO.ConfiguracaoFirebase;
import smartplug.smartplug.R;
import smartplug.smartplug.entidades.Usuarios;

import static android.support.constraint.Constraints.TAG;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth usuarioFirebase;
    private DatabaseReference mDatabase;
    private DatabaseReference statusDispositivo;
    private ConexaoDispositivo powerDispositivo;

    private Button btnVerProduto;
    private FloatingActionButton imgAddAparelho;
    private ImageView imglight, imgdados ;
    private TextView forca, dados, guest;
    private AlertDialog alerta;
    private String status = "", power = " ", nomeAparelho = "Teste";


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();
        btnVerProduto = findViewById(R.id.btnVerProduto);
        imgAddAparelho = findViewById(R.id.imgAdicionaaparelho);
        imglight = findViewById(R.id.imglight);
        imgdados = findViewById(R.id.imgDados);
        forca = findViewById(R.id.forca);
        guest = findViewById(R.id.txtguest_home);

        getStatus();
       // validaPower();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

       /* final String user_id = (usuarioFirebase.getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuario").child(user_id).child("nome");

        guest.setText(mDatabase.toString());*/


        imgAddAparelho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduto();

            }
        });

        imgdados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(getStatus()) {
                    Intent intent = new Intent(HomeActivity.this, DadosActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(HomeActivity.this, "Nenhum aparelho conectado no momento", Toast.LENGTH_SHORT).show();
                }

            }
        });

        imglight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getStatus()) {
                    if (forca.getText() == "Ligado") {

                        Drawable drawable = getResources().getDrawable(R.drawable.ic_light_off);
                        imglight.setImageDrawable(drawable);
                        forca.setText("Desligado");

                        powerDispositivo = ConexaoDispositivo.DesligaAparelho(nomeAparelho);
                        //    statusDispositivo = ConexaoDispositivo.DesativaAparelhoFirebase(nomeAparelho);

                        Toast.makeText(HomeActivity.this, "Aparelho desligado", Toast.LENGTH_LONG).show();

                    } else {

                        Drawable drawable = getResources().getDrawable(R.drawable.light);
                        imglight.setImageDrawable(drawable);

                        forca.setText("Ligado");

                       powerDispositivo = ConexaoDispositivo.LigaAparelho(nomeAparelho);
                        // statusDispositivo =  ConexaoDispositivo.AtivaAparelhoFirebase(nomeAparelho);

                        Toast.makeText(HomeActivity.this, "Aparelho ligado", Toast.LENGTH_LONG).show();



                    }
                }
                else
                {
                    Toast.makeText(HomeActivity.this, "Nenhum aparelho conectado no momento", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnVerProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verProduto();
            }
        });
    }

    private boolean getStatus(){

        statusDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"status");
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

    private boolean getPower(){

        statusDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"power");
        statusDispositivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                power = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

        return power.equals("ON");

    }

    private void validaPower(){

        if(getPower()){

            Drawable drawable = getResources().getDrawable(R.drawable.light);
            imglight.setImageDrawable(drawable);
            forca.setText("Ligado");

        }else{
            Drawable drawable = getResources().getDrawable(R.drawable.ic_light_off);
            imglight.setImageDrawable(drawable);
            forca.setText("Desligado");

        }

    }

    private void addProduto(){

        Intent intent = new Intent(HomeActivity.this, CadastroProdutos.class);
        startActivity(intent);

    }

    private void verProduto(){

        Intent intent = new Intent(HomeActivity.this, ProdutosActivity.class);
        startActivity(intent);

    }

    private void abreConfigurção(){

        Intent intent = new Intent(HomeActivity.this, ProdutosActivity.class);
        startActivity(intent);

    }

    private void deslogaUsuario(){
        usuarioFirebase.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void abreHome(){
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void alertaDeslogar(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Define Titulo
        builder.setTitle("SmartPlug");

        //define uma mensagem
        builder.setMessage("Você deseja realmente deslogar de sua conta?");

        //define botão sim
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

              deslogaUsuario();

            }
        });

        //define botao nao
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                abreHome();

            }
        });

        //Criar o alert Diaglog
        alerta = builder.create();

        //exibe alertdialog
        alerta.show();
    }

    private void abrirCompartilhar(){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String texto = "Olá sou um texto compartilhado";
        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            abreHome();

        } else if (id == R.id.nav_sobre2) {

            String url = "http://localhost/sobreProjeto.php";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        } else if (id == R.id.nav_condiguracao2) {

            abreConfigurção();

        } else if (id == R.id.nav_compartilhar2) {

           abrirCompartilhar();

        } else if (id == R.id.nav_deslogar2) {

          alertaDeslogar();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

package smartplug.smartplug.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

import smartplug.smartplug.DAO.ConexaoDispositivo;
import smartplug.smartplug.DAO.ConfiguracaoFirebase;
import smartplug.smartplug.R;
import smartplug.smartplug.entidades.Aparelhos;


public class DadosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth usuarioFirebase;
    private FloatingActionButton ampliar;

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private TextView gasto, corrente, tensao, potencia,
                      potenciaAl, FatPotencia, aparelho;
    private DatabaseReference correnteDispositivo;
    private Double consumoHr = 0.00, correnteEle = 0.00, tensaoEle = 0.00 ,
                   potenciaEle = 0.00, potenciaAlter = 0.00, FatorPot = 0.00;
    private AlertDialog alerta;
    private String nomeAparelho = "SmartPlug";
    private boolean pot = false, potAl = false, fatPot = false, tens = false, corre = false ,com = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados);

        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gasto = findViewById(R.id.txtGasto);
        corrente = findViewById(R.id.txtCorrente);
        tensao = findViewById(R.id.txtTensao);
        potencia = findViewById(R.id.txtPotencia);
        potenciaAl = findViewById(R.id.txtPotenciaAparente);
        FatPotencia = findViewById(R.id.txtFatorPotencia);
        aparelho = findViewById(R.id.txtnomeAparelho);

        aparelho.setText(nomeAparelho);


        @SuppressLint("WrongViewCast") final GraphView graph = findViewById(R.id.grafico_dados);
        //data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        graph.setTitle("Corrente Elétrica(A)");
        graph.setTitleTextSize(40);


        //customize
        final Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(50);
        viewport.setScrollable(true);

        ampliar = findViewById(R.id.ampliar_grafico);
        ampliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abreGrafico();
            }
        });

        gasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com = true;
                tens = false;
                pot = false;
                corre = false;
                potAl = false;
                fatPot = false;

                graph.setTitle("Consumo(Wh/dia)");
           //     viewport.setMaxY(2000);
            }
        });
        corrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com = false;
                tens = false;
                pot = false;
                corre = true;
                potAl = false;
                fatPot = false;

                graph.setTitle("Corrente Elétrica(A)");
              //  viewport.setMaxY(100);
            }
        });
        tensao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com = false;
                tens = true;
                pot = false;
                corre = false;
                potAl = false;
                fatPot = false;

                graph.setTitle("Tensão(V)");
            //    viewport.setMaxY(300);
            }
        });
        potencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com = false;
                tens = false;
                pot = true;
                corre = false;
                potAl = false;
                fatPot = false;

                graph.setTitle("Potência Ativa(W)");
               // viewport.setMaxY(2000);
            }
        });
        potenciaAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com = false;
                tens = false;
                pot = false;
                corre = false;
                potAl = true;
                fatPot = false;

                graph.setTitle("Potência Aparente");
                // viewport.setMaxY(2000);
            }
        });
        FatPotencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com = false;
                tens = false;
                pot = false;
                corre = false;
                potAl = false;
                fatPot = true;

                graph.setTitle("Fator de Potência");
                // viewport.setMaxY(2000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //we´re going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                // we add 100 new entries
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });


                    gasto.setText("" + CalculoConsumo().toString());
                    gasto.setTextColor(Color.BLACK);
                    getCorrente();
                    // sleep to slow down the add of entries
                    verificaCorrente();
                    // sleep to slow down the add of entries

                    getPotencia();
                    // sleep to slow down the add of entries
                    getPotenciaAlternada();
                    // sleep to slow down the add of entries
                    getFatorPotencia();
                    // sleep to slow down the add of entries
                    getTensao();


                    // sleep to slow down the add of entries
                    intervalo(1000);


                }
            }
        }).start();
    }

    private void intervalo(int tempo){

        try {
            Thread.sleep(tempo);
        } catch (InterruptedException e) {
            // manage error ...

        }

    }

    private void getCorrente(){

        correnteDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"corrente");

        ValueEventListener valueEventListener = correnteDispositivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               Double correnteEletrica = dataSnapshot.getValue(Double.class);
               correnteEle = correnteEletrica;

                Log.d("file", "Value is: " + correnteEletrica);

                corrente.setText(String.format("%s", correnteEletrica));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("file", "Failed to read value.", databaseError.toException());
            }

        });
    }

    private void verificaCorrente(){

        if(correnteEle > 20.00){

            corrente.setText(String.format("%s", correnteEle));
            corrente.setTextColor(Color.RED);

        }else{
            corrente.setText(String.format("%s", correnteEle));
            corrente.setTextColor(Color.BLACK);
        }

    }

    private void getPotencia(){

        correnteDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"potencia");

        ValueEventListener valueEventListener = correnteDispositivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double potenciaEletrica = dataSnapshot.getValue(Double.class);
                potenciaEle = potenciaEletrica;

                Log.d("file", "Value is: " + potenciaEletrica);

                potencia.setText(String.format("%s", potenciaEletrica));
                potencia.setTextColor(Color.BLACK);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("file", "Failed to read value.", databaseError.toException());
            }

        });

    }

    private void getPotenciaAlternada(){

        correnteDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"potenciaAlter");

        ValueEventListener valueEventListener = correnteDispositivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double potenciaEletrica = dataSnapshot.getValue(Double.class);
                potenciaAlter = potenciaEletrica;

                Log.d("file", "Value is: " + potenciaEletrica);

                potenciaAl.setText(String.format("%s", potenciaEletrica));
                potenciaAl.setTextColor(Color.BLACK);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("file", "Failed to read value.", databaseError.toException());
            }

        });

    }

    private void getFatorPotencia(){

        correnteDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"fatorPotencia");

        ValueEventListener valueEventListener = correnteDispositivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double fator = dataSnapshot.getValue(Double.class);
                FatorPot = fator;

                Log.d("file", "Value is: " + fator);

                FatPotencia.setText(String.format("%s", fator));
                FatPotencia.setTextColor(Color.BLACK);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("file", "Failed to read value.", databaseError.toException());
            }

        });

    }

    private void getTensao(){

        correnteDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"tensao");

        ValueEventListener valueEventListener = correnteDispositivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double tensao_ele = dataSnapshot.getValue(Double.class);
                tensaoEle = tensao_ele;

                Log.d("file", "Value is: " + tensao_ele);

                tensao.setText(String.format("%s", tensao_ele));
                tensao.setTextColor(Color.BLACK);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("file", "Failed to read value.", databaseError.toException());
            }

        });

    }

    private Double CalculoConsumo(){

        double potencia = potenciaAlter;

        double Consumo = (potencia * 24) / 1000;
        consumoHr = Consumo;

        return Consumo;
    }

    // add random data to graph
    private void addEntry(){

        // here we choose to display max 10 points on the viewport and we scroll to end
       // series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), true, 10);

        if(corre){

            series.appendData(new DataPoint(lastX++, correnteEle), true, 10);
            // sleep to slow down the add of entries
            //intervalo(1000);

        }else if(tens){

            series.appendData(new DataPoint(lastX++, tensaoEle), true, 10);
            // sleep to slow down the add of entries
            //intervalo(1000);

        }else if(pot){

            series.appendData(new DataPoint(lastX++, potenciaEle), true, 10);
            // sleep to slow down the add of entries
            //intervalo(1000);

        }else if(potAl){

            series.appendData(new DataPoint(lastX++, potenciaAlter), true, 10);
            // sleep to slow down the add of entries
            //intervalo(1000);

        }else if(fatPot){

            series.appendData(new DataPoint(lastX++, FatorPot), true, 10);
            // sleep to slow down the add of entries
            //intervalo(1000);

        }else if(com){

            series.appendData(new DataPoint(lastX++, consumoHr), true, 10);
            // sleep to slow down the add of entries
            //intervalo(1000);

        }else{
            series.appendData(new DataPoint(lastX++, correnteEle), true, 10);
            // sleep to slow down the add of entries
            //intervalo(1000);
        }



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dados, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       /* //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home_dados) {

            abreHome();

        } else if (id == R.id.nav_sobre_dados) {

            abreSobre();

        } else if (id == R.id.nav_condiguracao_dados) {
            abreConfiguracao();

        } else if (id == R.id.nav_compartilhar_dados) {

            abrirCompartilhar();

        } else if (id == R.id.nav_deslogar_dados) {

           alertaDeslogar();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void abreConfiguracao(){

        Intent intent = new Intent(DadosActivity.this, AparelhosActivity.class);
        startActivity(intent);

    }

   private void abreSobre(){

       Intent intent = new Intent(DadosActivity.this, SobreActivity.class);
       startActivity(intent);

    }

    private void deslogaUsuario(){
        usuarioFirebase.signOut();
        Intent intent = new Intent(DadosActivity.this, MainActivity.class);
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

            }
        });

        //Criar o alert Diaglog
        alerta = builder.create();

        //exibe alertdialog
        alerta.show();
    }

    private void abreHome(){
        Intent intent = new Intent(DadosActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void abreGrafico(){
        Intent intent = new Intent(DadosActivity.this, GraficoActivity.class);
        startActivity(intent);
    }

    private void abrirCompartilhar(){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String texto = "Olá sou um texto compartilhado";
        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }
}

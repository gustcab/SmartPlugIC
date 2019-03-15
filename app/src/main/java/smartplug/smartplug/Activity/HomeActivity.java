package smartplug.smartplug.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import smartplug.smartplug.DAO.ConexaoDispositivo;
import smartplug.smartplug.DAO.ConfiguracaoFirebase;
import smartplug.smartplug.R;

import static android.support.constraint.Constraints.TAG;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CountDownTimer TimerCountdown;
    private boolean timerAtivo;

    private long StartTimeInMillis;
    private long TimerinMillis;
    private long TempoFinal;

    private FirebaseAuth usuarioFirebase;
    private DatabaseReference mDatabase;
    private DatabaseReference statusDispositivo, nomeUsuario;
    private ConexaoDispositivo powerDispositivo;

    private EditText inputTempo;
    private Button btnVerProduto, btnIniciaTimer, btnReset, btnSetTempo;
    private FloatingActionButton imgAddAparelho;
    private ImageView imglight, imgdados ;
    private TextView forca, dados, guest, txtTimer, txtLigaDesliga;
    private AlertDialog alerta;
    private String status = "", power = " ", nomeUser = " ", nomeAparelho = "Teste";


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
        inputTempo = findViewById(R.id.inputTempo);
        guest = findViewById(R.id.txtguest_home);
        txtTimer = findViewById(R.id.timer);
        txtLigaDesliga = findViewById(R.id.txtLigaDesliga);
        btnIniciaTimer = findViewById(R.id.btnIniciaTimer);
        btnReset = findViewById(R.id.reset);
        btnSetTempo = findViewById(R.id.btnsetMinutos);

        getStatus();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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

               PowerAparelho();

            }
        });

        btnVerProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verProduto();
            }
        });

        btnSetTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputTempo.getText().toString();
                if(input.length()==0){
                    Toast.makeText(HomeActivity.this,"O campo não pode estar vazio",Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(input) * 60000;
                if(millisInput == 0){
                    Toast.makeText(HomeActivity.this, "Use um valor maior do que zero !", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTempo(millisInput);
                btnIniciaTimer.setVisibility(View.VISIBLE);
                txtTimer.setVisibility(View.VISIBLE);
                inputTempo.setText("");

            }
        });

        btnIniciaTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(timerAtivo)
                {
                    pausarTimer();

                }else{
                    iniciaTimer();
                }

            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetTimer();
            }
        });

        atualizaContagem();
    }

    private void setTempo(long millisegunos){
        StartTimeInMillis = millisegunos;
        resetTimer();
        closeKeyboard();

    }

    private void iniciaTimer()
    {
        TimerCountdown = new CountDownTimer(TimerinMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimerinMillis = millisUntilFinished;
                atualizaContagem();

            }

            @Override
            public void onFinish() {

                timerAtivo = false;
                btnIniciaTimer.setText("Iniciar");
                atualizaBotoes();
                PowerAparelho();

            }
        }.start();

        timerAtivo = true;
        btnIniciaTimer.setText("Pausar");
        btnReset.setVisibility(View.INVISIBLE);
    }

    private void atualizaBotoes(){
        if(timerAtivo){
            inputTempo.setVisibility(View.INVISIBLE);
            btnSetTempo.setVisibility(View.INVISIBLE);
            btnReset.setVisibility(View.INVISIBLE);
            btnIniciaTimer.setText("Pausar");
        }else{
            inputTempo.setVisibility(View.VISIBLE);
            btnSetTempo.setVisibility(View.VISIBLE);

            btnIniciaTimer.setText("Iniciar");

            if(TimerinMillis < 1000){
                btnIniciaTimer.setVisibility(View.INVISIBLE);
            }else{
                btnIniciaTimer.setVisibility(View.VISIBLE);
            }

            if(TimerinMillis < StartTimeInMillis){
                btnReset.setVisibility(View.VISIBLE);
            }else{
                btnReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = getSharedPreferences("preferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong("Inicio tempo", StartTimeInMillis);
        editor.putLong("tempo restante",  TimerinMillis);
        editor.putBoolean("Tempo ativo", timerAtivo);
        editor.putLong("final", TempoFinal);

        editor.apply();

        if(TimerCountdown !=null)
        {
            TimerCountdown.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        StartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        TimerinMillis = prefs.getLong("millisLeft", TimerinMillis);
        timerAtivo = prefs.getBoolean("timerRunning", false);

        atualizaContagem();
        atualizaBotoes();

        if (timerAtivo) {
            TempoFinal = prefs.getLong("endTime", 0);
            TimerinMillis = TempoFinal - System.currentTimeMillis();

            if (TimerinMillis < 0) {
                TimerinMillis = 0;
                timerAtivo = false;
                atualizaBotoes();
                atualizaContagem();
            } else {
              iniciaTimer();
            }
        }
    }

    private void atualizaContagem()
    {
        int horas = (int) (TimerinMillis / 1000) / 3600;
        int minuto = (int) ((TimerinMillis / 1000) % 3600) / 60;
        int segundo = (int) (TimerinMillis / 1000) % 60;

        String tempo;
        if(horas > 0){
            tempo = String.format(Locale.getDefault(), "%d:%02d:%02d",horas, minuto, segundo);

        }else{
            tempo = String.format(Locale.getDefault(), "%02d:%02d", minuto, segundo);
        }

        txtTimer.setText(tempo);

    }

    private void resetTimer()
    {
        TimerinMillis = StartTimeInMillis;
        atualizaContagem();
        atualizaBotoes();
    }

    private void pausarTimer()
    {
        TimerCountdown.cancel();
        timerAtivo = false;
        atualizaBotoes();
    }

    private void PowerAparelho()
    {
        if(getStatus()) {
            if (forca.getText() == "Ligado") {
                // if(validaPower()){

                Drawable drawable = getResources().getDrawable(R.drawable.ic_light_off);
                imglight.setImageDrawable(drawable);
                forca.setText("Desligado");

                powerDispositivo = ConexaoDispositivo.DesligaAparelho(nomeAparelho);
                //    statusDispositivo = ConexaoDispositivo.DesativaAparelhoFirebase(nomeAparelho);

                Toast.makeText(HomeActivity.this, "Aparelho desligado", Toast.LENGTH_LONG).show();

                txtLigaDesliga.setText("Ligar em");

            } else {

                Drawable drawable = getResources().getDrawable(R.drawable.light);
                imglight.setImageDrawable(drawable);

                forca.setText("Ligado");

                powerDispositivo = ConexaoDispositivo.LigaAparelho(nomeAparelho);
                // statusDispositivo =  ConexaoDispositivo.AtivaAparelhoFirebase(nomeAparelho);

                Toast.makeText(HomeActivity.this, "Aparelho ligado", Toast.LENGTH_LONG).show();

                txtLigaDesliga.setText("Desligar em");


            }
        }
        else
        {
            Toast.makeText(HomeActivity.this, "Nenhum aparelho conectado no momento", Toast.LENGTH_SHORT).show();
        }

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

    private String getNomeUsuario(){

        nomeUsuario = ConexaoDispositivo.PegaDados(nomeAparelho,"Nome");
        nomeUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                nomeUser = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

        return nomeUser;

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

    private boolean validaPower(){

        statusDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"power");

        if( statusDispositivo.equals("ON")){

            Drawable drawable = getResources().getDrawable(R.drawable.light);
            imglight.setImageDrawable(drawable);
            forca.setText("Ligado");

            return true;

        }else{
            Drawable drawable = getResources().getDrawable(R.drawable.ic_light_off);
            imglight.setImageDrawable(drawable);
            forca.setText("Desligado");
            return false;

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

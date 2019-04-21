package smartplug.smartplug.Activity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import smartplug.smartplug.DAO.ConexaoDispositivo;
import smartplug.smartplug.R;

import static android.support.constraint.Constraints.TAG;

public class TimerActivity extends AppCompatActivity {
    private FirebaseAuth usuarioFirebase;
    private DatabaseReference mDatabase;
    private DatabaseReference statusDispositivo;
    private ConexaoDispositivo powerDispositivo;

    private CountDownTimer TimerCountdown;
    private boolean timerAtivo;

    private long StartTimeInMillis;
    private long TimerinMillis;
    private long TempoFinal;
    private String status = "", power = " ", nomeUser = " ", nomeAparelho = "Teste";

    private EditText inputTempo;
    private TextView txtTimer;
    private TextView forca, txtLigaDesliga;
    private Button btnIniciaTimer, btnReset, btnSetTempo;
    private ImageView imglight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        imglight = findViewById(R.id.imglight);
        forca = findViewById(R.id.forca);
        txtLigaDesliga = findViewById(R.id.txtLigaDesliga);
        txtTimer = findViewById(R.id.timer);
        btnIniciaTimer = findViewById(R.id.btnIniciaTimer);
        btnReset = findViewById(R.id.reset);
        btnSetTempo = findViewById(R.id.btnsetMinutos);
        inputTempo = findViewById(R.id.inputTempo);

        btnSetTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputTempo.getText().toString();
                if(input.length()==0){
                    Toast.makeText(TimerActivity.this,"O campo não pode estar vazio",Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(input) * 60000;
                if(millisInput == 0){
                    Toast.makeText(TimerActivity.this, "Use um valor maior do que zero !", Toast.LENGTH_SHORT).show();
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

    private void PowerAparelho()
    {
        if(getStatus()) {
            if (forca.getText() == "Ligado") {
                // if(validaPower()){

                Drawable drawable = getResources().getDrawable(R.drawable.ic_light_off);
                imglight.setImageDrawable(drawable);
                forca.setText("Desligado");

                ConexaoDispositivo.DesligaAparelho(nomeAparelho);
                //    statusDispositivo = ConexaoDispositivo.DesativaAparelhoFirebase(nomeAparelho);

                Toast.makeText(TimerActivity.this, "Aparelho desligado", Toast.LENGTH_LONG).show();

                txtLigaDesliga.setText("Ligar em");

            } else {

                Drawable drawable = getResources().getDrawable(R.drawable.light);
                imglight.setImageDrawable(drawable);

                forca.setText("Ligado");

                ConexaoDispositivo.LigaAparelho(nomeAparelho);
                // statusDispositivo =  ConexaoDispositivo.AtivaAparelhoFirebase(nomeAparelho);

                Toast.makeText(TimerActivity.this, "Aparelho ligado", Toast.LENGTH_LONG).show();

                txtLigaDesliga.setText("Desligar em");


            }
        }
        else
        {
            Toast.makeText(TimerActivity.this, "Nenhum aparelho conectado no momento", Toast.LENGTH_SHORT).show();
        }

    }
}

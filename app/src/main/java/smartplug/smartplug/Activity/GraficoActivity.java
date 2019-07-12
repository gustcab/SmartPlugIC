package smartplug.smartplug.Activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

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
import smartplug.smartplug.R;

public class GraficoActivity extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private DatabaseReference correnteDispositivo;
    private Double consumoHr = 0.00, correnteEle = 0.00, tensaoEle = 0.00 ,
                  potenciaEle = 0.00, potenciaAlter = 0.00, FatorPot = 0.00;

    private String nomeAparelho = "SmartPlug";

    private TextView gasto, corrente, tensao, potencia, gastoLabel, correnteLabel,
                     tensaoLabel, potenciaLabel, potenciaAl, FatPotencia,
                     potenciaAlLabel, FatPotenciaLabel,aparelho;
    private boolean pot = false,tens = false,corre = false ,com = false, potAl = false, fatPot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);


        gasto = findViewById(R.id.txtGasto_Grafico);
        corrente = findViewById(R.id.txtCorrente_Grafico);
        tensao = findViewById(R.id.txtTensao_Grafico);
        potencia = findViewById(R.id.txtPotencia_Grafico);
        potenciaAl = findViewById(R.id.txtPotenciaAparente_Grafico);
        FatPotencia = findViewById(R.id.txtFatorPotencia_Grafico);

        gastoLabel = findViewById(R.id.txt_LabelGasto_Grafico);
        correnteLabel = findViewById(R.id.txtLabel_Corrente_Grafico);
        tensaoLabel = findViewById(R.id.txtLabel_Tensao_Grafico);
        potenciaLabel =  findViewById(R.id.txtLabel_Potencia_Grafico);
        potenciaAlLabel =  findViewById(R.id.txtLabel_PotenciaAparente_Grafico);
        FatPotenciaLabel =  findViewById(R.id.txtLabel_FatorPotencia_Grafico);

        aparelho = findViewById(R.id.txtnomeAparelho);

        aparelho.setText(nomeAparelho);

        @SuppressLint("WrongViewCast") final GraphView graph = (GraphView) findViewById(R.id.grafico_inteiro);
        //data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        graph.setTitle("Corrente Elétrica(A)");
        graph.setTitleTextSize(50);

        //customize
        final Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(200);
        viewport.setScrollable(true);

         pegaDados();

        gasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com = true;
                tens = false;
                pot = false;
                corre = false;

                gastoLabel.setTextColor(Color.rgb(142,3,180));
                correnteLabel.setTextColor(Color.BLACK);
                tensaoLabel.setTextColor(Color.BLACK);
                potenciaLabel.setTextColor(Color.BLACK);
                FatPotenciaLabel.setTextColor(Color.BLACK);
                potenciaAlLabel.setTextColor(Color.BLACK);


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

                gastoLabel.setTextColor(Color.BLACK);
                correnteLabel.setTextColor(Color.rgb(142,3,180));
                tensaoLabel.setTextColor(Color.BLACK);
                potenciaLabel.setTextColor(Color.BLACK);
                FatPotenciaLabel.setTextColor(Color.BLACK);
                potenciaAlLabel.setTextColor(Color.BLACK);

                graph.setTitle("Corrente Elétrica(A)");
                //  viewport.setMaxY(100);
            }
        });
        tensao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com = true;
                tens = false;
                pot = false;
                corre = false;

                gastoLabel.setTextColor(Color.BLACK);
                correnteLabel.setTextColor(Color.BLACK);
                tensaoLabel.setTextColor(Color.rgb(142,3,180));
                potenciaLabel.setTextColor(Color.BLACK);
                FatPotenciaLabel.setTextColor(Color.BLACK);
                potenciaAlLabel.setTextColor(Color.BLACK);

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

              // potenciaLabel.setTextColor(Color.GREEN);
                gastoLabel.setTextColor(Color.BLACK);
                correnteLabel.setTextColor(Color.BLACK);
                tensaoLabel.setTextColor(Color.BLACK);
                FatPotenciaLabel.setTextColor(Color.BLACK);
                potenciaAlLabel.setTextColor(Color.BLACK);
                potenciaLabel.setTextColor(Color.rgb(142,3,180));

                graph.setTitle("Potência Ativa(W)");
                //  viewport.setMaxY(2000);
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

                gastoLabel.setTextColor(Color.BLACK);
                correnteLabel.setTextColor(Color.BLACK);
                tensaoLabel.setTextColor(Color.BLACK);
                potencia.setTextColor(Color.BLACK);
                FatPotenciaLabel.setTextColor(Color.BLACK);
                potenciaAlLabel.setTextColor(Color.rgb(142,3,180));

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

                gastoLabel.setTextColor(Color.BLACK);
                correnteLabel.setTextColor(Color.BLACK);
                tensaoLabel.setTextColor(Color.BLACK);
                potencia.setTextColor(Color.BLACK);
                potenciaAlLabel.setTextColor(Color.BLACK);
                FatPotenciaLabel.setTextColor(Color.rgb(142,3,180));

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
            @Override
            public void run() {

              //  geraGrafico();

                // we add 100 new entries
                while(true){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    pegaDados();
                    gasto.setText(String.format("%s", consumoHr));


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

                corrente.setText(String.format("%s", correnteEle));

                Log.d("file", "Value is: " + correnteEletrica);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("file", "Failed to read value.", databaseError.toException());
            }

        });
    }

    private void pegaDados(){
        getPotencia();
         intervalo(100);
        getPotenciaAlternada();
         intervalo(100);
        getFatorPotencia();
         intervalo(100);
        getCorrente();
          intervalo(100);
        getTensao();
          intervalo(100);
        consumoHr = CalculoConsumo();
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

                potencia.setText(String.format("%s", potenciaEle));

                Log.d("file", "Value is: " + potenciaEletrica);

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

    private void getTensao() {

        correnteDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho, "tensao");

        ValueEventListener valueEventListener = correnteDispositivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double tensao_ele = dataSnapshot.getValue(Double.class);
                tensaoEle = tensao_ele;

                tensao.setText(String.format("%s", tensao_ele));

                Log.d("file", "Value is: " + tensao_ele);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("file", "Failed to read value.", databaseError.toException());
            }

        });
    }

    private Double CalculoConsumo(){

        double potencia = potenciaEle;

        double Consumo = (potencia * 24) / 1000;

        return Consumo;
    }

    // add random data to graph
    private void addEntry(){

        // here we choose to display max 10 points on the viewport and we scroll to end
       // series.appendData(new DataPoint(lastX++, correnteEle), true, 10);

        if(corre){

            series.appendData(new DataPoint(lastX++, correnteEle), true, 10);
            intervalo(1000);

        }else if(tens){

            series.appendData(new DataPoint(lastX++, tensaoEle), true, 10);
            intervalo(1000);

        }else if(potAl){

            series.appendData(new DataPoint(lastX++, potenciaAlter), true, 10);
            intervalo(1000);

        }else if(fatPot){

            series.appendData(new DataPoint(lastX++, FatorPot), true, 10);
            intervalo(1000);

        }else if(pot){

            series.appendData(new DataPoint(lastX++, potenciaEle), true, 10);
            intervalo(1000);

        }else if(com){

            series.appendData(new DataPoint(lastX++, consumoHr), true, 10);
            intervalo(1000);

        }else{
            series.appendData(new DataPoint(lastX++, correnteEle), true, 10);
            intervalo(1000);
        }


    }


}

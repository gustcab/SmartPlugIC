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

    private LinearLayout ll;
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private DatabaseReference correnteDispositivo;
    private Double consumoHr = 0.00, correnteEle = 0.00, tensaoEle = 0.00 , potenciaEle = 0.00;
    private RadioButton radPotencia,radTensao,radConsumo,radCorrente;
    private String nomeAparelho = "Teste";
    private TextView gasto, corrente, tensao, potencia, gastoLabel, correnteLabel, tensaoLabel, potenciaLabel;
    private boolean pot = false,tens = false,corre = false ,com = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);


        gasto = (TextView) findViewById(R.id.txtGasto_Grafico);
        corrente = (TextView) findViewById(R.id.txtCorrente_Grafico);
        tensao = (TextView) findViewById(R.id.txtTensao_Grafico);
        potencia = (TextView) findViewById(R.id.txtPotencia_Grafico);

        gastoLabel = (TextView) findViewById(R.id.txt_LabelGasto_Grafico);
        correnteLabel = (TextView) findViewById(R.id.txtLabel_Corrente_Grafico);
        tensaoLabel = (TextView) findViewById(R.id.txtLabel_Tensao_Grafico);
        potenciaLabel = (TextView) findViewById(R.id.txtLabel_Potencia_Grafico);

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
        viewport.setMaxY(100);
        viewport.setScrollable(true);

         pegaDados();
        // geraGrafico();

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
                potenciaLabel.setTextColor(Color.rgb(142,3,180));

                graph.setTitle("Potência Elétrica(W)");
                //  viewport.setMaxY(2000);
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
                for(int i = 0;  i >= 0 ; i++){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    pegaDados();
                    gasto.setText(""+consumoHr);


                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // manage error ...

                    }
                }
            }
        }).start();
    }

    private void getCorrente(){

        correnteDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"corrente");

        ValueEventListener valueEventListener = correnteDispositivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double correnteEletrica = dataSnapshot.getValue(Double.class);
                correnteEle = correnteEletrica;

                corrente.setText("" + correnteEle);

                Log.d("file", "Value is: " + correnteEletrica);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("file", "Failed to read value.", databaseError.toException());
            }

        });
    }

    private void geraGrafico(){

        @SuppressLint("WrongViewCast") final GraphView graph = (GraphView) findViewById(R.id.grafico_inteiro);
        //data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        graph.setTitle("Corrente Elétrica(A)");

        //customize
        final Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setScrollable(true);


        /*
       if(radCorrente.isChecked()){

            graph.setTitle("Corrente Elétrica(A)");
            viewport.setMaxY(10);

        }else if(radTensao.isChecked()){

           graph.setTitle("Tensão Elétrica(V)");
           viewport.setMaxY(10);

       }else if(radPotencia.isChecked()){

           graph.setTitle("Potência Elétrica(W)");
           viewport.setMaxY(10);

       }else if(radConsumo.isChecked()){

           graph.setTitle("Consumo (Wh/dia)");
           viewport.setMaxY(10);

       }else
       {
           graph.setTitle("Corrente Elétrica(A)");
           viewport.setMaxY(10);
       }
       */
    }

    private void pegaDados(){
        getPotencia();
        getCorrente();
        getTensao();
        consumoHr = CalculoConsumo();
    }

    private void getPotencia(){

        correnteDispositivo = ConexaoDispositivo.PegaDados(nomeAparelho,"potencia");

        ValueEventListener valueEventListener = correnteDispositivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double potenciaEletrica = dataSnapshot.getValue(Double.class);
                potenciaEle = potenciaEletrica;

                potencia.setText(""+ potenciaEle);

                Log.d("file", "Value is: " + potenciaEletrica);

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

                tensao.setText(""+ tensao_ele);

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

        double Consumo = potencia * 24;


        return Consumo;
    }

    // add random data to graph
    private void addEntry(){

        // here we choose to display max 10 points on the viewport and we scroll to end
       // series.appendData(new DataPoint(lastX++, correnteEle), true, 10);

        if(corre){

            series.appendData(new DataPoint(lastX++, correnteEle), true, 10);

        }else if(tens){

            series.appendData(new DataPoint(lastX++, tensaoEle), true, 10);

        }else if(pot){

            series.appendData(new DataPoint(lastX++, potenciaEle), true, 10);

        }else if(com){

            series.appendData(new DataPoint(lastX++, consumoHr), true, 10);

        }else{
            series.appendData(new DataPoint(lastX++, correnteEle), true, 10);
        }


    }
}

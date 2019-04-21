package smartplug.smartplug.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import smartplug.smartplug.R;
import smartplug.smartplug.entidades.Aparelhos;

public class ProdutosAdapter extends ArrayAdapter<Aparelhos> {

    private ArrayList<Aparelhos> produto;
    private Context context;

    public ProdutosAdapter(Context c, ArrayList<Aparelhos> objects){
        super(c, 0, objects);
        this.context = c;
        this.produto = objects;
    }


    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        View view = null;

        if(produto !=null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_conectados, parent, false);

            TextView txtNome = (TextView) view.findViewById(R.id.txtNome);
            TextView txtIp = (TextView) view.findViewById(R.id.txtIp);
            TextView txtStatus = (TextView) view.findViewById(R.id.txtStatus);

            Aparelhos produto2 = produto.get(position);

            txtIp.setTextSize(30);
            txtNome.setTextSize(30);
            txtStatus.setTextSize(20);

            txtNome.setText("Aparelho: " + produto2.getNome());
            txtIp.setText("IP: " + produto2.getIp());
            txtStatus.setText("Status: " + produto2.getStatus());

        }
        return view;
    }



}

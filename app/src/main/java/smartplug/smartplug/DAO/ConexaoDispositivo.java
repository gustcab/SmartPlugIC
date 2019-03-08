package smartplug.smartplug.DAO;

import com.google.firebase.database.DatabaseReference;

public class ConexaoDispositivo{

    private static DatabaseReference deviceStatus, deviceDados, devicePower;
    private String nomeAparelho;
    private String status;


    public static ConexaoDispositivo  AtivaAparelhoFirebase(String aparelho){

        deviceStatus = ConfiguracaoFirebase.getFirebase().child("addaparelho").child(aparelho).child("status");
        deviceStatus.setValue("Ativado");

        return null;

    }

    public static ConexaoDispositivo DesativaAparelhoFirebase(String aparelho){

        deviceStatus = ConfiguracaoFirebase.getFirebase().child("addaparelho").child(aparelho).child("status");
        deviceStatus.setValue("Desativado");

        return null;
    }

    public static ConexaoDispositivo LigaAparelho(String aparelho){

            devicePower = ConfiguracaoFirebase.getFirebase().child("addaparelho").child(aparelho).child("power");
            devicePower.setValue("ON");
            return null;

    }

    public static ConexaoDispositivo DesligaAparelho(String aparelho){
        devicePower = ConfiguracaoFirebase.getFirebase().child("addaparelho").child(aparelho).child("power");
        devicePower.setValue("OFF");

        return null;
    }


    public static DatabaseReference PegaDados(String aparelho,String dado) {

        final Double[] corrente = new Double[1];
        corrente[0] = 0.00;

        deviceDados = ConfiguracaoFirebase.getFirebase().child("addaparelho").child(aparelho).child(dado);

        return deviceDados;

    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if(produto !=null)
        {
            Produtos produto2 = produto.get(position);
            nomeAparelho = produto2.getNome();

        }
            return view;
        }
        */

}

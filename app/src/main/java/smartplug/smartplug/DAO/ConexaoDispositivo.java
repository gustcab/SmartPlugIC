package smartplug.smartplug.DAO;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class ConexaoDispositivo{

    private static DatabaseReference deviceStatus, deviceDados, devicePower;

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

    public static void LigaAparelho(String aparelho){

        devicePower = ConfiguracaoFirebase.getFirebase().child("addaparelho").child(aparelho).child("power");
        devicePower.setValue("ON");

    }

    public static void DesligaAparelho(String aparelho){

        devicePower = ConfiguracaoFirebase.getFirebase().child("addaparelho").child(aparelho).child("power");
        devicePower.setValue("OFF");


    }

    public static DatabaseReference PegaDados(String aparelho,String dado) {

        deviceDados = ConfiguracaoFirebase.getFirebase().child("addaparelho").child(aparelho).child(dado);

        return deviceDados;

    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if(produto !=null)
        {
            Aparelhos produto2 = produto.get(position);
            nomeAparelho = produto2.getNome();

        }
            return view;
        }
        */

}

package smartplug.smartplug.entidades;

public class Produtos {

    private String nome;
    private String ip;
    private String power;
    private String status;
    private Double corrente;
    private Double tensao;
    private Double potencia;


    public Produtos() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public Double getCorrente() {
        return corrente;
    }

    public void setCorrente(Double corrente) {
        this.corrente = corrente;
    }

    public Double getTensao() {
        return tensao;
    }

    public void setTensao(Double tensao) {
        this.tensao = tensao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPotencia() {
        return potencia;
    }

    public void setPotencia(Double potencia) {
        this.potencia = potencia;
    }
}

package com.auditoria.cic.datosJSON;

public class Archivo {
    private String archivo;
    private String tipo;
    private String nombre;
    private String fecha;
    private int tipoPDF;
    public String getArchivo() {
        return archivo;
    }
    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public int getTipoPDF() {
        return tipoPDF;
    }
    public void setTipoPDF(int tipoPDF) {
        this.tipoPDF = tipoPDF;
    }
    
}

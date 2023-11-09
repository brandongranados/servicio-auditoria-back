package com.auditoria.cic;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.HashMap;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;

import com.auditoria.cic.datosJSON.Archivo;

public class CargarDoc {
    static String RUTA_EXCEL = "/home/archivos/excel/";
    static String CSV = "csv";

    private HashMap<String, Object> rep;
    private DataOutputStream sal;
    private Archivo obj;
    private int estado;
    private String nombre;

    public CargarDoc()
    {
        sal = null;
        estado = 0;
        rep = new HashMap<String, Object>();
    }
    public void setArchivo(Archivo obj)
    {
        this.obj = obj;
    }
    public HashMap<String, Object> getRespuesta()
    {
        setNombre();
        if( validaDoc() )
            cargarDoc();
        return rep;
    }
    public int getEstado()
    {
        return estado;
    }
    public String getNombre()
    {
        return nombre;
    }

    private void setNombre()
    {
        //Digest digesto = new SHA512Digest();
        //byte entrada[] = obj.getNombre().getBytes();
        //byte sal[] = new byte[digesto.getDigestSize()];
        
        //digesto.update(entrada, 0, entrada.length);
        //digesto.doFinal(sal, 0);
        
        //this.nombre = new String(sal);
        if( obj.getTipo().equals(CSV) )
            this.nombre = obj.getNombre();
    }
    private boolean validaDoc()
    {
        if( !obj.getTipo().equals(CSV) )
        {
            rep.put("msm", "Archivo no valido verificar tipo de archivo admitido");
            estado = 400;
            return false;
        }

        if( obj.getArchivo() == null || obj.getArchivo().equals("") )
        {
            rep.put("msm", "No fue posible procesar su operacion");
            estado = 400;
            return false;
        }

        return true;
    }
    private void cargarDoc()
    {
        try {
            sal = new DataOutputStream(new FileOutputStream(RUTA_EXCEL+nombre));
            sal.write(Base64.getDecoder().decode(obj.getArchivo()));
            sal.close();
            rep.put("msm", "Operacion realizada con exito");
            estado = 200;
        } catch (Exception e) {
            e.printStackTrace();
            rep.put("msm", "No fue posible procesar su operacion");
            estado = 400;
        }
        finally
        {
            try {
                sal.close();
            } catch (Exception e) {}
        }
    }
}

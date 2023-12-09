package com.auditoria.cic.ReportesParam;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;

public class TiposDoc {
    static String ESTUDIANTE ="comunidadestudiantil";
    static String DOCENTE ="comunidaddocente";
    static String PAAE ="comunidadpaae";
    static String EGRESADOS ="comunidadegresados";

    static String PRESTAMO_SALA = "prestamodelibrosensala";
    static String PRESTAMO_DOMICILIO = "prestamodelibrosadomicilio";
    static String PRESTAMO_INTERBIBLIO = "prestamointerbibliotecario";
    static String PRESTAMO_COMPUTO = "prestamodeequipodecomputo";
    static String BASE_DATOS = "accesoabasededatos";
    static String BIBLIO_VIRTUAL = "bibliotecavirtualbobenlinea";

    static String FORMATO_FECHA_ENTRADA = "yyyy/MM/dd";
    static String FORMATO_FECHA_SALIDA = "dd 'de' MMMM 'de' yyyy";
    static String FILTRAR_FECHA = " ";

    static int FOLIO_TAM = 4;

    private HashMap<String, Object> param;
    private String variable;

    public TiposDoc(String per)
    {
        param = new HashMap<String, Object>();
        param.put("FolioAno", per);
    }

    public HashMap<String, Object> getParametros()
    {
        return param;
    }
    public void setVariable(String variable)
    {
        this.variable = variable;
    }
    public void setFolio(String val)
    {
        while( val.length() < FOLIO_TAM )
            val = "0"+val;
        param.put("Folio", val);
    }
    public void setFechaCompleta(String val)
    {
        param.put("FechaCompleta", val);
    }
    public void setComentarios(String val)
    {
        param.put("Comentarios", val);
    }
    public void setPregunta5(String pregunta)
    {
        param.put("Pregunta5", pregunta);
    }
    public void setServicio(String servicio)
    {
        servicio = resetCadena(servicio);
        
        if( servicio.equals(PRESTAMO_SALA) )
            param.put("S1", "x");
        else if( servicio.equals(PRESTAMO_DOMICILIO ) )
            param.put("S2", "x");
        else if( servicio.equals(PRESTAMO_INTERBIBLIO) )
            param.put("S3", "x");
        else if( servicio.equals(PRESTAMO_COMPUTO) )
            param.put("S4", "x");
        else if( servicio.equals(BASE_DATOS) )
            param.put("S5", "x");
        else if( servicio.equals(BIBLIO_VIRTUAL) )
            param.put("S6", "x");
    }
    public void setSatisfaccionEspecifico(String seleccionada, String regCol)
    {
        param.put(variable+regCol, seleccionada);
    }

    public void setOtrosParametros(int seleccionada, int renglon, int satisfacionMax)
    {
        satisfacionMax ++;
        for(int f = 1; f< satisfacionMax; f++)
            if( f == seleccionada )
                param.put(variable+renglon+f, "x");
            else
                param.put(variable+renglon+f, "");
    }
    public void setTipoComunidad(String seleccionada)
    {
        seleccionada = resetCadena(seleccionada);

        if( seleccionada.equals(ESTUDIANTE) )
            param.put("C1", "x");
        else if( seleccionada.equals(DOCENTE) )
            param.put("C2", "x");
        else if( seleccionada.equals(PAAE) )
            param.put("C3", "x");
        else if( seleccionada.equals(EGRESADOS) )
            param.put("C4", "x");
    }

    public String getConvertFecha(String fecha)
    {
        String sal = "";
        String arrayFecha[] = null;
        SimpleDateFormat formatoEnt = new SimpleDateFormat(FORMATO_FECHA_ENTRADA);
        SimpleDateFormat formatoSal = new SimpleDateFormat(FORMATO_FECHA_SALIDA);
        Date fechaEnt = null;
        try {
            arrayFecha = fecha.split(FILTRAR_FECHA);
            fechaEnt = formatoEnt.parse(arrayFecha[0]);
            sal = formatoSal.format(fechaEnt);
        } catch (Exception e) {
        e.printStackTrace();
        }
        return sal;
    }
    public String getConvertFecha(String fecha, String format)
    {
        String sal = "";
        String arrayFecha[] = null;
        SimpleDateFormat formatoEnt = new SimpleDateFormat(FORMATO_FECHA_ENTRADA);
        SimpleDateFormat formatoSal = new SimpleDateFormat(format);
        Date fechaEnt = null;
        try {
            arrayFecha = fecha.split(FILTRAR_FECHA);
            fechaEnt = formatoEnt.parse(arrayFecha[0]);
            sal = formatoSal.format(fechaEnt);
        } catch (Exception e) {
        e.printStackTrace();
        }
        return sal;
    }

    private String resetCadena(String cadena)
    {
        cadena = Normalizer.normalize(cadena, Normalizer.Form.NFD);
        cadena = cadena.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        cadena = cadena.replaceAll(" ", "").toLowerCase();
        return cadena;
    }
}

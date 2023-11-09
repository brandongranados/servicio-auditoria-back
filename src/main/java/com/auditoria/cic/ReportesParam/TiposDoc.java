package com.auditoria.cic.ReportesParam;

import java.util.HashMap;

public class TiposDoc {
    private HashMap<String, Object> param;
    private String variable;

    public TiposDoc()
    {
        param = new HashMap<String, Object>();
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
        param.put("Folio", val);
    }
    public void setFolioAno(String val)
    {
        param.put("FolioAno", val);
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
    public void setServicio(int servicio)
    {
        param.put(variable+servicio, "x");
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
    public void setTipoComunidad(int seleccionada)
    {
        for(int f = 1; f< 5; f++)
            if( f == seleccionada )
                param.put("C"+f, "x");
            else
                param.put("C"+f, "");
    }
}

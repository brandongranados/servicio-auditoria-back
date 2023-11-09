package com.auditoria.cic;

import java.io.File;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class ReportePDF {
    static String RUTA_PDF = "/home/archivos/pdf/";
    static String JASPER1 = "/home/archivos/config/cosie.jasper";
    static String JASPER2 = "/home/archivos/config/biblioteca.jasper";
    static String JASPER3 = "/home/archivos/config/culturales.jasper";
    static String JASPER4 = "/home/archivos/config/pretamo.jasper";
    static String DOC1 = "Elaboración de Dictámenes COSIE ante el CTCE ";

    public void genrarReporte(HashMap<String, Object> parameters, int tipo)
    {
        String nombreArch = crearRutaDocuemnto(tipo, (String)parameters.get("FolioAno"), (String)parameters.get("Folio"))+".pdf";
        JasperReport jasperReport = null;
        JasperPrint jasperPrint = null;
        try {
            jasperReport = (JasperReport) JRLoader.loadObjectFromFile(archivoJasper(tipo));
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, nombreArch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String crearRutaDocuemnto(int tipo, String periodo, String nombre)
    {
        String absoluta = rutaPeriodoDoc(rutaTipoReporte(tipo), periodo);

        return absoluta+nombre;
    }
    private String rutaPeriodoDoc(String histRuta, String periodo)
    {
        String retorno = histRuta+periodo;
        File dir = new File(retorno);

        if( !dir.exists() )
            if( dir.mkdirs() )
                return retorno+File.separator;
            else
                return histRuta;
        else
            return retorno+File.separator;

    }

    private String rutaTipoReporte(int tipo)
    {
        String carpRepor = tipoDoc(tipo);
        File dir = new File(RUTA_PDF+carpRepor);
        if( !dir.exists() )
            if( dir.mkdirs() )
                carpRepor = RUTA_PDF+carpRepor+File.separator;
            else
                carpRepor = RUTA_PDF;
        else
            carpRepor = RUTA_PDF+carpRepor+File.separator;

        return carpRepor;      
    }

    private String tipoDoc(int tipo)
    {
        String retorno = "";
        switch (tipo) 
        {
            case 0:
                retorno = "COSIE";
                break;
            case 1:
                retorno  = "BIBLIOTECA";
                break;
            case 2:
                retorno  = "CULTURALES";
                break;
            case 3:
                retorno  = "PRESTAMOCOMPUTO";
                break;
        }
        return retorno;
    }
    private String archivoJasper(int tipo)
    {
        String retorno = "";
        switch (tipo) 
        {
            case 0:
                retorno = JASPER1;
                break;
            case 1:
                retorno  = JASPER2;
                break;
            case 2:
                retorno  = JASPER3;
                break;
            case 3:
                retorno  = JASPER4;
                break;
        }
        return retorno;
    }
}

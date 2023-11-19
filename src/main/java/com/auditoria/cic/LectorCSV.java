package com.auditoria.cic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import org.springframework.http.ResponseEntity;

import com.auditoria.cic.ReportesParam.TiposDoc;
import com.auditoria.cic.datosJSON.Periodos;
import com.google.gson.Gson;
import com.opencsv.CSVReader;

public class LectorCSV extends Thread {
    static String JSON_PERIODOS = "/home/archivos/config/per.json";

    private final Semaphore semaforo = new Semaphore(1);
    private HashMap<String, Object> err = null;
    private int cant = 0;
    private class hiloExcel extends Thread{
        private String rengExcel[];
        private ReportePDF trayectoria;
        private int tipo;
        private TiposDoc nuevo;
        private Semaphore semaforo;
        private Periodos per;

        public hiloExcel(String rengExcel[], int tipo, Semaphore semaforo, Periodos per)
        {
            this.rengExcel = rengExcel;
            this.tipo = tipo;
            this.semaforo  = semaforo;
            this.per = per;

            trayectoria = new ReportePDF();
            nuevo = new TiposDoc(calcularPeriodo());  
        }
        public void run()
        {
            try 
            {
                if( !revisaPeriodo() )
                {
                    semaforo.acquire();
                    err.put("msm"+rengExcel[0], "El periodo del folio número: "+rengExcel[0]+" no se encuentra dentro de las fechas parametrizadas favor de revisar");
                    semaforo.release();
                    incrementaVariable();
                }
                else
                    convertirCsvToPdf();
            } catch (Exception e) {
                err.put("msm"+rengExcel[0], "Error al interpretar el folio :"+rengExcel[0]+" revisar que las columnas del documento excel sean las correctas");
            }

            incrementaVariable();            
        }


        private void incrementaVariable()
        {
            try {
                semaforo.acquire();
                cant++;
                semaforo.release();
            } catch (Exception e) {}
        }
        private void convertirCsvToPdf()throws Exception
        {
            nuevo.setFolio(rengExcel[0]);
            nuevo.setFechaCompleta( nuevo.getConvertFecha(rengExcel[1]) );
            switch (tipo) {
                case 0://COSIE
                    cosie();
                    break;
                case 1://BIBLIOTECA
                    biblioteca();
                    break;
                case 2://CULTURALES
                    culturales();
                    break;
                case 3://Prestamo
                    prestamo();
                    break;
            }
            trayectoria.genrarReporte(nuevo.getParametros(), tipo, calcularPeriodo());
        }
        private boolean revisaPeriodo()
        {
            Instant fechaIni = Instant.parse(per.getFechIni());
            Instant fechaFin = Instant.parse(per.getFechFin());
            Instant fechaCSV = Instant.parse(nuevo.getConvertFecha(rengExcel[1], "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

            if( fechaCSV.isBefore(fechaIni) || fechaCSV.isAfter(fechaFin) )
                return false;
            else
                return true;
        }
        private String calcularPeriodo()
        {
            String fecha = per.getFechFin().split("-")[0];
            int periodo = Integer.parseInt(fecha);

            if( per.getPerido() == 1 )
                periodo ++;

            return periodo+"-"+per.getPerido();
        }
        private void cosie() throws Exception
        {
            nuevo.setVariable("R");
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[3]), 1, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[4]), 2, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[5]), 3, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[6]), 4, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[7]), 5, 5);
            nuevo.setComentarios(rengExcel[8]);
        }
        private void biblioteca()throws Exception
        {
            nuevo.setTipoComunidad(rengExcel[2]);
            nuevo.setVariable("R");
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[3]), 1, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[4]), 2, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[5]), 3, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[6]), 4, 5);
            nuevo.setVariable("S");
            nuevo.setServicio(rengExcel[7]);
            nuevo.setComentarios(rengExcel[8]);
        }
        private void culturales()throws Exception
        {
            nuevo.setVariable("R");
            nuevo.setSatisfaccionEspecifico(rengExcel[2], "11");
            nuevo.setSatisfaccionEspecifico(rengExcel[3], "12");
            nuevo.setSatisfaccionEspecifico(rengExcel[4], "13");
            nuevo.setSatisfaccionEspecifico(rengExcel[5], "14");
            nuevo.setSatisfaccionEspecifico(rengExcel[6], "15");
            nuevo.setComentarios(rengExcel[7]);
        }
        private void prestamo()throws Exception
        {
            nuevo.setTipoComunidad(rengExcel[2]);
            nuevo.setVariable("R");
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[3]), 1, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[4]), 2, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[5]), 3, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[6]), 4, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[7]), 5, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[8]), 6, 5);
            nuevo.setPregunta5(rengExcel[9]);
            nuevo.setComentarios(rengExcel[10]);
        }
    }
    public ResponseEntity leerArchivoExcel(String ruta, int tipo, HashMap<String, Object> ret)
     {
        FileReader arch = null;
        CSVReader csv = null;
        Periodos per = leerPeriodo();
        boolean ciclo = true;

        err = new HashMap<String, Object>();

        try {
            int i = 0;
            String linea[] = null;
            
            arch = new FileReader(ruta);
            csv = new CSVReader(arch);

            while ( (linea = csv.readNext()) != null ) 
            {
                if( i != 0 )
                {
                    hiloExcel docExcel = new hiloExcel(linea, tipo, semaforo, per);
                    docExcel.start();
                    docExcel.join();
                }
                i++;
            }

            while( ciclo ) 
                try {
                    semaforo.acquire();
                    if( cant >= (i-2) )
                        ciclo = false;
                    semaforo.release();
                    sleep(100);
                } catch (Exception e) {}

            if( err.size() > 0 )
            {
                err.put("msm", "Lista de errores");
                return ResponseEntity.badRequest().body(err);
            }
            cant = 0;

            csv.close();
            arch.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                csv.close();
            } catch (Exception e) {}
            try {
                arch.close();
            } catch (Exception e) {}
        }
        return ResponseEntity.ok(ret);
    }
    private Periodos leerPeriodo()
    {
    BufferedReader ent = null;
    String temp = "", cadena = "";
    Gson obj = new Gson();

    try {
        ent = new BufferedReader(new FileReader(JSON_PERIODOS));

        while ( (temp = ent.readLine()) != null ) 
            cadena += temp+"\n";
            
        ent.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

    return obj.fromJson(cadena, Periodos.class);
    }
}

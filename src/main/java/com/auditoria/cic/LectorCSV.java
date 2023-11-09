package com.auditoria.cic;

import java.io.FileReader;
import java.util.concurrent.Semaphore;

import com.auditoria.cic.ReportesParam.TiposDoc;
import com.opencsv.CSVReader;

public class LectorCSV extends Thread {

    static public class hiloExcel extends Thread{
        private String rengExcel[];
        private ReportePDF trayectoria;
        private int tipo;
        private TiposDoc nuevo;
        private Semaphore semaforo;

        public hiloExcel(String rengExcel[], int tipo, Semaphore semaforo)
        {
            trayectoria = new ReportePDF();
            nuevo = new TiposDoc();
            this.rengExcel = rengExcel;
            this.tipo = tipo;
            this.semaforo  = semaforo;
        }
        public void run()
        {
            nuevo.setFolio(rengExcel[0]);
            nuevo.setFolioAno(rengExcel[1]);
            nuevo.setFechaCompleta(rengExcel[2]);

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

            trayectoria.genrarReporte(nuevo.getParametros(), tipo);

            semaforo.release();

        }
        private void cosie()
        {
            nuevo.setVariable("R");
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[3]), 1, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[4]), 2, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[5]), 3, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[6]), 4, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[7]), 5, 5);
            nuevo.setComentarios(rengExcel[8]);
        }
        private void biblioteca()
        {
            nuevo.setTipoComunidad(Integer.parseInt(rengExcel[3]));
            nuevo.setVariable("R");
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[4]), 1, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[5]), 2, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[6]), 3, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[7]), 4, 5);
            nuevo.setVariable("S");
            nuevo.setServicio(Integer.parseInt(rengExcel[8]));
            nuevo.setComentarios(rengExcel[9]);
        }
        private void culturales()
        {
            nuevo.setVariable("R");
            nuevo.setSatisfaccionEspecifico(rengExcel[3], "11");
            nuevo.setSatisfaccionEspecifico(rengExcel[4], "12");
            nuevo.setSatisfaccionEspecifico(rengExcel[5], "13");
            nuevo.setSatisfaccionEspecifico(rengExcel[6], "14");
            nuevo.setSatisfaccionEspecifico(rengExcel[7], "15");
            nuevo.setComentarios(rengExcel[8]);
        }
        private void prestamo()
        {
            nuevo.setTipoComunidad(Integer.parseInt(rengExcel[3]));
            nuevo.setVariable("R");
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[4]), 1, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[5]), 2, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[6]), 3, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[7]), 4, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[8]), 5, 5);
            nuevo.setOtrosParametros(Integer.parseInt(rengExcel[9]), 6, 5);
            nuevo.setPregunta5(rengExcel[10]);
            nuevo.setComentarios(rengExcel[11]);
        }
    }
    public void leerArchivoExcel(String ruta, int tipo)
     {
        FileReader arch = null;
        CSVReader csv = null;
        Semaphore semaforo = null;
        try {
            int i = 0;
            String linea[] = null;
            
            arch = new FileReader(ruta);
            csv = new CSVReader(arch);
            semaforo = new Semaphore(0);

            while ( (linea = csv.readNext()) != null ) 
            {
                if( i != 0 )
                {
                    hiloExcel docExcel = new hiloExcel(linea, tipo, semaforo);
                    docExcel.start();
                    docExcel.join();
                }
                i++;
            }

            semaforo.acquire(i-1);

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
     }
}

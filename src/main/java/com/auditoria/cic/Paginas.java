package com.auditoria.cic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auditoria.cic.datosJSON.Archivo;
import com.auditoria.cic.datosJSON.Periodos;
import com.google.gson.Gson;

@RestController
public class Paginas {
    static String RUTA_EXCEL = "/home/archivos/excel/";
    static String JSON_PERIODOS = "/home/archivos/config/per.json";

    @PostMapping("/consultarPeriodo")
    public ResponseEntity revisaPeriodo(){
        BufferedReader ent = null;
        String temp = "", cadena = "";
        try {
            ent = new BufferedReader(new FileReader(JSON_PERIODOS));

            while ( (temp = ent.readLine()) != null ) 
                cadena += temp+"\n";
                
            ent.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(cadena);
    }

    @PostMapping("/actualizarPeriodo")
    public ResponseEntity periodoActualiza(@RequestBody Periodos per){

        BufferedWriter sal = null;
        Gson obj = new Gson();
        try {
            sal = new BufferedWriter(new FileWriter(JSON_PERIODOS));
            sal.write(obj.toJson(per));
            sal.close();
        } catch (Exception e) { 
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
         }
        finally{
            try {
                sal.close();
            } catch (Exception e) {}
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/compartirExcel")
    public ResponseEntity recibeExcel(@RequestBody Archivo obj)
    {
        HashMap<String, Object> sal  = null;
        CargarDoc recibe = new CargarDoc();
        LectorCSV csv = new LectorCSV();

        recibe.setArchivo(obj);
        sal = recibe.getRespuesta();

        if( recibe.getEstado() != 200 )
            return ResponseEntity.status(recibe.getEstado()).body(sal);
        else
            return csv.leerArchivoExcel(RUTA_EXCEL+recibe.getNombre(), obj.getTipoPDF(), sal);
    }

    /*@PostMapping("/excel")
    public ResponseEntity excelReporte()
    {
        HashMap<Integer, HashMap<Double, String>> map = new HashMap<Integer, HashMap<Double, String>>();

        try {
            FileInputStream arch = new FileInputStream("/home/brandon985/Descargas/uno.csv");
            Workbook libro = new XSSFWorkbook(arch);
            Sheet hoja = (Sheet)libro.getSheetAt(0);

            for ( int i = 0; i < hoja.getPhysicalNumberOfRows(); i++ ) 
            {
                HashMap<Double, String> pdf = new HashMap<Double, String>();
                if( i != 0 )
                    for ( Cell celda : hoja.getRow(i) )
                        pdf.put(celda.getNumericCellValue(), celda.toString());
                map.put(i, pdf);
            }

            libro.close();
            arch.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }*/
}

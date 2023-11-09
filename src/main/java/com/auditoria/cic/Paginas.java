package com.auditoria.cic;

import java.io.FileInputStream;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auditoria.cic.datosJSON.Archivo;

@RestController
public class Paginas {
    static String RUTA_EXCEL = "/home/archivos/excel/";

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

        csv.leerArchivoExcel(RUTA_EXCEL+recibe.getNombre(), obj.getTipoPDF());

        return ResponseEntity.ok(sal);
    }

    @PostMapping("/excel")
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
    }
}

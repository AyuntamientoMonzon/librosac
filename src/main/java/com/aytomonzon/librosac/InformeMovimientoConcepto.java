/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aytomonzon.librosac;


import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase generadora de informes por concepto
 * @author albertoaraguas
 * @version 1.0
 */
public class InformeMovimientoConcepto {
  /**
  *  declaraciones publicas 
  */ 
  public static final String DEST = "C:\\librocaja\\Recibo\\InformeConcepto.pdf";
  
  /**
  *  declaraciones publicas 
  */ 
  public static final String IMAGES = "C:\\librocaja\\images\\logo.jpg"; 
  ConectorDB basedatos;
  Document doc;
  String ListaConceptos;
  String Libro;
  String RangoFechas;
  /**
  *  declaraciones publicas 
  */ 
  public static final PdfNumber PORTRAIT = new PdfNumber(0);
  /**
  *  declaraciones publicas 
  */ 
  public static final PdfNumber LANDSCAPE = new PdfNumber(90);
  String FechaInicio;
  String FechaFinal;
  Herramientas herramientas;
  
/**
 * Constructor
 * @param basedatos pasar la clase conectordb
 * @param ListaConceptos string con todos los conceptos que existen
 * @param Libro libro de caja
 * @param RangoFechas rango de fechas en string
 * @throws java.net.MalformedURLException exception controlada
 * @throws java.io.FileNotFoundException exception controlada
 * @throws java.sql.SQLException exception controlada
 */
    public InformeMovimientoConcepto(ConectorDB basedatos, String ListaConceptos, String Libro, String RangoFechas) throws MalformedURLException, IOException, FileNotFoundException, SQLException {
        this.basedatos = basedatos;
        this.ListaConceptos = ListaConceptos;
        this.Libro = Libro;
        this.RangoFechas = RangoFechas;
        this.herramientas=new Herramientas();
        GenerarInforme();
    }

 /**
 * metodo adicional para extraer del string fecha las fechas
 * @param fecha string en formato "Fecha BETWEEN '"+FechaInicial+"' AND '"+FechaFinal+"'"
 */  
  final void ExtraerFechas(String fecha){
     String[] partes = fecha.split("BETWEEN|AND");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato de cadena incorrecto");
        } 
    FechaInicio=herramientas.cambiarOrdenFecha(partes[1].trim().replaceAll("'", ""), false);
    FechaFinal=herramientas.cambiarOrdenFecha(partes[2].trim().replaceAll("'", ""), false);
  }
 /**
 * metodo para generar el pdf en a4 apaisado
 * @throws FileNotFoundException
 */ 
  private void CrearPdf() throws FileNotFoundException {
      //crea el doc en a4 y rotado
      PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
      pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
      doc = new Document(pdfDoc);
    }
  

 /**
 * metodo que crea el documento y lo rellenamos
 * @throws FileNotFoundException exception controlada
 * @throws MalformedURLException exception controlada
 * @throws IOException exception controlada
 * @throws SQLException exception controlada
 */ 
  final void GenerarInforme() throws FileNotFoundException, MalformedURLException, IOException, SQLException{
      if (basedatos.isConected()){
      //lanzamos en otro hilo una ventana para que espere mientras se genera
      VentanaEsperar ventana= new VentanaEsperar();
        
        Object[][] resultados; 
      //crear pdf horizontal
       CrearPdf();
      //sacamos textos para el titulo del informe 
       String LibroString;
      if (Libro.equals("1")){
       LibroString="CAJA SAC";
      }else{
       LibroString="TARJETAS SAC";
      }  
     ExtraerFechas(RangoFechas);
    //tamaños de fuente del informe
    PdfFont Negrita= PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    PdfFont code= PdfFontFactory.createFont(StandardFonts.HELVETICA);
       Style style =new Style()
            .setFont(code)
            .setFontSize(10);   
        Style stylefooter =new Style()
            .setFont(code)
            .setFontSize(8);   
        Style styleTitulo =new Style()
            .setFont(Negrita)
            .setFontSize(14);   
     //encabezado  informe
      doc.add(new Paragraph("------------------  INFORME DE MOVIMIENTOS POR CONCEPTO---------------------    AYUNTAMIENTO DE MONZON.SAC").addStyle(styleTitulo));
      doc.add(new Paragraph(" Consulta entre las fechas "+FechaInicio+" y "+FechaFinal+" del Libro: "+LibroString).addStyle(style));
      doc.add(new Paragraph(""));
      //buscamos los usuarios con movimientos en el intervalo de fechas y el libro seleccionado
      List<String> listaconceptos=basedatos.ConceptosIntervalo(ListaConceptos, Libro, RangoFechas);
      for (String concepto : listaconceptos){
        doc.add( new Paragraph(" "+concepto).addStyle(style));
        doc.add(new Paragraph(""));
        //por cada concepto sacamos los movimientos y los imprimimos
        resultados=basedatos.RegistrosConceptoIntervalo(concepto, Libro, RangoFechas);
        double totalIn=0;
        double totalOut=0;
        //Creamos la tabla con los datos
        Table tableR = new Table(UnitValue.createPointArray(new float[] {60, 50,50,290,280,280,50,50}));
        tableR.setMarginTop(0);
        tableR.addHeaderCell(new Paragraph("Fecha"));
        tableR.addHeaderCell(new Paragraph("Apunte"));
        tableR.addHeaderCell(new Paragraph("NIF"));
        tableR.addHeaderCell(new Paragraph("Nombre"));
        tableR.addHeaderCell(new Paragraph("Descripcion"));
        tableR.addHeaderCell(new Paragraph("Trabajador"));
        tableR.addHeaderCell(new Paragraph("Entrada"));
        tableR.addHeaderCell(new Paragraph("Salida"));
      for(int i=0;i<resultados.length;i++){
            if (resultados[i][0] != null){ 
             tableR.addCell(new Paragraph(resultados[i][0].toString()).addStyle(stylefooter));
            }
            if (resultados[i][1] != null){ 
             tableR.addCell(new Paragraph(resultados[i][1].toString()).addStyle(stylefooter));
            }
            if (resultados[i][2] != null){ 
             tableR.addCell(new Paragraph(resultados[i][2].toString()).addStyle(stylefooter));
            }
            if (resultados[i][3] != null){ 
             tableR.addCell(new Paragraph(resultados[i][3].toString()).addStyle(stylefooter));
            }
            if (resultados[i][4] != null){ 
             tableR.addCell(new Paragraph(resultados[i][4].toString()).addStyle(stylefooter));
            }
            if (resultados[i][5] != null){ 
             tableR.addCell(new Paragraph(resultados[i][5].toString()).addStyle(stylefooter));
            }
            if (resultados[i][6] != null){ 
             tableR.addCell(new Paragraph(resultados[i][6].toString()).addStyle(style));
             totalIn += Double.parseDouble(resultados[i][6].toString());
            }
            if (resultados[i][7] != null){ 
             tableR.addCell(new Paragraph(resultados[i][7].toString()).addStyle(style));
             totalOut += Double.parseDouble(resultados[i][7].toString());
            }
         }
        doc.add(tableR);
        doc.add(new Paragraph("-------------------------------------------------------------------------------------------------------------------------- TOTAL ENTRADA: "+totalIn+"€ TOTAL SALIDA: -"+totalOut+"€"));      }
      doc.close();
  }
  }
}

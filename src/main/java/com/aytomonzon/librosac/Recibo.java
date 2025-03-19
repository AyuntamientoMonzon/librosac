/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aytomonzon.librosac;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;



/**
 * Clase que genera la carta de pago en pdf
 * @author albertoaraguas
 * @version 1.0
 */
public class Recibo {
     public static final String DEST = "C:\\librocaja\\Recibo\\recibo.pdf";
     public static final String IMAGES = "C:\\librocaja\\images\\logo.jpg";
    PdfDocument pdfDoc;
    Document doc;
    PdfFont code;
    
    
    //genera el recibo y devuelve un string con la uri del archivo generado
    protected String manipulatePdf(String[] datosFactura) throws MalformedURLException, FileNotFoundException, IOException {
        //lanzamoso una ventana popup para avisar que espere en otro hilo
        VentanaEsperar ventana= new VentanaEsperar();
        
        String FicheroImpreso;
        try{
            pdfDoc = new PdfDocument(new PdfWriter(DEST));
            FicheroImpreso=DEST;
        }catch (Exception ex){
            //generamos un nombrealeatorio, ya que el pdf original puede estar bloqueado
            Random random = new Random();
            int nombreAleatorio = random.nextInt(99001) + 1000;
            String nombreAleatorioString = String.valueOf(nombreAleatorio);
            FicheroImpreso="C:\\librocaja\\Recibo\\"+nombreAleatorioString+".pdf";
            pdfDoc = new PdfDocument(new PdfWriter(FicheroImpreso));
        }
        doc = new Document(pdfDoc);
        doc.setMargins(50, 20, 20, 50);
        //intentar añadir el logo
        Image img = new Image(ImageDataFactory.create(IMAGES));
        doc.add(img);
        //configurarmos las fuentes del documento
        code= PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Style style =new Style()
            .setFont(code)
            .setFontSize(12);   
        Style stylefooter =new Style()
            .setFont(code)
            .setFontSize(8);   
        Style styleTitulo =new Style()
            .setFont(code)
            .setFontSize(14); 
        doc.add(new Paragraph((new Text("Plaza Mayor 4, 22400 Monzón (Huesca)    CIF: P2221800B").addStyle(stylefooter))));
        //generamos la tabla para rellenar datos
        Table tableTitle = new Table(UnitValue.createPointArray(new float[] {550, 0}));
        tableTitle.setMarginTop(5);
        tableTitle.addStyle(styleTitulo);
        Cell cell = new Cell().add(new Paragraph("CARTA DE PAGO")
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
             cell.setTextAlignment(TextAlignment.CENTER);         
        tableTitle.addCell(cell);
        doc.add(tableTitle);
        
        Table tableR = new Table(UnitValue.createPointArray(new float[] {100, 90,100,90,100,90}));
        tableR.setMarginTop(5);
        tableR.addCell("Justificante Nº:");
        tableR.addCell(datosFactura[0]);   //aqui va el valor del apunte
        tableR.addCell("Medio de pago:");
        tableR.addCell(datosFactura[1]);   //aqui va el valor del medio de pago (efectivo o tarjeta)
        tableR.addCell("Importe €");
        tableR.addCell(datosFactura[2]);   //aqui va el importe 
        doc.add(tableR);
       
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(new Text("PERSONA ORDENANTE:").addStyle(style)));
        Table table = new Table(UnitValue.createPointArray(new float[] {100, 450}));
        table.setMarginTop(5);
        table.addCell(new Paragraph(new Text("NIF").addStyle(style)));
        table.addCell(new Paragraph(new Text("APELLIDOS Y NOMBRE O RAZON SOCIAL").addStyle(style)));
        table.addCell(datosFactura[3]);
        table.addCell(datosFactura[4]);
        table.addCell(new Paragraph(new Text("DIRECCIÓN").addStyle(style)));
        table.addCell(datosFactura[5]);
        doc.add(table);
        
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(new Text("CONCEPTO:").addStyle(style)));
        Table tableConcepto = new Table(UnitValue.createPointArray(new float[] {550}));
        tableConcepto.setMarginTop(5);
        tableConcepto.addCell(datosFactura[6]);
        doc.add(tableConcepto);
        //caluclamos la fecha de hoy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        String formattedDate = dateFormat.format(currentDate);
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(new Text("En Monzón a "+formattedDate).addStyle(style)));
        doc.add(new Paragraph(new Text(".    Sello y firma").addStyle(stylefooter)));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(""));
        doc.add(new Paragraph(new Text("De acuerdo con la normativa vigente, sobre Protección de Datos Personales, le informamos que los datos personales contenidos en el presente documento serán incorporados al fichero de Contabilidad titularidad del Ayuntamiento de Monzón con la única finalidad de gestionar los ingresos y gastos del Ayutamiento. Sus datos podrán ser cedidos a la Administración Tributaria y al Tribunal de Cuentas a fin de cumplir con las obligaciones correspondientes.").addStyle(stylefooter)));
        doc.add(new Paragraph(new Text("Sin perjuicio de ello, le informamos de la posbilidad que tiene de ejercitar sus derechos de acceso, cancelación, oposición y rectificación en cualquier momento, dirigiéndose al Ayuntamiento de Monzón, Plaza Mayor nº4, 22400 Monzón.").addStyle(stylefooter)));

        doc.close();
        return FicheroImpreso;  //devuelve la url del fichero generado
    }
     
}

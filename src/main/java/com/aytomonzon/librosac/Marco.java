
package com.aytomonzon.librosac;

import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.*;



/**
 * Toda la logica y botones del programa
 * @author albertoaraguas
 * @version 1.5
 */
public class Marco extends javax.swing.JFrame {
    private final String miurldb;
    /**
    * dato publico
    */
    ConectorDB basedatos;
    private ArrayList<String> listados;
    /**
    * dato publico
    */
    Herramientas herramientas;
    // Crear el JDateChooser
    JDateChooser dateChooser = new JDateChooser();
    private String usuario= "No identificado";
    DocumentoChecker DocCheck = new DocumentoChecker();
    
    /**
     * Creates new form Marco
     * @throws java.io.IOException execpcion controlada
     * @throws java.lang.InterruptedException excepcion controlada
     */
    public Marco() throws IOException, InterruptedException {
        // Establecer el icono del JFrame
           initComponents();
           CargarConfig();        
        miurldb=this.TxtIP.getText();
        basedatos = new ConectorDB(miurldb);
        herramientas = new Herramientas();
         //comprobar la conexion con la base de datos     
        if (basedatos.conexion){
            this.jLabel16.setText("CONECTADO A LA BASE DE DATOS");
            CargarCombos();
            PonerFecha();
            Logearse();
        } else {
            this.jLabel16.setForeground(Color.red);
            this.jLabel16.setText("NO HAY CONEXION CON LA BASE DE DATOS");
        }

    }
   
 
 /**
 *
 * Pone en el campo fecha la fecha actual
 * 
 */
private void PonerFecha(){
// Formatear la fecha en el formato deseado (día/mes/año)
        Date fechaActual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaFormateada = sdf.format(fechaActual);
        this.jTextField9.setText(fechaFormateada);
        this.jTextField14.setText(fechaFormateada);
        this.jTextField15.setText(fechaFormateada);
}
/**
 *
 * Este metodo pone el contenido del ultimo registro de la base de datos en las casillas correcpondientes
 * 
 */
private void CargarLastReg(){
    String temp = this.jTextField9.getText();
    this.jTextField12.setText(temp);
    this.jTextField10.setText(this.jTextField5.getText());
    this.jTextField11.setText(this.jTextField6.getText());
}

/**
 *
 * Este metodo llama a la clase para leer el archivo config.txt
 * @throws IOExcepcion excepcion controlada
 * @throws InterruptedException excepcion controlada
 */
private void CargarConfig() throws IOException, InterruptedException{
        List<String> loadedConfig = ConfigManager.loadConfig();
        if (loadedConfig != null) {
            for (String line : loadedConfig) {
                System.out.println(line);
            }
        }
        this.TxtIP.setText(loadedConfig.getFirst()); //ruta db
        this.TxtPuertoCom.setText(loadedConfig.get(1));    // puerto serie selladora
        SerialSender.setNumeroPuertoCom(loadedConfig.get(1));  //set puerto com en clase
        SerialSender.SetCom(loadedConfig.get(1));   
}
/**
 *
 * Este metodo pide ususario y contraseña
 * 
 */
private void Logearse(){
        try {
            //recupera de la basedatos la lista de usuarios
                DefaultTableModel miListaNombres = basedatos.listaNombres();
                this.jTable2.setModel(miListaNombres);
                UsuarioController us=new UsuarioController(basedatos);
                //pedimos usuario y contraseña de acceso
                String usr=us.login();
                if (usr.equalsIgnoreCase("Falso")){
                    // si son iguales, el login es malo
                    basedatos.CerrarConexion();
                    System.exit(0);
                }else{
                    usuario=usr;
                    this.jLabel25.setText(usuario);
                    this.jTextField18.setText(usuario);
                    if (usuario.equalsIgnoreCase("admin")){
                        this.BtnUserNuevo.setEnabled(true);
                        this.BtnUserBorrar.setEnabled(true);
                        this.BtnPassword.setEnabled(true);
                    }
                }
         } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error Acceso a la base de datos", "Warning", JOptionPane.WARNING_MESSAGE);
        }
}
/**
 *
 * Este metodo rellena los combos del formulario
 * 
 */
private void CargarCombos() {
        try {
            if (basedatos.isConected()){
                try{
                    listados = basedatos.leerDescripcionesTabla("Libro", "Descripcion");
                    this.ComboLibro.removeAllItems();
                    this.jComboBox4.removeAllItems();
                    for (String elemento : listados) {
                        this.ComboLibro.addItem(elemento);
                        this.jComboBox4.addItem(elemento);
                    }
                    this.jComboBox5.removeAllItems();
                    listados = basedatos.leerDescripcionesTabla("mediopago", "tipo");
                    this.ComboMedioPago.removeAllItems();
                    for (String elemento : listados) {
                        this.ComboMedioPago.addItem(elemento);
                    }
                    listados = basedatos.leerDescripcionesTabla("TipoDocumento", "TDDescripcion");
                    this.ComboTipoDocumento.removeAllItems();
                    for (String elemento : listados) {
                        this.ComboTipoDocumento.addItem(elemento);
                    }
                    this.jTextField1.setText("");
                    this.jTextField2.setText("");
                    this.jTextField3.setText("");
                    this.jTextField4.setText("");
                    this.jTextField5.setText("");
                    this.jTextField6.setText("");
                    this.jTextField7.setText("");
                    this.jTextField19.setText("");
                    //leemos el ultimo registro de la base de datos y lo pintamos en el form
                    ArrayList ultimo = basedatos.ultimoregistro();
                    this.jTextField16.setText(ultimo.get(0).toString()+"/"+ultimo.get(1).toString());
                    //String f=herramientas.cambiarOrdenFecha(ultimo.get(2).toString(),false);
                    //this.jTextField12.setText(f);
                    this.jTextField10.setText(ultimo.get(3).toString());
                    this.jTextField11.setText(ultimo.get(4).toString());
                }catch(Exception e){
                    JOptionPane.showMessageDialog(this, "No se han podido cargar los combos", "Marco.java", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No hay conexion con la base de datos", "Marco.java", JOptionPane.WARNING_MESSAGE);
            }       } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error Acceso a la base de datos", "Warning", JOptionPane.WARNING_MESSAGE);
        }
}   

/**
 *
 * Este metodo true o false segun si los campos estan llenos o vacios
 * @return boolean estado campos dni y nombre
 * 
 */
private boolean CamposVacios(){
    String campo1 = this.jTextField1.getText();
    String campo2 = this.jTextField2.getText();
    if(campo1.isEmpty() ||  campo2.isEmpty()  ){
     return false;
    }else{
     return true;
    }
}
/**
 *
 * Este metodo recoge los datos del jframe y los vuelca en la bbdd
 * @throws InterruptedException excepcion controlada 
 * 
 */
private void HacerApunteSinFactura() throws InterruptedException{
 // preparar los datos para guardar en la base de datos
        String Fecha = herramientas.cambiarOrdenFecha(this.jTextField9.getText(),true);
        int Libro=this.ComboLibro.getSelectedIndex();
        String Nombre= this.jTextField4.getText();
        String Apellido = this.jTextField2.getText();
        String Apellido2 = this.jTextField3.getText();
        String Descripcion= this.jTextField5.getText();
        int TipoDoc=this.ComboTipoDocumento.getSelectedIndex();
        //quitamos el color del campo texto
        this.jTextField1.setBackground(Color.white);
        this.jTextField1.setForeground(Color.black);
        String Numero= this.jTextField1.getText();
        int Concepto=this.jComboBox5.getSelectedIndex();
        
        if (this.jTextField6.isEnabled()){
            ////si es entrada de dinero concepto va del 1 al 39
        }else{
           Concepto=Concepto+39;
        }  //si es salida de dinero concepto es de 40 a 42  
        
        int Medio=this.ComboMedioPago.getSelectedIndex();
        double cashIn=herramientas.convertirTextoADouble(this.jTextField6.getText());
        double cashOut=herramientas.convertirTextoADouble(this.jTextField7.getText());
        double saldo=0;
        String direc=this.jTextField19.getText();
        try {
            if (basedatos.isConected() && CamposVacios()){
                //recuperar el saldo ultimo si se va a incluir en libro caja sac
                if (Libro==0){
                    saldo=basedatos.getSaldo();
                    System.err.println();
                    //calcular nuevo saldo, para los demas libros el saldo queda igual
                    saldo=saldo+cashIn-cashOut;
                }
                int Registrado=basedatos.InsertaDatos(Fecha, Libro + 1, Nombre, Apellido, Apellido2, Descripcion,TipoDoc+1,Numero,Concepto+1,Medio+1,cashIn,cashOut,saldo,direc,usuario);
                
                if (Registrado==1) {
                    //JOptionPane.showMessageDialog(null, "Apunte de caja creado correctamente");
                    //presentar ultimo registro y borramos los campos del frame
                    CargarLastReg();
                    CargarCombos();
                    
                } else {
                    JOptionPane.showMessageDialog(this, "No se hapodido hacer el apunte de caja. Compruebe datos vacios", "Marco.java", JOptionPane.WARNING_MESSAGE);
                }
            } else {
              JOptionPane.showMessageDialog(this, "La casilla Documento y Apellido no pueden estar vacias", "Marco.java", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error Acceso a la base de datos", "Warning", JOptionPane.WARNING_MESSAGE);
        } 
        
                
}

/**
 *
 * Este registra en la base de datos y devuelve los datos para generar la factura
 * @return Vector de String con los datos
 * @throws InterruptedException excepcion controlada 
 * @throws SQLExcepction excepcion controlada
 */
private String[] HacerApunteConFactura() throws SQLException, InterruptedException{      
 // preparar los datos para guardar en la base de datos
        String[] datosFactura = new String[7];   
        String Fecha = herramientas.cambiarOrdenFecha(this.jTextField9.getText(),true);
        int Libro=this.ComboLibro.getSelectedIndex();
        String Nombre= this.jTextField4.getText();
        String Apellido = this.jTextField2.getText();
        String Apellido2 = this.jTextField3.getText();
        String Descripcion= this.jTextField5.getText();
        int TipoDoc=this.ComboTipoDocumento.getSelectedIndex();
        String Numero= this.jTextField1.getText();
        int Concepto=this.jComboBox5.getSelectedIndex();
        int Medio=this.ComboMedioPago.getSelectedIndex();
        double cashIn=herramientas.convertirTextoADouble(this.jTextField6.getText());
        double cashOut=herramientas.convertirTextoADouble(this.jTextField7.getText());
        double saldo=0;
        String direc=this.jTextField19.getText();
        //quitamos el color del campo texto
        this.jTextField1.setBackground(Color.white);
        this.jTextField1.setForeground(Color.black);
        
        String nombre=Nombre+" "+Apellido+" "+Apellido2;
        datosFactura[4]= nombre;
        datosFactura[3]= Numero;
        datosFactura[5]=direc;
        datosFactura[6]=Descripcion;
        if (TipoDoc==1){
         datosFactura[1]="Tarjeta";
        } else {
         datosFactura[1]="Efectivo";
        }
        if (basedatos.isConected()){
            //recuperar el saldo ultimo si se va a incluir en libro caja sac
            if (Libro==0){
                saldo=basedatos.getSaldo();
            //calcular nuevo saldo, para los demas libros el saldo queda igual 
            saldo=saldo+cashIn-cashOut;
            }

            int Registrado=basedatos.InsertaDatos(Fecha, Libro + 1, Nombre, Apellido, Apellido2, Descripcion,TipoDoc+1,Numero,Concepto+1,Medio+1,cashIn,cashOut,saldo,direc,usuario);
        
            if (Registrado==1) {
                JOptionPane.showMessageDialog(null, "Apunte de caja creado correctamente");
                //presentar ultimo registro y borramos los campos del frame
                CargarLastReg();
                CargarCombos();
                datosFactura[0]= this.jTextField16.getText();  //carga el numero de apunte
                datosFactura[2]=this.jTextField11.getText();    //carga el importe
                return  datosFactura;
            } else {
                JOptionPane.showMessageDialog(this, "No se ha podido hacer el apunte de caja con carta de pago", "Marco.java", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }else{
         JOptionPane.showMessageDialog(this, "No se ha podido hacer el apunte de caja con carta de pago, no hay conexion con la base de datos", "Marco.java", JOptionPane.WARNING_MESSAGE);
        }
        JOptionPane.showMessageDialog(this, "No se ha podido hacer el apunte de caja con carta de pago, no hay conexion con la base de datos", "Marco.java", JOptionPane.WARNING_MESSAGE);
        return null;
}

/**
 *
 * Esta funcion genera el recibo
 * @param datosfactura Vector con los datos para hacer la factura
 */
private void GenerarRecibo(String[] datosfactura){
    Recibo recibo=new Recibo();
        try {
            String URLFichero = recibo.manipulatePdf(datosfactura);
            File file = new File(URLFichero);
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se ha podido generar el recibo", "Marco.java", JOptionPane.WARNING_MESSAGE);
        }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ComboLibro = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        ComboTipoDocumento = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        BtnApeiron = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jTextField12 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        BtnSellar = new javax.swing.JButton();
        jTextField16 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        ComboMedioPago = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        BtnRegistrarApunte = new javax.swing.JButton();
        CheckBoxCartaPago = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox<>();
        jTextField13 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        BtnBuscar = new javax.swing.JButton();
        jTextField14 = new javax.swing.JTextField();
        jTextField15 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        BtnSellarSeleccion = new javax.swing.JButton();
        BtnPagoSeleccionado = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        BtnUserNuevo = new javax.swing.JButton();
        BtnUserBorrar = new javax.swing.JButton();
        BtnPassword = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        TxtPuertoCom = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        TxtIP = new javax.swing.JTextField();
        BtnGuardarConfig = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        BtnModificar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        BtnComprobar = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        BtnCompactar = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        BtnSalir = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        BtnHoyIndividual = new javax.swing.JButton();
        BtnConsultaCondicional = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Libro De Caja");
        setResizable(false);
        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                formComponentAdded(evt);
            }
        });

        jTabbedPane2.setName("Libro"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        jLabel1.setText("Apunte de caja o Justificante de cobro");

        jLabel2.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel2.setText("Libro:");
        jLabel2.setToolTipText("");

        ComboLibro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ComboLibro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ComboLibro.setName("CmbLibro"); // NOI18N

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Documento"));

        ComboTipoDocumento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ComboTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ComboTipoDocumento.setName("CmbTipo"); // NOI18N

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jTextField1.setText(" ");
        jTextField1.setName("TxtNumero"); // NOI18N
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jTextField1.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTextField1InputMethodTextChanged(evt);
            }
        });
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });

        BtnApeiron.setText("<html><center> Buscar en<br>APEIRON</center></html>");
        BtnApeiron.setEnabled(false);
        BtnApeiron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnApeironActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ComboTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(BtnApeiron, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ComboTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BtnApeiron, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Identificación  personal"));

        jLabel6.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel6.setText("1r Apellido o razón social:");

        jTextField2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField2.setText(" ");
        jTextField2.setName("TxtApellido1"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel7.setText("2º Apellido");

        jTextField3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField3.setText(" ");
        jTextField3.setName("TxtApellido2"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel8.setText("Nombre:");

        jTextField4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField4.setText(" ");
        jTextField4.setName("TxtNombre"); // NOI18N

        jLabel27.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel27.setText("Dirección:");

        jTextField19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField19.setText(" ");
        jTextField19.setName("TxtNombre"); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(84, 84, 84)
                        .addComponent(jLabel7)
                        .addGap(139, 139, 139)
                        .addComponent(jLabel8))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel27)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField19)
                        .addContainerGap())))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Apunte de caja"));

        jLabel9.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel9.setText("Fecha:");
        jLabel9.setToolTipText("");

        jTextField9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField9.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField9.setText("jTextField9");
        jTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });

        jButton2.setLabel("F");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel10.setText("Descripción:");
        jLabel10.setName("TxtDescripcion"); // NOI18N

        jTextField5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField5.setText(" ");
        jTextField5.setName("TxtNumero"); // NOI18N

        jLabel13.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel13.setText("Concepto Apunte:");
        jLabel13.setToolTipText("");

        jComboBox5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox5.setEnabled(false);
        jComboBox5.setName("CmbConcepto"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel11.setText("Importe entrada:");
        jLabel11.setName("TxtEntrada"); // NOI18N

        jTextField6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField6.setText(" ");
        jTextField6.setName("TxtNumero"); // NOI18N
        jTextField6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField6FocusLost(evt);
            }
        });
        jTextField6.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTextField6InputMethodTextChanged(evt);
            }
        });
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField6KeyTyped(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel12.setText("Importe salida:");
        jLabel12.setToolTipText("");
        jLabel12.setName("TxtSalida"); // NOI18N

        jTextField7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField7.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField7.setText(" ");
        jTextField7.setName("TxtNumero"); // NOI18N
        jTextField7.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField7FocusLost(evt);
            }
        });
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField7KeyTyped(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Ultimo Apunte"));

        jTextField12.setEditable(false);
        jTextField12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField12.setText(" ");
        jTextField12.setName("TxtNumero"); // NOI18N

        jTextField10.setEditable(false);
        jTextField10.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField10.setText(" ");
        jTextField10.setName("TxtNumero"); // NOI18N

        jTextField11.setEditable(false);
        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.setText(" ");
        jTextField11.setName("TxtNumero"); // NOI18N

        BtnSellar.setText("SELLAR");
        BtnSellar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSellarActionPerformed(evt);
            }
        });

        jTextField16.setEditable(false);
        jTextField16.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField16ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(BtnSellar, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnSellar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField16))
                .addContainerGap())
        );

        jLabel14.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel14.setText("Medio de pago:");
        jLabel14.setToolTipText("");
        jLabel14.setName("Medio:"); // NOI18N

        ComboMedioPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ComboMedioPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ComboMedioPago.setName("CmbConcepto"); // NOI18N
        ComboMedioPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboMedioPagoActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("€");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("€");

        BtnRegistrarApunte.setBackground(new java.awt.Color(204, 255, 204));
        BtnRegistrarApunte.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        BtnRegistrarApunte.setText("REGISTAR APUNTE");
        BtnRegistrarApunte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRegistrarApunteActionPerformed(evt);
            }
        });

        CheckBoxCartaPago.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        CheckBoxCartaPago.setText("Generar Carta de Pago");
        CheckBoxCartaPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBoxCartaPagoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(CheckBoxCartaPago, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                    .addComponent(BtnRegistrarApunte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(BtnRegistrarApunte, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CheckBoxCartaPago)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(116, 116, 116)
                        .addComponent(jLabel10))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel13))
                                .addGap(15, 15, 15)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 635, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboMedioPago, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3))
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addGap(0, 41, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(3, 3, 3)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel14)
                                .addComponent(ComboMedioPago, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13)))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addComponent(ComboLibro, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(106, 106, 106)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ComboLibro, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Registro en Libro", jPanel1);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("LIBRO:"));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel22.setText("TIPO DE BUSQUEDA");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Por Descripción", "Por Nombre", "Por Fecha", "Por Importe", "Todo" }));
        jComboBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox7ActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel20.setText("Del: ");

        jLabel21.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel21.setText("Al:");

        BtnBuscar.setBackground(new java.awt.Color(204, 255, 255));
        BtnBuscar.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        BtnBuscar.setText("Buscar");
        BtnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBuscarActionPerformed(evt);
            }
        });

        jTextField14.setText("dd/mm/yyyy");
        jTextField14.setEnabled(false);

        jTextField15.setText("dd/mm/yyyy");
        jTextField15.setEnabled(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(176, 176, 176)
                        .addComponent(jLabel22)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField13))
                .addGap(18, 18, 18)
                .addComponent(BtnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(12, 12, 12)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21)
                            .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(BtnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados de la búsqueda:"));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Acc Directos"));

        BtnSellarSeleccion.setBackground(new java.awt.Color(255, 255, 204));
        BtnSellarSeleccion.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        BtnSellarSeleccion.setText("<html><center>Sellar<br>Registro<br>Seleccionado</center></html>");
        BtnSellarSeleccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSellarSeleccionActionPerformed(evt);
            }
        });

        BtnPagoSeleccionado.setBackground(new java.awt.Color(153, 204, 255));
        BtnPagoSeleccionado.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        BtnPagoSeleccionado.setText("<html><center>Carta Pago<br>Registro<br>Seleccionado</center></html>");
        BtnPagoSeleccionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPagoSeleccionadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BtnSellarSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnPagoSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(127, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnSellarSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnPagoSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 99, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Buscar", jPanel2);

        jLabel23.setFont(new java.awt.Font("Open Sans", 1, 18)); // NOI18N
        jLabel23.setText("EL ARCHIVO DE CONFIGURACION DEBE ESTAR EN LA RUTA C:\\LibroCaja");

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Gestion Usuarios"));

        jLabel26.setText("Usuario Activo:");

        jTextField18.setEditable(false);
        jTextField18.setText(" ");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        BtnUserNuevo.setText("<html><center>Nuevo<br>Usuario</center></html>");
        BtnUserNuevo.setEnabled(false);
        BtnUserNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUserNuevoActionPerformed(evt);
            }
        });

        BtnUserBorrar.setText("<html><center>Borrar<br>Usuario</center></html>");
        BtnUserBorrar.setEnabled(false);
        BtnUserBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUserBorrarActionPerformed(evt);
            }
        });

        BtnPassword.setText("<html><center>Cambiar<br>Contraseña</center></html>");
        BtnPassword.setEnabled(false);
        BtnPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPasswordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(BtnPassword, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(BtnUserBorrar, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(BtnUserNuevo, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(32, 32, 32))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(BtnUserNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtnUserBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtnPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(75, Short.MAX_VALUE))))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Comunicaciones"));

        jLabel17.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel17.setText("Numero de puerto COM de la selladora:");

        TxtPuertoCom.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TxtPuertoCom.setText("2");
        TxtPuertoCom.setToolTipText("");

        jLabel15.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel15.setText("IP MySQL Server:PUERTO");

        TxtIP.setText("192.168.5.9:3306");
        TxtIP.setName("LblURI"); // NOI18N
        TxtIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtIPActionPerformed(evt);
            }
        });

        BtnGuardarConfig.setBackground(new java.awt.Color(153, 255, 153));
        BtnGuardarConfig.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        BtnGuardarConfig.setText("<html><center>GUARDAR<br>CONFIGURACION</center></html>");
        BtnGuardarConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnGuardarConfigActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(BtnGuardarConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(30, 30, 30)
                                .addComponent(TxtIP, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(TxtPuertoCom, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 37, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(TxtIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(TxtPuertoCom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(BtnGuardarConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TxtIP.getAccessibleContext().setAccessibleName("LblURI");

        jLabel28.setFont(new java.awt.Font("Open Sans", 1, 18)); // NOI18N
        jLabel28.setText("EL ARCHIVO DE RECIBOS DEBE ESTAR EN LA RUTA C:\\LibroCaja\\Recibo");

        jLabel29.setFont(new java.awt.Font("Open Sans", 1, 18)); // NOI18N
        jLabel29.setText("LOS ARCHIVOS DE IMAGENES DEBEN ESTAR EN LA RUTA C:\\LibroCaja\\images");

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Base Datos"));

        BtnModificar.setText("Modifcar");
        BtnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnModificarActionPerformed(evt);
            }
        });

        jLabel5.setText("Borrar Seguridad TLS Java (Apeiron)");

        jLabel30.setText("Check apuntes duplicados");

        BtnComprobar.setText("Comprobar");
        BtnComprobar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnComprobarActionPerformed(evt);
            }
        });

        jLabel31.setText("Optimizar la Base de Datos");

        BtnCompactar.setText("Compactar");
        BtnCompactar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCompactarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BtnModificar, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(BtnComprobar, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BtnCompactar)))
                .addGap(27, 27, 27))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnModificar)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnComprobar)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(BtnCompactar))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(346, 346, 346))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel29)
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(53, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Configuración", jPanel3);

        jLabel19.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 102, 255));
        jLabel19.setText("V1.51 2025");

        jLabel18.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 102, 255));
        jLabel18.setText("APLICACION DESARROLLADA POR EL DTO INFORMATICA DEL AYUNTAMIENTO DE MONZON");

        jLabel24.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 102, 255));
        jLabel24.setText("Usuario:");

        jLabel25.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 102, 255));
        jLabel25.setText("No identificado");

        BtnSalir.setBackground(new java.awt.Color(255, 204, 204));
        BtnSalir.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        BtnSalir.setText("SALIR");
        BtnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSalirActionPerformed(evt);
            }
        });

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Movimientos de caja", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 51, 204))); // NOI18N
        jPanel14.setForeground(new java.awt.Color(0, 51, 204));

        BtnHoyIndividual.setBackground(new java.awt.Color(204, 204, 255));
        BtnHoyIndividual.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        BtnHoyIndividual.setText("<html><center>HOY<br>INDIVIDUAL</center></html>");
        BtnHoyIndividual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHoyIndividualActionPerformed(evt);
            }
        });

        BtnConsultaCondicional.setBackground(new java.awt.Color(204, 204, 255));
        BtnConsultaCondicional.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        BtnConsultaCondicional.setText("<html><center>CONSULTA<br>CONDICIONAL</center></html>");
        BtnConsultaCondicional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnConsultaCondicionalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(BtnConsultaCondicional, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(BtnHoyIndividual, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnHoyIndividual, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnConsultaCondicional, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "Conexión con la Base de Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 51, 204)), "Conexion con la base de datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Open Sans", 0, 12), new java.awt.Color(0, 51, 255))); // NOI18N

        jLabel16.setFont(new java.awt.Font("Open Sans", 0, 12)); // NOI18N
        jLabel16.setText("Estado conexion con la base de datos");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addContainerGap(99, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel16)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(BtnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel18)
                                .addGap(23, 23, 23)
                                .addComponent(jLabel19))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(399, 399, 399)))))
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel18)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(BtnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jTabbedPane2.getAccessibleContext().setAccessibleName("Registrar");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSalirActionPerformed
            int option = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres salir?",
                    "Salir", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                basedatos.CerrarConexion();
                System.exit(0);
            }
              
       
    }//GEN-LAST:event_BtnSalirActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       
       int result = JOptionPane.showConfirmDialog(null, dateChooser, "Seleccionar Fecha", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    // Obtener la fecha seleccionada
                    Date selectedDate = dateChooser.getDate();
                     SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = sdf.format(selectedDate);
                    this.jTextField9.setText(formattedDate);
                }
    }//GEN-LAST:event_jButton2ActionPerformed
        //guarda los datos en la base de datos-apartado sellar
    private void BtnRegistrarApunteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRegistrarApunteActionPerformed
        try {

            if (this.CheckBoxCartaPago.isSelected()){
                 //si esta el check de carta de pago
                GenerarRecibo(HacerApunteConFactura());
            }else{
                // Si no está el check de carta de pago
                HacerApunteSinFactura();
            }
            
        } catch (InterruptedException | SQLException ex ) {
            JOptionPane.showMessageDialog(this, "Error "+ex, "Marco.java", JOptionPane.WARNING_MESSAGE);
        }
        this.jTextField6.setEditable(true);
        this.jTextField6.setEnabled(true);
        this.jTextField7.setEditable(true);
        this.jTextField7.setEnabled(true);
        this.jTextField6.setBackground(Color.WHITE);
        this.jTextField7.setBackground(Color.WHITE);
        this.CheckBoxCartaPago.setSelected(false);
        
    }//GEN-LAST:event_BtnRegistrarApunteActionPerformed

    private void formComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentAdded

    private void jComboBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox7ActionPerformed
        //si se selecciona busqueda por fecha se desabilita el campo de tesxto
        if (this.jComboBox7.getSelectedIndex() == 2) {
            // Deshabilitamos el JTextField si el segundo elemento está seleccionado
            this.jTextField13.setEnabled(false);
            this.jTextField14.setEnabled(true);
            this.jTextField15.setEnabled(true);
        } else {
            // Habilitamos el JTextField si otro elemento está seleccionado
            this.jTextField13.setEnabled(true);
            this.jTextField14.setEnabled(false);
            this.jTextField15.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBox7ActionPerformed

    private void BtnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBuscarActionPerformed
        // busca los datos en la bbdd y nos lo pinta en la jtable
        String miConsulta;
        //acondicionado de valores generales
        String miLibro = String.valueOf(this.jComboBox4.getSelectedIndex()+1);
        String textoconsulta = this.jTextField13.getText();
        String CamposDevueltos="CONCAT(NumLiq,'/',YLiq) As Apunte,Fecha,Documento,CONCAT(Apellido,' ',Apellido2,' ',Nombre) as Nombre,Descripcion,Entrada,Salida,Saldo";
        
        switch (this.jComboBox7.getSelectedIndex()) {
            case 0 -> //seleccion por descripcion
                miConsulta= "SELECT "+CamposDevueltos+" FROM Apuntes WHERE libro = " + miLibro + " AND Descripcion LIKE '" + textoconsulta + "'";
            case 1 -> //seleccion por nombre
             
                miConsulta= "SELECT "+CamposDevueltos+" FROM Apuntes WHERE libro = " + miLibro + " AND (Nombre LIKE '" + textoconsulta + "' OR Apellido LIKE '"+textoconsulta+"')";
            case 2 -> {
                //seleccion por fecha
                //buscamos y adaptamos las fechas para la busqueda
                String Fecha1 = herramientas.cambiarOrdenFecha(this.jTextField14.getText(),true);
                String Fecha2= herramientas.cambiarOrdenFecha(this.jTextField15.getText(),true);
                miConsulta= "SELECT "+CamposDevueltos+" from Apuntes WHERE libro = " + miLibro + " AND Fecha BETWEEN '" + Fecha1 + "' AND '" + Fecha2 + "'";
            }
            case 3 -> {
                //seleccion por importe
                String importe = herramientas.convertirComaAPunto(this.jTextField13.getText());
                miConsulta= "SELECT "+CamposDevueltos+" FROM Apuntes WHERE Entrada = " + importe + "  ";
            }
            case 4 -> //seleccion todo
                miConsulta= "SELECT "+CamposDevueltos+" FROM Apuntes WHERE libro = " + miLibro;
            default -> miConsulta= "SELECT * from Libro";
       
        }
        //String miConsulta= "SELECT * from Apuntes";
        DefaultTableModel miModelo = basedatos.consulta(miConsulta);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(miModelo);
        this.jTable1.setModel(miModelo);
        this.jTable1.setRowSorter(sorter);
        // Ajustar el ancho de las columnas
        TableColumnModel columnModel = jTable1.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(0); // Resetea el ancho
            columnModel.getColumn(i).setPreferredWidth(getMaximumWidthForColumn(i));
        }
       //al final borramos el texto de la consulta 
       this.jTextField13.setText("");
        
    }//GEN-LAST:event_BtnBuscarActionPerformed

    // Obtener el ancho máximo para una columna
    private int getMaximumWidthForColumn(int column) {
        int maxWidth = 0;
        for (int row = 0; row < jTable1.getRowCount(); row++) {
            TableCellRenderer cellRenderer = jTable1.getCellRenderer(row, column);
            Object value = jTable1.getValueAt(row, column);
            Component cell = cellRenderer.getTableCellRendererComponent(jTable1, value, false, false, row, column);
            maxWidth = Math.max(maxWidth, cell.getPreferredSize().width);
        }
        return maxWidth + 10; // Agrega un pequeño espacio extra
    }
    
    
    private void BtnSellarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSellarActionPerformed
       //boton imprimir
    SerialSender.imprimir("Apunte: "+this.jTextField16.getText());
    SerialSender.imprimir("Fecha: "+this.jTextField12.getText());
    SerialSender.imprimir("Importe: "+this.jTextField11.getText()+ " Euros");
    SerialSender.LiberarPapel(this.TxtPuertoCom.getText());

    }//GEN-LAST:event_BtnSellarActionPerformed

    private void BtnGuardarConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnGuardarConfigActionPerformed
        // Guardar la configuracion en el archivo
        List<String> config = new ArrayList<>();
        config.add(this.TxtIP.getText());  //uri db
        config.add(this.TxtPuertoCom.getText());  //com selladora
        ConfigManager.saveConfig(config);
    }//GEN-LAST:event_BtnGuardarConfigActionPerformed

    private void ComboMedioPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboMedioPagoActionPerformed
        //si se selecciona tarjeta pone en libro tarjeta y si selecciona efectivo selecciona libro caja sac
        int tipoPago=this.ComboMedioPago.getSelectedIndex();
        if (tipoPago==0) {
         this.ComboLibro.setSelectedIndex(0);
        }else {
         this.ComboLibro.setSelectedIndex(1);
        }
        
    }//GEN-LAST:event_ComboMedioPagoActionPerformed
 /**
 *
 * @author albertoaraguas
 * Boton que añade un usuario nuevo en la aplicacion 
 * 
 */
    private void BtnUserNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUserNuevoActionPerformed
        String nombre = JOptionPane.showInputDialog("Nombre y Apellido con un espacio");
        try{
         if (nombre != null &&!nombre.isEmpty() ){
           String pwd = JOptionPane.showInputDialog("Contraseña");
           if (pwd != null &&!pwd.isEmpty() ){
               UsuarioController uc = new UsuarioController(basedatos);
               uc.agregarUsuario(nombre, pwd);
               DefaultTableModel miListaNombres = basedatos.listaNombres();
               this.jTable2.setModel(miListaNombres);
           }
         }
        }catch (HeadlessException e){
           JOptionPane.showMessageDialog(this, "Error al ingresar usuario", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_BtnUserNuevoActionPerformed

 /**
 *
 * Función que busca los datos en apeion y los mete en los textos correspondientes 
 * 
 */
    private void getAperionData(){
    
    String documento=this.jTextField1.getText();
        int assignedValue=1;
        try {
            // recogemos el dato de tipodocumetno y documento para buscar en apeiron
             String selectedItem = (String) this.ComboTipoDocumento.getSelectedItem();
             if (selectedItem != null) {
                switch (selectedItem) {
                    case "DNI":
                        assignedValue = 1;
                    break;
                    case "NIF":
                        assignedValue = 4;
                    break;
                    case "PASAPORTE":
                        assignedValue = 2;
                    break;
                    case "SIN DOCUMENT":
                        assignedValue = 0;
                    break;
                    case "TARJ RESIDENTE":
                        assignedValue = 3;
                    break;
                    default:
                        assignedValue = 1; // Valor por defecto si el elemento no coincide
                    break;
                }
             }        
           
            ApeironConector apeiron = new ApeironConector();
            if (apeiron.Conectar()){
              String[] resultado= apeiron.getDatosApeiron(assignedValue, documento);
              if (resultado[0] == null ){
                JOptionPane.showMessageDialog(null, "No existe en Apeiron", "Resultado", JOptionPane.WARNING_MESSAGE);    
              }else{        
                this.jTextField2.setText(resultado[1]);
                this.jTextField3.setText(resultado[2]);
                this.jTextField4.setText(resultado[0]);
                this.jTextField19.setText(resultado[3]);
              }
              apeiron.Desconectar();
            }
        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error Acceso a Apeiron Pulsa Modificar Propiedades Java en Configuracion", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    
 /**
 *
 * Función que comprueba el DNI y si esta
 * rellena el nombre y direccion
 */
    private void ComprobarDNI() throws SQLException{
    //comprueba el dni y si esta carga los datos en la pantalla
        this.jTextField2.setText("");
        this.jTextField3.setText("");
        this.jTextField4.setText("");
        this.jTextField19.setText("");  
        int tipo=this.ComboTipoDocumento.getSelectedIndex();
        String doc = this.jTextField1.getText();
        //if (basedatos.isConected()){
            if(basedatos.ExisteUsuario(tipo,doc)){
                String[] resultado=basedatos.DatosUsuario(tipo, doc);
                this.jTextField2.setText(resultado[1]);
                this.jTextField3.setText(resultado[2]);
                this.jTextField4.setText(resultado[0]);
                this.jTextField19.setText(resultado[3]);        
            }else{
             //si no esta el usuario, habilitamos el boton para buscarlo en apeiron   
             this.BtnApeiron.setEnabled(true);
            }
        //}
    }
    
 /**
 *
 * Este registra en la base de datos y devuelve los datos para generar la factura
 * @param targetWidth ancho del icono
 * @param targetHeight altura del icono
 * @param  imagePath ruta del icono
 * @return Image ajustada
 */
    public Image prepareImage(String imagePath, int targetWidth, int targetHeight) {
        Image originalImage = Toolkit.getDefaultToolkit().getImage(imagePath);
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return resultingImage;
    }
          
 /**
 *
 * borra el usuario seleccionado
 */
    private void BtnUserBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUserBorrarActionPerformed
       try{ 
        String nombre= (String) this.jTable2.getValueAt(jTable2.getSelectedRow(), jTable2.getSelectedColumn());
        UsuarioController uc = new UsuarioController(basedatos);
        if (nombre.equalsIgnoreCase("admin")){
          JOptionPane.showConfirmDialog(rootPane, "AVISO","El usuario administrador no se puede borrar", JOptionPane.CLOSED_OPTION);
        }else{
            uc.eliminarUsuario(nombre);
        }
        DefaultTableModel miListaNombres = basedatos.listaNombres();
        this.jTable2.setModel(miListaNombres);
       }catch (HeadlessException e){
         JOptionPane.showConfirmDialog(rootPane, "Selecciona el nombre a borrar","Confirmación", JOptionPane.CLOSED_OPTION);
       } 
    }//GEN-LAST:event_BtnUserBorrarActionPerformed
 /**
 *
 * cambia la contraseña
 */
    private void BtnPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPasswordActionPerformed
        try{
         String nombre= (String) this.jTable2.getValueAt(jTable2.getSelectedRow(), jTable2.getSelectedColumn());
         String pass = JOptionPane.showInputDialog("Nueva contraseña de "+nombre);
            UsuarioController uc = new UsuarioController(basedatos);
            if (uc.cambiarContrasena(nombre, pass)){
                JOptionPane.showMessageDialog(rootPane, "Contraseña cambiada correctamente");
            }
        }catch (HeadlessException e){
         JOptionPane.showConfirmDialog(rootPane, "Selecciona el nombre a borrar","Confirmación", JOptionPane.CLOSED_OPTION);
        } 
        
    }//GEN-LAST:event_BtnPasswordActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        //rellena los campos de usuario si el usuario existe en la base de datos
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField16ActionPerformed
/**
 *
 * busca uduarios y se vuelve a deshabilitar
 */
    private void BtnApeironActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnApeironActionPerformed
        //cuando el bobon esta habilitado, busca uduarios y se vuelve a deshabilitar
        getAperionData();      
        this.BtnApeiron.setEnabled(false);
    }//GEN-LAST:event_BtnApeironActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox4ActionPerformed
/**
 * sella el registro seleccionado
 * si no hay registro seleccionado manda un pop y sale
 */
    private void BtnSellarSeleccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSellarSeleccionActionPerformed
       
    int filaSeleccionada = this.jTable1.getSelectedRow();
    if (filaSeleccionada != -1) { // Si hay alguna fila seleccionada
       //recupero los datos del registro para sellar 
        String nreg = jTable1.getValueAt(filaSeleccionada, 0).toString();  //columna apunte
        //String Yreg = jTable1.getValueAt(filaSeleccionada, 1).toString();
        String freg = herramientas.cambiarOrdenFecha(jTable1.getValueAt(filaSeleccionada, 1).toString(),false); //columna fecha
        String impreg = String.valueOf(jTable1.getValueAt(filaSeleccionada, 5));  //columna Entrada importe
        SerialSender.imprimir("Apunte: "+nreg);
        SerialSender.imprimir("Fecha: "+freg);
        SerialSender.imprimir("Importe: "+impreg+ " Euros");
        SerialSender.LiberarPapel(this.TxtPuertoCom.getText());
    } else { // Si no hay ninguna fila seleccionada
        JOptionPane.showMessageDialog(this, "No has seleccionado ningún registro", "Mensaje", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_BtnSellarSeleccionActionPerformed
/**
 * funcion
 */
    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        
        
    }//GEN-LAST:event_jTextField6ActionPerformed
/**
 * funcion
 */
    private void jTextField6InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTextField6InputMethodTextChanged
       
    }//GEN-LAST:event_jTextField6InputMethodTextChanged
/**
 * funcion
 */
    private void jTextField6KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyTyped
    char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != ',' && c != '.') {
            evt.consume(); // ignora el caracter ingresado
        }
        try {
            // si es entrada, inhabilitamos salida y cargamos combo concepto con entradas
            if (basedatos.isConected()){
                String miString = this.jTextField6.getText();
                if (miString != null && !miString.equals("")  ){
                    this.jTextField7.setEnabled(false);
                    this.jTextField7.setBackground(Color.LIGHT_GRAY);
                    this.jComboBox5.setEnabled(true);
                    listados = basedatos.leerDescripcionesTabla("Concepto", "CDescripcion",1);
                    this.jComboBox5.removeAllItems();
                    for (String elemento : listados) {
                        this.jComboBox5.addItem(elemento);
                    }
                } else {
                    this.jTextField7.setEnabled(true);
                    this.jTextField7.setBackground(Color.WHITE);
                    this.jComboBox5.removeAllItems();
                }
            }else{
                JOptionPane.showMessageDialog(this, "No hay conexion con la base de datos", "Error conexion", JOptionPane.WARNING_MESSAGE);
            }} catch (SQLException ex) {
           JOptionPane.showMessageDialog(null, "Error Acceso a la base de datos", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jTextField6KeyTyped
/**
 * funcion
 */
    private void jTextField7KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != ',' && c != '.') {
            evt.consume(); // ignora el caracter ingresado
        }
        try {
            //
            if (basedatos.isConected()) {
                String miString = this.jTextField7.getText();
                if (miString != null && !miString.equals("")  ){
                    this.jTextField6.setEnabled(false);
                    this.jTextField6.setBackground(Color.LIGHT_GRAY);
                    this.jComboBox5.setEnabled(true);
                    listados = basedatos.leerDescripcionesTabla("Concepto", "CDescripcion",0);
                    this.jComboBox5.removeAllItems();
                    for (String elemento : listados) {
                        this.jComboBox5.addItem(elemento);
                    }
                } else {
                    this.jTextField6.setEnabled(true);
                    this.jTextField6.setBackground(Color.WHITE);
                    this.jComboBox5.removeAllItems();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error Acceso a la base de datos", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jTextField7KeyTyped
/**
 * funcion
 */
    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

/**
 * funcion
 */
    private void BtnPagoSeleccionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPagoSeleccionadoActionPerformed
        // carta pago de un registro buscado
    int filaSeleccionada = this.jTable1.getSelectedRow();
    if (filaSeleccionada != -1 && basedatos.conexion) { // Si hay alguna fila seleccionada y hay conexion con la base de datos
       //recupero los datos del registro para la carta de pago
       String[] datosFactura = {" ", " ", " ", " "," "," "," "};
        String nreg = jTable1.getValueAt(filaSeleccionada, 0).toString();       //columna apunte
        //String Yreg = jTable1.getValueAt(filaSeleccionada, 1).toString();
        String impreg = String.valueOf(jTable1.getValueAt(filaSeleccionada, 5));//columna importe
        datosFactura[0]=(" "+nreg);            //carga el valor apunte
        datosFactura[4]=jTable1.getValueAt(filaSeleccionada, 3).toString();
        datosFactura[3]=jTable1.getValueAt(filaSeleccionada, 2).toString();
        datosFactura[2]=(" "+impreg);
        //datosFactura[6]=jTable1.getValueAt(filaSeleccionada, 4).toString();   //pone el campo descripcion en la carta de pago
          //consultar el tipo de pago y la dirección del registro seleccionado
        String[] misdatos=basedatos.DatosFacturaUsuario(nreg);
        if (misdatos[0] != null){datosFactura[1]= misdatos[0];}
        if (misdatos[1] != null){datosFactura[5]= misdatos[1];}
        if (misdatos[2] != null){datosFactura[6]= misdatos[2];}  //pone el campo concepto en la carta de pago
        GenerarRecibo(datosFactura);  
    } else { // Si no hay ninguna fila seleccionada
        JOptionPane.showMessageDialog(this, "No has seleccionado ningún registro", "Mensaje", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_BtnPagoSeleccionadoActionPerformed
/**
 * funcion
 */
    private void BtnHoyIndividualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHoyIndividualActionPerformed
            
        try {
            //String ConsultaUsuario= "SELECT Fecha,Nombre,Apellido,Entrada from Apuntes WHERE usuario='"+usuario+"'";
            String currentDate=new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String ConsultaUsuario= "SELECT CONCAT(CAST(ap.NumLiq AS CHAR), '/', ap.YLiq) AS Apunte, CONCAT(ap.Apellido, ' ', ap.Apellido2, ', ', ap.Nombre) AS Nombre,mp.tipo, ap.Descripcion, c.CDescripcion AS Concepto, ap.Entrada " +
                    "FROM Apuntes ap " +
                    "JOIN mediopago mp ON ap.Medio = mp.IdMedio " +
                    "JOIN Concepto c ON ap.Concepto = c.IdConcepto "+
                    "WHERE usuario='"+usuario+"' "+
                    "AND ap.Fecha='"+currentDate+"'";
            if (basedatos.isConected()){
                DefaultTableModel miModelo2 = basedatos.consulta(ConsultaUsuario);
                DiarioMovimientoIndividual diarioI = new DiarioMovimientoIndividual(miModelo2 ,usuario);
                diarioI.setLocationRelativeTo(null);
                diarioI.setTitle("Diario Movimientos Individual");
                diarioI.setIconImage(prepareImage("C:/librocaja/images/ico.jpg", 32, 32));
                diarioI.setVisible(true);
            }else{
                JOptionPane.showMessageDialog(this, "No hay conexión a la base de datos", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error Acceso a la base de datos", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_BtnHoyIndividualActionPerformed
/**
 * funcion
 */
    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        try {
            //aqui debe ejecutar lo mismo que el boton comprobar
            ComprobarDNI();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error Acceso a la base de datos", "Warning", JOptionPane.WARNING_MESSAGE);
        }
       
    }//GEN-LAST:event_jTextField1FocusLost
/**
 * funcion
 */
    private void jTextField6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusLost
    if (this.jTextField16.getText() != null && basedatos.conexion ){
        String miString = this.jTextField6.getText();
        if (miString != null && !miString.equals("") ){
            this.jTextField7.setEnabled(false);
            this.jTextField7.setBackground(Color.LIGHT_GRAY);
            this.jComboBox5.setEnabled(true);
            listados = basedatos.leerDescripcionesTabla("Concepto", "CDescripcion",1);
            this.jComboBox5.removeAllItems();
                for (String elemento : listados) {
                    this.jComboBox5.addItem(elemento);
                }
        } else {
           this.jTextField7.setEnabled(true);
           this.jTextField7.setBackground(Color.WHITE);
           this.jComboBox5.removeAllItems();
        }
    }
    }//GEN-LAST:event_jTextField6FocusLost
/**
 * funcion
 */
    private void jTextField7FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusLost
      if (this.TxtPuertoCom.getText() != null && basedatos.conexion){
        String miString = this.jTextField7.getText();
        if (miString != null && !miString.equals("")  ){
            this.jTextField6.setEnabled(false);
            this.jTextField6.setBackground(Color.LIGHT_GRAY);
            this.jComboBox5.setEnabled(true);
            listados = basedatos.leerDescripcionesTabla("Concepto", "CDescripcion",0);
            this.jComboBox5.removeAllItems();
                for (String elemento : listados) {
                    this.jComboBox5.addItem(elemento);
                }
        } else {
           this.jTextField6.setEnabled(true);
           this.jTextField6.setBackground(Color.WHITE);
           this.jComboBox5.removeAllItems();
        }
      }
    }//GEN-LAST:event_jTextField7FocusLost
/**
 * funcion
 */
    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
          evt.setKeyChar(Character.toUpperCase(c));
        }
        
    }//GEN-LAST:event_jTextField1KeyTyped
/**
 * funcion
 */
    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed
/**
 * funcion
 */
    private void BtnConsultaCondicionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnConsultaCondicionalActionPerformed
        try {
            MovimientoDetallado diario =new MovimientoDetallado(basedatos);
            diario.setLocationRelativeTo(null);
            diario.setTitle("Seleccion de consulta de movimientos");
            diario.setIconImage(prepareImage("C:/librocaja/images/ico.jpg", 32, 32));
            diario.setVisible(true);
        } catch (SQLException ex) {
           JOptionPane.showMessageDialog(null, "Error acceso ", "InformeMovimientoUsuario.java", JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_BtnConsultaCondicionalActionPerformed
/**
 * funcion
 */
    private void TxtIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtIPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtIPActionPerformed

    private void jTextField1InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTextField1InputMethodTextChanged
     
    }//GEN-LAST:event_jTextField1InputMethodTextChanged

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
      //comprobar si hay 9 digitos escritos
        int n=this.jTextField1.getText().length();
        if (n >= 9){
            //seleccionar segun el combobox si es DNI o NIE
            switch ((String) this.ComboTipoDocumento.getSelectedItem()){
                case "DNI":
                            if (DocCheck.validarDNI(this.jTextField1.getText())){
                                this.jTextField1.setBackground(Color.green);
                                this.jTextField1.setForeground(Color.black);
                            }else{
                                this.jTextField1.setBackground(Color.red);
                                this.jTextField1.setForeground(Color.white);
                            } 
                    break;    
                case "TARJ RESIDENTE":
                            if (DocCheck.validarNIE(this.jTextField1.getText())){
                                this.jTextField1.setBackground(Color.green);
                                this.jTextField1.setForeground(Color.black);
                            }else{
                                this.jTextField1.setBackground(Color.red);
                                this.jTextField1.setForeground(Color.white);
                            } 
                    break;
                default:
                    break;
            }
        }else if (n<8){
         this.jTextField1.setBackground(Color.white);
         this.jTextField1.setForeground(Color.black);
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void CheckBoxCartaPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBoxCartaPagoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CheckBoxCartaPagoActionPerformed

    private void BtnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnModificarActionPerformed
        JavaSecurityModifier mod= new JavaSecurityModifier();
      
    }//GEN-LAST:event_BtnModificarActionPerformed

    private void BtnComprobarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnComprobarActionPerformed
        // chekear que no haya apuntes duplicados y presentar en un pop up
        
         JOptionPane.showMessageDialog(null, basedatos.verificarDuplicados(), "Apuntes Duplicados", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_BtnComprobarActionPerformed

    private void BtnCompactarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCompactarActionPerformed
        try {
            basedatos.consultaSQL("OPTIMIZE TABLE Apuntes");
            JOptionPane.showMessageDialog(null, "LA OPTIMIZACION PUEDE LLEVAR UNOS MINUTOS....", "ESPERE", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
           JOptionPane.showMessageDialog(null, "Error Acceso a la base de datos", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_BtnCompactarActionPerformed
/**
 * funcion
 */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnApeiron;
    private javax.swing.JButton BtnBuscar;
    private javax.swing.JButton BtnCompactar;
    private javax.swing.JButton BtnComprobar;
    private javax.swing.JButton BtnConsultaCondicional;
    private javax.swing.JButton BtnGuardarConfig;
    private javax.swing.JButton BtnHoyIndividual;
    private javax.swing.JButton BtnModificar;
    private javax.swing.JButton BtnPagoSeleccionado;
    private javax.swing.JButton BtnPassword;
    private javax.swing.JButton BtnRegistrarApunte;
    private javax.swing.JButton BtnSalir;
    private javax.swing.JButton BtnSellar;
    private javax.swing.JButton BtnSellarSeleccion;
    private javax.swing.JButton BtnUserBorrar;
    private javax.swing.JButton BtnUserNuevo;
    private javax.swing.JCheckBox CheckBoxCartaPago;
    private javax.swing.JComboBox<String> ComboLibro;
    private javax.swing.JComboBox<String> ComboMedioPago;
    private javax.swing.JComboBox<String> ComboTipoDocumento;
    private javax.swing.JTextField TxtIP;
    private javax.swing.JTextField TxtPuertoCom;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}

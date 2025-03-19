
package com.aytomonzon.librosac;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.*;
/**
 * Clase para la gestion de consultas a la base de datos
 * 
 * @author albertoaraguas
 * @version 1.0
 * @since 2025-02-25
 * 
 */
public  final class ConectorDB {
   
    private String miURL="jdbc:mysql://192.168.5.9:3306/Librocaja";
    private static final String USUARIO = "MySql";
    private static final String CONTRASEÑA = "22400Monzon";
    private Connection conn = null;
    /**
    * Variable que indica el estado de la conexión con la base de datos.
    * {@code true} si está conectada, {@code false} si no lo está.
    */
    public boolean conexion=false;

    /**
    *
    * Constructor de la clase
    * @param URL direccion de la base de datos en formato ip:puerto
    */
    public ConectorDB(String URL) {
        this.miURL="jdbc:mysql://"+URL+"/Librocaja";
         Conectar();
    }
    
    /**
    *
    * Metodo que conecta con la base de datos y deja 
    * en la variable conn la conexion
    */
    public void Conectar(){
        conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(miURL, USUARIO, CONTRASEÑA);
            if (conn != null) {
                System.out.println("Conexión exitosa a la base de datos MySQL.");
                conexion=true;
                conn.setAutoCommit(false);
            }
        } catch (ClassNotFoundException e) {
            conexion=false;
            JOptionPane.showMessageDialog(null, "No se pudo encontrar el driver JDBC.", "Warning", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            conexion=false;
            JOptionPane.showMessageDialog(null, "No se puedo conectar a la base de datos", "Warning", JOptionPane.WARNING_MESSAGE);
        } finally {
            
        }
   } 
   
   /**
   *
   * Metodo que cierra la conexión con la base de datos 
   * 
   */ 
   public void CerrarConexion(){
        try {
                if (conexion) {
                    conn.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexion ", "ConectorDB.java", JOptionPane.WARNING_MESSAGE);
            }
   }
   
   /**
   *
   * Función que comprueba el estado de la conexión con la base de datos 
   * @return {@code true} si esta conectado y {@code false} si no lo está
   * @throws java.sql.SQLException si ocurre un error al verificar la conexion
   */
   public boolean isConected() throws SQLException{
    if (!conn.isClosed()) {
        return true;
    } else {
        return false;
    }
   }
   /**
   *
   * Función que calcula el año actual y lo devuelve en formato YYYY
   * @return String con el año en formato YYYY
   */
   private String calculaYear(){
    // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();
    // Formatear el año en formato YYYY
        String añoActual = fechaActual.format(DateTimeFormatter.ofPattern("yyyy"));
    return añoActual;
   }
           
   /**
   *
   * Función que recoge el ultimo numliq y lo actualiza
   * Si es el primero del año, lo resetea a 1
   * @return numero de numliq
   */
   private int calculaNumLiq(){
  
    try{
     String consulta = "SELECT COALESCE(MAX(NumLiq), 0) FROM Apuntes WHERE YLiq="+calculaYear();   
     Statement stmt = conn.createStatement();
     ResultSet rs= stmt.executeQuery(consulta);
     // Procesar el resultado
            if (rs.next()) {
                String resultadoString = rs.getString(1); // Obtener el resultado como String
                int num=Integer.parseInt(resultadoString);    
                 return num+1;
            }
     rs.close();
     stmt.close();
     }catch(SQLException e){
      JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base DAtos", JOptionPane.WARNING_MESSAGE);
      return 9999;
     }
   return 9999;
   }
   
   /**
    *
    * Metodo que inserta datos en la bbdd
    * @param Fecha Fecha del asiento
    * @param Libro Efectivo o tarjeta
    * @param Nombre N Persona 
    * @param Apellido A Persona
    * @param Apellido2 A Persona
    * @param Descripcion Detalle del asiento
    * @param TipoDoc tipo documento ident
    * @param Numero Numero del documento
    * @param Concepto descripcion 
    * @param Medio forma pago
    * @param In    cash in
    * @param Out   cash out
    * @param Saldo Saldo global
    * @param direccion direccion persona
    * @param usuario trabajador
    * @return 0 si da error o 1 si ok // EL IDAPUNTE ASIGNADO
    * @throws java.sql.SQLException error conexion bas datos
    * @throws java.lang.InterruptedException  error interupcion
    */             
   public int InsertaDatos ( String Fecha, int Libro, String Nombre,String Apellido,String Apellido2,String Descripcion,int TipoDoc,String Numero,int Concepto,int Medio, double In,double Out,double Saldo, String direccion,String usuario   ) throws SQLException, InterruptedException{
     //recogemos los datos
     
     int estado;
  
     String sql_insert = "INSERT INTO Apuntes (Fecha,Libro,Nombre,Apellido,Apellido2,Descripcion,TipoDocumento,Documento,Concepto,Medio,Entrada,Salida,Saldo,Direccion,usuario,NumLiq,Yliq) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+ calculaNumLiq() +","+calculaYear()+")";
     try{
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        conn.setAutoCommit(false);
        PreparedStatement pstmt = conn.prepareStatement (sql_insert);
        pstmt.setString(1, Fecha);
        pstmt.setInt(2, Libro);
        pstmt.setString(3, Nombre);
        pstmt.setString(4, Apellido);
        pstmt.setString(5, Apellido2);
        pstmt.setString(6, Descripcion);
        pstmt.setInt(7, TipoDoc);
        pstmt.setString(8, Numero);
        pstmt.setInt(9, Concepto);
        pstmt.setInt(10, Medio);
        pstmt.setDouble(11, In);
        pstmt.setDouble(12, Out);
        pstmt.setDouble(13, Saldo);
        pstmt.setString(14, direccion);
        pstmt.setString(15, usuario);
        pstmt.executeUpdate();
        conn.commit();
        
        estado=1;
       
     }catch(SQLException e){
        conn.rollback();
         JOptionPane.showMessageDialog(null, "Espere mientras se guardan los datos", "ConectorDB.java", JOptionPane.WARNING_MESSAGE);
          estado= 0;
    }
      return estado;
    }
   
    /**
    *
    * Clase para leer el IdApunte
    * @return String con el IdApunte creado
    */
   public String leerIDApunte (){
    //recogemos los datos
    String ida="";
    try{
 
     String consulta = "SELECT IdApunte FROM Apuntes DESC LIMIT 1";   
     Statement stmt = conn.createStatement();
     ResultSet rs= stmt.executeQuery(consulta);
     ida = rs.getString("IdApunte");
     rs.close();
     stmt.close();
     
     }catch(SQLException e){
      JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
     }
     return ida;
   }

   /**
    *
    * Clase para leer el titulo de una tabla
    * @param Tabla tabla de la que leer
    * @param Campo Campo para leer
    * @return ArrayList con las descripciones
    */
   public ArrayList<String> leerDescripcionesTabla (String Tabla,String Campo){
    //recogemos los datos
    ArrayList<String> descripciones = new ArrayList<>();

    try{
     String consulta = "SELECT "+ Campo + " FROM " + Tabla;   
     Statement stmt = conn.createStatement();
     ResultSet rs= stmt.executeQuery(consulta);
     while (rs.next()){
         String desc = rs.getString(Campo);
         descripciones.add(desc);        
     }
     rs.close();
     stmt.close();
     
     }catch(SQLException e){
      JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base DAtos", JOptionPane.WARNING_MESSAGE);
     }
     return descripciones;
   }
   
    /**
    *
    * Clase para leer el contenido de una table
    * @param Tabla tabla de la que leer
    * @param Campo Campo para leer
    * @param EsEntrada indica si es dinero que entra o sale
    * @return ArrayList con los datos 
    */
   public ArrayList<String> leerDescripcionesTabla (String Tabla,String Campo,int EsEntrada){
     //recogemos los datos
    ArrayList<String> descripciones = new ArrayList<>();
    try{
     String consulta = "SELECT "+ Campo + " FROM " + Tabla+" WHERE EsEntrada="+EsEntrada;   
     Statement stmt = conn.createStatement();
     ResultSet rs= stmt.executeQuery(consulta);
     while (rs.next()){
         String desc = rs.getString(Campo);
         descripciones.add(desc);        
     }
      rs.close();
      stmt.close();
     }catch(SQLException e){
       JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base DAtos", JOptionPane.WARNING_MESSAGE);
     }
     return descripciones;
   }
   
   
   /**
   *
   * Esta funcion devuelve una matriz unidimensional con los nombres de los trabajadores
   * @return un string[] de textos 
   */
   public String[] getTrabajadores(){
    List<String> datalist = new ArrayList<>();
        try{
            String consulta = "SELECT Nombre FROM usuarios ORDER BY Nombre" ;   
            Statement stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(consulta);
            int i=0;
            while (rs.next()){
                String desc = rs.getString("Nombre");
                datalist.add(desc);
                i++;
            }
            rs.close();
            stmt.close();
            return datalist.toArray(new String[0]);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
   }
   
   /**
   *
   * Esta funcion devuelve un listado de usuarios que han hecho algun movimiento en el libro y rango de fechas consignadas
   * @param listaUsuarios una cadena de texto de usuarios separados por comas
   * @param Libro si es libro saco o tarjeta (1 o 2)
   * @param Fecha texto entre dos fechas BETWEEN fecha1 AND fecha 2
   * @return un List de String 
   */
   public List<String> UsuariosIntervalo (String listaUsuarios,String Libro,String Fecha){
       List<String> datalist = new ArrayList<>();
        try{
            String consulta= "SELECT usuario " +
                                 " FROM Apuntes " +
                                 " WHERE Libro="+Libro+
                                 " AND "+Fecha+
                                 " AND usuario IN ("+listaUsuarios+")"+
                                 " GROUP BY usuario";
            
            Statement stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(consulta);
            int i=0;
            while (rs.next()){
                String desc = rs.getString("Usuario");
                datalist.add(desc);
                i++;
            }
            rs.close();
            stmt.close();
      }catch(SQLException e){
           System.err.println(e);
           datalist.add("Sin Datos");
      }
       return datalist;
    }       
   
   /**
   *
   * devuelve un listado de conceptos con movimientos en el libro y rango de fechas consignadas
   * @param listaConceptos una cadena de texto de conceptos
   * @param Libro si es libro saco o tarjeta (1 o 2)
   * @param Fecha texto entre dos fechas BETWEEN fecha1 AND fecha 2
   * @return un List de String 
   */
   public List<String> ConceptosIntervalo (String listaConceptos,String Libro,String Fecha){
       List<String> datalist = new ArrayList<>();
       try{
            String consulta= "SELECT c.CDescripcion as Concepto" +
                                 " FROM Apuntes as ap" +
                                 " JOIN Concepto as c ON ap.Concepto=c.IdConcepto"+
                                 " WHERE Libro="+Libro+
                                 " AND "+Fecha+
                                 " AND c.CDescripcion IN ("+listaConceptos+")"+
                                 " GROUP BY c.CDescripcion";
            
            Statement stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(consulta);
            int i=0;
            while (rs.next()){
                String desc = rs.getString("Concepto");
                datalist.add(desc);
                i++;
            }
            rs.close();
            stmt.close();
      }catch(SQLException e){
           System.err.println(e);
           datalist.add("Sin Datos");
      }
        return datalist;
    }       
   
   /**
   *
   * devuelve un listado de conceptos con movimientos en el libro y rango de fechas consignadas
   * @param Usuario consulta por usuario
   * @param Libro si es libro saco o tarjeta (1 o 2)
   * @param Fecha texto entre dos fechas BETWEEN fecha1 AND fecha 2
   * @return devuelve una matriz con todos los registros de un usuario y rango de fechas para el informe
   */
   public Object[][] RegistrosUsuariosIntervalo(String Usuario,String Libro, String Fecha){
   Object[][] matriz=null; 
   try{
        String Consulta= "SELECT  ap.Fecha, CONCAT(ap.NumLiq, '/', ap.YLiq) AS Apunte, ap.Documento,CONCAT(ap.Apellido, ' ', ap.Apellido2, '  ', ap.Nombre) AS Nombre,ap.Descripcion,c.CDescripcion,ap.Entrada,ap.Salida" +
                    " FROM Apuntes as ap" +
                    " JOIN Concepto as c ON ap.Concepto=c.IdConcepto"+
                    " WHERE Libro="+Libro+" "+
                    " AND "+Fecha+
                    " AND usuario = '"+ Usuario +"'"+
                    " ORDER BY ap.Fecha";
        //necesitamos saber la cantidad de registros que nos devuelve, lo calculamos con 
        Statement stmt = conn.createStatement();
        // Get the number of rows in the query result
         ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM (" + Consulta + ") AS subquery");
         countRs.next();
         int rowCount = countRs.getInt(1);
        //ya sabemos las filas, ahora hacemos la consulta buena
        ResultSet rs= stmt.executeQuery(Consulta);
        // Get the metadata of the ResultSet
        ResultSetMetaData rsmd = rs.getMetaData();
        matriz = new Object[rowCount+1][rsmd.getColumnCount()];
        // Iterate through the ResultSet and populate the matrix
            int rowIndex = 0;
            while (rs.next()) {
                for (int colIndex = 0; colIndex < rsmd.getColumnCount(); colIndex++) {
                    matriz[rowIndex][colIndex] = rs.getObject(colIndex + 1);
                }
                rowIndex++;
            }
   }catch (SQLException e){
     JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
   }
   return matriz;
   }
   
   /**
   *
   * devuelve una matriz con todos los registros de un concepto y rango de fechas para el informe
   * @param Concepto consulta por concepto
   * @param Libro si es libro saco o tarjeta (1 o 2)
   * @param Fecha texto entre dos fechas BETWEEN fecha1 AND fecha 2
   * @return devuelve una matriz con todos los registros filtrado por conepto y rango de fechas para el informe
   */
   public Object[][] RegistrosConceptoIntervalo(String Concepto,String Libro, String Fecha){
   Object[][] matriz=null; 
   try{
        String Consulta= "SELECT  ap.Fecha,CONCAT(ap.NumLiq, '/', ap.YLiq) AS Apunte, ap.Documento,CONCAT(ap.Apellido, ' ', ap.Apellido2, '  ', ap.Nombre) AS Nombre,ap.Descripcion,ap.Usuario,ap.Entrada,ap.Salida" +
                    " FROM Apuntes as ap" +
                    " JOIN Concepto as c ON ap.Concepto=c.IdConcepto"+
                    " WHERE Libro="+Libro+" "+
                    " AND "+Fecha+
                    " AND c.CDescripcion = '"+ Concepto +"'"+
                    " ORDER BY c.CDescripcion";
        //necesitamos saber la cantidad de registros que nos devuelve, lo calculamos con 
        Statement stmt = conn.createStatement();
        // Get the number of rows in the query result
         ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM (" + Consulta + ") AS subquery");
         countRs.next();
         int rowCount = countRs.getInt(1);
        //ya sabemos las filas, ahora hacemos la consulta buena
        ResultSet rs= stmt.executeQuery(Consulta);
        // Get the metadata of the ResultSet
        ResultSetMetaData rsmd = rs.getMetaData();
        matriz = new Object[rowCount+1][rsmd.getColumnCount()];
        // Iterate through the ResultSet and populate the matrix
            int rowIndex = 0;
            while (rs.next()) {
                for (int colIndex = 0; colIndex < rsmd.getColumnCount(); colIndex++) {
                    matriz[rowIndex][colIndex] = rs.getObject(colIndex + 1);
                }
                rowIndex++;
            }
   
   }catch (SQLException e){
     JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
   }
   return matriz;
   } 
   
   /**
   *
   * Esta funcion devuelve un string[] con los conceptos
   * @return devuelve un vector con la lista de conceptos
   */
   public String[] getConceptos(){
    List<String> datalist = new ArrayList<>();
        try{
            String consulta = "SELECT CDescripcion FROM Concepto" ;   
            Statement stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(consulta);
            int i=0;
            while (rs.next()){
                String desc = rs.getString("CDescripcion");
                datalist.add(desc);
                i++;
            }
            rs.close();
            stmt.close();
            return datalist.toArray(new String[0]);
        }catch(SQLException e){
           JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
           return null;
        }
   }
   
/**
   *
   * Esta funcion consulta si hay apuntes duplicaddos
   * @return devuelve un vector con la lista de conceptos
   */
   public  String verificarDuplicados() {
        String consulta = "SELECT NumLiq, YLiq, COUNT(*) AS Cantidad " +
                          "FROM Apuntes " +
                          "GROUP BY NumLiq, YLiq " +
                          "HAVING COUNT(*) > 1;";
        StringBuilder resultado = new StringBuilder();
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(consulta);
            
            if (!rs.isBeforeFirst()) { // Verifica si no hay resultados
                return "No hay registros duplicados.";
            } else {
                resultado.append("Hay elementos duplicados: ");
                while (rs.next()) {
                    String numLiq = rs.getString("NumLiq");
                    String yLiq = rs.getString("YLiq");
                    int cantidad = rs.getInt("Cantidad");
                    resultado.append(String.format("Apunte: %s/%s (Cantidad: %d); ", numLiq, yLiq, cantidad));
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
           return "Error al verificar duplicados: " + e.getMessage();
        }
        
        return resultado.toString();
    } 
   
   
   
   
   
   
   /**
   *
   * Esta funcion devuelve nombre y contraseña
   * @param Usuario El usuario a consultar
   * @return devuelve un vector con el nombre y la contraseña
   */
   public String[] RecuperaAccesoUsuario (String Usuario){
     //recogemos los datos
    String[] datUser= {"string1", "string2"};
    try{
     String consulta = "SELECT Nombre,Contraseña FROM usuarios WHERE Nombre = '"+Usuario+"'" ;   
     Statement stmt = conn.createStatement();
     ResultSet rs= stmt.executeQuery(consulta);
     while (rs.next()){
         String desc = rs.getString("Nombre");
         datUser[0]=desc;
         desc = rs.getString("Contraseña");
         datUser[1]=desc;
     }
     rs.close();
     stmt.close();
     
     }catch(SQLException e){
      JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
     }
     return datUser;
   }
   
   /**
   *
   * Esta funcion devuelve true si el usuario existe en la base de datos
   * @param tipodoc Integer indicando si es DNI, Pasaporte...
   * @param DNI el numero del documento
   * @return {@code true} el usuario existe, y {@code false} si no existe
   */
   public boolean ExisteUsuario(int tipodoc,String DNI){
    try {
        String sql = "SELECT COUNT(*) FROM Apuntes WHERE TipoDocumento = ? AND Documento = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, tipodoc+1);
        pstmt.setString(2, DNI);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
        int count = rs.getInt(1);
        return count > 0;
        }   
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
    }
   return false;
  }
   
   /**
   *
   * Esta funcion devuelve nombre,apellidos y direccion si el dni existe en la bbdd
   * @param tipodoc Integer indicando si es DNI, Pasaporte...
   * @param DNI el numero del documento
   * @return vector con nombre, apellidos y dirección
   */
   public String[] DatosUsuario (int tipodoc,String DNI){
     int tdoc= tipodoc+1;
     String[] fields = new String[4];
     try {
        String sql = "SELECT Nombre, Apellido, Apellido2, Direccion FROM Apuntes WHERE TipoDocumento = "+tdoc+" AND Documento = '"+DNI+"'";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            fields[0] = rs.getString("Nombre");
            fields[1] = rs.getString("Apellido");
            fields[2] = rs.getString("Apellido2");
            fields[3] = rs.getString("Direccion");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
    }
    return fields;
   }
   
   /**
   *
   * Esta funcion devuelve direccion y tipo de pago de un registro que le pasamos un apunte en formato xx/YYYY
   * @param apunte Integer indicando si es DNI, Pasaporte...
   * @return vector con tipo, direccion y descripcion
   */   
   public String[] DatosFacturaUsuario (String apunte){
     //sacamos el numliq y el yliq para la consulta
     String numLiq = apunte.substring(0, 2); // Extrae los dos primeros caracteres
     String Yliq = apunte.substring(3);
     String[] fields = new String[3];
     try {
        String sql = "SELECT mediopago.tipo, Apuntes.Direccion, Concepto.CDescripcion "+
                     " FROM Apuntes INNER JOIN mediopago ON Apuntes.Medio = mediopago.IdMedio"+
                     " JOIN Concepto ON Apuntes.Concepto = Concepto.IdConcepto"+
                     " WHERE YLiq = '"+Yliq+"' AND NumLiq = '"+numLiq+"'";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            fields[0] = rs.getString("tipo");
            fields[1] = rs.getString("Direccion");
            fields[2] = rs.getString("CDescripcion");
         }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
        fields=null;
    }
    return fields;
   }
   
   
   /**
   *
   * Esta funcion devuelve el saldo total
   * @return un double con el valor
   */   
   public double getSaldo(){
    double saldo=0 ;
    try{
     String query = "SELECT Saldo FROM Apuntes WHERE Libro=1 ORDER BY IdApunte DESC LIMIT 1";
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                // Obtener los valores del registro
                saldo= rs.getDouble("Saldo");
            }
               
    }catch (SQLException e) {
     JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
     saldo=0;
    }
     return saldo;
   }
    
   
   /**
   *
   * Esta funcion devuelve el ultimo apunte de caja
   * @return Un ArrayList con numliq, yliq,fecha,descripcion y entrada
   */ 
   public ArrayList<String> ultimoregistro(){
    ArrayList<String> ultimoRegistro = new ArrayList<>();
    try {
            // Consulta SQL para obtener el último registro (cambia "tabla" por el nombre de tu tabla y "campo_fecha" por el nombre del campo que identifica la fecha o "campo_autoincremental" por el nombre del campo autoincremental)
            String query = "SELECT * FROM Apuntes ORDER BY IdApunte DESC LIMIT 1"; // Para campo autoincremental
            // Crear una declaración SQL
            Statement stmt = conn.createStatement();
            // Ejecutar la consulta y obtener el resultado
            ResultSet rs = stmt.executeQuery(query);
            // Comprobar si hay algún resultado
            if (rs.next()) {
                // Obtener los valores del registro
                ultimoRegistro.add(rs.getString("NumLiq"));
                ultimoRegistro.add(rs.getString("YLiq"));
                ultimoRegistro.add(rs.getString("Fecha"));
                ultimoRegistro.add(rs.getString("Descripcion"));
                ultimoRegistro.add(rs.getString("Entrada"));
                return ultimoRegistro;
            } else {
                // Si no hay resultados, devolver un mensaje indicando que no hay registros
                //conn.close();
                System.err.println("No hay registros en la base de datos");
                return null;
            }
        } catch (SQLException e) {
            // Manejar cualquier excepción de SQL
            JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
           return null;
        }    
   } 
   
   
   /**
   *
   * hacemos una consulta con string y nos devuelve el resultado en un table model
   * @param consultaSQL consulta para rellenar la tabla
   * @return Un TableModel para rellenar una tabla en un formulario
   */
   public DefaultTableModel consulta (String consultaSQL){
      try {
            // Creamos un Statement para ejecutar la consulta
            Statement statement = conn.createStatement();
            // Ejecutamos la consulta
            ResultSet resultSet = statement.executeQuery(consultaSQL);
            // Creamos un modelo de tabla para almacenar los datos
            DefaultTableModel modelo = new DefaultTableModel();
            // Obtenemos la información sobre las columnas del ResultSet
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numeroColumnas = metaData.getColumnCount();
            // Añadimos las columnas al modelo de tabla
            for (int i = 1; i <= numeroColumnas; i++) {
                modelo.addColumn(metaData.getColumnLabel(i));
            }
             // Añadimos las filas al modelo de tabla
            while (resultSet.next()) {
                Object[] fila = new Object[numeroColumnas];
                for (int i = 0; i < numeroColumnas; i++) {
                    fila[i] = resultSet.getObject(i + 1);
                }
                modelo.addRow(fila);
            }
             return modelo;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
   }
   
   
   /**
   *
   * realiza una consulta sql y devuelve un boolean con el resultado
   * @param consultaSQL consulta para rellenar la tabla
   * @return Un boolen con true si ha tenido exito la consulta
   * @throws java.sql.SQLException dvuleve el error
   */
   public boolean consultaSQL (String consultaSQL) throws SQLException{
      try {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);                                           //aislamiento de transacciones
            // Creamos un Statement para ejecutar la consulta
            PreparedStatement statement = conn.prepareStatement(consultaSQL);
            // Ejecutamos la consulta
            statement.executeUpdate();
            System.out.println(consultaSQL);
            conn.commit();
            System.out.println("FIN: "+consultaSQL);
            return true;
        } catch (SQLException e) {
            // Manejo de errores
           conn.rollback();
           JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
           return false;
        }
   }
   
   
   /**
   *
   * pedimos un listado de nombres de la tabla usuarios y  nos devuelve un tablemodel
   * @return Un TableModel para rellenar una tabla en un formulario
   */
   public DefaultTableModel listaNombres (){
      try {
              // Creamos un Statement para ejecutar la consulta
            Statement stment = conn.createStatement();
            // Ejecutamos la consulta
            ResultSet resultSet = stment.executeQuery("SELECT Nombre FROM usuarios ORDER BY Nombre");
            // Creamos un modelo de tabla para almacenar los datos
            DefaultTableModel modelo = new DefaultTableModel();
            // Obtenemos la información sobre las columnas del ResultSet
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numeroColumnas = metaData.getColumnCount();
            // Añadimos las columnas al modelo de tabla
            for (int i = 1; i <= numeroColumnas; i++) {
                modelo.addColumn(metaData.getColumnLabel(i));
            }
             // Añadimos las filas al modelo de tabla
            while (resultSet.next()) {
                Object[] fila = new Object[numeroColumnas];
                for (int i = 0; i < numeroColumnas; i++) {
                    fila[i] = resultSet.getObject(i + 1);
                }
                modelo.addRow(fila);
            }
             return modelo;
        } catch (SQLException e) {
            // Manejo de errores
           JOptionPane.showMessageDialog(null, "Error de lectura de datos   ", "Error Base Datos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
   }
  
}

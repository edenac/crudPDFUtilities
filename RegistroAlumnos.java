package ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;



public class RegistroAlumnos extends JFrame {
    private JTextField txt_nombre;
    private JTextField txt_grupo;
    private JButton registrarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JTextField txt_buscar;
    private JButton buscarButton;
    private JPanel panel;
    private JLabel lblStatus;
    private JButton generarReporteButton;

    public RegistroAlumnos() {
        setLocationRelativeTo(null);
        setVisible(true);
        add(panel);

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        botonRegistrar();
        botonBuscar();
        botonModificar();
        botonEliminar();
        botonGenerarReporte();
    }
 
    private void botonGenerarReporte(){
        generarReporteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Document documento = new Document();

          
                try{
          
                    String ruta=System.getProperty("user.home");
                    PdfWriter.getInstance(documento,new FileOutputStream(ruta+"/urlaGuardar/archivo.pdf"));

                    Image header = Image.getInstance("src/img/header.png"); 

                    header.scaleToFit(650,1000);
                    header.setAlignment(Chunk.ALIGN_CENTER);

 
                    Paragraph parrafo =new Paragraph();
                    parrafo.setAlignment(Paragraph.ALIGN_CENTER);
                    parrafo.add("Formato creado por xxx \n\n");
                    parrafo.setFont(FontFactory.getFont("Tahoma",18,Font.BOLD,BaseColor.DARK_GRAY));
                    parrafo.add("Alumnos registrados \n\n");

                    documento.open();

                    documento.add(header);
                    documento.add(parrafo);

                    PdfPTable tabla= new PdfPTable(3);
 
                    tabla.addCell("Código");
                    tabla.addCell("Alumno");
                    tabla.addCell("Grupo");

                    try{
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/bd_ins","root","");
                        PreparedStatement pst = con.prepareStatement("select * from alumnos");

                        ResultSet rs = pst.executeQuery();

                        if (rs.next()){
                            do{
                                tabla.addCell(rs.getString(1));
                                tabla.addCell(rs.getString(2));
                                tabla.addCell(rs.getString(3));
                            }while(rs.next());
                            documento.add(tabla);
                        }
                    }catch(DocumentException | SQLException exx){
                        JOptionPane.showMessageDialog(null,"Error en documento o base de datos"+exx);
                    }
                    documento.close();
                    JOptionPane.showMessageDialog(null,"El reporte ha sido creado");
                }catch(DocumentException | HeadlessException | FileNotFoundException exxx){ 
                    JOptionPane.showMessageDialog(null,"Errores de archivos"+exxx);
                }catch(IOException exImg){
                    JOptionPane.showMessageDialog(null,"Error en la imagen"+exImg);
                }
            }
        });
    }

    private void botonEliminar(){
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/bd_ins","root","");
                    PreparedStatement pst = con.prepareStatement("delete from alumnos where ID= ?");

                    pst.setInt(1,Integer.parseInt(txt_buscar.getText().trim()));
                    pst.executeUpdate();

                    txt_nombre.setText("");
                    txt_grupo.setText("");
                    txt_buscar.setText("");
                    lblStatus.setText("Registro eliminado");
                }catch(Exception ex){

                }
            }
        });
    }

    private void botonModificar(){
        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/bd_ins","root","");
 
                    PreparedStatement pst = con.prepareStatement("update alumnos set NombreAlumno = ?, Grupo = ? where ID = "+Integer.parseInt(txt_buscar.getText().trim()));

                    pst.setString(1,txt_nombre.getText().trim());
                    pst.setString(2,txt_grupo.getText().trim());
                    pst.executeUpdate();

                    lblStatus.setText("Modificacion exitosa");
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Error de conexion a dba");
                };
            }
        });
    }
    private void botonBuscar(){
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/bd_ins","root","");
                    PreparedStatement pst= con.prepareStatement("select * from alumnos where ID= ?");

                    pst.setInt(1,Integer.parseInt(txt_buscar.getText().trim()));
                    ResultSet rs= pst.executeQuery();

                    if(rs.next()){
                        txt_nombre.setText(rs.getString("NombreAlumno"));
                        txt_grupo.setText(rs.getString("Grupo"));
                    }else{
                        JOptionPane.showMessageDialog(null, "Alumno no registrado");
                    }

                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Error de conexion a dba");
                }
            }
        });
    }
    private void botonRegistrar(){
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/bd_ins","root","");
                    PreparedStatement pst= con.prepareStatement("insert into alumnos values (?,?,?)");

                    
                    pst.setString(1, "0");
                    pst.setString(2, txt_nombre.getText().trim());
                    pst.setString(3, txt_grupo.getText().trim());
                    pst.executeUpdate();

                    txt_nombre.setText("");
                    txt_grupo.setText("");
                    lblStatus.setText("Registro exitoso");
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Error de conexion a dba");
                }
            }
        });
    }

    public static void main(String[] args) {
        RegistroAlumnos f=new RegistroAlumnos();
    }
}

/*
 * searchDevice.java
 *
 * Created on 14 czerwiec 2008, 02:33
 */

package customComponents;

import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JPanel;

import database.DBConn;
import java.lang.Object;
import java.lang.System;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.sql.*;
import javax.swing.border.Border;

/**
 *
 * @author  Piotrek
 */
public class searchDevice extends JPanel {

    /** Creates new form BeanForm */
    public searchDevice() {
        initComponents();
        initEquipmentTable();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel32 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        equipmentTable = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        idCheckBox = new javax.swing.JCheckBox();
        descriptionTextField = new javax.swing.JTextField();
        descriptionCheckBox = new javax.swing.JCheckBox();
        valueCheckBox = new javax.swing.JCheckBox();
        valueTextField = new javax.swing.JTextField();
        idTextField = new javax.swing.JTextField();
        deactivatedCheckBox = new javax.swing.JCheckBox();
        assignedCheckBox = new javax.swing.JCheckBox();
        deactivatedComboBox = new javax.swing.JComboBox();
        assignedComboBox = new javax.swing.JComboBox();
        searchButton = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(logIn.LogIn.class).getContext().getResourceMap(searchDevice.class);
        jLabel32.setText(resourceMap.getString("jLabel32.text")); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        equipmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "OPIS", "CENA", "ID_PRACOWNIKA", "IMIĘ", "NAZWISKO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        equipmentTable.setColumnSelectionAllowed(true);
        equipmentTable.setName("equipmentTable"); // NOI18N
        equipmentTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(equipmentTable);
        equipmentTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        equipmentTable.getColumnModel().getColumn(0).setMinWidth(10);
        equipmentTable.getColumnModel().getColumn(0).setMaxWidth(50);
        equipmentTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("equipmentTable.columnModel.title0")); // NOI18N
        equipmentTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("equipmentTable.columnModel.title1")); // NOI18N
        equipmentTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("equipmentTable.columnModel.title2")); // NOI18N
        equipmentTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("equipmentTable.columnModel.title3")); // NOI18N
        equipmentTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("equipmentTable.columnModel.title4")); // NOI18N
        equipmentTable.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("equipmentTable.columnModel.title5")); // NOI18N

        jLabel33.setText(resourceMap.getString("jLabel33.text")); // NOI18N
        jLabel33.setName("jLabel33"); // NOI18N

        idCheckBox.setText(resourceMap.getString("idCheckBox.text")); // NOI18N
        idCheckBox.setName("idCheckBox"); // NOI18N
        idCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                idCheckBoxStateChanged(evt);
            }
        });

        descriptionTextField.setEnabled(false);
        descriptionTextField.setName("descriptionTextField"); // NOI18N

        descriptionCheckBox.setText(resourceMap.getString("descriptionCheckBox.text")); // NOI18N
        descriptionCheckBox.setName("descriptionCheckBox"); // NOI18N
        descriptionCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                descriptionCheckBoxStateChanged(evt);
            }
        });

        valueCheckBox.setText(resourceMap.getString("valueCheckBox.text")); // NOI18N
        valueCheckBox.setName("valueCheckBox"); // NOI18N
        valueCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                valueCheckBoxStateChanged(evt);
            }
        });

        valueTextField.setText(resourceMap.getString("valueTextField.text")); // NOI18N
        valueTextField.setEnabled(false);
        valueTextField.setName("valueTextField"); // NOI18N

        idTextField.setText(resourceMap.getString("idTextField.text")); // NOI18N
        idTextField.setEnabled(false);
        idTextField.setName("idTextField"); // NOI18N

        deactivatedCheckBox.setText(resourceMap.getString("deactivatedCheckBox.text")); // NOI18N
        deactivatedCheckBox.setName("deactivatedCheckBox"); // NOI18N
        deactivatedCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                deactivatedCheckBoxStateChanged(evt);
            }
        });

        assignedCheckBox.setText(resourceMap.getString("assignedCheckBox.text")); // NOI18N
        assignedCheckBox.setName("assignedCheckBox"); // NOI18N
        assignedCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                assignedCheckBoxStateChanged(evt);
            }
        });

        deactivatedComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TAK", "NIE" }));
        deactivatedComboBox.setEnabled(false);
        deactivatedComboBox.setName("deactivatedComboBox"); // NOI18N

        assignedComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TAK", "NIE" }));
        assignedComboBox.setEnabled(false);
        assignedComboBox.setName("assignedComboBox"); // NOI18N

        searchButton.setText(resourceMap.getString("searchButton.text")); // NOI18N
        searchButton.setName("searchButton"); // NOI18N
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(deactivatedCheckBox)
                                    .addComponent(assignedCheckBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(assignedComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(deactivatedComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(valueCheckBox)
                                    .addComponent(descriptionCheckBox)
                                    .addComponent(idCheckBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(idTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                    .addComponent(descriptionTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                    .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jLabel32)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idCheckBox)
                    .addComponent(idTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(descriptionCheckBox)
                    .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valueCheckBox)
                    .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deactivatedCheckBox)
                    .addComponent(deactivatedComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(assignedComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(assignedCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton)
                .addContainerGap(118, Short.MAX_VALUE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void idCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_idCheckBoxStateChanged
//Odblokowanie pola edycji
        idTextField.setEnabled(idCheckBox.isSelected());
}//GEN-LAST:event_idCheckBoxStateChanged

private void descriptionCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_descriptionCheckBoxStateChanged
//Odblokowanie pola edycji
        descriptionTextField.setEnabled(descriptionCheckBox.isSelected());
}//GEN-LAST:event_descriptionCheckBoxStateChanged

private void valueCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_valueCheckBoxStateChanged
//Odblokowanie pola edycji
        valueTextField.setEnabled(valueCheckBox.isSelected());
}//GEN-LAST:event_valueCheckBoxStateChanged

private void deactivatedCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_deactivatedCheckBoxStateChanged
//Odblokowanie ComboBoxa
        deactivatedComboBox.setEnabled(deactivatedCheckBox.isSelected());
}//GEN-LAST:event_deactivatedCheckBoxStateChanged

private void assignedCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_assignedCheckBoxStateChanged
//Odblokowanie ComboBoxa
        assignedComboBox.setEnabled(assignedCheckBox.isSelected());
}//GEN-LAST:event_assignedCheckBoxStateChanged

private void searchButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseClicked
// Wyszukiwanie wedlug kryteriow

        DefaultTableModel tableModel =(DefaultTableModel) equipmentTable.getModel();
        
        //Usunięcie wpisow z tabeli
        //XXX: Czy jest to poprawna metoda usuwania?
        tableModel.setRowCount(0);

        try {
            String joinType=" LEFT OUTER JOIN ";
            String addCondition="";
            if (assignedCheckBox.isSelected()){
                String s=(String)assignedComboBox.getSelectedItem();
                if (s.equals("TAK")) joinType=" JOIN ";
                else if (s.equals("NIE")) addCondition=" AND p.id_pracownika IS NULL ";
                        
            }
            java.sql.PreparedStatement pstmt=DBConn.getConnection().prepareStatement(
                    "SELECT s.id_sprzetu, s.opis, s.cena, p.id_pracownika, p.imie, p.nazwisko " +
                    "FROM " +
                    "Sprzet s " + joinType + " wypozyczyl w USING(id_sprzetu) " +
                     joinType + " Pracownik p USING(id_pracownika) " +
                    "WHERE id_sprzetu like ? AND opis like ? AND cena <= ? AND dezaktywowany like ?" + addCondition);
            Border red=new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 1, true);
            Border black=new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true);
            
            if (idTextField.getText().equals("")) idTextField.setText("0");
            if (valueTextField.getText().equals("")) valueTextField.setText("0.0");
            java.util.regex.Pattern p=Pattern.compile("\\d*");
            java.util.regex.Matcher m;



            
            if (Pattern.matches("\\d*[\\.\\d]\\d*", valueTextField.getText())) valueTextField.setBorder(black);
            else {
                valueTextField.setBorder(red);
                return;
            }

            
            //Sprawdzenie wszystkich CheckBoxow
            if(idCheckBox.isSelected()) {
                if (Pattern.matches("\\d*", idTextField.getText())) idTextField.setBorder(black);
                else {
                    idTextField.setBorder(red);
                    return;
                }
                
                pstmt.setString(1, idTextField.getText()+"%");
            }
            else {
                pstmt.setString(1, "%");
            }
            
            if (descriptionCheckBox.isSelected()) {
                pstmt.setString(2, descriptionTextField.getText()+"%");
            }
            else {
                pstmt.setString(2, "%");
            }
            
            if(valueCheckBox.isSelected()) {
                if (Pattern.matches("\\d*[\\.\\d]\\d*", valueTextField.getText())) valueTextField.setBorder(black);
                else {
                    valueTextField.setBorder(red);
                    return;
                }
                pstmt.setString(3, valueTextField.getText());
            }
            else {
                pstmt.setInt(3, Integer.MAX_VALUE);
            }
            
            if(deactivatedCheckBox.isSelected()) {
                //XXX: Jest jakas prostrza i bardziej elegancka metoda?
                pstmt.setString(4, deactivatedComboBox.getSelectedItem().toString().charAt(0) + "%");
            }
            else {
                pstmt.setString(4, "%");
            }
        
            //Wykonanie zapytania
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                //Przygotowanie wiersza
                Object[] obj = new Object[6];
                
                obj[0] = rs.getInt(1);
                obj[1] = rs.getString(2);
                obj[2] = rs.getDouble(3);
                obj[3] = rs.getInt(4);
                if (rs.wasNull()) obj[3]=null;
                obj[4] = rs.getString(5);
                if (rs.wasNull()) obj[4]="";
                obj[5] = rs.getString(6);
                if (rs.wasNull()) obj[5]=null;
                //Dodanie wiersza
                tableModel.addRow(obj);
                
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
}//GEN-LAST:event_searchButtonMouseClicked

private void initEquipmentTable() {
    try {
        DefaultTableModel tablemodel =(DefaultTableModel) equipmentTable.getModel();
        
        equipmentTable.setModel(tablemodel);
        tablemodel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                TableModel model = (TableModel)e.getSource();
                String columnName = model.getColumnName(column);
                Connection connection = DBConn.getConnection();
                
                if (connection == null)
                    return;
                
                java.sql.PreparedStatement pstmt;
                
                try {
                    if (columnName.equals("OPIS")) {
                        String sqlQuery = "UPDATE Sprzet SET opis = ? WHERE id_sprzetu = ?";
                        pstmt = connection.prepareStatement(sqlQuery);
                        pstmt.setString(1, (String)model.getValueAt(row, column));
                        pstmt.setInt(2, (Integer)model.getValueAt(row, 0));
                        pstmt.execute();
                        return;
                    }
                    else if (columnName.equals("CENA")) {
                        String sqlQuery = "UPDATE Sprzet SET cena = ? WHERE id_sprzetu = ?";
                        pstmt = connection.prepareStatement(sqlQuery);
                        pstmt.setDouble(1, (Double)model.getValueAt(row, column));
                        pstmt.setInt(2, (Integer)model.getValueAt(row, 0));
                        pstmt.execute();
                        return;
                    }
                }
                catch (SQLException ex) {
                        Logger.getLogger(searchDevice.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    catch (Exception ex) 
    {
            System.out.println(ex.getMessage());
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox assignedCheckBox;
    private javax.swing.JComboBox assignedComboBox;
    private javax.swing.JCheckBox deactivatedCheckBox;
    private javax.swing.JComboBox deactivatedComboBox;
    private javax.swing.JCheckBox descriptionCheckBox;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JTable equipmentTable;
    private javax.swing.JCheckBox idCheckBox;
    private javax.swing.JTextField idTextField;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton searchButton;
    private javax.swing.JCheckBox valueCheckBox;
    private javax.swing.JTextField valueTextField;
    // End of variables declaration//GEN-END:variables
 public static void main (String[] args){
        JFrame frm=new JFrame("Szukaj Sprzętu");
        searchDevice sdev=new searchDevice();
        frm.add(sdev);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
        frm.pack();
 }
}

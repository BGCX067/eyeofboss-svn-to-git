/*
 * EmployeeFrame.java
 *
 * Created on 30 marzec 2008, 12:28
 */
package employee;

import com.mysql.jdbc.Statement;
import customComponents.Schedule;
import customComponents.ShiftSchedule;
import database.DBConn;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import logIn.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Design: OLO, Implementation: Łukasz Spintzyk
 */
public class EmployeeFrame extends javax.swing.JFrame {
    //zmienne dodatkowe
    final static int DELAYTIME = 100;  //100msekund
    private long todayWorkTime = 0;
    Timestamp t1 = null;
    Timestamp t2 = null;
    Calendar calendar = Calendar.getInstance();
    ActionListener uaktualnijDate = new ActionListener() {

        public void actionPerformed(ActionEvent event) {
            //aktualizuje datę na pierwszej zakładce
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dayLabel.setText(sdf.format(Calendar.getInstance().getTime()));
            sdf.applyPattern("hh:mm:ss");
            hourLabel.setText(sdf.format(Calendar.getInstance().getTime()));

            if ((t1 != null) && (t2 != null)) {
                long percent = t2.getTime() - calendar.getTimeInMillis();

                if (percent < 0) {
                    percent = 100;
                } else {
                    percent = (int) (((double) percent / (double) todayWorkTime) * 100.0);
                }
                if (percent > 100) {
                    percent = 100;
                    infoLabel.setText("Koniec Zmiany");
                } else {
                    infoLabel.setText("");
                //jProgressBar1.setValue((int)percent);
                }
            }
        }
    };    //Zmienne gui
    Timer timer = new Timer(DELAYTIME, uaktualnijDate);
    User user;
    
    ActionListener showNextSchedule=new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            //Funkcja bedzie wywoływana za każdym razem jak uzytkownik naciśnie mySchedulemonthComboBoxa
            //i wyświetli na nim następny harmonogram
            Calendar cal=Calendar.getInstance();
            int month=cal.get(Calendar.MONTH);
            int year=cal.get(Calendar.YEAR);
            String monthName=(String)mySchedule.monthComboBox.getSelectedItem();
            int comboYear=(Integer)mySchedule.yearComboBox.getSelectedItem();
            int i;
            for (i=0;i<12;i++)
                if (Schedule.monthNames[i].equals(monthName)) break;

            boolean old_zmiana=false;
            if ((month+1 +year*12)>=(i+1 + comboYear*12))old_zmiana=true;//to znaczy, że jest wybrany rok i miesiąc wcześniej niż teraz.
            //w przeciwnym razie uzytkownik chce wyświetlić miesiąc w przyszłości
            //daltego używamy pola id_nast_zmiany do wyznaczenia zmiany
            
            if (user==null) return;
            try {

                PreparedStatement pstmt = DBConn.getConnection().prepareStatement(
                        "SELECT id_nast_zmiany, id_zmiany FROM Pracownik where id_pracownika=?");
                pstmt.setInt(1, user.getId());
                ResultSet rs=pstmt.executeQuery();
                int zmianaNext = 0;
                int zmianaNow  = 0;
                if (rs.next()){
                    zmianaNext=rs.getInt(1);
                    zmianaNow=rs.getInt(2);
                }
                rs.close();
                pstmt.close();
                
                System.out.println("Zmiana sie zmianiła");
                ArrayList<Integer> al=new ArrayList<Integer>(1);
                if (old_zmiana==true) al.add(zmianaNow);
                else al.add(zmianaNext);
                mySchedule.setShiftID(al);
                mySchedule.repaintSchedule();
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
    };
           

    /** Creates new form EmployeeFrame */
    public EmployeeFrame() {
        initComponents();
        timer.start();
        user = User.getUser();

        
        SwingUtilities.invokeLater(new Runnable() {
            //inicjalizacja formatki dane
            public void run() {
                if (user != null) {
                    //
                    nameTextField.setText(user.getImie());

                    nameTitleLabel.setText(user.getImie() + " " + user.getNazwisko());
                    nazwiskoTextField.setText(user.getNazwisko());
                    cityTextField.setText(user.getMiasto());
                    adresTextField.setText(user.getAdres());


                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    Date date = user.getDataZatrudnienia();
                    dataZatrudnieniaTextField.setText(sdf.format(user.getDataZatrudnienia()));
                    Calendar cal = Calendar.getInstance();
                    dayLabel.setText(sdf.format(cal.getTime()));

                    emailTextField.setText(user.getEmail());
                    phoneTextField.setText(user.getTelefon().toString());
                }
            }
        });


        mySchedule.monthComboBox.addActionListener(showNextSchedule);
        mySchedule.yearComboBox.addActionListener(showNextSchedule);
        
               // jest pierwsze logowanie w tym miesiącua, zmień id_zmiany na id_nast_zmiany
        
        if (user==null) return;
        
        Connection conn=DBConn.getConnection();
            try {
                java.sql.PreparedStatement pstmt = conn.prepareStatement("select MAX(godz_we) from Przyjscie_Wyjscie where id_pracownika=?");
                pstmt.setInt(1,user.getId());
                ResultSet rs=pstmt.executeQuery();
                int zmiana=user.getIdStawki();
                boolean first_log=false;
                if (rs.next()){
                    java.sql.Timestamp t=rs.getTimestamp(1);
                    Calendar cal=Calendar.getInstance();
                    cal.setTimeInMillis(t.getTime());
                    int month_old=cal.get(Calendar.MONTH);
                    int year_old=cal.get(Calendar.YEAR);
                    cal=Calendar.getInstance();
                    int month_now=cal.get(Calendar.MONTH);
                    int year_now=cal.get(Calendar.YEAR);
                    if (month_now+year_now*12>month_old+year_old*12) first_log=true;
                }
                else {
                    //pierwsze logowanie pracownika
                    first_log=true;
                }
                
                if (first_log){
                    pstmt=conn.prepareStatement("SELECT id_nast_zmiany FROM Pracownik WHERE id_pracownika=?");
                    pstmt.setInt(1, user.getId());
                    rs=pstmt.executeQuery();
                    int nowa_zmiana=0;
                    if (rs.next())
                        nowa_zmiana=rs.getInt(1);
                    else return; 
                    
                    pstmt=conn.prepareStatement("UPDATE Pracownik SET id_zmiany=? where id_pracownika=?");
                    pstmt.setInt(1, nowa_zmiana);
                    pstmt.setInt(2,user.getId());
                    user.setShiftId(nowa_zmiana);
                    pstmt.execute();
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        initUrlopTable();
        initHarmonogram();
    }
    //funkcja inicjuje zakładke urlop
    private void initUrlopTable() {
        //tabela Zwolnienia ulegnie zmianie, nie zapomnij wtedy zmienić zapytań i wypełnienia kolumny "Zatwierdzono" w tablicy urlopTable
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        wniosekOdTextField.setText(sdf.format(Calendar.getInstance().getTime()));
        wniosekDoTextField.setText(sdf.format(Calendar.getInstance().getTime()));

        try {
            java.sql.Statement stmt = DBConn.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nazwa_zwolnienia FROM Typy_zwolnien");
            while (rs.next()) {
                choiceZwolnienie.addItem(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }





        try {
            ResultSet rs;
            if (user != null) {
                DefaultTableModel tabmodel = (DefaultTableModel) urlopTable.getModel();

                java.sql.PreparedStatement pstmt = DBConn.getConnection().prepareStatement("SELECT z.zwolnienie_od, z.zwolnienie_do, tz.nazwa_zwolnienia, z.zatwierdzone, z.odrzucone FROM Zwolnienia z JOIN Typy_zwolnien tz USING(id_typu) WHERE id_pracownika=? ORDER BY z.zwolnienie_od DESC");

                pstmt.setInt(1, user.getId());
                rs = pstmt.executeQuery();

                while (tabmodel.getRowCount() > 0) {
                    tabmodel.removeRow(0);
                }
                while (rs.next()) {
                    Object[] obj = new Object[5];
                    obj[0] = rs.getDate(1);
                    obj[1] = rs.getDate(2);
                    obj[2] = rs.getString(3);
                    obj[3] = rs.getBoolean(4);
                    obj[4] = rs.getBoolean(5);
                    tabmodel.addRow(obj);
                }


                urlopTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                urlopTable.setCellSelectionEnabled(true);
                urlopTable.setRowSelectionAllowed(false);
                urlopTable.setModel(tabmodel);


            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initHarmonogram() {
        try {
            //pobranie id_zmiany pracownika
            Connection conn = DBConn.getConnection();
            Statement stmt = (Statement) conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id_zmiany FROM Pracownik WHERE id_pracownika=" + Integer.toString(user.getId()));
            if (rs.next()) {
                mySchedule.addShiftID(rs.getInt(1));
            }

            //wświetlaj grafik, urlopy i spóźnienia/nadgodziny
            mySchedule.setWorkerId(user.getId());
            mySchedule.setGrafik(true);
            mySchedule.setLates(true);
            mySchedule.setFreeTime(true);

            stmt = (Statement) conn.createStatement();
            rs = stmt.executeQuery("SELECT MONTH(g.dzien_od), YEAR(g.dzien_od) " +
                    " FROM Zmiana z JOIN Grafik g USING(id_zmiany) WHERE z.id_zmiany=" + Integer.toString(mySchedule.getShiftID().get(0)) +
                    " GROUP BY 1, 2");
            if (rs.next()) {
                mySchedule.monthComboBox.removeAllItems();
                mySchedule.yearComboBox.removeAllItems();
                do {
                    mySchedule.addToMonthComboBox(rs.getInt(1));
                    mySchedule.addToYearComboBox(rs.getInt(2));
                } while (rs.next());
            }

            //ustawienie aktualnego harmonogramu
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            cal.add(Calendar.MONTH,1);
            mySchedule.addToMonthComboBox(cal.get(Calendar.MONTH)+1);
            mySchedule.addToYearComboBox(cal.get(Calendar.YEAR));
            
            for (int i = 0; i < mySchedule.monthComboBox.getItemCount(); i++) {
                if (ShiftSchedule.monthNames[month].equals((String) mySchedule.monthComboBox.getItemAt(i))) {
                    mySchedule.monthComboBox.setSelectedIndex(i);
                }
            }

            for (int i = 0; i < mySchedule.yearComboBox.getItemCount(); i++) {
                if ((Integer) mySchedule.yearComboBox.getItemAt(i) == year) {
                    mySchedule.yearComboBox.setSelectedIndex(i);
                }
            }
        } catch (SQLException e) {
        } catch (NullPointerException e) {
            System.out.println(e.getStackTrace());
            return;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        registerPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        beginWorkButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        endWorkButton = new javax.swing.JButton();
        dayLabel = new javax.swing.JLabel();
        hourLabel = new javax.swing.JLabel();
        startedInfoLabel = new javax.swing.JLabel();
        infoLabel = new javax.swing.JLabel();
        danePanel = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        nazwiskoTextField = new javax.swing.JTextField();
        adresTextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        dataZatrudnieniaTextField = new javax.swing.JTextField();
        cityTextField = new javax.swing.JTextField();
        emailTextField = new javax.swing.JTextField();
        phoneTextField = new javax.swing.JTextField();
        harmonogramPanel = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        previewButton = new javax.swing.JButton();
        mySchedule = new customComponents.ShiftSchedule();
        printScheduleButton = new javax.swing.JButton();
        urlopPanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        wnoiosekButton = new javax.swing.JButton();
        wniosekOdTextField = new javax.swing.JTextField();
        wniosekDoTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        urlopTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        choiceZwolnienie = new javax.swing.JComboBox();
        nameTitleLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        registerPanel.setName("registerPanel"); // NOI18N
        registerPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                registerPanelComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                registerPanelComponentShown(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(logIn.LogIn.class).getContext().getResourceMap(EmployeeFrame.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        beginWorkButton.setText(resourceMap.getString("beginWorkButton.text")); // NOI18N
        beginWorkButton.setName("beginWorkButton"); // NOI18N
        beginWorkButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                beginWorkButtonMouseClicked(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        endWorkButton.setText(resourceMap.getString("endWorkButton.text")); // NOI18N
        endWorkButton.setName("endWorkButton"); // NOI18N
        endWorkButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                endWorkButtonMouseClicked(evt);
            }
        });

        dayLabel.setText(resourceMap.getString("dayLabel.text")); // NOI18N
        dayLabel.setName("dayLabel"); // NOI18N

        hourLabel.setText(resourceMap.getString("hourLabel.text")); // NOI18N
        hourLabel.setName("hourLabel"); // NOI18N

        startedInfoLabel.setForeground(resourceMap.getColor("startedInfoLabel.foreground")); // NOI18N
        startedInfoLabel.setText(resourceMap.getString("startedInfoLabel.text")); // NOI18N
        startedInfoLabel.setName("startedInfoLabel"); // NOI18N

        infoLabel.setForeground(resourceMap.getColor("infoLabel.foreground")); // NOI18N
        infoLabel.setText(resourceMap.getString("infoLabel.text")); // NOI18N
        infoLabel.setName("infoLabel"); // NOI18N

        javax.swing.GroupLayout registerPanelLayout = new javax.swing.GroupLayout(registerPanel);
        registerPanel.setLayout(registerPanelLayout);
        registerPanelLayout.setHorizontalGroup(
            registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(registerPanelLayout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jLabel5))
                    .addGroup(registerPanelLayout.createSequentialGroup()
                        .addGap(210, 210, 210)
                        .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(24, 24, 24)
                        .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dayLabel)
                            .addComponent(hourLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(registerPanelLayout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(beginWorkButton, 0, 0, Short.MAX_VALUE)
                            .addComponent(endWorkButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                        .addComponent(startedInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
                .addContainerGap(238, Short.MAX_VALUE)
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(194, 194, 194))
        );
        registerPanelLayout.setVerticalGroup(
            registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(dayLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(hourLabel))
                .addGap(27, 27, 27)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(beginWorkButton)
                        .addComponent(startedInfoLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endWorkButton)
                .addGap(95, 95, 95)
                .addComponent(infoLabel)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("registerPanel.TabConstraints.tabTitle"), registerPanel); // NOI18N

        danePanel.setName("danePanel"); // NOI18N

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        nameTextField.setEditable(false);
        nameTextField.setText(resourceMap.getString("imieTextField.text")); // NOI18N
        nameTextField.setName("imieTextField"); // NOI18N

        nazwiskoTextField.setEditable(false);
        nazwiskoTextField.setText(resourceMap.getString("nazwiskoTextField.text")); // NOI18N
        nazwiskoTextField.setName("nazwiskoTextField"); // NOI18N

        adresTextField.setEditable(false);
        adresTextField.setText(resourceMap.getString("adresTextField.text")); // NOI18N
        adresTextField.setName("adresTextField"); // NOI18N

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        dataZatrudnieniaTextField.setEditable(false);
        dataZatrudnieniaTextField.setText(resourceMap.getString("fdateTextField.text")); // NOI18N
        dataZatrudnieniaTextField.setName("fdateTextField"); // NOI18N

        cityTextField.setEditable(false);
        cityTextField.setText(resourceMap.getString("cityTextField.text")); // NOI18N
        cityTextField.setName("cityTextField"); // NOI18N

        emailTextField.setEditable(false);
        emailTextField.setText(resourceMap.getString("emailTextField.text")); // NOI18N
        emailTextField.setName("emailTextField"); // NOI18N

        phoneTextField.setEditable(false);
        phoneTextField.setText(resourceMap.getString("phoneTextField.text")); // NOI18N
        phoneTextField.setName("phoneTextField"); // NOI18N

        javax.swing.GroupLayout danePanelLayout = new javax.swing.GroupLayout(danePanel);
        danePanel.setLayout(danePanelLayout);
        danePanelLayout.setHorizontalGroup(
            danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(danePanelLayout.createSequentialGroup()
                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(danePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE))
                    .addGroup(danePanelLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(danePanelLayout.createSequentialGroup()
                                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel26)
                                    .addComponent(jLabel21))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(cityTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(nazwiskoTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(nameTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                                    .addComponent(adresTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(danePanelLayout.createSequentialGroup()
                                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25)
                                    .addComponent(jLabel24))
                                .addGap(33, 33, 33)
                                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(phoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(danePanelLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dataZatrudnieniaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(191, 191, 191)))
                .addContainerGap())
        );
        danePanelLayout.setVerticalGroup(
            danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(danePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(nazwiskoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(cityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(adresTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(dataZatrudnieniaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(danePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(phoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("danePanel.TabConstraints.tabTitle"), danePanel); // NOI18N

        harmonogramPanel.setName("harmonogramPanel"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N

        previewButton.setText(resourceMap.getString("previewButton.text")); // NOI18N
        previewButton.setName("previewButton"); // NOI18N
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });

        mySchedule.setName("mySchedule"); // NOI18N

        printScheduleButton.setText(resourceMap.getString("printScheduleButton.text")); // NOI18N
        printScheduleButton.setName("printScheduleButton"); // NOI18N
        printScheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printScheduleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout harmonogramPanelLayout = new javax.swing.GroupLayout(harmonogramPanel);
        harmonogramPanel.setLayout(harmonogramPanelLayout);
        harmonogramPanelLayout.setHorizontalGroup(
            harmonogramPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, harmonogramPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(previewButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 257, Short.MAX_VALUE)
                .addComponent(printScheduleButton)
                .addContainerGap())
            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
            .addGroup(harmonogramPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mySchedule, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                .addContainerGap())
        );
        harmonogramPanelLayout.setVerticalGroup(
            harmonogramPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, harmonogramPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mySchedule, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(harmonogramPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(printScheduleButton)
                    .addComponent(previewButton))
                .addGap(27, 27, 27))
        );

        jTabbedPane1.addTab(resourceMap.getString("harmonogramPanel.TabConstraints.tabTitle"), harmonogramPanel); // NOI18N

        urlopPanel.setName("urlopPanel"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        wnoiosekButton.setText(resourceMap.getString("wnoiosekButton.text")); // NOI18N
        wnoiosekButton.setName("wnoiosekButton"); // NOI18N
        wnoiosekButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                wnoiosekButtonMouseClicked(evt);
            }
        });

        wniosekOdTextField.setText(resourceMap.getString("wniosekOdTextField.text")); // NOI18N
        wniosekOdTextField.setName("wniosekOdTextField"); // NOI18N

        wniosekDoTextField.setText(resourceMap.getString("wniosekDoTextField.text")); // NOI18N
        wniosekDoTextField.setName("wniosekDoTextField"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        urlopTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "OD", "DO", "TYPU", "ZATWIERDZONE", "ODRZUCONE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        urlopTable.setName("urlopTable"); // NOI18N
        urlopTable.setRowSelectionAllowed(false);
        urlopTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        urlopTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(urlopTable);
        urlopTable.getColumnModel().getColumn(0).setMinWidth(100);
        urlopTable.getColumnModel().getColumn(0).setMaxWidth(100);
        urlopTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("urlopTable.columnModel.title0")); // NOI18N
        urlopTable.getColumnModel().getColumn(1).setMinWidth(100);
        urlopTable.getColumnModel().getColumn(1).setMaxWidth(100);
        urlopTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("urlopTable.columnModel.title1")); // NOI18N
        urlopTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("urlopTable.columnModel.title2")); // NOI18N
        urlopTable.getColumnModel().getColumn(3).setMinWidth(100);
        urlopTable.getColumnModel().getColumn(3).setMaxWidth(100);
        urlopTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("urlopTable.columnModel.title3")); // NOI18N
        urlopTable.getColumnModel().getColumn(4).setMinWidth(100);
        urlopTable.getColumnModel().getColumn(4).setMaxWidth(100);
        urlopTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("urlopTable.columnModel.title4")); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        choiceZwolnienie.setName("choiceZwolnienie"); // NOI18N

        javax.swing.GroupLayout urlopPanelLayout = new javax.swing.GroupLayout(urlopPanel);
        urlopPanel.setLayout(urlopPanelLayout);
        urlopPanelLayout.setHorizontalGroup(
            urlopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(urlopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(urlopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(urlopPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                    .addGroup(urlopPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addContainerGap(262, Short.MAX_VALUE))
                    .addGroup(urlopPanelLayout.createSequentialGroup()
                        .addGroup(urlopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(urlopPanelLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(wniosekOdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel12)
                                .addGap(4, 4, 4)
                                .addComponent(wniosekDoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(urlopPanelLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(choiceZwolnienie, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(34, 34, 34)
                        .addComponent(wnoiosekButton)
                        .addGap(172, 172, 172))))
        );
        urlopPanelLayout.setVerticalGroup(
            urlopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(urlopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(urlopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(choiceZwolnienie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(urlopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(wnoiosekButton)
                    .addComponent(jLabel12)
                    .addComponent(wniosekOdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wniosekDoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("urlopPanel.TabConstraints.tabTitle"), urlopPanel); // NOI18N

        nameTitleLabel.setFont(resourceMap.getFont("nameTitleLabel.font")); // NOI18N
        nameTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nameTitleLabel.setText(resourceMap.getString("nameTitleLabel.text")); // NOI18N
        nameTitleLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        nameTitleLabel.setName("nameTitleLabel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nameTitleLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nameTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName(resourceMap.getString("jTabbedPane1.AccessibleContext.accessibleName")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void registerPanelComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_registerPanelComponentHidden
        // TODO add your handling code here:
        timer.stop();
}//GEN-LAST:event_registerPanelComponentHidden

    private void registerPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_registerPanelComponentShown
        // TODO add your handling code here:
        timer.start();
}//GEN-LAST:event_registerPanelComponentShown

private void beginWorkButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beginWorkButtonMouseClicked
// TODO add your handling code here:
    
 
        
    
    
    //sprawdź czy niciśnięto poprzednio wyjście
    //jeśli nie to nic nie rób.

    if (user != null) {
        try {
            Connection conn = DBConn.getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                    "SELECT MAX(id_wewy) FROM Przyjscie_Wyjscie where id_pracownika=?");
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            Integer id_wewy = null;
            if (rs.next()) {
                id_wewy = rs.getInt(1);
            }
            boolean logged = false;
            if (id_wewy != null) {

                stmt = conn.prepareStatement(
                        "Select id_wewy, godz_wy, godz_we,id_pracownika from Przyjscie_Wyjscie WHERE id_wewy=?");

                stmt.setInt(1, id_wewy);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    //to oznacza, że użytkownik ma już wpis o przyjsciu i wychodzimy.
                    if (rs.getDate("godz_wy") == null) {
                        startedInfoLabel.setText("Logged In");
                        logged = true;
                    }
                }
            }
            if (!logged) {
                //nie ma wpisu o wejściu jeszcze
                //datę z komputera trzeba wziąść bo baza danych może być w innej strefie czasowej
                Timestamp data = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
                stmt = conn.prepareStatement(
                        "INSERT into Przyjscie_Wyjscie (id_pracownika,godz_we) values (?,?)");
                stmt.setInt(1, user.getId());
                stmt.setTimestamp(2, data);
                stmt.execute();
                startedInfoLabel.setText("Logged In");
            }

//teraz pobierz dwie daty harmonogramu:, które będą potrzebne do obliczenia postępu:
            stmt = conn.prepareStatement(
                    "SELECT dzien_od, dzien_do, " +
                    "ABS(UNIX_TIMESTAMP(dzien_od)-UNIX_TIMESTAMP(NOW())),  " +
                    "SIGN(UNIX_TIMESTAMP(dzien_od)-UNIX_TIMESTAMP(NOW())) " +
                    "FROM Grafik " +
                    "WHERE MONTH(dzien_od)=? AND YEAR(dzien_od)=? " +
                    "AND id_zmiany=? ORDER BY 3");
            stmt.setInt(1, calendar.get(Calendar.MONTH) + 1);
            stmt.setInt(2, calendar.get(Calendar.YEAR));
            //stmt.setInt(3, calendar.get(Calendar.DATE));
            stmt.setInt(3, user.getShiftId());
            rs = stmt.executeQuery();
            long temp=0;
            if (rs.next()) {
                t1 = rs.getTimestamp(1);
                t2 = rs.getTimestamp(2);
                temp=rs.getInt(3)*1000;
                temp*=rs.getInt(4);
                todayWorkTime = t2.getTime() - t1.getTime();

            }

// sprawdź czy są spóźnienia +- 5min

           // long temp = calendar.getTimeInMillis() - t1.getTime();
            if (temp > 5 * 60 * 1000) {
                //jest spóźnienie
                stmt = conn.prepareStatement("INSERT INTO nadgodziny (id_pracownika, data_wyjscia, czas) " +
                        "VALUES (?,?,?)");
                stmt.setInt(1, user.getId());
                stmt.setTimestamp(2, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                stmt.setTime(3, new java.sql.Time(temp-3600*1000));
                stmt.execute();
            } else if (temp < -5 * 60 * 1000) {
                //są nadgodziny
                stmt = conn.prepareStatement("INSERT INTO spoznienie (id_pracownika, data_przyjscia, czas_spoznienia) " +
                        "VALUES (?,?,?)");
                stmt.setInt(1, user.getId());
                stmt.setTimestamp(2, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                stmt.setTime(3, new java.sql.Time((-temp)-3600*1000));
                stmt.execute();
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}//GEN-LAST:event_beginWorkButtonMouseClicked

private void endWorkButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_endWorkButtonMouseClicked
// TODO add your handling code here:
    //sprawdź czy naciśnięto poprzednio wejście
    //jeśli nie to nic nie rób.

    if (user != null) {
        try {
            Connection conn = DBConn.getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                    "Select id_wewy, godz_wy, godz_we,id_pracownika from Przyjscie_Wyjscie WHERE id_wewy=(SELECT MAX(id_wewy) FROM Przyjscie_Wyjscie where id_pracownika=?)");
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                //to oznacza, że użytkownik już nasisnął wyjście
                if (rs.getDate("godz_wy") != null) {
                    startedInfoLabel.setText("");
                    return;
                } else {
                    stmt = conn.prepareStatement("UPDATE Przyjscie_Wyjscie SET godz_wy=? where id_wewy =? and id_pracownika=?");
                    stmt.setTimestamp(1, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
                    stmt.setInt(2, rs.getInt(1));
                    stmt.setInt(3, user.getId());
                    stmt.execute();
                    startedInfoLabel.setText("");
                }
                //spradź czy były nadgodziny
                
                long temp = Calendar.getInstance().getTimeInMillis() - t2.getTime();

                //( (temp >-5*60*1000) && (temp <5*60*1000)){
                if (temp > 5 * 60 * 1000) {
                    //są nadgodziny
                    stmt = conn.prepareStatement("INSERT INTO nadgodziny (id_pracownika, data_wyjscia, czas) " +
                            "VALUES (?,?,?)");
                    stmt.setInt(1, user.getId());
                    stmt.setTimestamp(2, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                    stmt.setTime(3, new java.sql.Time(temp-3600*1000));
                    stmt.execute();
                } else if (temp < -5 * 60 * 1000) {
                    //jest spoznienie(za wcześnie wyszedł z pracy
                     stmt = conn.prepareStatement("INSERT INTO spoznienie (id_pracownika, data_przyjscia, czas_spoznienia) " +
                        "VALUES (?,?,?)");
                     stmt.setInt(1, user.getId());
                     stmt.setTimestamp(2, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                     stmt.setTime(3, new java.sql.Time((-temp)-3600*1000));
                     stmt.execute();
                }
            //else w granicach normy 5 minutowej



            //albo czy za wcześnie nie wyszedł
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}//GEN-LAST:event_endWorkButtonMouseClicked

private void wnoiosekButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wnoiosekButtonMouseClicked
// sprawdź dane wejściowe

    Border red = new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 1, true);
    Border green = new javax.swing.border.LineBorder(new java.awt.Color(0, 255, 0), 1, true);
    boolean areErrors = false;
    if (!Pattern.matches("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}", wniosekOdTextField.getText())) {
        areErrors = true;
        wniosekOdTextField.setBorder(red);
    } else {
        //sprawdź jeszcze, czy miesiąc mniejszy od 1 do 12
        char tab[] = {0, 0};
        wniosekOdTextField.getText().getChars(5, 7, tab, 0);

        String s = new String(tab);
        Integer i = Integer.parseInt(s);
        wniosekOdTextField.getText().getChars(8, 10, tab, 0);
        s = new String(tab);
        Integer d = Integer.parseInt(s);
        if ((i < 1) || (i > 12) || (d < 1) || (d > 31)) {
            areErrors = true;
            wniosekOdTextField.setBorder(red);
        } else {
            wniosekOdTextField.setBorder(green);
        }
    }
    if (!Pattern.matches("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}", wniosekDoTextField.getText())) {
        areErrors = true;
        wniosekDoTextField.setBorder(red);
    } else {
        //sprawdź jeszcze, czy miesiąc mniejszy od 1 do 12
        char tab[] = {0, 0};
        wniosekDoTextField.getText().getChars(5, 7, tab, 0);

        String s = new String(tab);
        Integer i = Integer.parseInt(s);
        wniosekDoTextField.getText().getChars(8, 10, tab, 0);
        s = new String(tab);
        Integer d = Integer.parseInt(s);
        if ((i < 1) || (i > 12) || (d < 1) || (d > 31)) {
            areErrors = true;
            wniosekDoTextField.setBorder(red);
        } else {
            wniosekDoTextField.setBorder(green);
        }
    }
    DateFormat df = DateFormat.getDateInstance();
    java.sql.Date data1 = null;
    java.sql.Date data2 = null;
    try {
        data1 = new java.sql.Date(df.parse(wniosekDoTextField.getText()).getTime());
        data2 = new java.sql.Date(df.parse(wniosekOdTextField.getText()).getTime());
    } catch (ParseException ex) {
        Logger.getLogger(EmployeeFrame.class.getName()).log(Level.SEVERE, null, ex);
    }
    if (data1.before(data2)) {
        wniosekDoTextField.setBorder(red);
        wniosekOdTextField.setBorder(red);
        areErrors = true;
    }

    if (areErrors) {
        return;
    //złóż wniosek o urlop
// Tablica Zwolnienia się zmieni, nie zapomnij wtedy zmodyfikowac tego kodu ( zapytań, i kolumny Zatwiedzono)
    }
    try {
        //sprawdź jaki typ zwolnienia wybrano w choiceZwolnienie
        java.sql.Statement stmt = DBConn.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id_typu FROM  Typy_zwolnien WHERE nazwa_zwolnienia=" + "\"" + choiceZwolnienie.getSelectedItem().toString() + "\"");
        if (!rs.next()) {
            return; //brak danych o typie zwolnienia
        }

        if (user != null) {
            java.sql.PreparedStatement pstmt = DBConn.getConnection().prepareStatement(
                    "INSERT into Zwolnienia (id_pracownika, id_typu, zwolnienie_do, zwolnienie_od)" +
                    " values (?,?,?,?)");

            pstmt.setInt(1, user.getId());
            pstmt.setInt(2, rs.getInt(1));
            df = DateFormat.getDateInstance();
            pstmt.setDate(3, new java.sql.Date(df.parse(wniosekDoTextField.getText()).getTime()));
            pstmt.setDate(4, new java.sql.Date(df.parse(wniosekOdTextField.getText()).getTime()));
            pstmt.execute();
            initUrlopTable();
        }
    } catch (SQLException e) {
        e.getMessage();
    } catch (Exception e) {
        System.out.println(e.getStackTrace());
    }
}//GEN-LAST:event_wnoiosekButtonMouseClicked

private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
    try {
        HashMap<String, Integer> params = new HashMap<String, Integer>(1);
        params.put("id_prac", user.getId());


        JasperReport jrep = JasperCompileManager.compileReport("./jasperRaports/prac_wyplaty.jrxml");
        JasperPrint jasperprint = JasperFillManager.fillReport(jrep, params, DBConn.getConnection());
        JasperViewer jv = new JasperViewer(jasperprint, false);
        jv.setVisible(true);
    } catch (JRException ex) {
    }
}//GEN-LAST:event_previewButtonActionPerformed

private void printScheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printScheduleButtonActionPerformed
    try {
        HashMap<String, Integer> params = new HashMap<String, Integer>(3);
        params.put("id_zmiany", mySchedule.getShiftID().get(0));//user.getShiftId(); zamiast tego dać id wyświetlanego
        params.put("year", (Integer) mySchedule.yearComboBox.getSelectedItem());
        int month = 1;
        for (month = 0; month < Schedule.monthNames.length; month++) {
            if (Schedule.monthNames[month].equals(mySchedule.monthComboBox.getSelectedItem().toString())) {
                month++; //bo numerujemy od zera
                break;
            }
        }
        params.put("month", month);
        int czas_spoznien = 0;
        int czas_nadgodzin = 0;
        Connection conn = DBConn.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT sum(TIME_TO_SEC(czas_spoznienia)) FROM spoznienie WHERE id_pracownika=?");
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                czas_spoznien = rs.getInt(1)/60;//czas w minutach

            }

            stmt = conn.prepareStatement("SELECT sum(TIME_TO_SEC(czas)) FROM nadgodziny WHERE id_pracownika=?");
            stmt.setInt(1, user.getId());
            rs = stmt.executeQuery();
            if (rs.next()) {
                czas_nadgodzin = rs.getInt(1)/60;
            }

        } catch (SQLException ex) {
            Logger.getLogger(EmployeeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        params.put("czas_spoznienia", new Integer(czas_spoznien));
        params.put("czas_nadgodzin", new Integer(czas_nadgodzin));

        JasperReport jrep = JasperCompileManager.compileReport("./jasperRaports/spoznienia_harmonogram.jrxml");
        JasperPrint jasperprint = JasperFillManager.fillReport(jrep, params, DBConn.getConnection());
        JasperViewer jv = new JasperViewer(jasperprint, false);
        jv.setVisible(true);
    } catch (JRException ex) {
        Logger.getLogger(EmployeeFrame.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_printScheduleButtonActionPerformed
        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EmployeeFrame eframe =new EmployeeFrame();
                eframe.setVisible(true);
                eframe.setTitle("Pulpit pracownika");
            }
        });
    }


    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField adresTextField;
    private javax.swing.JButton beginWorkButton;
    private javax.swing.JComboBox choiceZwolnienie;
    private javax.swing.JTextField cityTextField;
    private javax.swing.JPanel danePanel;
    private javax.swing.JTextField dataZatrudnieniaTextField;
    private javax.swing.JLabel dayLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JButton endWorkButton;
    private javax.swing.JPanel harmonogramPanel;
    private javax.swing.JLabel hourLabel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private customComponents.ShiftSchedule mySchedule;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel nameTitleLabel;
    private javax.swing.JTextField nazwiskoTextField;
    private javax.swing.JTextField phoneTextField;
    private javax.swing.JButton previewButton;
    private javax.swing.JButton printScheduleButton;
    private javax.swing.JPanel registerPanel;
    private javax.swing.JLabel startedInfoLabel;
    private javax.swing.JPanel urlopPanel;
    private javax.swing.JTable urlopTable;
    private javax.swing.JTextField wniosekDoTextField;
    private javax.swing.JTextField wniosekOdTextField;
    private javax.swing.JButton wnoiosekButton;
    // End of variables declaration//GEN-END:variables

}

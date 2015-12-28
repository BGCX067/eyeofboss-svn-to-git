/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package customComponents;

import database.DBConn;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * Klasa dziedziczy po klasie harmonogramu/kalendarza - Schedule
 * Udostepnia interfejs do połączenia się z bazą danych i pobrania
 * danych o zmianach. Jest to implementacja dla programu eyeofboss.
 * Co klasa udostępnia:
 * *** Wyświetlanie zmian (aktualnie ustawionych i poprzednich jeśli są w bazie) wg identyfikatora, 
 *     Dodatkowo postaram się trochę zmodyfikować formatkę tak by były dwa tryby wybierania zmian:
 *          * tylko wg indentyfikatora zmiany, który wybierany jest z pośród możliwych
 *            Jest to widok dla kadrowego, tak by miał wgląd do wszystkich zmian.
 *          * jeden stały identyfikator, ale można wybrać z poprzednich miesięcy
 *            Jest to widok dla pracownika, którego nie obchodzi w jakich był zmianach,
 *            ale jak one wyglądały.
 * 
 * *** Wypisywanie spóźnień, nadgodzin dla pracownika o danym ID
 * *** Wypisywanie dni urlopowych dla pracownika
 * *** 
 * @author Łukasz Spintzyk
 */
public class ShiftSchedule extends Schedule {
    private Connection conn=null;
    private boolean shiftMode=false;
    private boolean Grafik=false;
    private boolean Lates=false;
    private boolean freeTime=false;
    private int workerId=-1;
    public ShiftSchedule() {
        
                
        //usuń wszystkie wcześniejsze funkcje, które obsługuja akcje na month i yearComboBoxie
        for (ActionListener l: monthComboBox.getActionListeners())
            monthComboBox.removeActionListener(l);
        
        for (ActionListener l: yearComboBox.getActionListeners())
            yearComboBox.removeActionListener(l);

        shiftComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shiftComboBoxActionPerformed(e);
            }
        });
        //ustaw nową funkcję obsługi akcji dla monthComboBoxa
        monthComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                      monthComboBoxActionPerformed(e);
            }
        });
        
        yearComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                yearComboBoxActionPerformed(e);
            }
        });
        
        //kod obsługujący akcję po wyedytowaniu komórki
        
        CalendarDataModel model=getCalendarDataModel();
        model.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                int column=e.getColumn();
                int row=e.getFirstRow();
                CalendarDataModel model=(CalendarDataModel)e.getSource();
                CalendarDay day=(CalendarDay)model.getValueAt(row, column);
                if (day==null) return;
                try {
                    Integer shift=(Integer)shiftComboBox.getSelectedItem();
                    if (shift==null)return;
                    Connection conn=DBConn.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM Grafik WHERE YEAR(dzien_od)=? AND MONTH(dzien_od)=? AND DAY(dzien_od)=? and id_zmiany=?");
                    pstmt.setInt(1, day.getYear());
                    pstmt.setInt(2, day.getMonth());
                    pstmt.setInt(3, day.getDayInMonth());
                    pstmt.setInt(4, shift);
                    pstmt.execute();//usuń stare wartości.

                    
                    DayEvent[] events=day.getEvents();
                    for (int i=0;i<day.getEventsNum();i++)
                    {
                        pstmt=conn.prepareStatement("INSERT INTO Grafik (id_zmiany, dzien_od,dzien_do) VALUES(?,?,?)");
                        pstmt.setInt(1, shift);
                        
                        pstmt.setTimestamp(2,new Timestamp(events[i].getDataBegin().getTime()));
                        Date d=events[i].getDataEnd();
                        if (d==null)pstmt.setTimestamp(3, null);
                        else pstmt.setTimestamp(3,new Timestamp(events[i].getDataEnd().getTime()));
                        pstmt.execute();
                    }
                    
                } catch (SQLException ex) {
                    Logger.getLogger(ShiftSchedule.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
        });
        
        
    }

    
    
    private String sqlquery;
    /** Id zmian, które mają być wyświetlone.*/
    
    private List<Integer> shiftID=new ArrayList<Integer>();
    private List<Color> shiftColor=new ArrayList<Color>();
    
    public void setShiftMode(Boolean b){
        shiftComboBox.setVisible(b);
        zmianaLabel.setVisible(b);
        shiftMode=b;
        
        if (shiftMode){//wpisać do shiftComboBox występujące zmiany
            conn = DBConn.getConnection();
            if (conn == null) return;
                Statement stmt;
            
            //ustawia wartości shiftComboBoxa by wypisać wszystkie mozliwe zmiany
            try {
                stmt = conn.createStatement();
                ResultSet rs=stmt.executeQuery("SELECT DISTINCT id_zmiany from Zmiana");
                shiftComboBox.removeAllItems();//usun stare wartosci, żeby się nie powtarzały
                while (rs.next())
                    shiftComboBox.addItem((Integer)rs.getInt(1));

            } catch (SQLException ex) {
                Logger.getLogger(ShiftSchedule.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    
    
    private void shiftComboBoxActionPerformed(ActionEvent e){
        shiftID.removeAll(shiftID);
        shiftID.add((Integer)shiftComboBox.getSelectedItem());
        getShiftColor().removeAll(getShiftColor());
        getShiftColor().add(Color.GREEN);
//        System.out.println("ShiftComboBox action performed, shiftID" + shiftID + " kolory " +getShiftColor());
        //wypelnij month i yearComboBox odpowiednimi wartościami
        /*
        conn = DBConn.getConnection();
        if (conn == null) return;
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT MONTH(g.dzien_od), YEAR(g.dzien_od) " +
                    "FROM Zmiana z JOIN Grafik g USING(id_zmiany) WHERE z.id_zmiany=? GROUP BY 1, 2");
            pstmt.setInt(1,(Integer)shiftComboBox.getSelectedItem());
            ResultSet rs=pstmt.executeQuery();
           if (rs.next()){
                monthComboBox.removeAllItems();
                yearComboBox.removeAllItems();
                while(rs.next()){
                    addToMonthComboBox(rs.getInt(1));
                    addToYearComboBox(rs.getInt(2));
                }
            }
            
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }*/
        clearSchedule();
        repaintSchedule();
                
        
    }
    
    private void monthComboBoxActionPerformed(ActionEvent e){
        String m=(String)monthComboBox.getSelectedItem();
        CalendarDataModel dataModel=getCalendarDataModel();
        for(int i=0;i<monthNames.length;i++)
            if (m.equals(monthNames[i])) {
                dataModel.setMonth(i+1);
                break;
            }
        
        repaintSchedule();
    }
    
    private void yearComboBoxActionPerformed(ActionEvent e){
        CalendarDataModel dataModel=getCalendarDataModel();
        dataModel.setYear((Integer)yearComboBox.getSelectedItem());

        repaintSchedule();        
    }

   
    public void repaintSchedule() {
//        System.out.println("repaint Schedule");
        
        
        if (Grafik) {
            for (int i = 0; i < shiftID.size(); i++) {
                
                if (i >= shiftColor.size()) {
//                    System.out.println("Grafik "+shiftID.get(i) + Color.GREEN);
                    showGrafik(shiftID.get(i), Color.GREEN);
                } else {
//                    System.out.println("Grafik "+shiftID.get(i) + getShiftColor().get(i));
                    showGrafik(shiftID.get(i), getShiftColor().get(i));
                }
            }
        }


        if (Lates && workerId >= 0) 
            showLate(workerId);
        
        if (freeTime && workerId >= 0) 
            showFreeTime(workerId);
        
        super.repaint();
    }
    
    
    
    public void showGrafik(Integer shiftId,Color color){
        //System.out.println("Show Grafik"+shiftId +" kolo: "+color);
        conn = DBConn.getConnection();
        if (conn == null) return;
        
        CalendarDataModel tabmodel=this.getCalendarDataModel();
        int month=tabmodel.getMonth();
        int year=tabmodel.getYear();
            
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT g.dzien_od, g.dzien_do, DAY(g.dzien_od) , DAY(g.dzien_do) " +
                    "FROM Grafik g JOIN Zmiana z USING (id_zmiany) " +
                    "WHERE z.id_zmiany=? AND MONTH(g.dzien_od)= ? AND YEAR(g.dzien_od)=? " +
                    "ORDER BY g.dzien_od ASC");
            pstmt.setInt(1,shiftId);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            ResultSet rs=pstmt.executeQuery();
            
            CalendarDataModel model=this.getCalendarDataModel();

            Calendar cal=Calendar.getInstance();
            DayEvent e;
            while(rs.next()){//pętla umiescza rozklad pracy na grafiku.
                CalendarDay day=model.getCalendarDay(rs.getInt(3));
                if (day==null) return;
                //trzeba sprawdzić, czy zmiana w grafiku nie kończy się dnia następnego
                Date d1=new Date(rs.getTimestamp(1).getTime());
                Date d2=new Date(rs.getTimestamp(2).getTime());
                
                
                if (rs.getInt(3) < rs.getInt(4)){//zmiana kończy się następnego dnia.
                    cal.setTime(d1);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE,59);
                    cal.set(Calendar.SECOND,59);
                    cal.set(Calendar.MILLISECOND,100);
                    Date d3=cal.getTime();
                    e=new DayEvent("Zmiana", d1, d3, color);
                    day.addEvent(e);
                    //teraz dać drugie zdarzenie w dniu następnym.
                    
                    day=model.getCalendarDay(rs.getInt(4));
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.add(Calendar.DATE, 1);
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);
                    cal.set(Calendar.MILLISECOND,0);
                    d1=(Date) cal.getTime();
                }
                e=new DayEvent("Zmiana", d1, d2, color);
                day.addEvent(e);
                //zle rozmieszczenie grafiku na dw dni!
            }
            
            //teraz trzeba umieścić spóźnienia i nadgodziny w harmonogramie
            //ale to już powinno być w innej funkcji bo ta harmonogram może być
            //wyświetlany tez dla zmiany a nie dla konkretnego pracownika.
            
            
        } catch (SQLException ex) {
            Logger.getLogger(ShiftSchedule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * 
     * @param workerId Okresla ktorego uzytkownika ma wyswietlic spoznienia i nadgodziny
     */
    public void showLate(int workerId){
        if (conn==null) conn=DBConn.getConnection();
        
        try {
            //pobranie spoznien z tego miesiaca
            PreparedStatement pstmt=conn.prepareStatement(
                    "SELECT data_przyjscia, czas_spoznienia FROM spoznienie " +
                    "WHERE MONTH(data_przyjscia)=? AND YEAR(data_przyjscia)=? AND id_pracownika=?");
            CalendarDataModel tabmodel=this.getCalendarDataModel();
            pstmt.setInt(1,tabmodel.getMonth());
            pstmt.setInt(2,tabmodel.getYear());
            pstmt.setInt(3,workerId);
            ResultSet rs=pstmt.executeQuery();
            Calendar cal=Calendar.getInstance();
            while(rs.next()){
                cal.setTimeInMillis(rs.getTimestamp(1).getTime());
                Time t=rs.getTime(2);
                if (t==null) continue;
                CalendarDay day=tabmodel.getCalendarDay(cal.get(Calendar.DAY_OF_MONTH));
                                
                DayEvent e=new DayEvent("Spoznienie T=" + rs.getTime(2).toString(), new Date(rs.getTimestamp(1).getTime()), Color.RED);
                day.addEvent(e);
                
            }
            
            //taraz dodanie nadgodzin
            pstmt=conn.prepareStatement(
                    "SELECT data_wyjscia, czas FROM nadgodziny " +
                    "WHERE MONTH(data_wyjscia)=? AND YEAR(data_wyjscia)=? AND id_pracownika=?");
            pstmt.setInt(1,tabmodel.getMonth());
            pstmt.setInt(2,tabmodel.getYear());
            pstmt.setInt(3,workerId);
            rs=pstmt.executeQuery();
            
            while(rs.next()){
                cal.setTime(rs.getDate(1));
                CalendarDay day=tabmodel.getCalendarDay(cal.get(Calendar.DAY_OF_MONTH));
                  
                DayEvent e=new DayEvent("Nadgodziny T=" + rs.getTime(2).toString(), new Date(rs.getTimestamp(1).getTime()), Color.BLUE);
                day.addEvent(e);
            }
        }
        catch (Exception e){
            System.out.println("Show late: " + e.getStackTrace()+ " "+e.getMessage()); 
        }
    }
    
    public void showFreeTime(int workerId){
         if (conn==null) conn=DBConn.getConnection();
         
         try{
             CalendarDataModel tabmodel=this.getCalendarDataModel();
             PreparedStatement pstmt=conn.prepareStatement(
                     "SELECT z.zwolnienie_od, z.zwolnienie_do, t.nazwa_zwolnienia " +
                     "FROM Zwolnienia z JOIN Typy_zwolnien t USING(id_typu) " +
                     "WHERE z.id_pracownika=? AND z.zatwierdzone>0 AND " +
                     "((MONTH(z.zwolnienie_do)=?) OR (MONTH(z.zwolnienie_od)=?)) AND " +
                     "((YEAR(z.zwolnienie_od)=?) OR (YEAR(z.zwolnienie_do)=?) )");
             pstmt.setInt(1,workerId);
             pstmt.setInt(2,tabmodel.getMonth());
             pstmt.setInt(3,tabmodel.getMonth());
             pstmt.setInt(4,tabmodel.getYear());
             pstmt.setInt(5,tabmodel.getYear());
             ResultSet rs=pstmt.executeQuery();
             Calendar cal =Calendar.getInstance();
             Calendar cal2=Calendar.getInstance();
             Date d1, d2;
             while (rs.next()){
                 d1=rs.getDate(1);
                 d2=rs.getDate(2);
                 cal.setTime(d1);
                 cal2.setTime(d2);
                 CalendarDay day;
                 int min=0;
                 int max=0;
                 if (tabmodel.getMonth()>(cal.get(Calendar.MONTH)+1)){
                     min=1;//czy urlop zaczynał się w poprzednim miesiącu
                     max=cal2.get(Calendar.DATE);
                    /// System.out.println("1:min" +min + ", " + "max=" + max);
                 }
                 else if (cal2.get(Calendar.MONTH)>cal.get(Calendar.MONTH)){
                     min=cal.get(Calendar.DATE);
                     max=cal.getActualMaximum(Calendar.DATE);
                    /// System.out.println("2 :min" +min + ", " + "max=" + max);
                 }
                 else {
                     
                     min=cal.get(Calendar.DATE);
                     max=cal2.get(Calendar.DATE);
                    /// System.out.println("3:min" +min + ", " + "max=" + max);
                 }
                 
                 for (int i=min; i<=max ;i++){//kazdy dzien z przedzialu <cal;cal2)
                     
                     day=tabmodel.getCalendarDay(i);//zostaje ustawiony jako urlopowy
                     day.setUrlop(true);
                     day.setDayInfo(rs.getString(3));
                     day.setDayColor(Color.MAGENTA);
                 }
             }
         }
         catch(Exception e){
             System.out.println("Show Free Time "+e.getStackTrace() +" "+ e.getMessage());
         }
    }

    public boolean isGrafik() {
        return Grafik;
    }

    public void setGrafik(boolean Grafik) {
        this.Grafik = Grafik;
    }

    public boolean isLates() {
        return Lates;
    }

    public void setLates(boolean Lates) {
        this.Lates = Lates;
    }

    public boolean isFreeTime() {
        return freeTime;
    }

    public void setFreeTime(boolean freeTime) {
        this.freeTime = freeTime;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }
    
        public static void main(String [] args){
        JFrame frm=new JFrame("Plan zmiany");
        ShiftSchedule schedule=new ShiftSchedule();
        frm.add(schedule);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
        frm.pack();
        //schedule.setShiftMode(true);
        List<Integer> list=new ArrayList<Integer>();
        list.add(0);
        schedule.setShiftID(list);
//        
        schedule.setWorkerId(0);
        schedule.setFreeTime(true);
        schedule.setGrafik(true);
        schedule.setLates(true);
//       // System.out.println("Month6");
        schedule.addToMonthComboBox(6);
        schedule.addToMonthComboBox(1);
        System.out.println("year2008");
        schedule.addToYearComboBox(2008);
        schedule.addToYearComboBox(2008);
        schedule.addToYearComboBox(2008);
//       // System.out.println("year2007");
        schedule.addToYearComboBox(2007);
        schedule.addToYearComboBox(2009);
//        
        schedule.addToYearComboBox(2010);
        
       
    }

    public List<Integer> getShiftID() {
        return shiftID;
    }

    public void setShiftID(List<Integer> shiftID) {
        this.shiftID = shiftID;
    }
    
    public void addShiftID(Integer id){
        this.shiftID.add(id);
    }

    public List<Color> getShiftColor() {
        return shiftColor;
    }

    public void setShiftColor(List<Color> shiftColor) {
        this.shiftColor = shiftColor;
    }
    
    public void addShiftColor(Color c){
        this.shiftColor.add(c);
    }
            
}

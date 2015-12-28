/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package customComponents;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.table.*;
import javax.swing.*;
import java.util.*;
/**
 *
 * @author Łukasz Spintzyk
 */
//public class ScheduleDefaultCellRenderer implements TableCellRenderer {
public class ScheduleDefaultCellRenderer extends DefaultTableCellRenderer {
    @Override
    public void paint(Graphics g) {
                if (day!=null){
                    DayEvent[] events=day.getEvents();
                    if (day.isUrlop()){
                        g.setColor(Color.getHSBColor((float)(99.0/256.0),(float)(83.0/256.0), (float)(1.0)));
                        g.fillRect(0, 0, this.getWidth(), this.getHeight());
                    }
                    else{
                        if (day.isPrevNextMonth()){
                            g.setColor(Color.getHSBColor(0, 0,(float) (235.0/255.0)));//Very Light gray
                            g.fillRect(0, 0, this.getWidth(), this.getHeight());
                        }
                    }
                    if (events!=null){
                        int h1,h2,m1,m2;
                        
                        double temp=0;
                        for(DayEvent e:events){
                            //funkcja rysuje zdarzenia w ciągu dnia na kalendarzu
                            if (e==null) break;
                            g.setColor(e.getColor());
                            Calendar cal=Calendar.getInstance();
                            Date d=e.getDataBegin();
                            if (d==null) continue;
                            cal.setTime(d);
                            h1=cal.get(Calendar.HOUR_OF_DAY);
                            m1=cal.get(Calendar.MINUTE);
                            
                            
                            temp=(((double)h1+(double)m1/60.0)/24.0) * (double)this.getHeight();
                            h1=(int)temp;
                            if (!e.isWideEvent()) {
                                g.drawLine(0,h1, this.getWidth(), h1);
                                g.setFont(new Font("serif", Font.BOLD,9));
                                g.drawString(e.toString(),1, h1);
                            }
                            else{
                                d=e.getDataEnd();
                                if (d==null) continue;
                                
                                cal.setTime(d);
                                                           
                                h2=cal.get(Calendar.HOUR_OF_DAY);
                                m2=cal.get(Calendar.MINUTE);
                                temp=(((double)h2+(double)m2/60.0)/24.0) * (double)this.getHeight();
                                h2=(int)temp;
                                g.fillRect(0, h1, this.getWidth(), h2-h1);
                                
                                g.setColor(Color.BLACK);
                                g.setFont(new Font("serif", Font.BOLD,9));
                                g.drawString(e.toString(),1,h1);
                            }
                        }
                    }
                    
                }
                //draw orange border around cell
                g.setColor(Color.ORANGE);
                if (isSelected) {
                    g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
                }
                //draw number of the day
                g.setColor(Color.LIGHT_GRAY);
                g.setColor(day.getDayColor());
                g.setFont(new Font("serif", Font.BOLD, 20));
                g.drawString(value.toString(), 5 , 20);
                
    }
    
    private Integer value;
    private CalendarDay day;
    private boolean  isSelected;
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected, boolean hasFocus, int row, int column) {
        this.isSelected=isSelected;
        
        
        if (value instanceof CalendarDay){
            CalendarDay cal=(CalendarDay)value;
            this.value=cal.getDayInMonth();
            day=(CalendarDay)value;
            if (day.isUrlop()) setToolTipText("<html>Dźień wolny<br>");
            else setToolTipText("<html>"+day.getDayInfo());
            
            DayEvent[] events=day.getEvents();
                for(DayEvent e:events){
                    if (e!=null){
                        String s=getToolTipText();
                        if ((!s.equals("<html>")) && (s!=null))
                             setToolTipText(getToolTipText()+"<br>"+e.toString());
                        else if ((e.toString().equals("")) || (e.toString()==null)) setToolTipText(null);
                        else setToolTipText("<html>"+e.toString()+"<br>");
                        
                        
                    }
                }
        }
        
        else if (value instanceof String){
            this.value=Integer.parseInt((String) value);
        }
        else if ((value instanceof Integer)) {
            this.value=(Integer)value;
        }
        else return new JLabel(value.toString());

        return this;
    }//end getTableCellRendererComponent
                
    }
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package customComponents;

import java.awt.Color;

/**
 *
 * @author Łukasz Spintzyk
 */
public class CalendarDay {
    private int dayInMonth=1;
    private int month=1;
    private int year=1;
    private boolean urlop=false;
    private DayEvent[] eventsTab;
    private int eventsNum=0;
    private Color dayColor=Color.LIGHT_GRAY;
    private boolean prevNextMonth=false;//czy dzień z kartki kalendarza jest z tego miesiąca.
    private String dayInfo="";
    
    public CalendarDay(){
        eventsTab=new DayEvent[10];// stosuję tablice, bo lista byłaby nie tak efektywna,
                                    //a tablica jest wystarczająca
        
    }
    public boolean addEvent(DayEvent day){
        if (getEventsNum()<eventsTab.length){
            eventsTab[eventsNum++]=day;
            return true;
        }
        else return false;
        
    }
    /**
     * 
     * @param n from 1..eventsNum
     * @return true if succesful, false otherwise
     */
    public boolean removeEvent(int n){
        if (n<=0 || n>eventsNum) return false;
        if (n>0 && n<eventsNum){
            n--;
            for (int i=n;i<=eventsNum-1;i++){
                eventsTab[i]=eventsTab[i+1];
            }
            eventsTab[eventsNum]=null;
            eventsNum--;
            return true;
        }
        else if (n==eventsNum){
            eventsTab[eventsNum-1]=null;
            eventsNum--;
            return true;
        }else return false;
    }
    
    public void freeEvents(){
        for (DayEvent e:eventsTab)  //tak nie dziaala kasowanie !?!!!
                e=null;
        for (int i =0; i<eventsTab.length;i++)
                eventsTab[i]=null;

        //ustawienie domyślnych wartości
        eventsNum=0;
        urlop=false;
        dayColor=Color.LIGHT_GRAY;
        prevNextMonth=false;
        dayInfo="";
    }
    
    public DayEvent[] getEvents(){
        return eventsTab;
    }
        
    public int getDayInMonth() {
        return dayInMonth;
    }

    public void setDayInMonth(int dayInMonth) {
        this.dayInMonth = dayInMonth;
    }

    public boolean isUrlop() {
        return urlop;
    }

    public void setUrlop(boolean urlop) {
        this.urlop = urlop;
    }

    public int getEventsNum() {
        return eventsNum;
    }


    public boolean isPrevNextMonth() {
        return prevNextMonth;
    }

    public void setPrevNextMonth(boolean prevNextMonth) {
        this.prevNextMonth = prevNextMonth;
    }

    public Color getDayColor() {
        return dayColor;
    }

    public void setDayColor(Color dayColor) {
        this.dayColor = dayColor;
    }

    public String getDayInfo() {
        return dayInfo;
    }

    public void setDayInfo(String dayInfo) {
        this.dayInfo = dayInfo;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }
    
    public void setYearMonthDay(int y,int m,int d){
        setYear(y);
        setMonth(m);
        setDayInMonth(d);
    }

    public void setYear(int year) {
        this.year = year;
    }
    

}



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package customComponents;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Łukasz Spintzyk
 */
    /** 
     * Class represents information about some event in a day.
     * It will be presented as Colored line in the schedule.
     */
    public class DayEvent{
        private String info;
        private Date dataBegin;
        private Date dataEnd;
        private Color color=Color.RED;
        private boolean wideEvent=false;
        
        /**
         * Tworzy obiekt klasy DayEvent reprezentujący dyskretne zdarzenie
         * @param info Informacja o zdarzeniu
         * @param data Czas zajścia zdarzenia
         * @param color Kolor na jaki powinno być zaznaczone zdarzenie na harmonogramie
         */
        public DayEvent(String info, Date data, Color color){
            this.info=info;
            this.dataBegin=data;
            this.dataEnd=null;
            this.color=color;
            this.wideEvent=false;
        }
        /**
         * Tworzy obiekt klasy DayEvent reprezentujący zdarzenie trwające dłuzszy okres czasu
         * @param info Informacja o zdarzeniu
         * @param dataBegin Początek zajścia zdarzenia.
         * @param dataEnd Koniec zajścia zdarzenia
         * @param color Kolor na jaki będzie zaznaczone zdarzenie w harmonogramie
         */
        public DayEvent(String info, Date dataBegin, Date dataEnd, Color color){
            this.info=info;
            this.dataBegin=dataBegin;
            this.dataEnd=dataEnd;
            this.color=color;
            this.wideEvent=true;           
        }
        

        public String getInfo() {
            return info;
        }
        
        @Override
        public String toString(){
            String s=new String(info);
                       
            if (dataBegin!=null){
                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
                s += "  " + sdf.format(dataBegin);
            }
            if (dataEnd!=null){
                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
                s += "  " + sdf.format(dataEnd);
            }
            return s;
        }

        public Date getDataBegin() {
            return dataBegin;
        }
        
        public Color getColor() {
            return color;
        }

        public Date getDataEnd() {
            return dataEnd;
        }

        public boolean isWideEvent() {
            return wideEvent;
        }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setDataBegin(Date dataBegin) {
        this.dataBegin = dataBegin;
    }

    public void setDataEnd(Date dataEnd) {
        if (dataEnd==null) wideEvent=false;
        else wideEvent=true;
        this.dataEnd = dataEnd;
    }

    public void setWideEvent(boolean wideEvent) {
        this.wideEvent = wideEvent;
    }
    }
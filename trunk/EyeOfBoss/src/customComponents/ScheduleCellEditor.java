/*
 * ScheduleCellEditor.java
 *
 * Created on 17 lipiec 2008, 20:38
 */

package customComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author  Łukasz Spintzyk
 */
public class ScheduleCellEditor extends javax.swing.JPanel implements TableCellEditor{
    private cellEditorImp myCellEditor;
    /** Creates new form ScheduleCellEditor */
    public ScheduleCellEditor(int eventsNumber) {
        initComponents();
        SpinnerNumberModel model;
        ChangeListener chgListener=new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                //zaszła zmaina w ktorymś z hour1, hour2, min1, min2 Spinboxie
                CalendarDay day=myCellEditor.getDay();
                if ((Integer)eventNum.getValue()-1 <0) return;
                DayEvent event=day.getEvents()[(Integer)eventNum.getValue()-1];
                Calendar cal=Calendar.getInstance();
                cal.setTime(event.getDataBegin());
                cal.set(Calendar.HOUR_OF_DAY, (Integer)hour1.getValue());
                cal.set(Calendar.MINUTE, (Integer)min1.getValue());
                
                event.setDataBegin(cal.getTime());
                
                if (setToTime.isSelected()){
                    if ((Integer)hour2.getValue() <= (Integer)hour1.getValue()){
                        hour2.setValue(hour1.getValue());
                        if ((Integer)min2.getValue() < (Integer)min1.getValue())
                            min2.setValue(min1.getValue());
                    }
                            
                    cal.set(Calendar.HOUR_OF_DAY,(Integer)hour2.getValue());
                    cal.set(Calendar.MINUTE,(Integer)min2.getValue());
                    event.setDataEnd(cal.getTime());
                }
                else event.setDataEnd(null);
            }
        };
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                setNameTextFieldEditable(false);
            }
        });
        myCellEditor=new cellEditorImp();
        model=new SpinnerNumberModel(12,0,23,1);
        model.addChangeListener(chgListener);
        hour1.setModel(model);
        model=new SpinnerNumberModel(12,0,23,1);
        model.addChangeListener(chgListener);
        hour2.setModel(model);
        model=new SpinnerNumberModel(0,0,59,1);
        model.addChangeListener(chgListener);
        min1.setModel(model);
        model=new SpinnerNumberModel(0,0,59,1);
        model.addChangeListener(chgListener);
        min2.setModel(model);
        
        model=new SpinnerNumberModel(0, 0, eventsNumber, 1);
        model.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                myCellEditor.eventNumSpinnboxChanged();
            }
        });
                
        eventNum.setModel(model);
        
        
   }

    public Boolean getNameTextFieldEditable() {
        return nameTextFieldEditable;
    }

    public void setNameTextFieldEditable(Boolean nameTextFieldEditable) {
        this.nameTextFieldEditable = nameTextFieldEditable;
        nameTextField.setText("Zmiana");
        nameTextField.setEditable(nameTextFieldEditable);
        
    }
    private class cellEditorImp extends AbstractCellEditor implements TableCellEditor, ActionListener{
        private JButton button;
        
        private CalendarDay day;
        private JFrame frm;
        private static final String BUTTON="Button";
        public cellEditorImp(){
            
            button=new JButton();
            button.setText("Edit...");
            frm=new JFrame();
            frm.add(ScheduleCellEditor.this);
            frm.pack();
            frm.setResizable(false);
            
            frm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    fireEditingStopped();
                }
            });
            
            
            button.setBorderPainted(false);
            button.setActionCommand(BUTTON);
            
            button.addActionListener(this);

        }
        
        public Object getCellEditorValue() {
            return getDay();
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof CalendarDay){
                day=(CalendarDay)value;
                SpinnerNumberModel model=(SpinnerNumberModel)eventNum.getModel();
                model.setMaximum(getDay().getEventsNum());
                if (getDay().getEventsNum() > 0) model.setMinimum(new Integer(1));
                else model.setMinimum(new Integer(0));
                if (getDay().getEventsNum() > 0) eventNum.setValue(1);
                if ((Integer)eventNum.getValue() > getDay().getEventsNum()) eventNum.setValue((Integer)model.getMinimum());
                
            }
            return button;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(BUTTON)){
                //czyli akcja na przycisku

                frm.setVisible(true);
                
                frm.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                
                //Sprawi, że tabela będzie uważała edycję za zakończoną.
                //Natomiast edytor, który jest widoczny ma referencję do
                //edytowanego obiektu i może go zmieniać. 
                //Podczas gdy wyświetlany jest zamiast przycisku dzień.
//                fireEditingStopped();

            }
            else {
                //gasimy edytor
                frm.setVisible(false);
                

            }
        }
        
        public void eventNumSpinnboxChanged(){
                if (getDay()==null) return;
                //ustaw zawartość formatki według day
                DayEvent[] event=getDay().getEvents();
                Date data;
                Calendar cal=Calendar.getInstance();
                if (getDay().getEventsNum() >0 && (Integer)eventNum.getValue()>0) {
                    int num=(Integer)eventNum.getValue() -1;
                    data=event[num].getDataBegin();
                    cal.setTime(data);
                    hour1.setValue(cal.get(Calendar.HOUR_OF_DAY));
                    min1.setValue(cal.get(Calendar.MINUTE));
                    data=event[num].getDataEnd();
                    if (data==null) setToTime.setSelected(false);
                    else {
                        setToTime.setSelected(true);
                        cal.setTime(data);
                        hour2.setValue(cal.get(Calendar.HOUR_OF_DAY));
                        min2.setValue(cal.get(Calendar.MINUTE));
                    }
                    
                    nameTextField.setText(event[num].getInfo());
                    
                }
        }

        public CalendarDay getDay() {
            return day;
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        eventNum = new javax.swing.JSpinner();
        hour1 = new javax.swing.JSpinner();
        min1 = new javax.swing.JSpinner();
        hour2 = new javax.swing.JSpinner();
        min2 = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        godzLabel2 = new javax.swing.JLabel();
        minLabel2 = new javax.swing.JLabel();
        addEventButton = new javax.swing.JButton();
        delEventButton = new javax.swing.JButton();
        setToTime = new javax.swing.JCheckBox();
        nameTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        eventNum.setName("eventNum"); // NOI18N

        hour1.setName("hour1"); // NOI18N

        min1.setName("min1"); // NOI18N

        hour2.setName("hour2"); // NOI18N

        min2.setName("min2"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(logIn.LogIn.class).getContext().getResourceMap(ScheduleCellEditor.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        godzLabel2.setText(resourceMap.getString("godzLabel2.text")); // NOI18N
        godzLabel2.setName("godzLabel2"); // NOI18N

        minLabel2.setText(resourceMap.getString("minLabel2.text")); // NOI18N
        minLabel2.setName("minLabel2"); // NOI18N

        addEventButton.setIcon(resourceMap.getIcon("addEventButton.icon")); // NOI18N
        addEventButton.setText(resourceMap.getString("addEventButton.text")); // NOI18N
        addEventButton.setToolTipText(resourceMap.getString("addEventButton.toolTipText")); // NOI18N
        addEventButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        addEventButton.setName("addEventButton"); // NOI18N
        addEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEventButtonActionPerformed(evt);
            }
        });

        delEventButton.setText(resourceMap.getString("delEventButton.text")); // NOI18N
        delEventButton.setToolTipText(resourceMap.getString("delEventButton.toolTipText")); // NOI18N
        delEventButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        delEventButton.setName("delEventButton"); // NOI18N
        delEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delEventButtonActionPerformed(evt);
            }
        });

        setToTime.setSelected(true);
        setToTime.setText(resourceMap.getString("setToTime.text")); // NOI18N
        setToTime.setToolTipText(resourceMap.getString("setToTime.toolTipText")); // NOI18N
        setToTime.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        setToTime.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        setToTime.setName("setToTime"); // NOI18N
        setToTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setToTimeActionPerformed(evt);
            }
        });

        nameTextField.setText(resourceMap.getString("nameTextField.text")); // NOI18N
        nameTextField.setName("nameTextField"); // NOI18N
        nameTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                nameTextFieldCaretUpdate(evt);
            }
        });

        nameLabel.setText(resourceMap.getString("nameLabel.text")); // NOI18N
        nameLabel.setName("nameLabel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(nameLabel)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(eventNum, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(addEventButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(delEventButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(godzLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hour2))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hour1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(minLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(min2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(min1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel4)
                    .addComponent(setToTime, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(eventNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addEventButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delEventButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hour1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(min1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setToTime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(min2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hour2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(godzLabel2)
                    .addComponent(minLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


private void setToTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setToTimeActionPerformed
    //jeśli combobox zaznaczony to wyświetlają się spinboxy na drugą datę.
    hour2.setVisible(setToTime.isSelected());
    min2.setVisible(setToTime.isSelected());
    godzLabel2.setVisible(setToTime.isSelected());
    minLabel2.setVisible(setToTime.isSelected());
    
    //skasuj lub dodaj drugą datę zdarzenia.
    if ((Integer)eventNum.getValue()==0) return;
    DayEvent e=myCellEditor.getDay().getEvents()[(Integer)eventNum.getValue()-1];
    if (setToTime.isSelected()) {
        Calendar cal=Calendar.getInstance();
        cal.setTime(e.getDataBegin());
        cal.set(Calendar.HOUR_OF_DAY, (Integer)hour2.getValue());
        cal.set(Calendar.MINUTE, (Integer)min2.getValue());
        e.setDataEnd(cal.getTime());
        e.setWideEvent(true);
    }
    else {
        e.setDataEnd(null);
        e.setWideEvent(false);
    }
        
}//GEN-LAST:event_setToTimeActionPerformed

private void addEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEventButtonActionPerformed
// dodaj do myCellEditorImp.day nowe zdarzenie jeśli nie maxymalna ilość
    CalendarDay day=myCellEditor.getDay();
    DayEvent e=null;
    Calendar cal=Calendar.getInstance();
    cal.set(Calendar.YEAR, day.getYear());
    cal.set(Calendar.MONTH, day.getMonth()-1);
    cal.set(Calendar.DATE, day.getDayInMonth());
    cal.set(Calendar.HOUR, (Integer)hour1.getValue());
    cal.set(Calendar.MINUTE, (Integer)min1.getValue());
    if (setToTime.isSelected()){
        Date d1=cal.getTime();
        cal.set(Calendar.HOUR, (Integer)hour2.getValue());
        cal.set(Calendar.MINUTE, (Integer)min2.getValue());
        e=new DayEvent(new String(nameTextField.getText()),d1, cal.getTime(), Color.GREEN);
    }
    else e=new DayEvent(nameTextField.getText(), cal.getTime(), Color.GREEN);
    
    if (day.addEvent(e)){//udało się dodać nowe zdarzenie
        //zwiększ zakres spinboxa
        day.getEventsNum();
        SpinnerNumberModel sm=(SpinnerNumberModel)eventNum.getModel();
        Integer i=(Integer)eventNum.getValue() +1;
        sm.setMaximum(day.getEventsNum());
        sm.setMinimum(1);
        if (((Integer)eventNum.getValue()).equals(0)) eventNum.setValue((Integer)1);
    }
    
}//GEN-LAST:event_addEventButtonActionPerformed

private void delEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delEventButtonActionPerformed
//usuń aktualne zdarzenie z myCellEditorImp.day
    CalendarDay day=myCellEditor.getDay();
    if (day==null) return;
    if (day.removeEvent((Integer)eventNum.getValue())) {
        //udało się usunąć zdarzenie
        SpinnerNumberModel sm=(SpinnerNumberModel)eventNum.getModel();
        Integer max=(Integer) sm.getMaximum();
        Integer min=(Integer) sm.getMinimum();
        int n=((Integer)eventNum.getValue());

        
        if (day.getEventsNum()==0) {
            
            eventNum.setValue(0);
            sm.setMinimum(new Integer(0));
        }
        else if(day.getEventsNum()<n){
            eventNum.setValue(day.getEventsNum());
            sm.setMinimum(new Integer(1));
        }
        
        sm.setMaximum(day.getEventsNum());
    }
}//GEN-LAST:event_delEventButtonActionPerformed

private void nameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_nameTextFieldCaretUpdate
// TODO add your handling code here:
    if (myCellEditor.getDay()==null) return;
    int i=(Integer)eventNum.getValue()-1;
    if (i>=0)
        myCellEditor.getDay().getEvents()[(Integer)eventNum.getValue()-1].setInfo(nameTextField.getText());
}//GEN-LAST:event_nameTextFieldCaretUpdate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEventButton;
    private javax.swing.JButton delEventButton;
    private javax.swing.JSpinner eventNum;
    private javax.swing.JLabel godzLabel2;
    private javax.swing.JSpinner hour1;
    private javax.swing.JSpinner hour2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSpinner min1;
    private javax.swing.JSpinner min2;
    private javax.swing.JLabel minLabel2;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JCheckBox setToTime;
    // End of variables declaration//GEN-END:variables

    private Boolean nameTextFieldEditable=false;
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return myCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    public Object getCellEditorValue() {
        return myCellEditor.getCellEditorValue();
    }

    public boolean isCellEditable(EventObject anEvent) {
        return myCellEditor.isCellEditable(anEvent);
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return myCellEditor.shouldSelectCell(anEvent);
    }

    public boolean stopCellEditing() {
        return myCellEditor.stopCellEditing();
    }

    public void cancelCellEditing() {
        myCellEditor.cancelCellEditing();
    }

    public void addCellEditorListener(CellEditorListener l) {
        myCellEditor.addCellEditorListener(l);
    }

    public void removeCellEditorListener(CellEditorListener l) {

            myCellEditor.removeCellEditorListener(l);

    }
    
    public static void main(String args[]){
        JFrame frm=new JFrame("ScheduleCellEditor");
        ScheduleCellEditor sch=new ScheduleCellEditor(10);
        frm.add(sch);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
        frm.setResizable(false);
        frm.pack();
    }

}

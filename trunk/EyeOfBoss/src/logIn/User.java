/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package logIn;
import java.util.Calendar;
import java.util.Calendar.*;
import java.util.Date;

/**
 *
 * @author lukasz
 */
public class User {

    public User() {
        
    }
    private User(int id, String login, String pass, Privilages priv){
        this.login=login;
        this.password=pass;
        this.priv=priv;
        this.id=id;
    }
    private User(String pass){
       password=new String(pass);
    }
    
    int id=0;
    private Privilages priv;
    private String login="";
    private String password="";
    private String imie="";
    private String nazwisko="";
    private String miasto="";
    private String adres="";
    private String email="";
    private Date data_zatrudnienia=Calendar.getInstance().getTime();
    private Date data_zwolnienia=Calendar.getInstance().getTime();
    private static User user=null;
    private Integer telefon=0;
    private Integer idStawki=0;
    private int shiftId=0;
    
    
    public static User getUser(String pass){
        if (user==null && pass!=null) user=new User(pass);
        else if (user==null) user=new User("secretpass");
        return user;
    }
    public static User getUser(){
        return user;
    }
    
    public int getId(){
        return id;
    }
    public void setId(int ID){
        id=ID;
    }
    
    public String getLogin(){
        return login;
    }
    public void setLogin(String LOGIN){
        login =new String(LOGIN);//@TODO czy to jest dobrze ?
    }
    
    public void setImie(String name){
        imie=name;
    }
    public String getImie(){
        return imie;
    }
    
    public void setNazwisko(String nazwisko){
        this.nazwisko=nazwisko;
    }
    public String getNazwisko(){
        return nazwisko;
    }
    
    public void setMiasto(String miasto){
        this.miasto=miasto;
    }
    public String getMiasto(){
        return miasto;
    }
    
    public void setAdres(String adres){
        this.adres=adres;
    }
    public String getAdres(){
        return adres;
    }
    
    public void setEmail(String email){
        this.email=email;
    }
    public String getEmail(){
        return email;
    }
    public Integer getTelefon(){
        return telefon;
    }
    public void setTelefon(int tel){
        telefon=tel;
    }
    
    public void setDataZatrudnienia(Date data){
        this.data_zatrudnienia=data;
    }
    public Date getDataZatrudnienia(){
        return this.data_zatrudnienia;
    }
    
    public void setDataZwolnienia(Date data){
        this.setData_zwolnienia(data);
    }
    public Date getDataZwolnienia(){
        return this.getData_zwolnienia();
    }
    
    public boolean isPassword(String pass){
        return this.password.equals(pass);
    }

    public Integer getIdStawki() {
        return idStawki;
    }

    public void setIdStawki(Integer idStawki) {
        this.idStawki = idStawki;
    }

    public Date getData_zwolnienia() {
        return data_zwolnienia;
    }

    public void setData_zwolnienia(Date data_zwolnienia) {
        this.data_zwolnienia = data_zwolnienia;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }
    

}
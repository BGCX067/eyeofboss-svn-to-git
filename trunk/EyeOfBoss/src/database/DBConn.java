/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package database;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Łukasz Spintzyk
 */
public class DBConn {
    /* class DBConn 
     * Klasa ta zapewnia połączenie z bazą danych w aplikacji.
     * Ponieważ nawiazanie połączenia jest długotrwałe to jest ono nawiązywane tylko raz.
     * Klasa jest cała statyczna, nie trzeba tworzyć żadnego obiektu.
     * Aby otrzymać otwarte połączenie napisz:
     * Connection bd=DBConn.getConnection();
     * 
     * Klasa dodatkowo udostępnia metody do zmiany domyślnych ustawień.
     * Uwaga jeśli zmienisz driver wywołaj DBConn.closeConnection();
     * 
     * Metoda statyczna test służy do sprawdzenia czy wszystko jest okej.
     * Aby sprawdzić czy działa w NetBeans wciścnij Shift+F6 co uruchomi metodę main()
     * 
     * Pamiętaj aby załadować sterownik mysql-a:
     *      1.W okienku Projects->EyeOfBoss->properties->libaries->addLibrary
     *      2.Wybierz z dostępnych bibliotek Mysql-cośtam
     * 
     * 
     * 
     *
     */
    public static void main(String args[]){
        DBConn.getConnection();
        DBConn.test();

    }
    //static private String URL="jdbc:mysql://gajcz.bee.pl/bd";     //adres połączenia do servera gajcza
    //static private String usr="bd";
    static private String URL="jdbc:mysql://193.24.196.154/bd";     //adres połączenia do lokalnego serwera Łukasza
    //static private String URL="jdbc:mysql://localhost/bd";     //adres połączenia do lokalnego serwera Łukasza
    static private String usr="lukasz";
    static private String pass="bdprojekt";
    static private String className="com.mysql.jdbc.Driver";
    static private Connection conn;
    
    //193.24.196.154

    
    
    public static void setUser(String user){
        usr=user;
    }
    public static void setPassword(String password){
        pass=password;
    }
  
    public static void setDriver(String name){
        className=name;
    }   
    public static void setURL(String url){
        URL=url;
    }
    
    public static String getURL(){
        return URL;
    }
    public static void closeConnection(){
        try{
            if (conn!=null) conn.close();
            conn=null;
        }
        catch(Exception e){}
    }
    public static Connection getConnection(){
        if (conn == null) {
            try {
                Class.forName(className);
                DriverManager.setLoginTimeout(3);
                int num_try = 3;//ile będzie możliwych prób logowania
                while ((num_try) > 0) {
                    num_try--;
                    try {
                        conn = DriverManager.getConnection(URL, usr, pass);
                        
                    } catch (SQLException e) {
                        System.out.println("Connection faild:" + e.getMessage());

                        Object obj[] = {"TAK", "NIE"};
                        int opt = JOptionPane.showOptionDialog(null, "Nie można nawiązać połączenia z Bazą Danych,spróbować jeszcze raz?", "Błąd połączenia", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, obj, obj[0]);
                        if (opt == JOptionPane.NO_OPTION) break;
                    }
                }

                if (conn == null) 
                    JOptionPane.showMessageDialog(null, "Nie udało się nawiązać połączenia z bazą danych.");
                
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found " + e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        
        return conn;
    }
    public static void test(){
        try{
            if (conn==null) return;
            Statement stmt=conn.createStatement();
            System.out.println("Wypisywanie tabeli Pracownik");
            ResultSet rs=stmt.executeQuery("SELECT * from Pracownik");
            while(rs.next()){
                for (int i=1;i<10;i++)
                System.out.print(rs.getString(i)+ " ");
                System.out.println("");
            }
    }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Klasa testująca ma za zadanie sprawdzenie czy wszystkie zapytania do bazy danych są poprawne.
 * Jeśli ktoś dokona zmiany struktury bazy danych, lub doda nowe dane może 
 * wtedy uruchomi te testy jednostkowe i będzie wiedział co działa nadal, a co
 * zostało zepsute.
 * @author Łukasz Spintzyk
 */
public class DatabaseQueries {

    public DatabaseQueries() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() {
        System.out.println("Hello world");
        assertTrue(true);
    }

    @Test
    public void checkSelectOnTables() {
        String tabela="";
        try {
            java.sql.Statement stmt = DBConn.getConnection().createStatement();
                      tabela="Typy_zwolnien";
            ResultSet rs = stmt.executeQuery("SELECT nazwa_zwolnienia, id_typu, mnoznik FROM Typy_zwolnien");
                      tabela="Zwolnienia";
                      rs = stmt.executeQuery("SELECT z.zwolnienie_od, z.zwolnienie_do, tz.nazwa_zwolnienia FROM Zwolnienia z JOIN Typy_zwolnien tz USING(id_typu) WHERE id_pracownika=1 ORDER BY z.zwolnienie_od DESC");
                      tabela="Pracownik";
                      rs = stmt.executeQuery("SELECT id_pracownika, id_stawki, id_zmiany, imie, nazwisko, id_nast_zmiany, " +
                                            "data_zatrudnienia, data_zwolnienia, miasto, adres, telefon, email FROM Pracownik WHERE id_pracownika=0");
                      tabela="Grafik";
                      rs = stmt.executeQuery("SELECT id_grafiku, id_zmiany, dzien_od, dzien_do FROM Grafik WHERE id_grafiku=0");
                      tabela="Przyjscie_Wyjscie";
                      rs = stmt.executeQuery("SELECT id_wewy, id_pracownika, godz_we, godz_wy FROM Przyjscie_Wyjscie WHERE id_wewy=0");
                      tabela="Sprzet";
                      rs = stmt.executeQuery("SELECT id_sprzetu, opis,cena, dezaktywowany FROM Sprzet WHERE id_sprzetu=0");
                      tabela="Stawka";
                      rs = stmt.executeQuery("SELECT id_stawki, id_pracownika, dzienna, nadgodziny, weekendy, nazwa, spoznienia FROM Stawka WHERE id_stawki=0");
                      tabela="Wyplaty";
                      rs = stmt.executeQuery("SELECT id_wyplaty, id_pracownika, kwota, data_od, za_co FROM Wyplaty WHERE id_wyplaty=0");
                      tabela="Zmiana";
                      rs = stmt.executeQuery("SELECT id_zmiany, data_od FROM Zmiana WHERE id_zmiany=0");
                      tabela="Zwolnienia";
                      rs = stmt.executeQuery("SELECT id_zwolnienia, id_pracownika, id_typu, zwolnienie_od, zwolnienie_do, zatwierdzone FROM Zwolnienia WHERE id_zwolnienia=0");
                      tabela="nadgodziny";
                      rs = stmt.executeQuery("SELECT id_pracownika, id_nadgodzin, czas, data_wyjscia FROM nadgodziny WHERE id_pracownika=0");
                      tabela="spoznienie";
                      rs = stmt.executeQuery("SELECT id_spoznienia, id_pracownika, czas_spoznienia, data_przyjscia FROM spoznienie WHERE id_spoznienia=0");
                      tabela="uprawnienia";
                      rs = stmt.executeQuery("SELECT id_uprawnienia, uprawnienie, opis_uprawnienia FROM uprawnienia WHERE id_uprawnienia=0");
                      tabela="uzytkownik";
                      rs = stmt.executeQuery("SELECT id_uzytkownika, id_pracownika, id_uprawnienia, login, password FROM uzytkownik WHERE id_uzytkownika=0");
                      tabela="wyporzyczyl";
                      rs = stmt.executeQuery("SELECT id_pracownika, id_sprzetu FROM wypozyczyl WHERE id_pracownika=0");
        } catch (SQLException ex) {
            fail("Nie udalo się dokonanć SELECT na " + tabela + ex.getMessage());
        } catch (Exception e) {
            assertTrue(false);
        }

    }


    
    
  
   
    

   
}
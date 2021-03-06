package JDBC;

import sample.Cashier.CashierController;
import sample.Cashier.CashierPath;
import sample.Client.ClientController;
import sample.Client.ClientPath;
import sample.Marketing.MarketingReservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Reservation {

    private void addReservation(Connection conn, String date, int noPath, String status, int clientID, int generalID) throws SQLException {
        try {
            PreparedStatement stmt;

            conn.setAutoCommit(false);

            stmt = conn.prepareStatement("INSERT INTO Rezerwacje_Toru VALUES(?, to_date(?, 'YYYY-MM-DD HH24:MI'), ?, ?, ?, ?)");

            PreparedStatement stmt2 = conn.prepareStatement("SELECT MAX(Rezerwacje_Toru.Numer_rezerwacji) FROM Rezerwacje_Toru");
            ResultSet rset = stmt2.executeQuery();

            int id;
            if(rset.next()) id = rset.getInt(1)+1;
            else id = 1;

            rset.close();
            stmt2.close();

            stmt.setInt(1, id);
            stmt.setString(2, date);
            stmt.setInt(3, noPath);
            stmt.setString(4, status);
            stmt.setInt(5, clientID);
            stmt.setInt(6, generalID);
            stmt.executeQuery();
            stmt.close();
            conn.commit();
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
            conn.rollback();
        }
        finally {
            conn.setAutoCommit(true);
        }
    }

    public static void setReservation(Connection conn, int path, String state) throws SQLException {
        try {
            PreparedStatement stmt;

            conn.setAutoCommit(false);

            if(state.equals("Nie skorzystano")) stmt = conn.prepareStatement("UPDATE Rezerwacje_Toru SET Status = 1 WHERE Numer_Rezerwacji = ?");
            else stmt = conn.prepareStatement("UPDATE Rezerwacje_Toru SET Status = 0 WHERE Numer_Rezerwacji = ?");

            stmt.setInt(1, path);
            stmt.executeQuery();
            stmt.close();
            conn.commit();
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
            conn.rollback();
        }
        finally {
            conn.setAutoCommit(true);
        }
    }

    public static void addClientReservation(Connection conn, int clientID, String date, String path, String poolName) throws SQLException {
        try {
            PreparedStatement stmt;

            conn.setAutoCommit(false);

            stmt = conn.prepareStatement("INSERT INTO Rezerwacje_Toru VALUES(?, to_date(?, 'YYYY-MM-DD HH24:MI'), ?, 0, ?, ?)");

            PreparedStatement stmt2 = conn.prepareStatement("SELECT MAX(Rezerwacje_Toru.Numer_rezerwacji) FROM Rezerwacje_Toru");
            ResultSet rset = stmt2.executeQuery();

            int id;
            if(rset.next()) id = rset.getInt(1)+1;
            else id = 1;

            rset.close();
            stmt2.close();

            stmt2 = conn.prepareStatement(
                    "SELECT Ogolne_Numer_Uslugi FROM " +
                            "(SELECT b.Nazwa_Obiektu, u.Nazwa_Uslugi, u.Ogolne_Numer_Uslugi FROM Baseny b JOIN Uslugi u ON b.Numer_Obiektu = u.Baseny_Numer_Obiektu) " +
                         "WHERE Nazwa_Obiektu = ? AND Nazwa_Uslugi = 'rezerwacja toru' "
            );
            stmt2.setString(1, poolName);
            rset = stmt2.executeQuery();

            int generalID;
            if(rset.next()) generalID = rset.getInt(1);
            else return;

            rset.close();
            stmt2.close();

            stmt.setInt(1, id);
            stmt.setString(2, date);
            stmt.setString(3, path);
            stmt.setInt(4, clientID);
            stmt.setInt(5, generalID);
            stmt.executeQuery();
            stmt.close();
            conn.commit();
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
            conn.rollback();
        }
        finally {
            conn.setAutoCommit(true);
        }
    }

    static public CashierPath getCashierReservation(CashierController cc, Connection conn, int id, int poolNo) throws SQLException {
        PreparedStatement stmt;
        if(poolNo == 0)
            stmt = conn.prepareStatement(
                    "SELECT to_char(Data_i_Godzina, 'YYYY-MM-DD HH24:MI'), Numer_toru, Status FROM " +
                            "(SELECT rt.Data_i_Godzina, rt.Numer_Toru, rt.Status, rt.Numer_Rezerwacji, u.Baseny_Numer_Obiektu FROM Rezerwacje_Toru rt JOIN Uslugi u ON u.Ogolne_Numer_Uslugi = rt.Ogolne_Numer_Uslugi) " +
                         "WHERE Numer_Rezerwacji = ? AND Data_I_Godzina > SYSDATE");
        else
            {
            stmt = conn.prepareStatement(
                    "SELECT to_char(Data_i_Godzina, 'YYYY-MM-DD HH24:MI'), Numer_toru, Status FROM " +
                            "(SELECT rt.Data_i_Godzina, rt.Numer_Toru, rt.Status, rt.Numer_Rezerwacji, u.Baseny_Numer_Obiektu FROM Rezerwacje_Toru rt JOIN Uslugi u ON u.Ogolne_Numer_Uslugi = rt.Ogolne_Numer_Uslugi) " +
                            "WHERE Numer_Rezerwacji = ? AND Data_I_Godzina > SYSDATE AND Baseny_Numer_Obiektu = ?");
            stmt.setInt(2, poolNo);
        }
        stmt.setInt(1, id);
        ResultSet rSet = stmt.executeQuery();
        if(rSet.next()){
            String date = rSet.getString(1);
            int noPath = rSet.getInt(2);
            int state = rSet.getInt(3);
            String status;
            if(state == 0) status = "Nie skorzystano";
            else status = "Skorzystano";
            rSet.close();
            stmt.close();
            return new CashierPath(cc, id, date, noPath, status);
        }
        else{
            rSet.close();
            stmt.close();
            return null;
        }
    }

    static public ClientPath getClientReservation(ClientController cc, Connection conn, int id, String poolItem) throws SQLException {
        String date;
        int noPath;
        PreparedStatement stmt = conn.prepareStatement("SELECT to_char(Data_i_Godzina, 'YYYY-MM-DD HH24:MI'), Numer_Toru FROM" +
                "((SELECT Data_i_Godzina, Numer_Toru, Baseny_Numer_Obiektu FROM " +
                "(SELECT rt.Data_i_Godzina, rt.Numer_Toru, u.Baseny_Numer_Obiektu, rt.Status, rt.Numer_Rezerwacji FROM Rezerwacje_Toru rt JOIN Uslugi u ON rt.Ogolne_Numer_uslugi = u.Ogolne_Numer_Uslugi) " +
                "WHERE Numer_Rezerwacji = ? AND Status = 0 AND Data_I_Godzina > SYSDATE) a JOIN Baseny b ON a.Baseny_Numer_Obiektu = b.Numer_Obiektu)" +
                "WHERE Nazwa_Obiektu = ?");
        stmt.setInt(1, id);
        stmt.setString(2, poolItem);
        ResultSet rSet = stmt.executeQuery();
        if(rSet.next()){
            date = rSet.getString(1);
            noPath = rSet.getInt(2);
            rSet.close();
            stmt.close();
            return new ClientPath(cc, id, date, noPath);
        }
        else{
            rSet.close();
            stmt.close();
            return null;
        }
    }

    static public MarketingReservation getMarketingReservation(Connection conn, int id, String poolItem) throws SQLException {
        String date;
        String name;
        String surname;
        String status;
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT to_char(Data_i_Godzina, 'YYYY-MM-DD HH24:MI'), Imie, Nazwisko, Status FROM " +
                        "(SELECT z.Numer_Rezerwacji, z.Data_i_Godzina, z.Status, z.Imie, z.Nazwisko, b.Nazwa_Obiektu FROM " +
                            "(SELECT a.Numer_Rezerwacji, a.Data_i_Godzina, a.Status, a.Imie, a.Nazwisko, u.Baseny_Numer_Obiektu FROM " +
                                "(SELECT rt.Numer_Rezerwacji, rt.Data_I_Godzina, rt.Ogolne_Numer_Uslugi, rt.Status, k.Imie, k.Nazwisko FROM Rezerwacje_Toru rt JOIN Klienci k ON rt.Klienci_Numer_Klienta = k.Numer_Klienta) a " +
                            "JOIN Uslugi u ON a.Ogolne_Numer_Uslugi = u.Numer_Uslugi) z " +
                        "JOIN Baseny b ON z.Baseny_Numer_Obiektu = b.Numer_Obiektu) " +
                    "WHERE Numer_Rezerwacji = ? AND Nazwa_Obiektu = ?"
        );
        stmt.setInt(1, id);
        stmt.setString(2, poolItem);
        ResultSet rSet = stmt.executeQuery();
        if(rSet.next()){
            date = rSet.getString(1);
            name = rSet.getString(2);
            surname = rSet.getString(3);
            if(rSet.getInt(4)==0) status = "Nie wykorzystana";
            else status = "Wykorzystana";
            rSet.close();
            stmt.close();
            return new MarketingReservation(date, status, name, surname);
        }
        else{
            rSet.close();
            stmt.close();
            return null;
        }
    }
}

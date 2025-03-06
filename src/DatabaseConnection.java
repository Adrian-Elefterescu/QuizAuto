import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Clasa DatabaseConnection gestionează conexiunile la baza de date.
 * Aceasta oferă o metodă statică pentru a obține o conexiune JDBC către baza de date.
 */
public class DatabaseConnection {
    /**
     * URL-ul bazei de date.
     * Exemplu: "jdbc:mysql://localhost:3306/autoquiz"
     */
    private static final String URL = "jdbc:mysql://localhost:3306/AutoQuizDB";
    /**
     * Utilizatorul bazei de date.
     * Exemplu: "root"
     */
    private static final String USER = "root";
    /**
     * Parola utilizatorului bazei de date.
     * Exemplu: "password"
     */
    private static final String PASSWORD = "1234";
    /**
     * Returnează o conexiune către baza de date utilizând setările specificate.
     *
     * @return Un obiect Connection utilizat pentru a interacționa cu baza de date.
     * throws SQLExeption Dacă apare o eroare la conectare.
     */
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexiune la baza de date reușită!");
            return connection;
        } catch (SQLException e) {
            System.err.println("Eroare la conectarea la baza de date: " + e.getMessage());
            return null;
        }
    }

    public class TestConnection {
        public static void main(String[] args) {
            Connection connection = DatabaseConnection.getConnection();
            if (connection != null) {
                System.out.println("Conexiunea funcționează!");
            } else {
                System.out.println("Nu s-a putut stabili conexiunea!");
            }
        }
    }

}

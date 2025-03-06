import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

/**
 * Clasa principală AutoQuiz reprezintă o aplicație pentru chestionare auto.
 * Include funcționalități de autentificare, înregistrare, statistici și susținerea unui quiz.
 */

public class AutoQuiz {
    /**
     * Metoda principală a aplicației. Inițializează interfața de login.
     *
     * @param args Argumentele din linia de comandă (neutilizate).
     */
    private JFrame frame;
    private JPanel panel;
    private JLabel questionLabel, correctLabel, wrongLabel, timerLabel, progressLabel;
    private JButton[] optionsButtons = new JButton[3];
    private int currentQuestionIndex = 0, correctAnswers = 0, wrongAnswers = 0;
    private String[][] questionsData;
    private Timer timer;
    private int timeRemaining = 30 * 60; // 30 minutes in seconds
    private String loggedInUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AutoQuiz autoQuiz = new AutoQuiz();
            autoQuiz.showLoginScreen();
        });
    }
    /**
     * Afișează ecranul de login și înregistrare pentru utilizator.
     * Utilizatorii trebuie să introducă un nume și o parolă de cel puțin 3 caractere.
     */
    public void showLoginScreen() {
        JFrame loginFrame = new JFrame("Login/Register");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLayout(new GridLayout(4, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginFrame.add(usernameLabel);
        loginFrame.add(usernameField);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);
        loginFrame.add(registerButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.length() < 3 || password.length() < 3) {
                JOptionPane.showMessageDialog(loginFrame, "Username i parola trebuie sa aiba cel putin 3 caractere!", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authenticateUser(username, password)) {
                loggedInUser = username;
                loginFrame.dispose();
                showMenuScreen();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Username sau parola incorecta!", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.length() < 3 || password.length() < 3) {
                JOptionPane.showMessageDialog(loginFrame, "Username si parola trebuie să aiba cel puțin 3 caractere!", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (registerUser(username, password)) {
                JOptionPane.showMessageDialog(loginFrame, "Inregistrare realizata cu succes! Te poti conecta acum.", "Succes", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Inregistrare esuată. Username-ul exista deja.", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginFrame.setVisible(true);
    }
    /**
     * Autentifică utilizatorul în baza de date.
     *
     * @param username Numele de utilizator.
     * @param password Parola utilizatorului.
     * @return True dacă autentificarea reușește, False în caz contrar.
     */
    private boolean authenticateUser(String username, String password) {
        //Implemantarea autentificarii
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Înregistrează un nou utilizator în baza de date.
     *
     * @param username Numele de utilizator.
     * @param password Parola utilizatorului.
     * @return True dacă înregistrarea reușește, False dacă utilizatorul există deja.
     */
    private boolean registerUser(String username, String password) {
        //Implementarea inregistrarii
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (username, password) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Afișează statistici pentru utilizatorul autentificat.
     * Statistici includ numărul de chestionare trecute și picate.
     */
    public void showStatisticsScreen() {
        // Implementarea interfeței pentru statistici
        JFrame statsFrame = new JFrame("Statistici");
        statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statsFrame.setSize(400, 300);
        statsFrame.setLayout(new BorderLayout());

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT correct_answers, wrong_answers FROM QuizHistory WHERE username = ?")) {
            statement.setString(1, loggedInUser);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder statsText = new StringBuilder("Statistici pentru utilizatorul: " + loggedInUser + "\n\n");
            int quizCount = 0;
            int totalCorrect = 0;
            int totalWrong = 0;

            while (resultSet.next()) {
                int correct = resultSet.getInt("correct_answers");
                int wrong = resultSet.getInt("wrong_answers");

                statsText.append("Quiz ").append(++quizCount).append(": Corecte: ").append(correct)
                        .append(", Gresite: ").append(wrong).append("\n");
                totalCorrect += correct;
                totalWrong += wrong;
            }

            statsText.append("\nTotal intrebari corecte: ").append(totalCorrect).append("\n");
            statsText.append("Total intrebari gresite: ").append(totalWrong);

            statsArea.setText(statsText.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            statsArea.setText("Eroare la incarcarea statisticilor.");
        }

        statsFrame.add(new JScrollPane(statsArea), BorderLayout.CENTER);

        JButton backButton = new JButton("inapoi la meniu");
        backButton.addActionListener(e -> {
            statsFrame.dispose();
            showMenuScreen();
        });
        statsFrame.add(backButton, BorderLayout.SOUTH);

        statsFrame.setVisible(true);
    }
    /**
     * Afișează meniul principal al aplicației.
     * Include opțiuni pentru Start Quiz și Statistici.
     */
    public void showMenuScreen() {
        JFrame menuFrame = new JFrame("Auto Quiz - Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400, 200);
        menuFrame.setLayout(new GridLayout(2, 1));

        JButton startQuizButton = new JButton("Start Quiz");
        JButton statsButton = new JButton("Statistici");

        menuFrame.add(startQuizButton);
        menuFrame.add(statsButton);

        startQuizButton.addActionListener(e -> {
            menuFrame.dispose();
            startQuiz();
        });

        statsButton.addActionListener(e -> {
            menuFrame.dispose();
            showStatisticsScreen();
        });

        menuFrame.setVisible(true);
    }
    /**
     * Pornește un nou quiz pentru utilizatorul autentificat.
     * Încărca întrebări din baza de date și afișează interfața de quiz.
     */
    public void startQuiz() {
        // Implementarea interfeței de quiz
        frame = new JFrame("Auto Quiz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        questionLabel = new JLabel("", JLabel.CENTER);
        correctLabel = new JLabel("Corecte: 0", JLabel.CENTER);
        wrongLabel = new JLabel("Gresite: 0", JLabel.CENTER);
        timerLabel = new JLabel("Timp ramas: 30:00", JLabel.RIGHT);
        progressLabel = new JLabel("Intrebare: 1/26", JLabel.CENTER);

        JPanel topPanel = new JPanel(new GridLayout(2, 2));
        topPanel.add(correctLabel);
        topPanel.add(wrongLabel);
        topPanel.add(progressLabel);
        topPanel.add(timerLabel);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(3, 1));
        for (int i = 0; i < 3; i++) {
            optionsButtons[i] = new JButton();
            int index = i;
            optionsButtons[i].addActionListener(e -> checkAnswer(index));
            optionsPanel.add(optionsButtons[i]);
        }

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(questionLabel, BorderLayout.CENTER);
        panel.add(optionsPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        resetQuiz();
    }

    private void resetQuiz() {
        correctAnswers = 0;
        wrongAnswers = 0;
        currentQuestionIndex = 0;
        timeRemaining = 30 * 60;

        loadQuestions();
        displayQuestion();
        startTimer();
    }
    /**
     * Încarcă întrebările din baza de date și le stochează într-un array bidimensional.
     * Se selectează 26 de întrebări în ordine aleatoare.
     */
    private void loadQuestions() {
        // Implementarea încărcării întrebărilor
        ArrayList<String[]> questionList = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Questions ORDER BY RAND() LIMIT 26")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                questionList.add(new String[] {
                        resultSet.getString("question"),
                        resultSet.getString("option1"),
                        resultSet.getString("option2"),
                        resultSet.getString("option3"),
                        resultSet.getString("correct_option")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        questionsData = questionList.toArray(new String[0][0]);
    }
    /**
     * Afișează întrebarea curentă și opțiunile disponibile.
     * Se actualizează progresul și interfața.
     */

    private void displayQuestion() {
        // Implementarea afișării întrebării curente
        if (currentQuestionIndex < questionsData.length) {
            String[] question = questionsData[currentQuestionIndex];
            questionLabel.setText(question[0]);
            for (int i = 0; i < 3; i++) {
                optionsButtons[i].setText(question[i + 1]);
            }
            progressLabel.setText("Intrebare: " + (currentQuestionIndex + 1) + "/26");
        } else {
            endQuiz();
        }
    }
    /**
     * Verifică răspunsul utilizatorului la întrebarea curentă.
     * Actualizează numărul de răspunsuri corecte sau greșite.
     *
     * @param selectedOption Indexul opțiunii selectate de utilizator (0, 1 sau 2).
     */
    private void checkAnswer(int selectedOption) {
        String correctOption = questionsData[currentQuestionIndex][4];
        if (String.valueOf(selectedOption + 1).equals(correctOption)) {
            correctAnswers++;
            correctLabel.setText("Corecte: " + correctAnswers);
        } else {
            wrongAnswers++;
            wrongLabel.setText("Gresite: " + wrongAnswers);
        }

        currentQuestionIndex++;
        if (wrongAnswers >= 5) {
            JOptionPane.showMessageDialog(frame, "Ai picat examenul!", "Rezultat", JOptionPane.ERROR_MESSAGE);
            endQuiz();
        } else {
            displayQuestion();
        }
    }
    /**
     * Pornește un cronometru pentru quiz, setat la 30 de minute.
     * Actualizează interfața la fiecare secundă și gestionează expirarea timpului.
     */
    private void startTimer() {
        //Implementarea cronometrului
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                timerLabel.setText(String.format("Timp ramas: %02d:%02d", minutes, seconds));

                if (timeRemaining <= 0) {
                    JOptionPane.showMessageDialog(frame, "Timpul a expirat!", "Rezultat", JOptionPane.WARNING_MESSAGE);
                    endQuiz();
                }
            }
        });
        timer.start();
    }
    /**
     * Finalizează quiz-ul, salvează rezultatele în baza de date și afișează un mesaj final.
     * Redirecționează utilizatorul înapoi la meniul principal.
     */
    private void endQuiz() {
        // Implementarea finalizării quiz-ului
        timer.stop();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO QuizHistory (username, correct_answers, wrong_answers) VALUES (?, ?, ?)")) {
            statement.setString(1, loggedInUser);
            statement.setInt(2, correctAnswers);
            statement.setInt(3, wrongAnswers);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(frame, "Quiz terminat!\nCorecte: " + correctAnswers + "\nGresite: " + wrongAnswers, "Rezultat", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
        showMenuScreen();
    }
}

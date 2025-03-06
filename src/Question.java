/**
 * Clasa Questions reprezintă o întrebare din cadrul quiz-ului.
 * Aceasta conține informațiile legate de textul întrebării, opțiunile de răspuns
 * și care dintre acestea este răspunsul corect.
 */
public class Question {
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private int correctOption;

    public Question(String questionText, String option1, String option2, String option3, int correctOption) {
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.correctOption = correctOption;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public int getCorrectOption() {
        return correctOption;
    }
}

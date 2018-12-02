package sth;

public class SurveyPrintProfessor implements SurveyPrint {
    public String printSurveyCreated(Survey s) {
        return " (por abrir)";
    }

    public String printSurveyOpen(Survey s) {
        return " (aberto)";
    }

    public String printSurveyClosed(Survey s) {
        return " (fechado)";
    }

    public String printSurveyFinalized(Survey s) {
        Project p = s.getProject();

        String text = "\n * Numero de submissões: " + p.getNumberSubmissions() + "\n";
        text += " * Numero de respostas: " + s.getNumberEntries() + "\n";
        text += " * Tempos de resolução (horas) (mínimo, médio, máximo): ";
        text += +s.getMinimumTime() + ", " + s.getAverageTime() + ", " + s.getMaximumTime() + "\n";
        return text;
    }
}
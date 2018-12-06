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
        SurveyStats stats = s.getStats();

        String text = "\n * Número de submissões: " + p.getNumberSubmissions() + "\n";
        text += " * Número de respostas: " + s.getNumberEntries() + "\n";
        text += " * Tempos de resolução (horas) (mínimo, médio, máximo): ";
        text += + stats.getMin() + ", " + stats.getAvg() + ", " + stats.getMax() + "\n";
        return text;
    }
}
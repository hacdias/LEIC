package sth;

public class SurveyPrintStudent implements SurveyPrint {
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
        SurveyStats stats = s.getStats();
        String text = "\n * Número de respostas: " + s.getNumberEntries() + "\n";
        text += " * Tempo médio (horas): " + stats.getAvg() + "\n";
        return text;
    }
}
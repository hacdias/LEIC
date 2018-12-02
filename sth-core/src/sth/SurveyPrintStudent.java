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
        String text = "\n * Numero de respostas: " + s.getNumberEntries() + "\n";
        text += " * Tempo médio (horas): " + s.getAverageTime() + "\n";
        return text;
    }
}
package sth;

public class SurveyPrintRepresentative implements SurveyPrint {
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
        return " - " + s.getNumberEntries() + " respostas - " + stats.getAvg() + " horas\n";
    }
}
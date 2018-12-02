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
        return " - " + s.getNumberEntries() + " respostas - " + s.getAverageTime() + " horas\n";
    }
}
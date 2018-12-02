package sth;

public interface SurveyPrint {
    String printSurveyCreated(Survey s);
    String printSurveyOpen(Survey s);
    String printSurveyClosed(Survey s);
    String printSurveyFinalized(Survey s);
}
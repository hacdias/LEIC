package pt.tecnico.sauron.silo.domain;

import java.util.List;

import pt.tecnico.sauron.silo.grpc.Silo;

public class LogElement {
    private List<Integer> replicaTimestamp;
    private Silo.CamJoinRequest camRequest;
    private Silo.ReportRequest reportRequest;

    public LogElement(List<Integer> timestamp, Silo.CamJoinRequest request) {
        this.replicaTimestamp = timestamp;
        this.camRequest = request;
    }

    public LogElement(List<Integer> timestamp, Silo.ReportRequest request) {
        this.replicaTimestamp = timestamp;
        this.reportRequest = request;
    }

    public Silo.CamJoinRequest getCamJoinRequest() {
        return camRequest;
    }

    public Silo.ReportRequest getReportRequest() {
        return reportRequest;
    }
}
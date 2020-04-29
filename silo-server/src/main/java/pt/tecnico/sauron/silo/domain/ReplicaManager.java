package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pt.tecnico.sauron.silo.grpc.Silo;

public class ReplicaManager {
    private Integer instance;
    private List<Integer> replicaTimestamp = Collections.synchronizedList(new ArrayList<Integer>());
    private List<Integer> valueTimestamp = Collections.synchronizedList(new ArrayList<Integer>());
    private List<LogElement> log = Collections.synchronizedList(new ArrayList<LogElement>());
    private List<List<Integer>> operationTable = Collections.synchronizedList(new ArrayList<List<Integer>>());

    public ReplicaManager(Integer instance, Integer numberServers) {
        this.instance = instance;

        for (Integer i = 0; i < numberServers; i++) {
            this.replicaTimestamp.add(0);
            this.valueTimestamp.add(0);
        }
    }

    private Boolean timestampGreaterOrEquals(List<Integer> prev) {
        for (Integer i = 0; i < prev.size(); i++) {
            if (prev.get(i) > this.valueTimestamp.get(i))
                return false;
        }

        return true;
    }

    public List<Integer> read(List<Integer> prev) {
        while (!this.timestampGreaterOrEquals(prev)) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch( InterruptedException e ){
            }
        }
        return new ArrayList<Integer>(this.valueTimestamp);
    }

    public List<Integer> modify(List<Integer> prev, Object request) {
        if (!this.operationTable.contains(prev)) {
            this.replicaTimestamp.set(this.instance - 1, this.replicaTimestamp.get(this.instance - 1) + 1);

            prev.set(this.instance - 1, this.replicaTimestamp.get(this.instance - 1));

            if (request instanceof Silo.CamJoinRequest)
                log.add(new LogElement(prev, (Silo.CamJoinRequest)request));
            else if (request instanceof Silo.ReportRequest)
                log.add(new LogElement(prev, (Silo.ReportRequest)request));
            return prev;
        }

        return prev;
    }
}
package pt.tecnico.sauron.silo.client.domain;

import pt.tecnico.sauron.silo.grpc.Silo;

import java.util.Map;

public enum ObservationType {
    PERSON {
        public String toString() {
            return "person";
        }
    },
    CAR {
        public String toString() {
            return "car";
        }
    };

    public final static Map<ObservationType, Silo.ObservationType> toSilo = Map.ofEntries(
        Map.entry(ObservationType.PERSON, Silo.ObservationType.PERSON),
        Map.entry(ObservationType.CAR, Silo.ObservationType.CAR)
    );
}

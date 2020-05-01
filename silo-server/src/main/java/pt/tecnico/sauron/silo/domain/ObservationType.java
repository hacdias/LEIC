package pt.tecnico.sauron.silo.domain;

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

    public static final Map<ObservationType, Silo.ObservationType> toSiloType = Map.ofEntries(
        Map.entry(ObservationType.PERSON, Silo.ObservationType.PERSON),
        Map.entry(ObservationType.CAR, Silo.ObservationType.CAR)
    );

    public static final Map<Silo.ObservationType, ObservationType> fromSilo = Map.ofEntries(
        Map.entry(Silo.ObservationType.PERSON, ObservationType.PERSON),
        Map.entry(Silo.ObservationType.CAR, ObservationType.CAR)
    );
}

package pt.tecnico.sauron.silo.domain;

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
  }
}

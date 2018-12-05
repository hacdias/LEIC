package sth;

public class UserDescription {
  private String descPerson(Person p) {
    return p.getId() + "|" + p.getPhoneNumber() + "|" + p.getName();
  }

  private String descWithDisciplines(WithDisciplines w) {
    String str = "";
    for (Discipline d : w.getDisciplines()) {
      str += "\n" + d.toString();
    }
    return str;
  }

  public String descAdministrative(Administrative a) {
    return "FUNCIONÁRIO|" + descPerson(a);
  }

  public String descProfessor(Professor p) {
    return "DOCENTE|" + descPerson(p) + descWithDisciplines(p);
  }

  public String descStudent(Student s) {
    return "ALUNO|" + descPerson(s) + descWithDisciplines(s);
  }

  public String descRepresentative(Student s) {
    return "DELEGADO|" + descPerson(s) + descWithDisciplines(s);
  }
}
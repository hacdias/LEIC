package sth;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.HashMap;
import java.io.Serializable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import sth.exceptions.MaximumDisciplinesExceededException;
import sth.exceptions.MaximumRepresentativesExceeded;
import sth.exceptions.MaximumStudentsExceededException;
import sth.exceptions.BadEntryException;
import sth.exceptions.InvalidCourseSelectionException;
import sth.exceptions.NoSuchPersonIdException;

/**
 * School implementation.
 */
public class School implements Serializable {
  /** Serial number for serialization. */
  private static final long serialVersionUID = 201810051538L;
  private int _nextId = 100000;

  private TreeMap<Integer, Person> _people;
  private TreeSet<Person> _peopleByName;

  private HashMap<Integer, Administrative> _administratives;
  private HashMap<Integer, Student> _students;
  private HashMap<Integer, Professor> _professors;
  private HashMap<String, Course> _courses;

  transient private Person _session;

  School() {
    _people = new TreeMap<Integer, Person>();
    _peopleByName = new TreeSet<Person>(Person.NAME_COMPARATOR);
    _administratives = new HashMap<Integer, Administrative>();
    _students = new HashMap<Integer, Student>();
    _professors = new HashMap<Integer, Professor>();
    _courses = new HashMap<String, Course>();
  }
  
  /**
   * @param filename
   * @throws BadEntryException
   * @throws IOException
   */
  void importFile(String filename) throws IOException, BadEntryException {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line;

    String lastType = "";
    int lastId = 0;

    while ((line = reader.readLine()) != null) {
      String[] fields = line.split("\\|");
      try {
        if (fields[0].startsWith("#")) {
          registerCourseAndDiscipline(lastId, lastType, fields);
        } else {
          lastType = fields[0];
          lastId = registerPerson(fields);
        }
      } catch (BadEntryException e) {

      }
    }

    reader.close();

    //FIXME implement text file reader
  }

  private int registerPerson(String[] fields) throws BadEntryException {
    if (fields.length != 4) {
      throw new BadEntryException("TIPO|identificador|telefone|nome");
    }

    int id = Integer.parseInt(fields[1]);
    Person p;

    if (fields[0].equals("ALUNO") || fields[0].equals("DELEGADO")) {
      Student a = new Student(id, fields[2], fields[3]);
      _students.put(id, a);
      p = a;
    } else if (fields[0].equals("DOCENTE")) {
      Professor prof = new Professor(id, fields[2], fields[3]);
      _professors.put(id, prof);
      p = prof;
    } else if (fields[0].equals("FUNCIONÁRIO")) {
      Administrative a = new Administrative(id, fields[2], fields[3]);
      _administratives.put(id, a);
      p = a;
    } else {
      // TODO: dizer tipo errado?
      throw new BadEntryException("TIPO|identificador|telefone|nome");
    }

    _people.put(id, p);
    _peopleByName.add(p);

    _nextId = Math.max(_nextId, id + 1);

    return id;
  }

  private void registerCourseAndDiscipline(int id, String type, String[] fields) throws BadEntryException {
    if (fields.length != 2) {
      throw new BadEntryException("# Nome do curso|Nome da disciplina");
    }

    String courseName = fields[0].substring(2);
    String disciplineName = fields[1];
    Course c = _courses.get(courseName);
    Discipline d;

    try {
      if (c == null) {
        c = new Course(courseName);
        _courses.put(courseName, c);
      }
      
      if (type.equals("DELEGADO")) {
        c.addRepresentative(_students.get(id));
      }
  
      d = c.getDiscipline(disciplineName);
      if (d == null) {
        d = new Discipline(disciplineName, c);
      }
  
      c.addDiscipline(d);
      
      if (type.equals("DOCENTE")) {
        Professor p = _professors.get(id);
        p.addDiscipline(d);
        d.addProfessor(p);
      } else if (type.equals("ALUNO") || type.equals("DELEGADO")) {
        Student s = _students.get(id);
        s.setCourse(c);
        d.addStudent(s);
        s.addDiscipline(d);
      }
    } catch (MaximumStudentsExceededException e) {
      // TODO: see this ↓
      throw new BadEntryException("TODO: not working");
    } catch (MaximumRepresentativesExceeded e) {
      // TODO: see this ↓
      throw new BadEntryException("TODO: not working");
    } catch (MaximumDisciplinesExceededException e) {
      // TODO: see this ↓
      throw new BadEntryException("TODO: not working");
    }
  }

  public void login(int id) throws NoSuchPersonIdException {
    _session = _people.get(id);

    if (_session == null) {
      throw new NoSuchPersonIdException(id);
    }
  }
  
  public boolean hasAdministrative () {
    return _administratives.get(_session.getId()) != null;
  }
  
  public boolean hasStudent () {
    return _students.get(_session.getId()) != null;
  }
  
  public boolean hasProfessor () {
    return _professors.get(_session.getId()) != null;
  }
  
  public boolean hasRepresentative () {
    if (!hasStudent()) {
      return false;
    }
    
    // TODO: ask teacher: (Student)_session
    Student s = _students.get(_session.getId());
    return s.getCourse().isRepresentative(s);
  }
  
  public void doChangePhoneNumber(String phoneNumber) {
    _session.setPhoneNumber(phoneNumber);
  }

  public String getPerson() {
    return _session.toString();
  }

  public String getPeople () {
    String people = "";
    Set<Integer> ids = _people.keySet();

    for(Integer id : ids){
      people += _people.get(id).toString() + "\n";
    }

    return people.trim();
  }

  public String searchPerson(String name) {
    name = name.trim();

    if (name.equals("")) {
      return "";
    }

    String s = "";
    for (Person p : _peopleByName) {
      if (p.getName().contains(name)) {
        s += p.toString() + "\n";
      }
    }

    return s.trim();
  }
}

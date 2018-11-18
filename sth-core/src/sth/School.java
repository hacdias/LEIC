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
import sth.exceptions.NoSuchDisciplineNameException;
import sth.exceptions.NoSuchProjectNameException;
import sth.exceptions.BadEntryException;
import sth.exceptions.DuplicateProjectNameException;
import sth.exceptions.InvalidCourseSelectionException;
import sth.exceptions.NoSuchPersonIdException;

/**
* <h1> School </h1>
* Implementation of the class School
* <p>
*
* @author  Henrique Dias (ist189455) & Rodrigo Sousa (ist189535)
* @version 1.0
* @since   2018-11-13
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

  /**
   * Constructor of the class School
   */
  School() {
    _people = new TreeMap<Integer, Person>();
    _peopleByName = new TreeSet<Person>(Person.NAME_COMPARATOR);
    _administratives = new HashMap<Integer, Administrative>();
    _students = new HashMap<Integer, Student>();
    _professors = new HashMap<Integer, Professor>();
    _courses = new HashMap<String, Course>();
  }

  /**
   * Takes a string (name of the file) and iniciliazes School, Courses,
   * Disciplines and all other Objects from a state saved previously in a file.
   * @param filename name/path to file to import
   * @throws BadEntryException
   * @throws IOException
   */
  void importFile(String filename) throws IOException, BadEntryException {
    // TODO: cleanup and remake
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line;

    String lastType = "";
    int lastId = 0;

    while ((line = reader.readLine()) != null) {
      String[] fields = line.split("\\|");

      if (fields[0].startsWith("#")) {
        registerCourseAndDiscipline(lastId, lastType, fields);
      } else {
        lastType = fields[0];
        lastId = registerPerson(fields);
      }
    }

    reader.close();

  }

  /**
   * Registers a Person in School accordingly (type based choice)
   * @param fields array of strings with the following struct " {TIPO, identificador, telefone, nome} "
   * @return int ID of the Person registered
   * @throws BadEntryException
   */
  private int registerPerson(String[] fields) throws BadEntryException {
    if (fields.length != 4) {
      throw new BadEntryException("TIPO|identificador|telefone|nome");
    }

    int id;
    Person p;

    try {
      id = Integer.parseInt(fields[1]);
    } catch (NumberFormatException e) {
      throw new BadEntryException("identificador deve ser um número");
    }

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
      throw new BadEntryException("TIPO|identificador|telefone|nome: TIPO inválido");
    }

    _people.put(id, p);
    _peopleByName.add(p);
    _nextId = Math.max(_nextId, id + 1);

    return id;
  }

  /**
   * Registers a Course and Discipline in School accordingly (type based choice)
   * as well as the student in this Course/Discipline
   * @param id the id of the student that must be associated
   * @param type the type of person that is associated
   * @param fields array of strins with the following struct "# Nome do curso|Nome da disciplina"
   * @throws BadEntryException
   */
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
        c.addDiscipline(d);
      }

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
      throw new BadEntryException("Disciplina " + e.getDiscipline() + " atingiu capacidade de " + e.getMaximumStudents());
    } catch (MaximumRepresentativesExceeded e) {
      throw new BadEntryException("Curso " + e.getCourse() + " atingiu capacidade máxima de delegados");
    } catch (MaximumDisciplinesExceededException e) {
      throw new BadEntryException("Aluno " + e.getId() + " capacidade máxima de disciplinas inscritas");
    }
  }

  /**
   * Logs in a Person in the system according to its ID
   * @param id the id of the student that is logging in
   * @throws NoSuchPersonIdException
   */
  public void login(int id) throws NoSuchPersonIdException {
    _session = _people.get(id);

    if (_session == null) {
      throw new NoSuchPersonIdException(id);
    }
  }

  /**
   * Checks if user currently logged in is administrative
   * @return boolean
   */
  public boolean hasAdministrative () {
    return _administratives.get(_session.getId()) != null;
  }

  /**
   * Checks if user currently logged in is student
   * @return boolean
   */
  public boolean hasStudent () {
    return _students.get(_session.getId()) != null;
  }

  /**
   * Checks if user currently logged in is professor
   * @return boolean
   */
  public boolean hasProfessor () {
    return _professors.get(_session.getId()) != null;
  }

  /**
   * Checks if user currently logged in is a representative
   * @return boolean
   */
  public boolean hasRepresentative () {
    if (!hasStudent()) {
      return false;
    }

    Student s = _students.get(_session.getId());
    return s.getCourse().isRepresentative(s);
  }

  /**
   * Changes the phone number of the person logged in
   * @param phoneNumber string with the new phoneNumber
   */
  public void doChangePhoneNumber(String phoneNumber) {
    _session.setPhoneNumber(phoneNumber);
  }

  /**
   * Gets string of information about the user (Person) logged in
   * @return String with the information about the Person logged in
   */
  public String getPerson() {
    return _session.toString();
  }

  /**
   * Gets the ID of the user (Person) logged in
   * @return int ID of the Person logged in
   */
  public int getSessionId () {
    return _session.getId();
  }

  /**
   * Gets string of information about every Person registered in School
   * @return String
   */
  public String getPeople () {
    String people = "";
    Set<Integer> ids = _people.keySet();

    for(Integer id : ids){
      people += _people.get(id).toString() + "\n";
    }

    return people.trim();
  }

  /**
   * Searches for a specific name registered in School and return a string
   * with all the people that match the criteria
   * @param name name that is gonna be searched
   * @return String
   */
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

  /**
   * Returns a string with the information of all the students enrolled in the discipline
   * @param name name of the discipline
   * @return String
   * @throws NoSuchDisciplineNameException
   */
  public String doShowDisciplineStudents(String name) throws NoSuchDisciplineNameException {
    Professor p = _professors.get(_session.getId());
    Discipline d = p.teachesDiscipline(name);

    return d.getStudents();
  }

  /**
   * Creates a new Project in the discipline with <code>projName</code> as its name
   * @param discipline name of the discipline of the project
   * @param projName name of the project to create
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   */
  public void createProject(String discipline, String projName) throws NoSuchDisciplineNameException, DuplicateProjectNameException {
    Professor p = _professors.get(_session.getId());
    Discipline d = p.teachesDiscipline(discipline);

    d.addProject(new Project(projName));
  }

  /**
   * Closes a Project in the discipline that has <code>projName</code> as its name
   * @param discipline name of the discipline of the project
   * @param projName name of the project to close
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   */
  public void closeProject(String discipline, String projName) throws NoSuchDisciplineNameException, NoSuchProjectNameException {
    Professor p = _professors.get(_session.getId());
    Discipline d = p.teachesDiscipline(discipline);

    Project proj = d.getProject(projName);
    proj.close();
  }
}

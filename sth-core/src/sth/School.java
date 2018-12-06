package sth;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import sth.exceptions.BadTypeException;
import sth.exceptions.MaximumDisciplinesExceededException;
import sth.exceptions.MaximumRepresentativesExceeded;
import sth.exceptions.MaximumStudentsExceededException;
import sth.exceptions.NoSuchDisciplineNameException;
import sth.exceptions.NoSuchProjectNameException;
import sth.exceptions.NoSuchProjectOpenException;
import sth.exceptions.BadEntryException;
import sth.exceptions.DuplicateProjectNameException;
import sth.exceptions.InvalidCourseSelectionException;
import sth.exceptions.NoSuchPersonIdException;
import sth.exceptions.DuplicateSurveyProjectException;
import sth.exceptions.NoSurveyProjectException;
import sth.exceptions.NonEmptySurveyProjectException;
import sth.exceptions.SurveyFinishedProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.ClosingSurveyProjectException;
import sth.exceptions.FinishingSurveyProjectException;
import sth.exceptions.InvalidTimeException;

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
    BufferedReader reader = new BufferedReader(new FileReader(filename));

    String line;
    String person;
    LinkedList<String> data;

    try {
      if ((line = reader.readLine()) == null) {
        // file is empty, nothing to import
        return;
      }

      person = line;
      data = new LinkedList<String>();

      while ((line = reader.readLine()) != null) {
        if (line.startsWith("#")) {
          data.add(line);
        } else {
          registerData(person, data);
          data = new LinkedList<String>();
          person = line;
        }
      }

      // registers last person
      registerData(person, data);
    } finally {
      reader.close();
    }
  }

  /**
   * Registers a chunk of data containing the person info and their relations with courses and disciplines.
   * @param person a line containing a person. Must be of type "TIPO|identificador|telefone|nome".
   * @param data a list of strings containing the relations with courses and disciplines. Must be of type "# Nome do curso|Nome da disciplina".
   * @throws BadEntryException
   */
  private void registerData(String person, List<String> data) throws BadEntryException {
    String[] personFields = person.split("\\|");

    if (personFields.length != 4) {
      throw new BadEntryException("TIPO|identificador|telefone|nome");
    }

    int id;

    try {
      id = Integer.parseInt(personFields[1]);
    } catch (NumberFormatException e) {
      throw new BadEntryException("identificador deve ser um número");
    }

    try {
      registerPerson(personFields[0], id, personFields[2], personFields[3]);
    } catch (BadTypeException e) {
      throw new BadEntryException("Bad type: " + e.getType());
    }

    if (personFields[0].equals("FUNCIONÁRIO")) {
      return;
    }

    Boolean   isRepresentative = personFields[0].equals("DELEGADO");
    Student   stud = null;
    Professor prof = null;

    if (isRepresentative || personFields[0].equals("ALUNO")) {
      stud = _students.get(id);
    } else {
      prof = _professors.get(id);
    }

    for (String raw : data) {
      String[] line = raw.split("\\|");
      line[0] = line[0].substring(2);

      if (line.length != 2) {
        throw new BadEntryException("# Nome do curso|Nome da disciplina");
      }

      Course cour = _courses.get(line[0]);
      if (cour == null) {
        cour = new Course(line[0]);
        _courses.put(line[0], cour);
      }

      try {
        if (isRepresentative) {
          cour.addRepresentative(stud);
          isRepresentative = false;
        }

        Discipline disc;
        try {
          disc = cour.getDiscipline(line[1]);
        } catch (NoSuchDisciplineNameException e) {
          disc = new Discipline(line[1], cour);
          cour.addDiscipline(disc);
        }

        if (stud != null) {
          stud.setCourse(cour);
          disc.addStudent(stud);
          stud.addDiscipline(disc);
        } else if (prof != null) {
          prof.addDiscipline(disc);
          disc.addProfessor(prof);
        }
      } catch (MaximumStudentsExceededException e) {
        throw new BadEntryException("Disciplina atingiu capacidade máxima de inscritos", e);
      } catch (MaximumRepresentativesExceeded e) {
        throw new BadEntryException("Curso atingiu capacidade máxima de delegados", e);
      } catch (MaximumDisciplinesExceededException e) {
        throw new BadEntryException("Aluno atingiu capacidade máxima de inscrições", e);
      }
    }
  }

  /**
   * Registers a Person in School accordingly (type based choice)
   * @param type type of person to create (must be ALUNO, DELEGADO, FUNCIONÁRIO or DOCENTE)
   * @param id person id
   * @param phoneNumber user's phone number
   * @param name user's name
   * @throws BadEntryException
   */
  private void registerPerson(String type, int id, String phoneNumber, String name) throws BadTypeException {
    Person p;

    if (type.equals("ALUNO") || type.equals("DELEGADO")) {
      Student st = new Student(id, phoneNumber, name);
      _students.put(id, st);
      p = st;
    } else if (type.equals("DOCENTE")) {
      Professor prof = new Professor(id, phoneNumber, name);
      _professors.put(id, prof);
      p = prof;
    } else if (type.equals("FUNCIONÁRIO")) {
      Administrative a = new Administrative(id, phoneNumber, name);
      _administratives.put(id, a);
      p = a;
    } else {
      throw new BadTypeException(type);
    }

    _people.put(id, p);
    _peopleByName.add(p);
    _nextId = Math.max(_nextId, id + 1);
  }

  /**
   * Registers a Course and Discipline in School accordingly (type based choice)
   * as well as the student in this Course/Discipline
   * @param id the id of the student that must be associated
   * @param type the type of person that is associated
   * @param fields array of strins with the following struct "# Nome do curso|Nome da disciplina"
   * @throws BadEntryException

  private void registerCourseAndDiscipline(int id, String type, String[] fields) throws BadEntryException {

    try {


    } catch (MaximumStudentsExceededException e) {
      throw new BadEntryException("Disciplina " + e.getDiscipline() + " atingiu capacidade de " + e.getMaximumStudents());
    } catch (MaximumRepresentativesExceeded e) {
      throw new BadEntryException("Curso " + e.getCourse() + " atingiu capacidade máxima de delegados");
    } catch (MaximumDisciplinesExceededException e) {
      throw new BadEntryException("Aluno " + e.getId() + " capacidade máxima de disciplinas inscritas");
    }
  }  */

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
    return _session.accept(new UserDescription());
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
    UserDescription u = new UserDescription();

    for(Integer id : ids){
      people += _people.get(id).accept(u)+ "\n";
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

    UserDescription u = new UserDescription();
    String s = "";
    for (Person p : _peopleByName) {
      if (p.getName().contains(name)) {
        s += p.accept(u) + "\n";
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
    Discipline d = p.getDiscipline(name);

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
    Discipline d = p.getDiscipline(discipline);

    d.addProject(new Project(projName, d));
  }

  /**
   * Closes a Project in the discipline that has <code>projName</code> as its name
   * @param discipline name of the discipline of the project
   * @param projName name of the project to close
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   */
  public void closeProject(String discipline, String projName) throws NoSuchDisciplineNameException, NoSuchProjectNameException, OpeningSurveyProjectException {
    Professor p = _professors.get(_session.getId());
    Discipline d = p.getDiscipline(discipline);

    Project proj = d.getProject(projName);
    proj.close();
  }

  /**
   * Delivers a submission to a project in discipline from a student
   * @param discipline
   * @param projName
   * @param submission
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   * @throws NoSuchProjectOpenException
   */
  public void deliverProject(String discipline, String projName, String sub_text)
    throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSuchProjectOpenException {
    Student s = _students.get(_session.getId());
    Discipline d = s.getDiscipline(discipline);
    Project p = d.getProject(projName);

    Submission submission = new Submission(s, sub_text);
    p.addSubmission(submission);
  }

  /**
   * Returns a string with the information of all the students that submited a project
   * @param discipline
   * @param projName
   * @return String
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   */
  public String showProjectSubmissions(String discipline, String projName)
    throws NoSuchDisciplineNameException, NoSuchProjectNameException {
    
    Professor prof = _professors.get(_session.getId());
    Discipline d = prof.getDiscipline(discipline);
    Project p = d.getProject(projName);

    String header = d.getName() + " - " + p.getName() + "\n";
    return header + p.getSubmissions();
  }

  /**
   * Creates the survey of a Project in the discipline that has <code>projName</code> as its name
   * @param discipline name of the discipline of the project
   * @param projName name of the project to close
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   * @throws DuplicateSurveyProjectException
   */

  public void createSurvey(String discipline, String projName) 
    throws NoSuchDisciplineNameException, NoSuchProjectNameException, DuplicateSurveyProjectException {
  
    Student s = _students.get(_session.getId());
    Discipline d = s.getCourse().getDiscipline(discipline);
    Project p = d.getProject(projName);
    if (! p.isOpen())
      throw new NoSuchProjectNameException(projName);

    d.subscribeToSurvey(p.createSurvey());
  }

  /**
   * Cancels the survey of a Project in the discipline that has <code>projName</code> as its name
   * @param discipline name of the discipline of the project
   * @param projName name of the project to close
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   * @throws NoSurveyProjectException
   * @throws NonEmptySurveyProjectException
   * @throws SurveyFinishedProjectException
   */
  public void cancelSurvey(String discipline, String projName) 
    throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSurveyProjectException, NonEmptySurveyProjectException, SurveyFinishedProjectException {
    
    Student s = _students.get(_session.getId());
    Discipline d = s.getCourse().getDiscipline(discipline);
    Project p = d.getProject(projName);
    Survey survey = p.getSurvey();

    survey.cancel();
  }

  /**
   * Opens the survey of a Project in the discipline that has <code>projName</code> as its name
   * @param discipline name of the discipline of the project
   * @param projName name of the project to close
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   * @throws NoSurveyProjectException
   * @throws OpeningSurveyProjectException
   */
  public void openSurvey(String discipline, String projName) 
    throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSurveyProjectException, OpeningSurveyProjectException {
    
    Student s = _students.get(_session.getId());
    Discipline d = s.getCourse().getDiscipline(discipline);
    Project p = d.getProject(projName);
    Survey survey = p.getSurvey();

    survey.open();
  }

  /**
   * Closes the survey of a Project in the discipline that has <code>projName</code> as its name
   * @param discipline name of the discipline of the project
   * @param projName name of the project to close
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   * @throws NoSurveyProjectException
   * @throws ClosingSurveyProjectException
   */
  public void closeSurvey(String discipline, String projName) 
    throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSurveyProjectException, ClosingSurveyProjectException {
    
    Student s = _students.get(_session.getId());
    Discipline d = s.getCourse().getDiscipline(discipline);
    Project p = d.getProject(projName);
    Survey survey = p.getSurvey();

    survey.close();
  }

  /**
   * Finalizes the survey of a Project in the discipline that has <code>projName</code> as its name
   * @param discipline name of the discipline of the project
   * @param projName name of the project to close
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   * @throws NoSurveyProjectException
   * @throws FinishingSurveyProjectException
   */
  public void finishSurvey(String discipline, String projName) 
    throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSurveyProjectException, FinishingSurveyProjectException {
    
    Student s = _students.get(_session.getId());
    Discipline d = s.getCourse().getDiscipline(discipline);
    Project p = d.getProject(projName);
    Survey survey = p.getSurvey();

    survey.finalize();
  }
  
   /**
   * Answers a Survey from a specific Project
   * @param discipline
   * @param projName
   * @param time
   * @param comment
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   * @throws NoSurveyProjectException
   */
  public void answerSurvey(String discipline, String projName, int time, String comment)
    throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSurveyProjectException, InvalidTimeException {
    
    Student s = _students.get(_session.getId());
    Discipline d = s.getDiscipline(discipline);
    Project p = d.getProject(projName);
    Survey survey = p.getSurvey();

    if (! p.studentSubmited(s)) {
      throw new NoSuchProjectNameException(projName);
    }

    if (time < 0) {
      throw new InvalidTimeException(time);
    }

    SurveyEntry entry = new SurveyEntry(time, comment);
    survey.submitEntry(s, entry);
  }

  /**
   * Returns a string with the information about all the surveys about projects in discipline
   * @param discipline
   * @throws NoSuchDisciplineNameException
   */
  public String showSurveyInfo(String discipline) throws NoSuchDisciplineNameException {
    String text = "";
    Discipline d;
    SurveyPrint printer;
    Student r = _students.get(_session.getId());
    d = r.getCourse().getDiscipline(discipline);
    printer = new SurveyPrintRepresentative();

    if (hasRepresentative()) {
      for (Project proj : d.getProjects()) {
        try {
          Survey survey = proj.getSurvey();
          text += d.getName() + " - " + proj.getName() + survey.printInfo(printer);
        } catch (NoSurveyProjectException e) {
          continue;
        }
      }
    }

    return text;
  }

  /**
   * Returns a string with the information about the survey about a specific project
   * @param discipline
   * @param projName
   * @throws NoSuchDisciplineNameException
   * @throws NoSuchProjectNameException
   * @throws NoSurveyProjectException
   */
  public String showSurveyInfo(String discipline, String projName)
    throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSurveyProjectException {
    
    Discipline disc;
    SurveyPrint printer;
    Project proj;

    if (hasStudent()) {
      Student s = _students.get(_session.getId());
      disc = s.getDiscipline(discipline);
      printer = new SurveyPrintStudent();
      proj = disc.getProject(projName);
      if (!proj.studentSubmited(s))
        throw new NoSuchProjectNameException(projName);
      
    } else if (hasProfessor()) {
      Professor p = _professors.get(_session.getId());
      disc = p.getDiscipline(discipline);
      printer = new SurveyPrintProfessor();
      proj = disc.getProject(projName);
      
    } else {
      return "";
    }

    Survey survey = proj.getSurvey();
    return disc.getName() + " - " + proj.getName() + survey.printInfo(printer) + "\n";
  }


  /**
   * Gets the current session notifications.
   * @return the notifications
   */
  public String getNotifications () {
    return _session.getNotifications();
  }
}

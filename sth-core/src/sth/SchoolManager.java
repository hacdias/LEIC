package sth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import sth.exceptions.BadEntryException;
import sth.exceptions.DuplicateProjectNameException;
import sth.exceptions.ImportFileException;
import sth.exceptions.NoSuchDisciplineNameException;
import sth.exceptions.NoSuchPersonIdException;
import sth.exceptions.NoSuchProjectNameException;

/**
 * The façade class.
 */
public class SchoolManager {
  private School _school;
  private String _dumpFileName = "";
  private boolean _changed = false;

  public SchoolManager() {
    _school = new School();
  }
  
  /**
   * @param datafile
   * @throws ImportFileException
   * @throws InvalidCourseSelectionException
   */
  public void importFile(String datafile) throws ImportFileException {
    try {
      _school.importFile(datafile);
    } catch (IOException | BadEntryException e) {
      throw new ImportFileException(e);
    }
  }

  /**
   * @param id
   * @throws NoSuchPersonIdException
   */
  public void login(int id) throws NoSuchPersonIdException {
    _changed = true;
    _school.login(id);
  }

  /**
   * @return true when the currently logged in person is an administrative
   */
  public boolean hasAdministrative() {
    return _school.hasAdministrative();
  }

  /**
   * @return true when the currently logged in person is a professor
   */
  public boolean hasProfessor() {
    return _school.hasProfessor();
  }

  /**
   * @return true when the currently logged in person is a student
   */
  public boolean hasStudent() {
    return _school.hasStudent();
  }

  /**
   * @return true when the currently logged in person is a representative
   */
  public boolean hasRepresentative() {
    return _school.hasRepresentative();
  }

  public void doChangePhoneNumber(String phoneNumber) {
    _changed = true;
    _school.doChangePhoneNumber(phoneNumber);
  }

  public String getPerson() {
    return _school.getPerson();
  }

  public String getPeople() {
    return _school.getPeople();
  }

  public String searchPerson(String name) {
    return _school.searchPerson(name);
  }

  public String doShowDisciplineStudents(String name) throws NoSuchDisciplineNameException {
    return _school.doShowDisciplineStudents(name);
  }

  public void createProject(String discipline, String proj_name) throws NoSuchDisciplineNameException, DuplicateProjectNameException {
    _changed = true;
    _school.createProject(discipline, proj_name);
  }

  public void closeProject(String discipline, String proj_name) throws NoSuchDisciplineNameException, NoSuchProjectNameException {
    _changed = true;
    _school.closeProject(discipline, proj_name);
  }

  public boolean hasDumpFileName () {
    return !_dumpFileName.equals("");
  }

  public void setDumpFileName (String name) {
    _dumpFileName = name;
  }
  
  public void open () throws NoSuchPersonIdException, IOException, ClassNotFoundException {
    int id = _school.getSessionId();
    BufferedInputStream buff = new BufferedInputStream(new FileInputStream(_dumpFileName));
    ObjectInputStream in = new ObjectInputStream(buff);

    _school = (School)in.readObject();

    _school.login(id);
    in.close();
  }

  public void save () throws IOException {
    if (_changed) {
      BufferedOutputStream buff = new BufferedOutputStream(new FileOutputStream(_dumpFileName));
      ObjectOutputStream out = new ObjectOutputStream(buff);

      out.writeObject(_school);
      out.close();

      _changed = false;
    }
  }
}

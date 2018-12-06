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
import sth.exceptions.InvalidTimeException;
import sth.exceptions.NoSuchDisciplineNameException;
import sth.exceptions.NoSuchPersonIdException;
import sth.exceptions.NoSuchProjectNameException;
import sth.exceptions.NoSuchProjectOpenException;
import sth.exceptions.DuplicateSurveyProjectException;
import sth.exceptions.NoSurveyProjectException;
import sth.exceptions.NonEmptySurveyProjectException;
import sth.exceptions.SurveyFinishedProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.ClosingSurveyProjectException;
import sth.exceptions.FinishingSurveyProjectException;

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
    _school.login(id);
    _changed = true;
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
    _school.doChangePhoneNumber(phoneNumber);
    _changed = true;
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

  public void createProject(String discipline, String projName)
      throws NoSuchDisciplineNameException, DuplicateProjectNameException {
    _school.createProject(discipline, projName);
    _changed = true;
  }

  public void closeProject(String discipline, String projName)
      throws NoSuchDisciplineNameException, NoSuchProjectNameException, OpeningSurveyProjectException {
    _school.closeProject(discipline, projName);
    _changed = true;
  }

  public void deliverProject(String discipline, String projName, String submission)
      throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSuchProjectOpenException {
    _school.deliverProject(discipline, projName, submission);
    _changed = true;
  }

  public String showProjectSubmissions(String discipline, String projName)
      throws NoSuchDisciplineNameException, NoSuchProjectNameException {
    return _school.showProjectSubmissions(discipline, projName);
  }

  public void createSurvey(String discipline, String projName)
      throws NoSuchDisciplineNameException, NoSuchProjectNameException, DuplicateSurveyProjectException {
    _school.createSurvey(discipline, projName);
    _changed = true;
  }

  public void cancelSurvey(String discipline, String projName)
      throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSurveyProjectException,
      NonEmptySurveyProjectException, SurveyFinishedProjectException {

    _school.cancelSurvey(discipline, projName);
    _changed = true;
  }

  public void openSurvey(String discipline, String projName) throws NoSuchDisciplineNameException,
      NoSuchProjectNameException, NoSurveyProjectException, OpeningSurveyProjectException {

    _school.openSurvey(discipline, projName);
    _changed = true;
  }

  public void closeSurvey(String discipline, String projName) throws NoSuchDisciplineNameException,
      NoSuchProjectNameException, NoSurveyProjectException, ClosingSurveyProjectException {

    _school.closeSurvey(discipline, projName);
    _changed = true;
  }

  public void finishSurvey(String discipline, String projName) throws NoSuchDisciplineNameException,
      NoSuchProjectNameException, NoSurveyProjectException, FinishingSurveyProjectException {

    _school.finishSurvey(discipline, projName);
    _changed = true;
  }

  public void answerSurvey(String discipline, String projName, int time, String comment)
      throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSurveyProjectException, InvalidTimeException {
    _school.answerSurvey(discipline, projName, time, comment);
    _changed = true;
  }

  public String showSurveyInfo(String discipline) throws NoSuchDisciplineNameException {
    return _school.showSurveyInfo(discipline);
  }

  public String showSurveyInfo(String discipline, String projName)
      throws NoSuchDisciplineNameException, NoSuchProjectNameException, NoSurveyProjectException {
    return _school.showSurveyInfo(discipline, projName);
  }

  public boolean hasDumpFileName() {
    return !_dumpFileName.equals("");
  }

  public String open(String filename) throws NoSuchPersonIdException, IOException, ClassNotFoundException {
    _dumpFileName = filename;

    int id = _school.getSessionId();
    BufferedInputStream buff = new BufferedInputStream(new FileInputStream(_dumpFileName));
    ObjectInputStream in = new ObjectInputStream(buff);

    try {
      School newSchool = (School) in.readObject();
      newSchool.login(id);
      _school = newSchool;
      return _school.getNotifications();
    } finally {
      in.close();
    }
  }

  public void save(String filename) throws IOException {
    if (!_changed) {
      return;
    }

    if (filename != null) {
      _dumpFileName = filename;
    }

    BufferedOutputStream buff = new BufferedOutputStream(new FileOutputStream(_dumpFileName));
    ObjectOutputStream out = new ObjectOutputStream(buff);

    out.writeObject(_school);
    out.close();

    _changed = false;
  }
}

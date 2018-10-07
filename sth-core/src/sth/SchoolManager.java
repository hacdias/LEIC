package sth;

import sth.exceptions.BadEntryException;
import sth.exceptions.ImportFileException;
import sth.exceptions.NoSuchPersonIdException;

//FIXME import other classes if needed

/**
 * The façade class.
 */
public class SchoolManager {

  //FIXME add object attributes if needed

  //FIXME implement constructors if needed
  
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
    //FIXME implement method
  }

  /**
   * @return true when the currently logged in person is an administrative
   */
  public boolean hasAdministrative() {
    //FIXME implement predicate
  }

  /**
   * @return true when the currently logged in person is a professor
   */
  public boolean hasProfessor() {
    //FIXME implement predicate
  }

  /**
   * @return true when the currently logged in person is a student
   */
  public boolean hasStudent() {
    //FIXME implement predicate
  }

  /**
   * @return true when the currently logged in person is a representative
   */
  public boolean hasRepresentative() {
    //FIXME implement predicate
  }

  //FIXME implement other methods (in general, one for each command in sth-app)
  
}

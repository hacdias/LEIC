package sth;

import java.util.TreeMap;
import java.util.HashMap;
import java.io.Serializable;
import java.io.IOException;
import sth.exceptions.BadEntryException;
import sth.exceptions.InvalidCourseSelectionException;
import sth.exceptions.NoSuchPersonIdException;

/**
 * School implementation.
 */
public class School implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201810051538L;

  //FIXME define object fields (attributes and, possibly, associations)

  private TreeMap<Integer, Person> _people;
  private HashMap<Integer, Administrative> _administratives;
  private HashMap<Integer, Student> _students;
  private HashMap<Integer, Professor> _professors;
  private HashMap<String, Course> _courses;

  transient private int _sessionId;

  School() {
    _people = new TreeMap<Integer, People>();
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
    //FIXME implement text file reader
  }

  public void login(int id) throws NoSuchPersonIdException {
    if (!_students.containsKey(id)
      && !_professors.containsKey(id)
      && !_administratives.containsKey(id)) {
      throw new NoSuchPersonIdException(id);
    }

    _sessionId = id;
  }
  
  //FIXME implement other methods

}

package sth;

import java.util.HashMap;
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

  private String _name;
  private HashMap<Integer, Administrative> _administratives;
  private HashMap<Integer, Student> _students;
  private HashMap<Integer, Professor> _professors;
  private HashMap<String, Course> _courses;

  School(String name) {
    _name = name;
    _administratives = new HashMap<Integer, Administrative>();
    _students = new HashMap<Integer, Students>();
    _professors = new HashMap<Integer, Professor>();
    _courses = new HashMap<Integer, Course>();
  }
  
  /**
   * @param filename
   * @throws BadEntryException
   * @throws IOException
   */
  void importFile(String filename) throws IOException, BadEntryException {
    //FIXME implement text file reader
  }
  
  //FIXME implement other methods

}

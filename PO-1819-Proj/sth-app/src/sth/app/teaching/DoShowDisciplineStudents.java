package sth.app.teaching;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import sth.SchoolManager;
import sth.exceptions.NoSuchDisciplineNameException;
import sth.app.exceptions.NoSuchDisciplineException;

/**
 * 4.3.4. Show course students.
 */
public class DoShowDisciplineStudents extends Command<SchoolManager> {
  Input<String> _name;

  /**
   * @param receiver
   */
  public DoShowDisciplineStudents(SchoolManager receiver) {
    super(Label.SHOW_COURSE_STUDENTS, receiver);
    _name = _form.addStringInput(Message.requestDisciplineName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try {
      _display.popup(_receiver.doShowDisciplineStudents(_name.value()));
    } catch (NoSuchDisciplineNameException e) {
      throw new NoSuchDisciplineException(_name.value());
    }
  }

}

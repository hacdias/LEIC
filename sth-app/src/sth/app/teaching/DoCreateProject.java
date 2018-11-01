package sth.app.teaching;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import sth.SchoolManager;
import sth.app.exceptions.DuplicateProjectException;
import sth.exceptions.DuplicateProjectNameException;
import sth.app.exceptions.NoSuchDisciplineException;
import sth.exceptions.NoSuchDisciplineNameException;

//FIXME import other classes if needed

/**
 * 4.3.1. Create project.
 */
public class DoCreateProject extends Command<SchoolManager> {
  Input<String> _discipline;
  Input<String> _name;

  /**
   * @param receiver
   */
  public DoCreateProject(SchoolManager receiver) {
    super(Label.CREATE_PROJECT, receiver);
    _discipline = _form.addStringInput(Message.requestDisciplineName());
    _name = _form.addStringInput(Message.requestProjectName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try {
      _receiver.createProject(_discipline.value(), _name.value());
    } catch (NoSuchDisciplineNameException e) {
      throw new NoSuchDisciplineException(_discipline.value());
    } catch (DuplicateProjectNameException e) {
      throw new DuplicateProjectException(_discipline.value(), _name.value());
    }
  }

}

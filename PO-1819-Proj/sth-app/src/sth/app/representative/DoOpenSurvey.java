package sth.app.representative;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import sth.SchoolManager;

import sth.app.exceptions.NoSuchDisciplineException;
import sth.app.exceptions.NoSuchProjectException;
import sth.app.exceptions.NoSurveyException;
import sth.app.exceptions.OpeningSurveyException;

import sth.exceptions.NoSuchDisciplineNameException;
import sth.exceptions.NoSuchProjectNameException;
import sth.exceptions.NoSurveyProjectException;
import sth.exceptions.OpeningSurveyProjectException;

/**
 * 4.5.3. Open survey.
 */
public class DoOpenSurvey extends Command<SchoolManager> {
  Input<String> _discipline;
  Input<String> _project;

  /**
   * @param receiver
   */
  public DoOpenSurvey(SchoolManager receiver) {
    super(Label.OPEN_SURVEY, receiver);
    _discipline = _form.addStringInput(Message.requestDisciplineName());
    _project = _form.addStringInput(Message.requestProjectName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try {
      _receiver.openSurvey(_discipline.value(), _project.value());
    } catch (NoSuchDisciplineNameException e) {
      throw new NoSuchDisciplineException(_discipline.value());
    } catch (NoSuchProjectNameException e) {
      throw new NoSuchProjectException(_discipline.value(), _project.value());
    } catch (NoSurveyProjectException e) {
      throw new NoSurveyException(_discipline.value(), _project.value());
    } catch (OpeningSurveyProjectException e) {
      throw new OpeningSurveyException(_discipline.value(), _project.value());
    }
  }

}

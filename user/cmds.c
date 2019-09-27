#include "cmds.h"
#include "strings.h"

int checkCommand (char *cmd, char longForm[256], char shortForm[256]) {
  return strcmp(cmd, longForm) == 0 || strcmp(cmd, shortForm) == 0;
}

Command getCommand (char *cmd) {
  if (strcmp(cmd, "exit") == 0) {
    return Exit;
  } else if (checkCommand(cmd, "register", "reg")) {
    return Register;
  } else if (checkCommand(cmd, "topic_list", "tl")) {
    return TopicList;
  } else if (checkCommand(cmd, "topic_select", "ts")) {
    return TopicSelect;
  } else if (checkCommand(cmd, "topic_propose", "tp")) {
    return TopicPropose;
  } else if (checkCommand(cmd, "question_list", "ql")) {
    return QuestionList;
  } else if (checkCommand(cmd, "question_get", "qg")) {
    return QuestionGet;
  } else if (checkCommand(cmd, "question_submit", "qs")) {
    return QuestionSubmit;
  } else if (checkCommand(cmd, "answer_submit", "as")) {
    return AnswerSubmit;
  } else {
    return Unknown;
  }
}

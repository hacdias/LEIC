#include "cmds.h"

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
  } else if (checkCommand(cmd, "topic_propose", "tp")) {
    return TopicPropose;
  } else if (checkCommand(cmd, "question_list", "ql")) {
    return QuestionList;
  } else if (checkCommand(cmd, "question_submit", "qs")) {
    return QuestionSubmit;
  } else if (checkCommand(cmd, "answer_submit", "as")) {
    return AnswerSubmit;
  } else if (checkCommand(cmd, "help", "?")) {
    return Help;
  } else if (strcmp(cmd, "ts") == 0) {
    return TopicSelectShort;
  } else if (strcmp(cmd, "topic_select") == 0) {
    return TopicSelectLong;
  } else if (strcmp(cmd, "qg") == 0) {
    return QuestionGetShort;
  } else if (strcmp(cmd, "question_get") == 0) {
    return QuestionGetLong;
  } else {
    return Unknown;
  }
}

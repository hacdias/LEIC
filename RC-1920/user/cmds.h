#ifndef __CMDS_H__
#define __CMDS_H__

#include <string.h>

typedef enum command{
  Help,
  Exit,
  Register,
  TopicList,
  TopicSelectShort,
  TopicSelectLong,
  TopicPropose,
  QuestionList,
  QuestionGetShort,
  QuestionGetLong,
  QuestionSubmit,
  AnswerSubmit,
  Unknown
} Command;

Command getCommand (char *cmd);

#endif

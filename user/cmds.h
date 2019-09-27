#ifndef __CMDS_H__
#define __CMDS_H__

typedef enum command{
  Help,
  Exit,
  Register,
  TopicList,
  TopicSelect,
  TopicPropose,
  QuestionList,
  QuestionGet,
  QuestionSubmit,
  AnswerSubmit,
  Unknown
} Command;

Command getCommand (char *cmd);

#endif

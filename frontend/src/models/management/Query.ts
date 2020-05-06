import { ISOtoString } from '@/services/ConvertDateService';
import { QuestionAnswer } from './QuestionAnswer';

export default class Query {
  id: number | null = null;
  title: string = '';
  content: string = '';
  creationDate!: string;
  questionAnswer!: QuestionAnswer;
  numberAnswers: number | null = null;
  byUsername: string = '';
  byName: string = '';
  shared: boolean | null = false;

  constructor(jsonObj?: Query) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.content = jsonObj.content;
      this.numberAnswers = jsonObj.numberAnswers;
      this.questionAnswer = new QuestionAnswer(jsonObj.questionAnswer);
      this.byUsername = jsonObj.byUsername;
      this.byName = jsonObj.byName;
      this.shared = jsonObj.shared;

      if (jsonObj.creationDate)
        this.creationDate = ISOtoString(jsonObj.creationDate);
    }
  }
}

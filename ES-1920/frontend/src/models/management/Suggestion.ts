import Question from '@/models/management/Question';

export default class Suggestion {
  id: number | null = null;
  status: String = 'PENDING';
  creationDate: string | null = null;
  question: Question = new Question();

  constructor(jsonObj?: Suggestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.status = jsonObj.status;
      this.question = new Question(jsonObj.question);
      this.creationDate = jsonObj.creationDate;
    }
  }
}

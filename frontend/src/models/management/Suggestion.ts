import Question from '@/models/management/Question';

export default class Suggestion {
  id: number | null = null;
  approved: Boolean | null = false;
  creationDate: string | null = null;
  question: Question = new Question();

  constructor(jsonObj?: Suggestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.approved = jsonObj.approved;
      this.question = new Question(jsonObj.question);
      this.creationDate = jsonObj.creationDate;
    }
  }
}
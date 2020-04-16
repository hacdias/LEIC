export default class Query {
  id: number | null = null;
  title: string = '';
  content: string = '';
  creationDate!: string | null;
  numberAnswers: number | null = null;
  questionId: number | null = null;
  byUsername: string = '';
  byName: string = '';

  constructor(jsonObj?: Query) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.content = jsonObj.content;
      this.creationDate = jsonObj.creationDate;
      this.numberAnswers = jsonObj.numberAnswers;
      this.questionId = jsonObj.questionId;
      this.byUsername = jsonObj.byUsername;
      this.byName = jsonObj.byName;
    }
  }
}

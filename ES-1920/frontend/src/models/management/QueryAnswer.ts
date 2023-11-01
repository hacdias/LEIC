import { ISOtoString } from '@/services/ConvertDateService';

export default class QueryAnswer {
  id: number | null = null;
  content: string = '';
  creationDate!: string;
  byName: string = '';
  byUsername: string = '';
  answers: QueryAnswer[] = [];
  showNow: boolean | null = false;

  constructor(jsonObj?: QueryAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.content = jsonObj.content;
      this.byName = jsonObj.byName;
      this.byUsername = jsonObj.byUsername;

      if (jsonObj.creationDate)
      this.creationDate = ISOtoString(jsonObj.creationDate);

      if (jsonObj.answers) {
        this.answers = jsonObj.answers.map(
          (answer: QueryAnswer) => new QueryAnswer(answer)
        );
      }
    }
  }
}

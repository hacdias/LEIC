import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Tournament {
  id: number | null = null; //TO DO:check if undefined or null
  title: string | null = null;
  creationDate!: string; //TO DO: check exclamation mark
  availableDate!: string;
  conclusionDate!: string;
  numberQuestions: number | null = null;
  topics: Topic[] = [];
  enrolled: boolean | null = false;

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id; //TO DO: check if id needed
      this.title = jsonObj.title;
      this.numberQuestions = jsonObj.numberQuestions;
      this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));

      if (jsonObj.creationDate)
        this.creationDate = ISOtoString(jsonObj.creationDate);
      if (jsonObj.availableDate)
        this.availableDate = ISOtoString(jsonObj.availableDate);
      if (jsonObj.conclusionDate)
        this.conclusionDate = ISOtoString(jsonObj.conclusionDate);
    }
  }
}

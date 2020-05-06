import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Tournament {
  id!: number;
  title: string | null = null;
  status: string | null = null;
  creationDate!: string;
  availableDate!: string;
  conclusionDate!: string;
  numberQuestions: number | null = null;
  topics: Topic[] = [];
  enrolled: boolean | null = false;
  creator: boolean | null = false;
  solved: boolean | null = false;

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.numberQuestions = jsonObj.numberQuestions;
      this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
      this.status = jsonObj.status;

      if (jsonObj.creationDate)
        this.creationDate = ISOtoString(jsonObj.creationDate);
      if (jsonObj.availableDate)
        this.availableDate = ISOtoString(jsonObj.availableDate);
      if (jsonObj.conclusionDate)
        this.conclusionDate = ISOtoString(jsonObj.conclusionDate);
    }
  }
}

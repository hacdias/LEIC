import Topic from '@/models/management/Topic';

export default class Tournament {
    id: number | null = null;                                   //TO DO:check if undefined or null
    title: string | null = null;
    creationDate: string | null = null;                         //TO DO: check exclamation mark
    availableDate: string | null = null;
    conclusionDate: string | null = null;
    numberQuestions: number | null = null;
    topics: Topic[] = [];
    enrolled: boolean | null = false;

    constructor(jsonObj?: Tournament) {
        if (jsonObj) {
            this.id = jsonObj.id;                               //TO DO: check if id needed
            this.title = jsonObj.title;
            this.creationDate = jsonObj.creationDate;
            this.availableDate = jsonObj.availableDate;
            this.conclusionDate = jsonObj.conclusionDate;
            this.numberQuestions = jsonObj.numberQuestions;
            this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
        }
    }
}

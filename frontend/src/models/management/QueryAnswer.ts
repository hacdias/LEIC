export default class QueryAnswer {
    id: number | null = null;
    content: string = '';
    creationDate!: string | null;
  
    constructor(jsonObj?: QueryAnswer) {
      if (jsonObj) {
        this.id = jsonObj.id;
        this.content = jsonObj.content;
        this.creationDate = jsonObj.creationDate;
      } 
    }
  }
export default class Query {
  id: number | null = null;
  title: string = '';
  content: string = '';
  creationDate!: string | null;

  constructor(jsonObj?: Query) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.content = jsonObj.content;
      this.creationDate = jsonObj.creationDate;
    }
  }
}

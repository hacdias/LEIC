import Suggestion from '@/models/management/Suggestion';

export default class SuggestionReview {
  id: number | null = null;
  approved: Boolean | null = false;
  justification: string | null = null;
  creationDate: string | null = null;
  suggestion: Suggestion = new Suggestion();

  constructor(jsonObj?: SuggestionReview) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.approved = jsonObj.approved;
      this.justification = jsonObj.justification;
      this.suggestion = new Suggestion(jsonObj.suggestion);
      this.creationDate = jsonObj.creationDate;
    }
  }
}

<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Add Suggestion Review</span>
      </v-card-title>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn>Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import SuggestionReview from '@/models/management/SuggestionReview';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class AddSuggestionReviewDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: SuggestionReview, required: true })
  readonly suggestionReview!: SuggestionReview;

  addSuggestionReview!: SuggestionReview;

  created() {
    this.addSuggestionReview = new SuggestionReview(this.suggestionReview);
  }

  async saveSuggestionReview() {}
}
</script>

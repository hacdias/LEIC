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

      <v-card-text class="text-left" v-if="addSuggestionReview">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md12>
              <v-switch
                v-model="addSuggestionReview.approved"
                label="Approved"
                data-cy="Approved"
              />
              <v-textarea
                outline
                rows="10"
                v-model="addSuggestionReview.justification"
                label="Justification"
                data-cy="Justification"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          data-cy="saveSuggestionReviewButton"
          @click="saveSuggestionReview"
          >Save</v-btn
        >
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

  async saveSuggestionReview() {
    if (this.addSuggestionReview) {
      try {
        const result = await RemoteServices.createSuggestionReview(
          this.addSuggestionReview
        );
        this.$emit('save-suggestion-review', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

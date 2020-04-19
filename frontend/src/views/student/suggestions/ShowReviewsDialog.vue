<template>
  <v-dialog
    v-model="dialog"
    @keydown.esc="closeSuggestionDialog"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">"{{ suggestion.question.title }}" Reviews</span>
      </v-card-title>

      <v-card-text class="text-left">
        <ul>
          <li v-for="review in reviews" :key="review.id">
            <span v-if="review.approved">✅</span>
            <span v-else>❌</span>
            {{ review.creationDate }}
            <p>{{ review.justification }}</p>
          </li>
        </ul>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeSuggestionDialog"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Suggestion from '@/models/management/Suggestion';
import SuggestionReview from '@/models/management/SuggestionReview';

@Component({})
export default class ShowReviewsDialog extends Vue {
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;
  @Prop({ type: Array, required: true }) readonly reviews!: SuggestionReview[];
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  closeSuggestionDialog() {
    this.$emit('close-show-reviews-dialog');
  }
}
</script>

<template>
  <v-dialog
    v-model="dialog"
    @keydown.esc="closeSuggestionDialog"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ suggestion.question.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-suggestion :suggestion="suggestion" />
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
import ShowSuggestion from '@/views/student/suggestions/ShowSuggestion.vue';

@Component({
  components: {
    'show-suggestion': ShowSuggestion
  }
})
export default class ShowSuggestionDialog extends Vue {
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  closeSuggestionDialog() {
    this.$emit('close-show-suggestion-dialog');
  }
}
</script>

<template>
  <v-container grid-list-md fluid>
    <v-layout column wrap>
      <ol>
        <li
          v-for="suggestion in suggestions"
          :key="suggestion.question.sequence"
          class="text-left"
        >
          <show-suggestion :suggestion="suggestions" />
        </li>
      </ol>
    </v-layout>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Suggestion from '../../../models/management/Suggestion';
import ShowSuggestion from '@/views/student/suggestions/ShowSuggestion.vue';

@Component({
  components: { 'show-suggestion': ShowSuggestion }
})
export default class ShowSuggestionsList extends Vue {
  @Prop({ type: Array, required: true }) readonly suggestions!: Suggestion[];
}
</script>

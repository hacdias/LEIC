<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="suggestionReviews"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>

      <template v-slot:item.title="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.suggestion.question.title, null)"
      /></template>

      <template v-slot:item.approved="{ item }">
        <v-chip :color="item.approved ? 'green' : 'red'" small>
          <span>{{ item.approved ? 'Yes' : 'No' }}</span>
        </v-chip>
      </template>

      <template v-slot:item.justification="{ item }">
        <p v-html="convertMarkDownNoFigure(item.justification, null)"
      /></template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showSuggestionDialog(item.suggestion)"
              >visibility</v-icon
            >
          </template>
          <span>Show Suggestion</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <show-suggestion-dialog
      v-if="currentSuggestion"
      :dialog="suggestionDialog"
      :suggestion="currentSuggestion"
      v-on:close-show-suggestion-dialog="onCloseShowSuggestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Suggestion from '@/models/management/Suggestion';
import SuggestionReview from '@/models/management/SuggestionReview';
import ShowSuggestionDialog from '@/views/student/suggestions/ShowSuggestionDialog.vue';
import Store from '@/store';

@Component({
  components: {
    'show-suggestion-dialog': ShowSuggestionDialog
  }
})
export default class SuggestionReviewsView extends Vue {
  suggestionReviews: SuggestionReview[] = [];
  currentSuggestion: Suggestion | null = null;
  suggestionDialog: boolean = false;
  search: string = '';

  headers: object = [
    { text: 'Question Title', value: 'title', align: 'center' },
    { text: 'Approved', value: 'approved', align: 'center' },
    { text: 'Justification', value: 'justification', align: 'left' },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.suggestionReviews = await RemoteServices.getTeacherSuggestionReviews();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(value: string, search: string, suggestion: Suggestion) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(suggestion)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  showSuggestionDialog(suggestion: Suggestion) {
    this.currentSuggestion = suggestion;
    this.suggestionDialog = true;
  }

  onCloseShowSuggestionDialog() {
    this.suggestionDialog = false;
  }
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>

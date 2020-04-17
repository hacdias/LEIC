<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="suggestions"
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
          v-html="convertMarkDownNoFigure(item.question.title, null)"
          @click="showSuggestionDialog(item)"
      /></template>

      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.question.content, null)"
          @click="showSuggestionDialog(item)"
      /></template>

      <template v-slot:item.approved="{ item }">
        <v-chip :color="item.approved ? 'green' : 'red'" small>
          <span>{{ item.approved ? 'Yes' : 'No' }}</span>
        </v-chip>
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
import ShowSuggestionDialog from '@/views/student/suggestions/ShowSuggestionDialog.vue';

@Component({
  components: {
    'show-suggestion-dialog': ShowSuggestionDialog
  }
})
export default class TeacherSuggestionsView extends Vue {
  suggestions: Suggestion[] = [];
  currentSuggestion: Suggestion | null = null;
  suggestionDialog: boolean = false;
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Question', value: 'content', align: 'left' },
    { text: 'Approved', value: 'approved', align: 'center' },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center'
    },
    {
      text: 'Image',
      value: 'image',
      align: 'center',
      sortable: false
    },
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
      this.suggestions = await RemoteServices.getTeacherSuggestions();
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

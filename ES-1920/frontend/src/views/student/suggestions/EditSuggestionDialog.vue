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
        <span class="headline">
          {{
            editSuggestion && editSuggestion.id === null
              ? 'New Suggestion'
              : 'Edit Suggesstion'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editSuggestion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="editSuggestion.question.title"
                label="Title"
                data-cy="Title"
              />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="editSuggestion.question.content"
                label="Content"
                data-cy="Content"
              ></v-textarea>
            </v-flex>
            <v-flex
              xs24
              sm12
              md12
              v-for="index in editSuggestion.question.options.length"
              :key="index"
            >
              <v-switch
                v-model="editSuggestion.question.options[index - 1].correct"
                class="ma-4"
                label="Correct"
                :data-cy="`OptionCorrect[${index - 1}]`"
              />
              <v-textarea
                outline
                rows="10"
                v-model="editSuggestion.question.options[index - 1].content"
                label="Content"
                :data-cy="`OptionContent[${index - 1}]`"
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
          data-cy="saveSuggestionButton"
          @click="saveSuggestion"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Suggestion from '@/models/management/Suggestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditSuggestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;

  editSuggestion!: Suggestion;

  created() {
    this.editSuggestion = new Suggestion(this.suggestion);
  }

  async saveSuggestion() {
    if (
      this.editSuggestion &&
      (!this.editSuggestion.question.title ||
        !this.editSuggestion.question.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Suggestion must have title and content'
      );
      return;
    }

    if (this.editSuggestion && this.editSuggestion.id != null) {
      try {
        const result = await RemoteServices.updateSuggestion(
          this.editSuggestion
        );
        this.$emit('save-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else if (this.editSuggestion) {
      try {
        const result = await RemoteServices.createSuggestion(
          this.editSuggestion
        );
        this.$emit('save-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

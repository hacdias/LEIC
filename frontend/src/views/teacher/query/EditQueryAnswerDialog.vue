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
          Edit Query Answer
        </span>
      </v-card-title>
      <v-card-text class="text-left" v-if="editQueryAnswer">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md12>
              <v-textarea
                data-cy="Content"
                outline
                rows="10"
                v-model="editQueryAnswer.content"
                label="Content"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          data-cy="cancelButton"
          color="blue darken-1"
          @click="$emit('dialog', false)"
          >Cancel
        </v-btn>
        <v-btn
          color="blue darken-1"
          @click="saveQueryAnswer"
          data-cy="saveQueryAnswerButton"
          >Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import QueryAnswer from '@/models/management/QueryAnswer';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditQueryAnswerDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QueryAnswer, required: true })
  readonly queryAnswer!: QueryAnswer;
  editQueryAnswer!: QueryAnswer;

  created() {
    this.editQueryAnswer = new QueryAnswer(this.queryAnswer);
  }

  async saveQueryAnswer() {
    if (this.editQueryAnswer && !this.editQueryAnswer.content) {
      await this.$store.dispatch(
        'error',
        'Error: Query Answer must have content'
      );
      return;
    }

    if (this.editQueryAnswer) {
      try {
        const result = await RemoteServices.updateQueryAnswer(
          this.editQueryAnswer
        );
        this.$emit('save-query-answer', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

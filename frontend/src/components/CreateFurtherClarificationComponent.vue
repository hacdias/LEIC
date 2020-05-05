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
          New Further Clarification
        </span>
      </v-card-title>
      <v-card-text class="text-left" v-if="createFurtherClarification">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md12>
              <v-textarea
                data-cy="Content"
                outline
                rows="10"
                v-model="createFurtherClarification.content"
                label="Content"
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
          @click="saveFurtherClarification"
          data-cy="saveFurtherClarificationButton"
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
export default class createFurtherClarificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QueryAnswer, required: true })
  readonly furtherClarification!: QueryAnswer;
  createFurtherClarification!: QueryAnswer;

  created() {
    this.createFurtherClarification = new QueryAnswer(
      this.furtherClarification
    );
  }

  async saveFurtherClarification() {
    if (
      this.createFurtherClarification &&
      !this.createFurtherClarification.content
    ) {
      await this.$store.dispatch(
        'error',
        'Further Clarification must have content'
      );
      return;
    }

    if (this.createFurtherClarification) {
      try {
        const result = await RemoteServices.createFurtherClarification(
          this.createFurtherClarification
        );
        this.$emit('save-further-clarification', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

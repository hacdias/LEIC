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
          New Query
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="createQuery">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field v-model="createQuery.title" label="Title" />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="createQuery.content"
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
        <v-btn color="blue darken-1" @click="saveQuery">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Query from '@/models/management/Query';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class CreateQueryDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Query, required: true }) readonly query!: Query;
  createQuery!: Query;

  created() {
    this.createQuery = new Query(this.query);
  }

  async saveQuery() {
    if (
      this.createQuery &&
      (!this.createQuery.title || !this.createQuery.content)
    ) {
      await this.$store.dispatch('error', 'Query must have title and content');
      return;
    }

    if (this.createQuery) {
      try {
        const result = await RemoteServices.createQuery(this.createQuery);
        this.$emit('save-query', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

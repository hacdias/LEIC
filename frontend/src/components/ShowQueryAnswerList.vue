<template>
  <v-card v-if="noAnswers">
    <v-card-text class="text-center">
      <v-container grid-list-md fluid>
        <v-layout column wrap>
          NO ANSWERS YET
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
  <v-card v-else>
    <v-card-text class="text-left">
      <v-container
        grid-list-md
        fluid
        v-for="answer in answers"
        :key="answer.id"
      >
        <v-layout column wrap>
          <p>{{ answer.creationDate }} <b>by</b> {{ answer.byName }}</p>
          <div class="text--primary">
            {{ answer.content }}
          </div>
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import QueryAnswer from '@/models/management/QueryAnswer';

@Component
export default class ShowQueryAnswerList extends Vue {
  @Prop({ type: Array, required: true }) readonly answers!: QueryAnswer[];

  get noAnswers() {
    return this.answers.length == 0;
  }
}
</script>

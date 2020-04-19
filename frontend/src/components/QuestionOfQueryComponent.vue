<template>
  <v-card class="text-left" v-if="question">
    <v-card-text class="text-left">
      <v-container grid-list-md fluid>
        <v-layout column wrap>
          <p class="display-1 text--primary">
            Question
          </p>
          <span v-html="convertMarkDown(question.content, question.image)" />
          <br />
          <ul>
            <li v-for="option in question.options" :key="option.number">
              <span
                v-if="option.correct"
                v-html="convertMarkDown('**[★]** ', null)"
              />
              <span
                v-html="convertMarkDown(option.content, null)"
                v-bind:class="[option.correct ? 'font-weight-bold' : '']"
              />
            </li>
          </ul>
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';

@Component
export default class QuestionOfQueryComponent extends Vue {
  @Prop({ type: Question, required: false }) readonly question!: Question;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

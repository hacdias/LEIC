<template>
  <v-card class="text-left" v-if="questionAnswer">
    <v-card-text class="text-left">
      <v-container grid-list-md fluid>
        <v-layout column wrap>
          <p class="display-1 text--primary">
            Question
          </p>
          <span
            v-html="
              convertMarkDown(
                questionAnswer.question.content,
                questionAnswer.question.image
              )
            "
          />
          <br />
          <ul>
            <li
              v-for="(option, index) in questionAnswer.question.options"
              :key="option.number"
            >
              <span
                v-if="option.correct && questionAnswer.option.correct"
                v-html="convertMarkDown('**[★][ × ]** ' + option.content)"
                v-bind:class="[option.correct ? 'font-weight-bold' : '']"
              />
              <span
                v-else-if="index == questionAnswer.option.sequence"
                v-html="convertMarkDown('**[ × ]** ' + option.content)"
                v-bind:class="[option.correct ? 'font-weight-bold' : '']"
              />
              <span
                v-else-if="option.correct"
                v-html="convertMarkDown('**[★]** ' + option.content)"
                v-bind:class="[option.correct ? 'font-weight-bold' : '']"
              />
              <span v-else v-html="convertMarkDown(option.content)" />
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
import Image from '@/models/management/Image';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';

@Component
export default class QuestionOfQueryComponent extends Vue {
  @Prop({ type: QuestionAnswer, required: false })
  readonly questionAnswer!: QuestionAnswer;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<template>
  <div>
    <span
      v-html="
        convertMarkDown(suggestion.question.content, suggestion.question.image)
      "
    />
    <ul>
      <li v-for="option in suggestion.question.options" :key="option.number">
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
    <br />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Suggestion from '@/models/management/Suggestion';
import Image from '@/models/management/Image';

@Component
export default class ShowSuggestion extends Vue {
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

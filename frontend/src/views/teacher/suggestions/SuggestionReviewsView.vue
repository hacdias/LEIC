<template>
  <div class="container">
    <h2>Suggestion Reviews</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Approved</div>
        <div class="col">Justification</div>
        <div class="col last-col"></div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
@Component
export default class SuggestionReviewsView extends Vue {
  async created() {
    await this.$store.dispatch('loading');
    try {
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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

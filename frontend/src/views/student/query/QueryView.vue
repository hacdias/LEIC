<template>
  <div class="container">
    <h2>Query History</h2>
    <query-component :query="query" />
    <br />
    <show-query-answer-list :answers="answers" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import Query from '@/models/management/Query';
import QueryAnswer from '@/models/management/QueryAnswer';
import QuestionOfQueryComponent from '@/components/QuestionOfQueryComponent.vue';
import ShowQueryAnswerList from '@/components/ShowQueryAnswerList.vue';
import QueryComponent from '../../../components/QueryComponent.vue';

@Component({
  components: {
    'question-of-query-component': QuestionOfQueryComponent,
    'query-component': QueryComponent,
    'show-query-answer-list': ShowQueryAnswerList
  }
})
export default class QueryView extends Vue {
  createQueryAnswerDialog: boolean = false;

  question: Question | null = null;
  query: Query | null = this.$store.getters.getCurrentQuery;
  answers: QueryAnswer[] = [];
  currentQueryAnswer: QueryAnswer | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.answers = await RemoteServices.getAnswersToQuery();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async newQueryAnswer() {
    this.currentQueryAnswer = new QueryAnswer();
    this.createQueryAnswerDialog = true;
  }

  async onSaveQueryAnswer(queryAnswer: QueryAnswer) {
    this.answers = this.answers.filter(a => a.id !== queryAnswer.id);
    this.answers.push(queryAnswer);
    this.currentQueryAnswer = null;
  }
}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;
  }
}
</style>

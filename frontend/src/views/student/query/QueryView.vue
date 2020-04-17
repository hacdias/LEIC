<template>
  <div class="container">
    <h2>Query History</h2>
    <query-component
      :query="query"
      @edit-query="editQuery"
      @delete-query="deleteQuery"
    />
    <br />
    <show-query-answer-list :answers="answers" />
    <edit-query-dialog
      v-if="query"
      v-model="editQueryDialog"
      :query="query"
      v-on:save-query="onSaveQuery"
    />
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
import QueryComponent from '@/components/QueryComponent.vue';
import EditQueryDialog from '@/views/student/query/EditQueryDialog.vue';

@Component({
  components: {
    'question-of-query-component': QuestionOfQueryComponent,
    'query-component': QueryComponent,
    'edit-query-dialog': EditQueryDialog,
    'show-query-answer-list': ShowQueryAnswerList
  }
})
export default class QueryView extends Vue {
  createQueryAnswerDialog: boolean = false;
  editQueryDialog: boolean = false;

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

  editQuery() {
    this.editQueryDialog = true;
  }

  async onSaveQuery(query: Query) {
    this.query = query;
    this.editQueryDialog = false;
  }

  async deleteQuery() {
    if (
      this.query &&
      this.query.id &&
      confirm('Are you sure you want to delete this query?')
    ) {
      try {
        await RemoteServices.deleteQuery(this.query.id);
        this.$router.go(-1);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
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

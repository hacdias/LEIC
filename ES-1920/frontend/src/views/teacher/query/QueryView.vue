<template>
  <div class="container">
    <h2>Query History</h2>
    <question-of-query-component :questionAnswer="query.questionAnswer" />
    <br />
    <query-component :query="query" @share-query="shareQuery" />
    <br />
    <show-query-answer-list
      :answers="answers"
      @edit-query-answer="editQueryAnswer"
      @delete-query-answer="deleteQueryAnswer"
    />
    <div class="query-content">
      <v-btn
        data-cy="createQueryAnswerButton"
        color="primary"
        dark
        @click="newQueryAnswer()"
      >
        Answer
      </v-btn>
    </div>
    <create-query-answer-dialog
      v-if="currentQueryAnswer"
      v-model="createQueryAnswerDialog"
      :queryId="query.id"
      :queryAnswer="currentQueryAnswer"
      v-on:save-query-answer="onSaveQueryAnswer"
    />
    <edit-query-answer-dialog
      v-if="currentQueryAnswer"
      v-model="editQueryAnswerDialog"
      :queryAnswer="currentQueryAnswer"
      v-on:save-query-answer="onSaveQueryAnswer"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import Query from '@/models/management/Query';
import QueryAnswer from '@/models/management/QueryAnswer';
import CreateQueryAnswerDialog from '@/views/teacher/query/CreateQueryAnswerDialog.vue';
import EditQueryAnswerDialog from '@/views/teacher/query/EditQueryAnswerDialog.vue';
import QuestionOfQueryComponent from '@/components/QuestionOfQueryComponent.vue';
import ShowQueryAnswerList from '@/components/ShowQueryAnswerList.vue';
import QueryComponent from '../../../components/QueryComponent.vue';

@Component({
  components: {
    'question-of-query-component': QuestionOfQueryComponent,
    'query-component': QueryComponent,
    'show-query-answer-list': ShowQueryAnswerList,
    'create-query-answer-dialog': CreateQueryAnswerDialog,
    'edit-query-answer-dialog': EditQueryAnswerDialog
  }
})
export default class QueryView extends Vue {
  createQueryAnswerDialog: boolean = false;
  editQueryAnswerDialog: boolean = false;

  query: Query | null = this.$store.getters.getCurrentQuery;
  answers: QueryAnswer[] = [];
  currentQueryAnswer: QueryAnswer | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      if (this.query && this.query.id)
        this.answers = await RemoteServices.getAnswersToQuery(this.query.id);
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
    this.createQueryAnswerDialog = false;
    this.editQueryAnswerDialog = false;
    this.currentQueryAnswer = null;
  }

  editQueryAnswer(answer: QueryAnswer) {
    this.currentQueryAnswer = answer;
    this.editQueryAnswerDialog = true;
  }

  async deleteQueryAnswer(answer: QueryAnswer) {
    if (
      answer &&
      answer.id &&
      confirm('Are you sure you want to delete this answer?')
    ) {
      try {
        await RemoteServices.deleteQueryAnswer(answer.id);
        this.answers = this.answers.filter(a => a.id !== answer.id);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async shareQuery() {
    if (
      this.query &&
      this.query.id &&
      confirm('Are you sure you want to share this query?')
    ) {
      try {
        this.query = (await RemoteServices.shareQuery(this.query.id)).data;
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

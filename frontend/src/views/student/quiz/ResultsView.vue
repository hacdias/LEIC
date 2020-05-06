<template>
  <div class="quiz-container" v-if="statementManager.correctAnswers.length > 0">
    <div class="question-navigation">
      <div class="navigation-buttons">
        <span
          v-for="index in +statementManager.statementQuiz.questions.length"
          v-bind:class="[
            'question-button',
            index === questionOrder + 1 ? 'current-question-button' : '',
            index === questionOrder + 1 &&
            statementManager.correctAnswers[index - 1].correctOptionId !==
              statementManager.statementQuiz.answers[index - 1].optionId
              ? 'incorrect-current'
              : '',
            statementManager.correctAnswers[index - 1].correctOptionId !==
            statementManager.statementQuiz.answers[index - 1].optionId
              ? 'incorrect'
              : ''
          ]"
          :key="index"
          @click="changeOrder(index - 1)"
        >
          {{ index }}
        </span>
      </div>
      <span
        class="left-button"
        @click="decreaseOrder"
        v-if="questionOrder !== 0"
        ><i class="fas fa-chevron-left"
      /></span>
      <span
        class="right-button"
        @click="increaseOrder"
        v-if="
          questionOrder !== statementManager.statementQuiz.questions.length - 1
        "
        ><i class="fas fa-chevron-right"
      /></span>
    </div>
    <result-component
      v-model="questionOrder"
      :answer="statementManager.statementQuiz.answers[questionOrder]"
      :correctAnswer="statementManager.correctAnswers[questionOrder]"
      :question="statementManager.statementQuiz.questions[questionOrder]"
      :questionNumber="statementManager.statementQuiz.questions.length"
      @increase-order="increaseOrder"
      @decrease-order="decreaseOrder"
    />
    <div class="query-content">
      <v-btn
        :style="'margin: 10px'"
        data-cy="showQueriesButton"
        color="primary"
        dark
        @click="
          seeSharedQueries(
            statementManager.statementQuiz.questions[questionOrder]
          )
        "
      >
        Check Queries
      </v-btn>
      <v-btn
        :style="'margin: 10px'"
        data-cy="createQueryButton"
        color="primary"
        dark
        @click="newQuery()"
      >
        Create Query
      </v-btn>
    </div>
    <create-query-dialog
      v-if="currentQuery"
      v-model="createQueryDialog"
      :questionId="
        statementManager.statementQuiz.questions[questionOrder].questionId
      "
      :questionAnswerId="
        statementManager.statementQuiz.questions[questionOrder].id
      "
      :query="currentQuery"
      v-on:save-query="onSaveQuery"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
import CreateQueryDialog from '@/views/student/query/CreateQueryDialog.vue';
import Question from '@/models/management/Question';
import Query from '@/models/management/Query';
import { QuestionAnswer } from '../../../models/management/QuestionAnswer';
import StatementQuestion from '../../../models/statement/StatementQuestion';

@Component({
  components: {
    'result-component': ResultComponent,
    'create-query-dialog': CreateQueryDialog
  }
})
export default class ResultsView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  questionOrder: number = 0;
  currentQuery: Query | null = null;
  createQueryDialog: boolean = false;

  async created() {
    if (this.statementManager.isEmpty()) {
      await this.$router.push({ name: 'create-quiz' });
    } else if (this.statementManager.correctAnswers.length === 0) {
      await this.$store.dispatch('loading');
      setTimeout(() => {
        this.statementManager.concludeQuiz();
      }, 2000);

      await this.$store.dispatch('clearLoading');
    }
  }

  increaseOrder(): void {
    if (
      this.questionOrder + 1 <
      +this.statementManager.statementQuiz!.questions.length
    ) {
      this.questionOrder += 1;
    }
  }

  decreaseOrder(): void {
    if (this.questionOrder > 0) {
      this.questionOrder -= 1;
    }
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.statementManager.statementQuiz!.questions.length) {
      this.questionOrder = n;
    }
  }

  async newQuery() {
    this.currentQuery = new Query();
    this.createQueryDialog = true;
  }

  async onSaveQuery(query: Query) {
    this.createQueryDialog = false;
    this.currentQuery = null;
  }

  async seeSharedQueries(currentQuestion: StatementQuestion) {
    await this.$store.dispatch('currentQuestion', currentQuestion);
    await this.$router.push({ name: 'see-shared-queries' });
  }

  @Watch('createQueryDialog')
  closeError() {
    if (!this.createQueryDialog) {
      this.currentQuery = null;
    }
  }
}
</script>

<style lang="scss" scoped>
.incorrect {
  color: #cf2323 !important;
}

.incorrect-current {
  background-color: #cf2323 !important;
  color: #fff !important;
}
</style>

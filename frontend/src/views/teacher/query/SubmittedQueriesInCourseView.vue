<template>
  <div class="container">
    <h2>Submitted Queries In Course</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Date Created</div>
        <div class="col last-col"></div>
      </li>
      <li
        class="list-row"
        v-for="query in queries"
        :key="query.id"
        @click="solveQuiz(quiz)"
      >
        <div class="col">
          {{ query.title }}
        </div>
        <div class="col">
          {{ query.creationDate }}
        </div>
        <div class="col last-col">
          <i class="fas fa-chevron-circle-right"></i>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Query from '@/models/management/Query';

@Component
export default class SubmittedQueriesView extends Vue {
  queries: Query[] = [];
  async created() {
    await this.$store.dispatch('loading');
    try {
      this.queries = (
        await RemoteServices.getSubmittedQueriesInCourse()
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>

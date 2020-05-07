<template>
  <div class="container">
    <h2>Statistics</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalQuizzes">
          <animated-number :number="stats.totalQuizzes" />
        </div>
        <div class="project-name">
          <p>Total Quizzes Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="stats.totalAnswers" />
        </div>
        <div class="project-name">
          <p>Total Questions Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalUniqueQuestions">
          <animated-number :number="stats.totalUniqueQuestions" />
        </div>
        <div class="project-name">
          <p>Unique Questions Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="correctAnswers">
          <animated-number :number="stats.correctAnswers">%</animated-number>
        </div>
        <div class="project-name">
          <p>Total Correct Answers</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="improvedCorrectAnswers">
          <animated-number :number="stats.improvedCorrectAnswers"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Improved Correct Questions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageOfSeenQuestions">
          <animated-number
            :number="
              (stats.totalUniqueQuestions * 100) / stats.totalAvailableQuestions
            "
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Percentage of questions seen</p>
        </div>
      </div>
      <div class="items custom" data-cy="suggestionsStats">
        <div class="project-name ">
          <p>Suggestions</p>
        </div>
        <div class="small-info">
          <p data-cy="totalSuggestions">
            {{ stats.totalProposedSuggestions }} Total
          </p>
          <p data-cy="approvedSuggestions">
            {{ stats.approvedProposedSuggestions }} Approved
          </p>
          <p v-if="stats.totalProposedSuggestions > 0">
            {{
              stats.approvedProposedSuggestions === 0
                ? 0
                : (stats.approvedProposedSuggestions /
                    stats.totalProposedSuggestions) *
                  100
            }}% Approval Rate
          </p>
        </div>
        <v-checkbox
          @change="toggleSuggestionPrivacy()"
          v-model="stats.privateSuggestionStats"
          label="Private"
          data-cy="suggestionPrivacyToggler"
        ></v-checkbox>
      </div>
      <div class="items custom" data-cy="queriesStats">
        <div class="project-name ">
          <p>Queries</p>
        </div>
        <div class="small-info">
          <p data-cy="totalQueriesSubmitted">
            {{ stats.totalQueriesSubmitted }} Total
          </p>
          <p data-cy="sharedQueries">{{ stats.sharedQueries }} Shared</p>
          <p v-if="stats.totalQueriesSubmitted > 0">
            {{
              stats.sharedQueries === 0
                ? 0
                : (stats.sharedQueries / stats.totalQueriesSubmitted) * 100
            }}% Shared Rate
          </p>
        </div>
        <v-checkbox
          @change="toggleQueryPrivacy()"
          v-model="stats.privateQueryStats"
          label="Private"
          data-cy="queryPrivacyToggler"
        ></v-checkbox>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StudentStats from '@/models/statement/StudentStats';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';

@Component({
  components: { AnimatedNumber }
})
export default class StatsView extends Vue {
  stats: StudentStats | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getUserStats();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async toggleSuggestionPrivacy() {
    try {
      await RemoteServices.toggleSuggestionStatPrivacy();
    } catch (error) {
      await this.$store.dispatch(
        'error',
        new Error('Could not toggle suggestions privacy.')
      );
      if (this.stats !== null) {
        this.stats.privateSuggestionStats = !this.stats.privateSuggestionStats;
      }
    }
  }

  async toggleQueryPrivacy() {
    try {
      await RemoteServices.toggleQueryStatPrivacy();
    } catch (error) {
      await this.$store.dispatch(
        'error',
        new Error('Could not toggle queries privacy.')
      );
      if (this.stats !== null) {
        this.stats.privateQueryStats = !this.stats.privateQueryStats;
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}
.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }
}

.small-info {
  margin: 1rem 0;
}

.small-info p {
  margin: 0;
}

.custom .project-name {
  margin: 1rem;
}
</style>

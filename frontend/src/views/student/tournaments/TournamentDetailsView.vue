<template>
  <v-card v-if="viewMode && tournament" class="table">
    <v-card-title>
      <span>Tournament</span>
    <v-spacer></v-spacer>

    <v-btn color="primary" dark @click="showDetails">
      {{'Back' }}
    </v-btn>

    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" sm="10">
          <v-text-field
            disabled
            v-model="tournament.title"
            label="Title:"
          />
        </v-col>
        <v-col cols="12" sm="2">
          <v-text-field
            disabled
            v-model="tournament.numberQuestions"
            label="Number of Questions:"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" sm="6" data-cy="availableDate">
          <v-text-field
            disabled
            v-model="tournament.availableDate"
            label="Available Date:"
          />
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="12" sm="6" data-cy="conclusionDate">
          <v-text-field
            disabled
            v-model="tournament.conclusionDate"
            label="Conclusion Date:"
          />
        </v-col>
      </v-row>
    </v-card-text>
    <v-card-title>
        <span>Tournament Quiz</span>
      <v-spacer></v-spacer>
       <v-chip label :color="tournament.status === 'QUIZ_GENERATED' ? 'green' : 'red'" @click="solveQuiz">
        <span data-cy="status">{{ tournament.status === 'QUIZ_GENERATED' ? 'Generated - Click here to solve the quiz' : 'Conditions for quiz generation not satisfied'}}</span>
      </v-chip>
    </v-card-title>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Tournament from '@/models/management/Tournament';
import Topic from '@/models/management/Topic';
import StatementManager from '@/models/statement/StatementManager';

@Component
export default class TournamentDetailsView extends Vue {
  @Prop(Tournament) readonly tournament!: Tournament;
  @Prop(Boolean) readonly viewMode!: boolean;
  topics: Topic[] = [];
  headers: object = [
    {
      text: 'Topic',
      value: 'name',
      align: 'left',
      width: '70%',
      sortable: false
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '1%',
      sortable: false
    }
  ];

  showDetails() {
    this.$emit('showDetails');
  }

  async solveQuiz() {
    if (this.tournament.status == 'QUIZ_GENERATED') {
      if (confirm('Want to start the tournament quiz? Be careful, you only have one chance to answer it.')) {
        try {
          let statementManager: StatementManager = StatementManager.getInstance;
          statementManager.statementQuiz = await RemoteServices.getTournamentQuiz(this.tournament.id);
          await this.$router.push({name: 'solve-quiz'});
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>

<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="tournaments"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />

          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="$emit('newTournament')"
            data-cy="newTournamentButton"
            >New Tournament</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:item.enrolled="{ item }">
        <v-chip :color="item.enrolled ? 'green' : 'red'" small>
          <span>{{ item.enrolled ? 'Yes' : 'No' }}</span>
        </v-chip>
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom v-if="!item.enrolled">
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="enroll(item, item.id)">
              add</v-icon
            >
          </template>
          <span>Enroll</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import Tournament from '@/models/management/Tournament';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class TournamentList extends Vue {
  tournaments: Tournament[] = [];
  tournament: Tournament | null = null;
  enrolledTournaments: Tournament[] = [];
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'title', align: 'left' },
    { text: 'Enrolled', value: 'enrolled', align: 'left' },
    { text: 'Topics', value: 'topics', align: 'left', sortable: false },
    { text: 'Number of Questions', value: 'numberQuestions', align: 'center' },
    { text: 'Creation Date', value: 'creationDate', align: 'left' },
    {
      text: 'Available Date',
      value: 'availableDate',
      align: 'left'
    },
    {
      text: 'Conclusion Date',
      value: 'conclusionDate',
      align: 'left'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getOpenTournaments();
      this.enrolledTournaments = await RemoteServices.getEnrolledTournaments();
      this.tournaments.forEach(tournament => {
        this.enrolledTournaments.forEach(tournament1 => {
          if (tournament.id == tournament1.id) {
            tournament.enrolled = true;
          }
        });
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async enroll(tournament: Tournament, tournamentId: number) {
    if (confirm('Are you sure you want to enroll this tournament?')) {
      try {
        await RemoteServices.enroll(tournamentId);
        tournament.enrolled = true;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

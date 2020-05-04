<template>
  <div>
    <tournament-form
      @switchMode="changeMode"
      @updateTournament="updateTournament"
      :edit-mode="editMode"
      :tournament="tournament"
    />
    <tournament-list
      v-if="!editMode"
      @newTournament="newTournament"
      :tournaments="tournaments"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Tournament from '@/models/management/Tournament';
import TournamentForm from '@/views/student/tournaments/TournamentForm.vue';
import TournamentList from '@/views/student/tournaments/TournamentList.vue';

@Component({
  components: {
    TournamentForm,
    TournamentList
  }
})
export default class TournamentView extends Vue {
  tournaments: Tournament[] = [];
  tournament: Tournament | null = null;
  editMode: boolean = false;

  changeMode() {
    this.editMode = !this.editMode;
    if (this.editMode) {
      this.tournament = new Tournament();
    } else {
      this.tournament = null;
    }
  }

  updateTournament(updatedTournament: Tournament) {
    this.tournaments = this.tournaments.filter(
      tournament => tournament.id !== updatedTournament.id
    );
    this.tournaments.unshift(updatedTournament);
    this.editMode = false;
    this.tournament = null;
  }

  newTournament() {
    this.editMode = true;
    this.tournament = new Tournament();
  }

}
</script>

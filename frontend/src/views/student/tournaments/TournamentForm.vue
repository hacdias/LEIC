<template>
  <v-card v-if="editMode && tournament" class="table">
    <v-card-title>
      <span>New Tournament</span>

      <v-spacer />

      <v-btn color="primary" dark @click="switchMode">
        {{ editMode ? 'Close' : 'Create' }}
      </v-btn>

      <v-btn
        color="primary"
        data-cy="saveTournamentButton"
        dark
        v-if="editMode && canSave"
        @click="save"
        >Save</v-btn
      >
    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" sm="10">
          <v-text-field
            v-model="tournament.title"
            label="*Title"
            data-cy="Title"
          />
        </v-col>
        <v-col cols="12" sm="2">
          <v-text-field
            v-model="tournament.numberQuestions"
            label="*Number of Questions"
            type="number"
            min="1"
            data-cy="numberQuestions"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" sm="6" data-cy="availableDate">
          <v-datetime-picker
            label="*Available Date"
            format="yyyy-MM-dd HH:mm"
            v-model="tournament.availableDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
            data-cy="availableDateInput"
          >
          </v-datetime-picker>
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="*Conclusion Date"
            format="yyyy-MM-dd HH:mm"
            v-model="tournament.conclusionDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
            data-cy="conclusionDate"
          >
          </v-datetime-picker>
        </v-col>
      </v-row>

      <v-data-table
        :headers="headers"
        :custom-filter="customFilter"
        :items="topics"
        :search="search"
        :sort-desc="[false]"
        :mobile-breakpoint="0"
        must-sort
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      >
        <template v-slot:item.topics="{ item }">
          <span v-for="topic in item.topics" :key="topic.id">
            //check if topic or TournamentTopic
            {{ topic.name }}
          </span>
        </template>

        <template v-slot:item.action="{ item }">
          <v-tooltip bottom v-if="!tournamentTopics.includes(item)">
            <template v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                @click="addToTournament(item)"
              >
                add</v-icon
              >
            </template>
            <span>Add to Tournament</span>
          </v-tooltip>
          <div v-if="tournamentTopics.includes(item)" :key="item.sequence">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  small
                  class="mr-2"
                  v-on="on"
                  @click="removeFromTournament(item)"
                >
                  remove</v-icon
                >
              </template>
              <span>Remove from Tournament</span>
            </v-tooltip>
          </div>
        </template>
      </v-data-table>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Tournament from '@/models/management/Tournament';
import Topic from '@/models/management/Topic';

@Component
export default class TournamentForm extends Vue {
  @Prop(Tournament) readonly tournament!: Tournament;
  @Prop(Boolean) readonly editMode!: boolean;
  topics: Topic[] = [];
  search: string = '';
  tournamentTopics: Topic[] = [];

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

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  get canSave(): boolean {
    return (
      !!this.tournament.title &&
      !!this.tournament.availableDate &&
      !!this.tournament.conclusionDate &&
      !!this.tournament.numberQuestions &&
      this.tournament.numberQuestions > 0 &&
      this.tournamentTopics.length > 0
    );
  }

  switchMode() {
    this.cleanTournamentTopics();
    this.$emit('switchMode');
  }

  async save() {
    try {
      this.tournament.topics = this.tournamentTopics;
      let updatedTournament = await RemoteServices.saveTournament(
        this.tournament
      );
      this.tournament.enrolled = true;
      this.cleanTournamentTopics();
      this.$emit('updateTournament', updatedTournament);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  customFilter(value: string, search: string, topic: Topic) {
    return (
      search != null &&
      JSON.stringify(topic)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  addToTournament(topic: Topic) {
    this.tournamentTopics.push(topic);
  }

  removeFromTournament(topic: Topic) {
    let index: number = this.tournamentTopics.indexOf(topic);
    this.tournamentTopics.splice(index, 1);
  }

  cleanTournamentTopics() {
    this.tournamentTopics = [];
  }
}
</script>

<style lang="scss" scoped></style>

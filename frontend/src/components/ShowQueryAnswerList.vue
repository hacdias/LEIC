<template>
  <v-card v-if="noAnswers">
    <v-card-text class="text-center">
      <v-container grid-list-md fluid>
        <v-layout column wrap>
          NO ANSWERS YET
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
  <v-card v-else>
    <v-card-text class="text-left">
      <v-container
        grid-list-md
        fluid
        v-for="answer in answers"
        :key="answer.id"
      >
        <v-layout column wrap>
           <v-container class="query-answer-header" grid-list-md fluid>
            <div class="float-right" v-if="isAuthor(answer)">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    class="mr-2"
                    v-on="on"
                    @click="$emit('edit-query-answer', answer)"
                    >edit</v-icon
                  >
                </template>
                <span>Edit Query Answer</span>
              </v-tooltip>
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    class="mr-2"
                    v-on="on"
                    @click="$emit('delete-query-answer', answer)"
                    color="red"
                    >delete</v-icon
                  >
                </template>
                <span>Delete Query Answer</span>
              </v-tooltip>
            </div>
            <p>{{ answer.creationDate }} <b>by</b> {{ answer.byName }} ({{ answer.byUsername }})</p>
          </v-container>
          <div class="text--primary pre-formatted">
            {{ answer.content }}
          </div>
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import QueryAnswer from '@/models/management/QueryAnswer';

@Component
export default class ShowQueryAnswerList extends Vue {
  @Prop({ type: Array, required: true }) readonly answers!: QueryAnswer[];

  get noAnswers() {
    return this.answers.length == 0;
  }

  isAuthor(answer : QueryAnswer) {
    let activeUser = this.$store.getters.getUser;
    return activeUser && activeUser.username == answer.byUsername;
  }
}
</script>

<style lang="scss" scoped>
.query-answer-header {
  padding-left: 0px;
  padding-right: 0px;
  padding-top: 0px;
  padding-bottom: 0px;
}

.pre-formatted {
  white-space: pre-wrap;
}
</style>

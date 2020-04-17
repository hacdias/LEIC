<template>
  <v-card>
    <v-card-text class="text-left">
      <v-container grid-list-md fluid>
        <v-layout column wrap>
          <v-container class="query-header" grid-list-md fluid>
            <div class="float-right" v-if="isAuthor">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    data-cy="editQueryButton"
                    class="mr-2"
                    v-on="on"
                    @click="$emit('edit-query')"
                    >edit
                  </v-icon>
                </template>
                <span>Edit Query</span>
              </v-tooltip>
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    data-cy="deleteQueryButton"
                    class="mr-2"
                    v-on="on"
                    @click="$emit('delete-query')"
                    color="red"
                    >delete</v-icon
                  >
                </template>
                <span>Delete Query</span>
              </v-tooltip>
            </div>
            <p class="display-1 text--primary" data-cy="queryTitle">
              {{ query.title }}
            </p>
          </v-container>
          <p>
            {{ query.creationDate }} <b>by</b> {{ query.byName }} ({{
              query.byUsername
            }})
          </p>
          <div class="text--primary pre-formatted" data-cy="queryContent">
            {{ query.content }}
          </div>
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Query from '@/models/management/Query';
import User from '@/models/user/User';

@Component
export default class QueryComponent extends Vue {
  @Prop({ type: Query, required: true }) query!: Query;

  get isAuthor() {
    let activeUser = this.$store.getters.getUser;
    return activeUser && activeUser.username == this.query.byUsername;
  }
}
</script>

<style lang="scss" scoped>
.query-header {
  padding-left: 0px;
  padding-right: 0px;
  padding-top: 0px;
  padding-bottom: 0px;
}

.pre-formatted {
  white-space: pre-wrap;
}
</style>

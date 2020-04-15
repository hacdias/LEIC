<template>
  <v-card>
    <v-card-text class="text-left">
      <v-container grid-list-md fluid>
        <v-layout column wrap>
          <p class="display-1 text--primary">
            {{ query.title }}
          </p>
          <p>{{ query.creationDate }} <b>by</b> {{ query.byName }}</p>
          <div class="text--primary">
            {{ query.content }}
          </div>
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Query from '@/models/management/Query';

@Component
export default class QueryComponent extends Vue {
  @Prop({ type: Query, required: true }) readonly query!: Query;
}
</script>

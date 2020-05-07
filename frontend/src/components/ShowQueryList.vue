<template>
  <div class="container-none">
    <li
      class="list-row"
      v-for="query in queries"
      :key="query.id"
      @click="seeQuery(query)"
    >
      <div class="col">
        {{ query.title }}
      </div>
      <div class="col">
        {{ query.creationDate }}
      </div>
      <div class="col">
        {{ query.numberAnswers }}
      </div>
      <div class="col">
        <v-icon v-if="query.shared" class="mr-1" color="green"
          >fas fa-thumbs-up
        </v-icon>
        <v-icon v-if="!query.shared" class="mr-1" color="red"
          >fas fa-thumbs-down
        </v-icon>
      </div>
      <div class="col last-col">
        <i class="fas fa-chevron-circle-right"></i>
      </div>
    </li>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue, Emit } from 'vue-property-decorator';
import Query from '@/models/management/Query';
import { RawLocation } from 'vue-router';

@Component
export default class ShowQueryList extends Vue {
  @Prop({ type: Array, required: true }) readonly queries!: Query[];

  @Emit('see-query')
  seeQuery(query: Query) {
    this.$store.dispatch('currentQuery', query);
    return 1;
  }
}
</script>

<style lang="scss" scoped>
.container-none {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 0px;
  padding-right: 0px;
  padding-top: 0px;
  padding-bottom: 0px;

  li {
    border-radius: 3px;
    padding: 15px 10px;
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .list-row {
    background-color: #ffffff;
    box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
    display: flex;
  }

  .col {
    flex-basis: 25% !important;
    margin: auto; /* Important */
    text-align: center;
  }
}
</style>

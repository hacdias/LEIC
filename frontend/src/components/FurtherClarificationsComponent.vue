<template>
  <div>
    <v-card
      grid-list-md
      fluid
      color="blue"
      data-cy="furtherClarificationComponent"
    >
      <v-card-text class="text-left">
        <v-layout column wrap>
          <v-card
            v-for="clarification in queryAnswer.answers"
            :key="clarification.id"
            color="white"
            :style="'margin: 5px'"
          >
            <v-card-text class="text-left">
              <p>
                {{ clarification.creationDate }} <b>by</b>
                {{ clarification.byName }} ({{ clarification.byUsername }})
              </p>
              <div
                data-cy="furtherClarificationContent"
                class="text--primary pre-formatted"
              >
                <p style="margin-bottom:0px">{{ clarification.content }}</p>
              </div>
            </v-card-text>
          </v-card>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2"
                data-cy="addClarificationButton"
                v-on="on"
                color="white"
                @click="addClarification()"
                >fas fa-pen-square</v-icon
              >
            </template>
            <span>Add Clarification</span>
          </v-tooltip>
        </v-layout>
      </v-card-text>
    </v-card>
    <create-further-clarification-dialog
      v-if="currentFurtherClarification"
      v-model="createFurtherClarificationDialog"
      :queryAnswerId="queryAnswer.id"
      :furtherClarification="currentFurtherClarification"
      v-on:save-further-clarification="onSaveFurtherClarification"
    />
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import QueryAnswer from '../models/management/QueryAnswer';
import CreateFurtherClarificationDialog from '@/components/CreateFurtherClarificationComponent.vue';

@Component({
  components: {
    'create-further-clarification-dialog': CreateFurtherClarificationDialog
  }
})
export default class FurtherClarificationsComponent extends Vue {
  @Prop({ type: QueryAnswer, required: true }) queryAnswer!: QueryAnswer;
  createFurtherClarificationDialog: boolean = false;
  currentFurtherClarification: QueryAnswer | null = null;

  async addClarification() {
    this.currentFurtherClarification = new QueryAnswer();
    this.createFurtherClarificationDialog = true;
  }

  async onSaveFurtherClarification(furtherClarification: QueryAnswer) {
    this.queryAnswer.answers = this.queryAnswer.answers.filter(
      a => a.id !== furtherClarification.id
    );
    this.queryAnswer.answers.push(furtherClarification);
    this.currentFurtherClarification = null;
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

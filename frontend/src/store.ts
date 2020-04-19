import Vue from 'vue';
import Vuex from 'vuex';
import RemoteServices from '@/services/RemoteServices';
import AuthDto from '@/models/user/AuthDto';
import Course from '@/models/user/Course';
import StatementQuestion from '@/models/statement/StatementQuestion';
import User from '@/models/user/User';
import Query from './models/management/Query';
import Suggestion from '@/models/management/Suggestion';

interface State {
  token: string;
  user: User | null;
  currentCourse: Course | null;
  currentQuestion: StatementQuestion | null;
  currentQuery: Query | null;
  currentSuggestion: Suggestion | null;
  error: boolean;
  errorMessage: string;
  loading: boolean;
}

const state: State = {
  token: '',
  user: null,
  currentCourse: null,
  currentQuestion: null,
  currentQuery: null,
  currentSuggestion: null,
  error: false,
  errorMessage: '',
  loading: false
};

Vue.use(Vuex);
Vue.config.devtools = true;

export default new Vuex.Store({
  state: state,
  mutations: {
    login(state, authResponse: AuthDto) {
      state.token = authResponse.token;
      state.user = authResponse.user;
    },
    logout(state) {
      state.token = '';
      state.user = null;
      state.currentCourse = null;
    },
    error(state, errorMessage: string) {
      state.error = true;
      state.errorMessage = errorMessage;
    },
    clearError(state) {
      state.error = false;
      state.errorMessage = '';
    },
    loading(state) {
      state.loading = true;
    },
    clearLoading(state) {
      state.loading = false;
    },
    currentCourse(state, currentCourse: Course) {
      state.currentCourse = currentCourse;
    },
    currentQuestion(state, currentQuestion: StatementQuestion) {
      state.currentQuestion = currentQuestion;
    },
    currentQuery(state, currentQuery: Query) {
      state.currentQuery = currentQuery;
    },
    currentSuggestion(state, currentSuggestion: Suggestion) {
      state.currentSuggestion = currentSuggestion;
    }
  },
  actions: {
    error({ commit }, errorMessage) {
      commit('error', errorMessage);
    },
    clearError({ commit }) {
      commit('clearError');
    },
    loading({ commit }) {
      commit('loading');
    },
    clearLoading({ commit }) {
      commit('clearLoading');
    },
    async fenixLogin({ commit }, code) {
      const authResponse = await RemoteServices.fenixLogin(code);
      commit('login', authResponse);
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    async demoStudentLogin({ commit }) {
      const authResponse = await RemoteServices.demoStudentLogin();
      commit('login', authResponse);
      commit(
        'currentCourse',
        (Object.values(authResponse.user.courses)[0] as Course[])[0]
      );
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    async demoTeacherLogin({ commit }) {
      const authResponse = await RemoteServices.demoTeacherLogin();
      commit('login', authResponse);
      commit(
        'currentCourse',
        (Object.values(authResponse.user.courses)[0] as Course[])[0]
      );
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    async demoAdminLogin({ commit }) {
      const authResponse = await RemoteServices.demoAdminLogin();
      commit('login', authResponse);
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    logout({ commit }) {
      return new Promise(resolve => {
        commit('logout');
        // localStorage.removeItem("token");
        // localStorage.removeItem("userRole");
        resolve();
      });
    },
    currentCourse({ commit }, currentCourse) {
      commit('currentCourse', currentCourse);
    },
    currentQuestion({ commit }, currentQuestion) {
      commit('currentQuestion', currentQuestion);
    },
    currentQuery({ commit }, currentQuery) {
      commit('currentQuery', currentQuery);
    },
    currentSuggestion({ commit }, currentSuggestion) {
      commit('currentSuggestion', currentSuggestion);
    }
  },
  getters: {
    isLoggedIn(state): boolean {
      return !!state.token;
    },
    isAdmin(state): boolean {
      return (
        !!state.token &&
        state.user !== null &&
        (state.user.role == 'ADMIN' || state.user.role == 'DEMO_ADMIN')
      );
    },
    isTeacher(state): boolean {
      return (
        !!state.token && state.user !== null && state.user.role == 'TEACHER'
      );
    },
    isStudent(state): boolean {
      return (
        !!state.token && state.user !== null && state.user.role == 'STUDENT'
      );
    },
    getToken(state): string {
      return state.token;
    },
    getUser(state): User | null {
      return state.user;
    },
    getCurrentCourse(state): Course | null {
      return state.currentCourse;
    },
    getCurrentQuestion(state): StatementQuestion | null {
      return state.currentQuestion;
    },
    getCurrentQuery(state): Query | null {
      return state.currentQuery;
    },
    getCurrentSuggestion(state): Suggestion | null {
      return state.currentSuggestion;
    },
    getError(state): boolean {
      return state.error;
    },
    getErrorMessage(state): string {
      return state.errorMessage;
    },
    getLoading(state): boolean {
      return state.loading;
    }
  }
});

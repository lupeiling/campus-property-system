import Vue from 'vue'
import Vuex from 'vuex'
import { getCurrentUser } from '@/api/user'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  },
  getters: {
    token: state => state.token,
    user: state => state.user,
    role: state => state.user ? state.user.role : '',
    isAdmin: state => state.user && state.user.role === 'ADMIN',
    isStaff: state => state.user && (state.user.role === 'ADMIN' || state.user.role === 'STAFF')
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER(state, user) {
      state.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    CLEAR_AUTH(state) {
      state.token = ''
      state.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  },
  actions: {
    login({ commit }, { token, user }) {
      commit('SET_TOKEN', token)
      commit('SET_USER', user)
    },
    logout({ commit }) {
      commit('CLEAR_AUTH')
    },
    async fetchUser({ commit }) {
      try {
        const res = await getCurrentUser()
        commit('SET_USER', res.data)
      } catch (e) {
        commit('CLEAR_AUTH')
      }
    }
  }
})

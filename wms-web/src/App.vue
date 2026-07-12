<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>

import {defineComponent} from "vue";
import Index from "@/components/Index.vue";

export default ({
  // App.vue 中的代码片段
  data() {
    return {
      user: JSON.parse(sessionStorage.getItem('CurUser')),
    }
  },
  watch: {
    '$store.state.menu': {
      handler(val, old) {
        if(!old && this.user && this.user.no) {
          this.$store.commit("setMenu", val)
        }
      },
      immediate: true
    }
  },
  components: {

  }
})
</script>

<style>
  #app{
    height: 100%;
  }
</style>

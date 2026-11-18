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
    // https://v2.cn.vuejs.org/v2/api/#watch
    '$store.state.menu': {  // 一个数据 data中的或者。。
      handler(val, old) {
        if(!old && this.user && this.user.no) {// old为空，本地seesion存在user和user的序号
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

import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/global.css'
import axios from "axios";
import VueRouter from "vue-router";
import router from "./router";
import store from "./store";

Vue.prototype.$axios = axios;
// Vue.prototype.$httpUrl='http://192.168.139.128:8090'
Vue.prototype.$httpUrl='http://localhost:8090'
Vue.prototype.$Header = 'Token'

// Vue.use(ElementUI);
Vue.use(VueRouter);
Vue.use(ElementUI,{size:'small'});

Vue.config.productionTip = false


// ======================
// 👇 只加这一段即可！！！
// ======================
axios.interceptors.request.use(config => {
  const token = sessionStorage.getItem('token')
  if (token) config.headers['token'] = token
  return config
})

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')

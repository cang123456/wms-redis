<script>

export default {
  data(){
    return {
      // isCollapse:true
      // menu:[
      //     {
      //       menuclick:'user',
      //       menuName:'用户管理'
      //     },
      //     {
      //       menuclick:'admin',
      //       menuName:'管理员管理'
      //     }
      // ]
    }
  },
  computed:{
    "menu":{
      get(){
        return this.$store.state.menu
      }
    }
  },
  props:{
    isCollapse:Boolean
  },
}


</script>

<template>
  <el-menu style="height: 100%;"
           default-active="/home"
           background-color="#545c64"
           text-color="#fff"
           active-text-color="#ffd04b"
           :collapse="isCollapse"
           :collapse-transition="false"
           router
  >
    <el-menu-item  index="/home">
      <i class="el-icon-s-home"></i>
      <span slot="title">主页</span>
    </el-menu-item>


    <el-menu-item  :index="'/index/'+(item.menuClick.startsWith('/') ? item.menuClick.substring(1) : item.menuClick)" v-for="(item,i) in menu" :key="i">
      <i :class="item.menuIcon"></i>
      <span slot="title">{{ item.menuName }}</span>
    </el-menu-item>

<!--    <el-menu-item  index="/admin">-->
<!--      <i class="el-icon-s-flag"></i>-->
<!--      <span slot="title">数据</span>-->
<!--    </el-menu-item>-->

<!--    <el-menu-item index="/user">-->
<!--      <i class="el-icon-s-promotion"></i>-->
<!--      <span slot="title">导航2</span>-->
<!--    </el-menu-item>-->


  </el-menu>
</template>

<style>




</style>
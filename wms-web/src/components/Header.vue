<script>
export default {
  name:"Header",
  data(){
    return{
      user : JSON.parse(sessionStorage.getItem("CurUser"))
    }
  },
  methods:{
    toUser(){
      console.log("to_user")

      this.$router.push("/Home")
    },
    logout(){
      this.$confirm('您确定要退出登录吗？', '提示', {
        confirmButtonText: '确定', //确认按钮的文字显示
        type: 'warning',
        center: true, //文字居中显示
      })
      .then(() => {
        this.$message.success("退出登录成功")
        sessionStorage.clear()
        this.$router.push("/")

      })
      .catch(() => {
        this.$message.info("已取消退出登录")
      })
    },
    collapse(){
      this.$emit('doCollapse')
    }
  },
  created() {
    this.$router.push('/home')
  },
  props:{
    icon:String
  }
}
</script>

<template>
  <div style="display: flex;line-height: 60px;">
    <div style="margin-top:4px;">
      <i :class="icon" style="font-size: 25px;cursor:pointer;" @click="collapse"></i>
    </div>
    <div style="flex: 1;text-align: center;font-size: 20px;">
      <span>
        欢迎来到仓库管理系统
      </span>
    </div>
    <el-dropdown>
      <span>{{ user.name }}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item @click.native="toUser">个人中心</el-dropdown-item>
        <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<style scoped>

</style>
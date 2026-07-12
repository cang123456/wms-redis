<template>
  <div class="not-found-container">
    <div class="content">
      <h1 class="error-code">404</h1>
      <p class="error-message">抱歉，您访问的页面不存在</p>
      <p class="error-tip">可能是链接错误或页面已被移除</p>

      <div class="btn-group">
        <router-link to="/home" class="btn btn-primary">返回首页</router-link>
        <button @click="goBack" class="btn btn-default">返回上一页</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  methods: {
    // 返回上一页逻辑
    goBack() {
      if (window.history.length > 1) {
        this.$router.back()
      } else {
        this.$router.push('/home')
      }
    }
  }
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.content {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.error-code {
  font-size: 120px;
  font-weight: 800;
  color: #409eff;
  margin: 0 0 10px;
  line-height: 1;
}

.error-message {
  font-size: 24px;
  color: #303133;
  margin: 0 0 10px;
}

.error-tip {
  font-size: 14px;
  color: #909399;
  margin: 0 0 30px;
}

.btn-group {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn {
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s;
  border: 1px solid transparent;
  display: inline-block;
}

.btn-primary {
  background: #409eff;
  color: white;
}

.btn-primary:hover {
  background: #66b1ff;
}

.btn-default {
  background: white;
  border-color: #dcdfe6;
  color: #606266;
}

.btn-default:hover {
  border-color: #409eff;
  color: #409eff;
}
</style>
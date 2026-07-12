<script>
export default {
  name:"Register",
  data() {
    return {
      registerForm: {
        no: '',
        name: '',
        password: '',
        confirmPassword: '',
        phone: '',
        sex: 1,
        age: null
      },
      rules: {
        no: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      },
      confirm_disabled: false
    }
  },
  methods: {
    checkPassword(rule, value, callback) {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'));
      } else {
        callback();
      }
    },
    confirm() {
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.$message.error('两次输入密码不一致');
        return;
      }
      this.confirm_disabled = true;
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          const submitData = {
            no: this.registerForm.no,
            name: this.registerForm.name,
            password: this.registerForm.password,
            phone: this.registerForm.phone,
            sex: this.registerForm.sex,
            age: this.registerForm.age
          };
          this.$axios.post(this.$httpUrl + '/user/register', submitData)
            .then(res => res.data)
            .then(res => {
              if (res.code === 1) {
                this.$message.success('注册成功，请登录');
                this.$router.replace('/');
              } else {
                this.confirm_disabled = false;
                this.$message.error(res.msg || '注册失败');
              }
            })
            .catch(error => {
              console.error('注册请求失败:', error);
              this.confirm_disabled = false;
              this.$message.error('网络异常，请稍后重试');
            });
        } else {
          this.confirm_disabled = false;
        }
      });
    },
    goToLogin() {
      this.$router.replace('/');
    }
  }
}
</script>

<template>
  <div class="registerBody">
    <div class="registerDiv">
      <div class="register-content">
        <h1 class="register-title">用户注册</h1>
        <el-form
          :model="registerForm"
          label-width="100px"
          :rules="rules"
          ref="registerForm"
          class="register-form"
        >
          <el-form-item label="账号" prop="no">
            <el-input
              style="width: 200px"
              type="text"
              v-model="registerForm.no"
              autocomplete="off"
              size="small"
              placeholder="请输入账号"
              class="register-input"
            ></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input
              style="width: 200px"
              type="text"
              v-model="registerForm.name"
              autocomplete="off"
              size="small"
              placeholder="请输入姓名"
              class="register-input"
            ></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              style="width: 200px"
              type="password"
              v-model="registerForm.password"
              show-password
              autocomplete="off"
              size="small"
              placeholder="请输入密码"
              class="register-input"
            ></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              style="width: 200px"
              type="password"
              v-model="registerForm.confirmPassword"
              show-password
              autocomplete="off"
              size="small"
              placeholder="请确认密码"
              class="register-input"
              @keyup.enter.native="confirm"
            ></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input
              style="width: 200px"
              type="text"
              v-model="registerForm.phone"
              autocomplete="off"
              size="small"
              placeholder="请输入手机号"
              class="register-input"
            ></el-input>
          </el-form-item>
          <el-form-item class="btn-group">
            <el-button type="primary" @click="confirm" :disabled="confirm_disabled" class="register-btn">注册</el-button>
            <el-button @click="goToLogin" class="login-link-btn">已有账号？去登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.registerBody {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  background: linear-gradient(135deg, #B3C0D1 0%, #c9d6e8 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0;
  padding: 0;
}

.registerDiv {
  width: 450px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-content {
  width: 100%;
  padding: 30px 50px;
  box-sizing: border-box;
  text-align: center;
}

.register-title {
  font-size: 24px;
  color: #1f2d3d;
  margin: 0 0 20px 0;
  font-weight: 600;
  position: relative;
  padding-bottom: 15px;
}

.register-form {
  margin-top: 10px;
}

.register-input {
  width: 200px !important;
}

.btn-group {
  margin-top: 20px;
}

.register-btn {
  width: 120px;
}

.login-link-btn {
  width: 120px;
  margin-left: 10px;
}
</style>

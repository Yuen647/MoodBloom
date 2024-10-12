<template>
  <div class="auth-page"> <!-- 包含背景的容器 -->
    <!-- 动态文字 -->
    <div class="dynamic-text">
      <span v-for="(word, index) in words" :key="index" class="word">{{ word }}</span>
    </div>

    <div class="auth-container">
      <div class="wrapper">
        <!-- 注册表单 -->
        <div v-if="isRegister">
          <h2>注册</h2>
          <form @submit.prevent="register">
            <div class="input-box">
              <input v-model="registerForm.username" type="text" required />
              <label>用户名</label>
            </div>
            <div class="input-box">
              <input v-model="registerForm.email" type="email" required />
              <label>邮箱</label>
            </div>
            <div class="input-box">
              <input v-model="registerForm.password" type="password" required />
              <label>密码</label>
            </div>
            <button type="submit" class="btn">注册</button>
          </form>
          <div class="signup-link">
            <p>已有账户？<a href="#" @click="toggleForm">点击这里登录</a></p>
          </div>
        </div>

        <!-- 登录表单 -->
        <div v-else>
          <h2>登录</h2>
          <form @submit.prevent="login">
            <div class="input-box">
              <input v-model="loginForm.username" type="text" required />
              <label>用户名</label>
            </div>
            <div class="input-box">
              <input v-model="loginForm.password" type="password" required />
              <label>密码</label>
            </div>
            <button type="submit" class="btn">登录</button>
          </form>
          <div class="signup-link">
            <p>没有账户？<a href="#" @click="toggleForm">点击这里注册</a></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

  <script>
  import axios from 'axios';

  export default {

    data() {
      return {
        words: [],
        isRegister: false,
        // 注册表单数据
        registerForm: {
          username: '',
          email: '',
          password: ''
        },
        // 登录表单数据
        loginForm: {
          username: '',
          password: ''
        }
      };
    },

    mounted() {
      const fullText = '可以写点东西~';
      let index = 0;

      const typingEffect = () => {
        if (index < fullText.length) {
          this.words.push(fullText[index]);
          index++;
          setTimeout(typingEffect, 150); // 控制文字出现的速度
        }
      };

      typingEffect();
    },


    methods: {
      toggleForm() {
        this.isRegister = !this.isRegister;
      },
      async register() {
        try {
            await axios.post(`https://124.222.156.13/api/user/register`, this.registerForm);
            alert('注册成功！请登录');
            this.toggleForm();  // 注册成功后切换到登录表单
        } catch (error) {
            // 改进错误信息提示
            const errorMessage = error.response && error.response.data ? error.response.data : error.message;
            alert('注册失败: ' + errorMessage);
        }
        },
      async login() {
        try {
            const response = await axios.post(`https://124.222.156.13/api/user/login`, this.loginForm);

            // 检查登录响应是否包含用户名和 userId
            if (response.data.userId && response.data.username) {
            const userId = response.data.userId;
            const username = response.data.username;

            // 保存 userId 和 username 到 localStorage
            localStorage.setItem('userId', userId);
            localStorage.setItem('username', username); // 保存用户名

            alert('登录成功！欢迎 ' + username);
            this.$router.push('/HomePage');  // 跳转到首页
            } else {
            alert('登录响应中缺少用户名或 userId');
            }
        } catch (error) {
            alert('登录失败: ' + (error.response ? error.response.data : error.message));
        }
        }
    }
  };
</script>


<style scoped>
* {
    font-family: "Poppins", sans-serif;
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.auth-page {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #282a37; /* 深灰色背景 */
}

.auth-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh; /* 占满整个屏幕高度 */
}

.wrapper {
    position: relative;
    width: 400px;
    height: 450px;
    background: #3e404d;
    border: 2px solid rgba(255, 255, 255, 0.5);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(15px);
    box-shadow: 0px 0px 15px 5px rgba(255, 255, 255, 0.2);
}

.wrapper:hover {
    box-shadow: 0 0 40px rgba(255,255,255,0.5);
    background: #46474e;
}

.wrapper h1, .wrapper h2 {
    font-size: 2em;
    color: #fff;
    text-align: center;
}

.wrapper .input-box {
    position: relative;
    width: 310px;
    margin: 30px 0;
    border-bottom: 2px solid #fff;
}

.wrapper .input-box input {
    width: 100%;
    height: 50px;
    background: transparent;
    outline: none;
    border: none;
    font-size: 1em;
    color: #fff;
    padding: 0 40px 0 5px;
    border-bottom: 1px solid #ccc;
}

.wrapper .input-box label {
    position: absolute;
    top: 50%;
    left: 5px;
    transform: translateY(-50%);
    font-size: 1em;
    color: #fff;
    pointer-events: none;
    transition: 0.5s;
}

.wrapper .input-box input:focus ~ label {
    border-bottom: 1px solid #fff; /* 聚焦时边框变白 */
}

.wrapper .input-box input:focus ~ label,
.wrapper .input-box input:valid ~ label {
    top: -5px;
}

.wrapper .input-box .icon {
    position: absolute;
    right: 8px;
    color: #fff;
    font-size: 1.2em;
    line-height: 57px;
}

.wrapper .btn {
    width: 100%;
    height: 40px;
    background: #fff;
    outline: none;
    border: none;
    border-radius: 40px;
    cursor: pointer;
    font-size: 1em;
    font-weight: 500;
    color: #000;
    margin-top: 10px;
    border-radius: 20px;
}

.btn:hover {
    background: #ffffea;
}

.wrapper .signup-link {
    font-size: 0.9em;
    color: #fff;
    text-align: center;
    margin: 25px 0 10px;
}

.wrapper .signup-link a {
    color: #fff;
    text-decoration: none;
    font-weight: 600;
}

.wrapper .signup-link a:hover {
    text-decoration: underline;
    color: #aaa;
}

.dynamic-text {
  position: absolute;
  letter-spacing: 0.1em;
  top: 40px; 
  left: 4cqi;
  font-size: 24px;
  color: white;
  font-family: 'ZCOOL KuaiLe', 'Microsoft YaHei', 'PingFang SC', 'WenQuanYi Micro Hei', sans-serif !important;
  animation: fade-in 2.0s ease-in-out;
}


.word {
  display: inline-block;
  opacity: 0;
  animation: fade-in-word 0.5s ease forwards;
}

@keyframes fade-in-word {
  0% {
    opacity: 0;
    transform: translateY(-20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>


  
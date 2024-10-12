<template>
  <div id="app">
    <!-- 只在特定页面显示导航栏 -->
    <nav v-if="showNav">
      <ul>
        <!-- 注销按钮，使用 Font Awesome 的退出图标 -->
        <li>
          <a href="javascript:void(0)" @click="logout">
            <i class="fas fa-sign-out-alt"></i>
          </a>
        </li>
        
        <!-- 首页按钮，使用 Font Awesome 的首页图标 -->
        <li>
          <router-link to="/HomePage">
            <i class="fas fa-home"></i>
          </router-link>
        </li>
      </ul>
    </nav>
    
    <!-- 添加缩放过渡效果 -->
    <transition name="zoom" mode="out-in">
      <router-view></router-view>
    </transition>
  </div>
</template>

<script>
export default {
  computed: {
    showNav() {
      // 如果当前页面不是登录或注册页面，显示导航栏
      return this.$route.path !== '/';
    }
  },
  methods: {
    logout() {
      // 弹出确认框
      if (confirm("确定要退出登录吗？")) {
        // 清除 localStorage 中的用户数据
        localStorage.removeItem("userId");
        localStorage.removeItem("username");
        // 跳转到登录注册页面
        this.$router.push("/");
      }
    },
    checkLoginStatus() {
      const userId = localStorage.getItem("userId");
      const username = localStorage.getItem("username");

      // 仅在不是登录页面时检查用户登录状态
      if (this.$route.path !== '/' && (!userId || !username)) {
        // 用户未登录，跳转到登录注册页面
        alert("请先登录");
        this.$router.push("/");
      }
    }
  },
  mounted() {
    // 页面加载时检测登录状态
    this.checkLoginStatus();
  },
  name: 'App',
};
</script>

<style scoped>
nav {
  position: fixed;
  top: -10px;
  left: -30px;
  z-index: 9999;
  display: flex;
}

nav ul {
  list-style: none;
  display: flex;
  gap: 10px; /* 两个图标之间的间距 */
}

nav ul li {
  font-size: 1.5em; /* 图标大小 */
}

nav ul li a {
  color: rgb(213, 216, 216);
  text-decoration: none;
  display: flex;
  align-items: center;
  padding: 5px;
}

nav ul li a:hover {
  color: #eaf0f0; /* 悬停时颜色变化 */
}

nav ul li a i {
  font-size: 1.5em; /* 图标大小 */
}

/* 缩放过渡效果 */
.zoom-enter-active, .zoom-leave-active {
  transition: transform 0.5s ease, opacity 0.5s ease;
}

.zoom-enter-from, .zoom-leave-to {
  transform: scale(0.8);
  opacity: 0;
}

.zoom-enter-to, .zoom-leave-from {
  transform: scale(1);
  opacity: 1;
}
</style>

<template>
  <div class="app-body">
    <div class="container">
      <transition name="fade" mode="out-in">
        <router-view></router-view>
      </transition>

      <div class="box box1">
        <div class="box1-content">
          <p class="welcome-message">欢迎 <span class="highlight-username">{{ username }}</span> 来到主页！</p>
          <p class="user-activity">
            <i class="user-activity-icon">🔥</i> 已陪伴你 {{ activeDays }} 天!
          </p>

          <p class="weather-info" v-if="weather">
            <i class="weather-icon" :class="getWeatherIcon(weather.text)"></i> 
            <span class="weather-description">{{ weather.text }}</span> 
            <span class="weather-temp">{{ weather.temp }}°C</span>
          </p>

          <transition name="fade">
            <blockquote v-if="latestQuote" class="quote-text">
              “{{ latestQuote }}”
            </blockquote>
          </transition>
        </div>
      </div>


      <div class="box box2">
        <div class="music-player">
          <div :class="['record', { playing: isPlaying }]">
            <i class="fas fa-compact-disc"></i>
          </div>
          <div class="song-info">
            <h3 class="song-title">Shape of you</h3>
            <p class="artist-name">Ed Sheeran</p>
          </div>
          <div class="player-controls">
            <button class="prev-btn"><i class="fas fa-backward"></i></button>
            <button class="play-btn" @click="togglePlayPause">
              <span style="font-weight: bold;">{{ isPlaying ? '||' : '▶' }}</span>
            </button>
            <button class="next-btn"><i class="fas fa-forward"></i></button>
          </div>
        </div>
        <!-- 隐藏的 audio 元素 -->
        <audio ref="audio" @ended="handleEnded">
          <source src="/audio/soy.mp3" type="audio/mpeg" />
          Your browser does not support the audio element.
        </audio>
      </div>


      <div class="box box3" @click="navigateTo('/RecordMood')">
        <span>心境随笔</span>
      </div>


      <div class="box box4" @click="navigateTo('/ViewMood')">
        <div class="flip-card-inner">
          <div class="flip-card-front">
            <span>济忆</span>
          </div>
          <div class="flip-card-back">
            <img src="@/assets/view-mood-icon.png" alt="查看心情图片" class="mood-image" />
          </div>
        </div>
      </div>


      <!-- news -->
      <div class="box box5" @click="navigateTo('/NewsPage')">
        <div class="content-left">
          <h2>此时此刻</h2>
        </div>
        <div class="content-right">
          <img src="../assets/default.png" alt="News Image" class="news-thumbnail">
        </div>
      </div>



      <div class="box box6">
        <LocationMap />
      </div>
    </div>
  </div>
</template>

<script>
import LocationMap from '../components/LocationMap.vue'; // 引入 LocationMap 组件
import axios from 'axios';
export default {
  name: 'HomePageBentoLayout',
  components: {
    LocationMap,  // 注册 LocationMap 组件
  },
  data() {
    return {
      username: '',// 初始化用户名为空
      latestQuote: '', // 用于存储最新的鸡汤语录
      activeDays: 0, // 用于存储用户活跃天数
      weather: null,  // 用于存储天气信息
      isPlaying: false, // 初始化为未播放状态
    };
  },
  mounted() {
    // 从 localStorage 中获取用户名
    this.username = localStorage.getItem('username');

    // 计算用户活跃天数
    this.calculateActiveDays();

    // 获取最新心灵鸡汤语录
    this.getLatestQuote();

    // 获取天气信息
    this.fetchWeatherInfo();
  },
  methods: {
    // 跳转到不同的路由
    navigateTo(route) {
      this.$router.push(route);
    },
    // 获取最新心灵鸡汤语录
    getLatestQuote() {
      axios
        .get(`https://124.222.156.13/api/soup-quotes/latest`) // 请求后端API获取最新语录
        .then(response => {
          this.latestQuote = response.data.text; // 将语录赋值给latestQuote
        })
        .catch(error => {
          console.error("获取心灵鸡汤失败: ", error); // 错误处理
        });
    },
    // 计算用户活跃天数
    calculateActiveDays() {
      const username = this.username; // 假设你有用户名信息
      const firstLoginKey = `firstLoginDate_${username}`; // 为每个用户生成唯一的 key
      
      const firstLoginDate = localStorage.getItem(firstLoginKey);

      if (firstLoginDate) {
        // 计算当前日期与首次登录日期的时间差
        const diffTime = Math.abs(new Date() - new Date(firstLoginDate));
        this.activeDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); // 换算成天数
      } else {
        // 如果是首次登录，则将当前日期存储为首次登录日期
        localStorage.setItem(firstLoginKey, new Date());
        this.activeDays = 1; // 活跃天数为1天
      }
    },

    // 获取当前用户的天气信息
    fetchWeatherInfo() {
      const userId = localStorage.getItem('userId'); // 假设用户ID保存在localStorage中
      axios
        .get(`https://124.222.156.13/api/weather/current?userId=${userId}`) // 调用后端天气API
        .then(response => {
          this.weather = response.data; // 存储天气信息
        })
        .catch(error => {
          console.error("获取天气信息失败: ", error);
        });
    },
    getWeatherIcon(weatherText) {
      switch (weatherText) {
        case '晴天':
          return 'wi wi-day-sunny';
        case '雨天':
          return 'wi wi-rain';
        case '多云':
          return 'wi wi-cloudy';
        case '雪天':
          return 'wi wi-snow';
        default:
          return 'wi wi-day-cloudy'; // 默认图标
      }
    },
    togglePlayPause() {
      const audio = this.$refs.audio;
      if (this.isPlaying) {
        audio.pause(); // 暂停音频
      } else {
        audio.play(); // 播放音频
      }
      this.isPlaying = !this.isPlaying; // 切换播放状态
    },
    handleEnded() {
      // 当音频播放结束时，自动将 isPlaying 设为 false
      this.isPlaying = false;
    },
    
  }
};
</script>

<style scoped>
/* 导入字体 */
@import url('https://fonts.googleapis.com/css?family=Poppins:400,700&display=swap');

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html, body {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden; /* 防止出现滚动条 */
}

.app-body {
  font-family: 'Poppins', sans-serif;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(163, 218, 243, 1) 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(10px);
}

.container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(3, 1fr);
  gap: 30px;
  padding: 20px;
  width: 90vw;
  height: 90vh;
  max-width: 1200px;
  max-height: 800px;
}

.box {
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 15px;
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  background: linear-gradient(45deg, #7f8c8d, #34495e);
  padding: 10px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(5px);
  cursor: pointer; 
}


.box1 {
  background: linear-gradient(135deg, #9c89b8, #6a4c93);
  grid-column: span 1;
  grid-row: span 2;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  border-radius: 20px;
  height: 100%; /* 确保 box1 高度完全填充 */
  width: 100%;  /* 确保 box1 宽度完全填充 */
}

.box1-content {
  text-align: center; /* 内容居中 */
  width: 100%;
  height: 100%; /* 确保内容区域也填充父元素 */
  display: flex;
  flex-direction: column; /* 使子元素垂直排列 */
  justify-content: center; /* 垂直居中 */
}

.weather-info {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.2));
  border-radius: 10px;
  padding: 10px 20px;
  margin-top: 20px;
  color: #fff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
  font-size: 16px;
}

.weather-icon {
  font-size: 30px;
  margin-right: 10px;
}

.weather-temp {
  font-size: 16px;
  color: #fff;
}

.weather-description {
  margin-right: 5px;
}

.welcome-message {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 15px;
}

.highlight-username {
  color: #88d7f1; /* 改变颜色为金色 */
  font-size: 30px; /* 比普通文字稍大 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}


.user-activity {
  font-size: 18px;
  font-weight: bold;
  color: #f7f7f4;
  background: linear-gradient(135deg, #cde7eb, #88d7f1);
  padding: 10px 20px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  margin-bottom: 20px;
  display: inline-block;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.user-activity-icon {
  margin-right: 8px;
  font-size: 18px;
  vertical-align: middle;
  color: white;
}

.quote-text {
  font-size: 12px;
  font-style: italic;
  color: #f0e6f6;
  text-align: center;
  line-height: 1.5;
  padding: 10px;
  border-left: 5px solid #f0e6f6;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}



.box2 {
  position: relative;
  background: #e0e0e0; /* 灰色背景 */
  grid-column: span 2;
  grid-row: span 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  border-radius: 30px; /* 圆角 */
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2); /* 柔和的阴影 */
  overflow: hidden;
}

.music-player {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 600px;
  justify-content: space-between; /* 左右两侧分布 */
}

.record {
  width: 120px;
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 100px; /* 图标大小 */
  color: #333; /* 图标颜色 */
  transition: transform 0.3s ease; /* 旋转动画 */
}

.record.playing {
  animation: spin 4s linear infinite; /* 旋转动画 */
}

@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}

.song-info {
  flex-grow: 1;
  margin-left: 20px;
  text-align: left;
}

.song-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.artist-name {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.6);
  margin: 5px 0 0;
}

.player-controls {
  display: flex;
  align-items: center;
}

button {
  background-color: transparent;
  border: none;
  color: #333;
  font-size: 24px;
  margin: 0 10px;
  cursor: pointer;
}

button:focus {
  outline: none;
}


.box3 {
  position: relative;
  background: linear-gradient(135deg, #e0f7da, #b2fab4);
  grid-column: span 1;
  grid-row: span 1;
  border-radius: 15px;
  display: flex;
  flex-direction: column; /* 使图案和文字垂直排列 */
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  text-align: center;
  padding: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}


.box3::before {
  content: '';
  position: absolute;
  top: -10px; 
  left: -10px;
  width: 90px; /* 调整图案大小以适配 box3 */
  height: 90px;
  background: url('@/assets/record-mood-icon.png') no-repeat center center; /* 更新图案路径 */
  background-size: contain; 
  border-radius: 50%; /* 图片变为圆形，增加柔和感 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 轻微阴影，增加立体感 */
  opacity: 0.9; 
  z-index: 0;
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.box3:hover {
  transform: scale(1.05); /* 鼠标悬停时，略微放大 */
  transform: translateY(-5px); 
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3); /* 悬停时增加阴影 */
}

.box3:hover::before {
  transform: scale(1.1); /* 图片在悬停时放大 */
  opacity: 1; /* 悬停时增加图片的清晰度 */
}

.box3::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 15px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  transition: border-color 0.3s ease;
}

.box3:hover::after {
  border-color: rgba(255, 255, 255, 0.8); /* 悬停时改变边框颜色 */
}



.box4 {
  position: relative;
  perspective: 1000px; /* 添加透视效果，创造3D空间 */
  width: 100%;
  height: 100%;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 轻柔阴影 */
  background: #f6a6b2;
  border-radius: 15px; /* 确保容器有圆角 */
}

.flip-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.6s;
  transform-style: preserve-3d; /* 保持3D翻转效果 */
  border-radius: 15px; /* 圆角效果 */
}

.box4:hover .flip-card-inner {
  transform: rotateY(180deg); /* 悬停时翻转 */
}

.flip-card-front, .flip-card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden; /* 隐藏背面内容 */
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 15px; 
}

.flip-card-front {
  background: #f6a6b2; /* 前面背景 */
  color: #fff;
  box-shadow: none; 
}

.flip-card-back {
  background: #f6a6b2; /* 反面背景 */
  color: #fff;
  transform: rotateY(180deg); /* 反面旋转180度 */
  box-shadow: none; 
  padding: 0; /* 去除内边距，确保图片完全填充 */
  overflow: hidden; /* 确保图片不会溢出容器 */
}

/* 调整图片的样式，确保自然填充 */
.mood-image {
  width: 100%; 
  height: 100%; 
  object-fit: cover; 
  border-radius: 15px; 
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); 
}


.box5 {
  background: linear-gradient(135deg, #ffd97d, #ffb347);
  grid-column: span 2;
  grid-row: span 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0;
  border-radius: 10px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  overflow: hidden;
}

.box5:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

/* 左侧梯形 */
.content-left {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 100%;
  background-color: #ffd97d;
  clip-path: polygon(0 0, 100% 0, 80% 100%, 0% 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
}

/* 右侧互补梯形 */
.content-right {
  position: absolute;
  top: 0;
  right: 0;
  width: 62%;
  height: 100%;
  clip-path: polygon(20% 0, 100% 0, 100% 100%, 0% 100%);
  overflow: hidden;
}

.news-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}



.box6 {
  background: linear-gradient(135deg, #a8d0e6, #84b3d0);
  grid-column: span 1;
  grid-row: span 1;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to  {
  opacity: 0;
}

</style>

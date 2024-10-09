<template>
  <div class="view-mood-container">

    <!-- 使用 CalendarNav 组件 -->
    <CalendarNav :selectedDate="selectedDate" @date-selected="selectDate" />

    <!-- 显示选定日期的心情记录 -->
    <div v-if="!moodRecords || moodRecords.length === 0" class="empty-message">这里空空如也~</div>

    <div v-else>
      <div v-for="record in moodRecords" :key="record.moodRecord.id" class="mood-record" @click="toggleExpand(record.moodRecord.id)">
        <!-- 显示时间和心情内容 -->
        <div class="mood-info">
          <p class="record-time">{{ formatDate(record.moodRecord.recordTime) }}</p>
          <p class="mood-text">{{ record.moodRecord.moodText }}</p>
        </div>

        <!-- 点击后显示额外信息 -->
        <transition name="expand">
          <div v-if="expandedRecordId === record.moodRecord.id" class="mood-details">
            <div class="weather-info">
              <div class="weather-status">
                <i :class="getWeatherIcon(record.moodRecord.weatherInfo.text)"></i>
                <span class="weather-text">{{ record.moodRecord.weatherInfo ? record.moodRecord.weatherInfo.text : '未知' }}</span>
              </div>
              <div class="weather-temp">
                {{ record.moodRecord.weatherInfo ? record.moodRecord.weatherInfo.temp.toFixed(1) : '未知' }}℃
              </div>
              <p class="weather-detail">{{ record.moodRecord.weatherInfo ? record.moodRecord.weatherInfo.detail : '无详细信息' }}</p>
            </div>

            <!-- 显示位置 -->
            <div class="location-info">
              <i class="fas fa-map-marker-alt"></i>
              <span class="location-text">{{ record.moodRecord.location ? record.moodRecord.location.location_msg : '未知位置' }}</span>
            </div>

            <!-- 图像记录文字 -->
            <div class="image-record-text">
              <span>🧩图像记录</span>
            </div>
            <!-- 如果有图片，显示所有图片 -->
            <div v-if="record.imageUrls && record.imageUrls.length > 0" class="image-gallery">
              <div v-for="(imageUrl, index) in record.imageUrls" :key="index" class="image-container">
                <img :src="getFullImageUrl(imageUrl)" alt="心情图片" class="mood-image" @click.stop="openImageModal(imageUrl)" />
              </div>
            </div>


            <!-- 显示情感倾向 -->
            <div class="mood-analysis">
              <div class="analysis-text">
                <p>{{ getSentimentByIndex(calculateMoodIndex(record.moodRecord.moodAnalysis)) }}</p>
                <!-- 心情指数显示进度条 -->
                <div class="sentiment-display">
                  <div class="positive-label">
                    <i class="fas fa-smile" style="color: #ff5722;"></i>
                    <span>积极</span>
                  </div>

                  <!-- 进度条 -->
                  <div class="sentiment-bar">
                    <div class="sentiment-progress" :style="{ width: calculateMoodIndex(record.moodRecord.moodAnalysis) + '%' }"></div>
                    <span class="sentiment-percentage">{{ calculateMoodIndex(record.moodRecord.moodAnalysis) }}%</span>
                  </div>

                  <div class="negative-label">
                    <i class="fas fa-frown" style="color: #2196f3;"></i>
                    <span>消极</span>
                  </div>
                </div>

                <!-- 显示用户当前的心情指数 -->
                <p class="mood-score">{{ calculateMoodIndex(record.moodRecord.moodAnalysis) }} / 100</p>

                <!-- 显示对应的情感鸡汤句子 -->
                <p class="mood-quote">{{ getMoodQuote(calculateMoodIndex(record.moodRecord.moodAnalysis)) }}</p>
              </div>
            </div>

            <!-- 删除按钮 -->
            <button class="delete-btn" @click.stop="confirmDelete(record.moodRecord.id)">删除记录</button>
          </div>
        </transition>
      </div>

    </div>

    <!-- 图片全屏弹窗 -->
    <div v-if="isModalVisible" class="modal" @click="closeModal">
      <div class="modal-content">
        <img :src="fullImageUrl" class="full-image" alt="全屏图片" />
        <button class="close-btn" @click="closeModal">×</button>
      </div>
    </div>


  </div>
</template>

<script>

import axios from 'axios';
import { format } from 'date-fns';
import CalendarNav from '@/components/CalendarNav.vue'; // 导入日历组件

export default {
  components: {
    CalendarNav
  },
  data() {
    return {
      moodRecords: [], // 初始化为空数组
      selectedDate: new Date(), // 当前选中的日期，默认为今天
      expandedRecordId: null, // 控制展开的记录 ID
      isModalVisible: false, // 控制弹窗显示
      fullImageUrl: '' // 当前全屏显示的图片 URL
    };
  },
  methods: {
    // 打开图片弹窗
    openImageModal(imageUrl) {
      this.fullImageUrl = this.getFullImageUrl(imageUrl);
      this.isModalVisible = true;
    },
    // 关闭弹窗
    closeModal() {
      this.isModalVisible = false;
      this.fullImageUrl = '';
    },

    // 计算心情指数的方法
    calculateMoodIndex(moodAnalysis) {
      const { sentiment, confidence, positiveProb, negativeProb } = moodAnalysis;

      // 假设心情指数基于置信度、积极概率、消极概率的加权总和
      let moodIndex = confidence * 0.5 + positiveProb * 0.3 - negativeProb * 0.2;

      // 根据情感倾向动态调整指数，正向情感增加比例，负向情感减少比例
      if (sentiment === 1) {
        moodIndex += confidence * 0.05;
      } else if (sentiment === -1) {
        moodIndex -= confidence * 0.05;
      }

      // 将 moodIndex 转换到 0-100 范围内，并保留两位小数
      moodIndex = moodIndex * 100;
      return Math.min(100, Math.max(0, moodIndex.toFixed(2)));
    },

    // 根据心情指数显示不同等级的情感倾向
    getSentimentByIndex(moodIndex) {
      if (moodIndex <= 16) return "感觉世界欠我一百万";
      else if (moodIndex <= 33) return "有点丧，但还能凑合";
      else if (moodIndex <= 50) return "心如止水，波澜不惊";
      else if (moodIndex <= 66) return "心情不错，有点小确幸";
      else if (moodIndex <= 83) return "情绪高涨，充满正能量";
      else return "心花怒放，今天谁都挡不住你的好心情！";
    },

    // 根据心情指数返回相应的鸡汤句子
    getMoodQuote(sentimentScore) {
      if (sentimentScore <= 16) return "再大的风雨，也阻挡不了前进的步伐！";
      else if (sentimentScore <= 33) return "不怕失败，继续努力，阳光终会到来。";
      else if (sentimentScore <= 50) return "生活充满了起起落落，但保持微笑，总有希望。";
      else if (sentimentScore <= 66) return "每一天都是一个新的开始，充满希望与挑战。";
      else if (sentimentScore <= 83) return "今天的你已经足够优秀，继续加油吧！";
      else return "阳光满满的一天，继续保持这样的好心情！";
    },

    getWeatherIcon(weatherText) {
      switch (weatherText) {
        case '晴':
          return 'fas fa-sun';  // 晴天
        case '多云':
          return 'fas fa-cloud';  // 多云
        case '阴':
          return 'fas fa-cloud-meatball';  // 阴天
        case '小雨':
          return 'fas fa-cloud-rain';  // 小雨
        case '中雨':
          return 'fas fa-cloud-showers-heavy';  // 中雨
        case '大雨':
          return 'fas fa-cloud-showers-heavy';  // 大雨
        case '雷阵雨':
          return 'fas fa-poo-storm';  // 雷阵雨
        case '阵雨':
          return 'fas fa-cloud-sun-rain';  // 阵雨
        case '小雪':
          return 'fas fa-snowflake';  // 小雪
        case '中雪':
          return 'fas fa-snowflake';  // 中雪
        case '大雪':
          return 'fas fa-snowman';  // 大雪
        case '雾':
          return 'fas fa-smog';  // 雾
        case '霾':
          return 'fas fa-wind';  // 霾
        case '冰雹':
          return 'fas fa-icicles';  // 冰雹
        case '风':
          return 'fas fa-wind';  // 风
        case '沙尘暴':
          return 'fas fa-cloud-meatball';  // 沙尘暴
        case '浮尘':
          return 'fas fa-wind';  // 浮尘
        default:
          return 'fas fa-question';  // 未知天气
      }
    },

    // 获取当前用户的心情记录
    async fetchMoodRecords(date) {
      try {
        const formattedDate = format(date, 'yyyy-MM-dd');
        const userId = localStorage.getItem('userId'); // 从 localStorage 获取用户 ID

        if (!userId) {
          alert('用户未登录');
          return;
        }

        const response = await axios.get(`https://124.222.156.13/api/mood/byDate`, {
          params: {
            date: formattedDate,
            userId: userId
          }
        });

        // 在这里打印 response.data
        console.log("Response Data:", response.data);

        // 按照时间从新到旧排序记录
        this.moodRecords = (response.data || []).map(record => ({
          ...record,
          moodRecord: record.moodRecord || {},
          imageUrls: record.imageUrls || [],
          aiResponse: record.aiResponse || '无 AI 回复'
        })).sort((a, b) => 
          new Date(b.moodRecord.recordTime) - new Date(a.moodRecord.recordTime)
        );


      } catch (error) {
        console.error("获取心情记录失败: ", error);
      }
    },

    // 确认删除心情记录
    confirmDelete(id) {
      if (confirm('确定要删除这条心情记录吗？')) {
        this.deleteMoodRecord(id);
      }
    },

    // 删除心情记录
    async deleteMoodRecord(id) {
      try {
        const userId = localStorage.getItem('userId'); // 获取用户 ID
        await axios.delete(`https://124.222.156.13/api/mood/delete/${id}`, {
          params: {
            userId: userId
          }
        });
        this.moodRecords = this.moodRecords.filter(record => record.moodRecord.id !== id); // 更新前端显示
      } catch (error) {
        console.error("删除记录失败: ", error);
      }
    },

    // 切换展开或收起记录
    toggleExpand(recordId) {
      this.expandedRecordId = this.expandedRecordId === recordId ? null : recordId;
    },

    // 选择日期并获取记录
    selectDate(date) {
      this.selectedDate = date;
      this.fetchMoodRecords(date);
    },

    // 格式化记录时间
    formatDate(dateStr) {
      return format(new Date(dateStr), 'yyyy年MM月dd日 HH:mm');
    },

    // 获取心情的情感倾向
    getSentiment(sentiment) {
      return sentiment === 1 ? '积极' : sentiment === -1 ? '消极' : '中性';
    },

    // 构建完整的图片 URL
    getFullImageUrl(imageUrl) {
      return `https://124.222.156.13/${imageUrl}`;
    }
  },

  // 页面加载时，自动获取用户的心情记录
  mounted() {
    const userId = localStorage.getItem('userId'); // 获取当前用户 ID

    if (!userId) {
      alert('用户未登录，请先登录');
      this.$router.push('/login'); // 如果没有用户 ID，重定向到登录页面
      return;
    }
    console.log("Current userId: ", userId); // 检查 userId 是否被正确获取

    this.fetchMoodRecords(this.selectedDate); // 加载当前日期的心情记录
  }
};
</script>


<style scoped>
.empty-message {
  color: #f5a623; /* 橙色 */
  font-family: 'Cinzel Decorative', serif;
  font-size: 18px;
  text-align: left; /* 左对齐 */
  margin-top: 20px; /* 添加一些间距 */
}

.view-mood-container {
  padding: 20px;
  background-color: #f9f9f9;
}

.mood-record {
  background: linear-gradient(135deg, #f5f7fa 0%, #d5f3f7 100%); /* 渐变背景 */
  border: none;
  border-radius: 15px; /* 更大圆角 */
  padding: 20px;
  margin-bottom: 20px;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1), 0 1px 4px rgba(0, 0, 0, 0.1); /* 更立体的阴影效果 */
  transition: all 0.3s ease; /* 过渡效果 */
}

.mood-record:hover {
  transform: translateY(-5px); /* 鼠标悬停时轻微上移 */
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2), 0 3px 8px rgba(0, 0, 0, 0.1); /* 加强悬浮时的阴影 */
}

.mood-info p {
  margin-bottom: 10px;
}

.mood-info .record-time {
  font-size: 14px; /* 缩小时间字体 */
  color: #999; /* 设置时间为较浅的颜色以弱化显示 */
  font-family: 'Cinzel Decorative', serif;
}

.mood-info .mood-text {
  font-size: 22px; /* 增大心情记录的字体 */
  font-weight: bold;
  color: #f5a623; /* 保持橙色 */
  font-family: 'Cinzel Decorative', serif;
}


.mood-details p {
  color: #f5a623;
  font-size: 16px;
  font-family: 'Cinzel Decorative', serif;
  line-height: 1.6;
}

.delete-btn {
  background-color: #ff4c4c;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 25px; /* 按钮也做圆角处理 */
  cursor: pointer;
  margin-top: 10px;
  box-shadow: 0px 4px 10px rgba(255, 76, 76, 0.4); /* 增加按钮的阴影效果 */
}

.delete-btn:hover {
  background-color: #ff1a1a;
  transform: scale(1.05); /* 鼠标悬浮时按钮略微放大 */
}

.mood-content {
  color: #555;
  font-size: 16px;
  margin-bottom: 15px;
}

/* 过渡效果 */
.expand-enter-active, .expand-leave-active {
  transition: all 0.3s ease;
}

.expand-enter, .expand-leave-to {
  transform: scaleY(0);
  opacity: 0;
}

.weather-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: linear-gradient(135deg, #f9f9f9 0%, #e1f1f7 100%);
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.weather-status {
  display: flex;
  align-items: center;
  font-size: 18px;
  color: #b4b8b3; 
  margin-bottom: 10px;
}

.weather-status i {
  margin-right: 8px;
  font-size: 30px;
  color: #f3f8f7;
}

.weather-temp {
  font-size: 36px;
  font-weight: bold;
  background: -webkit-linear-gradient(#cdeef1, #67e8dd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 10px;
}

.weather-detail {
  font-size: 14px;
  color: #666;
  padding: 8px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  text-align: center;
}

.location-info {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #4a90e2; /* 位置的文字颜色 */
  margin-top: 10px;
}

.location-info i {
  margin-right: 8px;
  font-size: 20px; /* 图标稍大 */
  color: #e74c3c; /* 图标的颜色设为红色 */
}

.location-text {
  font-family: 'Cinzel Decorative', serif; /* 保持字体风格一致 */
}

.mood-analysis {
  text-align: center;
  margin: 20px 0;
}

.sentiment-display {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.positive-label,
.negative-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #888;
}

.positive-label span,
.negative-label span {
  margin-left: 5px;
}

.sentiment-bar {
  position: relative;
  flex-grow: 1;
  height: 12px;
  background-color: #e0e0e0;
  border-radius: 6px;
  margin: 0 10px;
  overflow: hidden;
}

.sentiment-progress {
  height: 100%;
  background-color:  #f5a623;
}

.sentiment-percentage {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  color: #333;
}

.mood-score {
  font-size: 16px;
  margin-top: 10px;
  color: #333;
}

.mood-quote {
  color: #888888 !important; 
  font-size: 12px; 
  font-style: italic; 
  margin-top: 10px; 
  text-align: left; 
}

.image-gallery {
  display: flex;
  flex-wrap: wrap;
  margin-top: 15px;
  justify-content: start; /* 确保图片左对齐 */
}

.image-container {
  margin-right: 10px;
  margin-bottom: 10px;
  width: 150px; /* 调整容器的宽度，适应图片大小 */
  height: 150px; /* 调整容器的高度 */
}

.mood-image {
  width: 100%; /* 让图片宽度占满容器 */
  height: 100%; /* 让图片高度占满容器 */
  object-fit: cover; /* 保持图片比例，不拉伸变形 */
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.image-record-text {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #34e8be;
  margin-top: 10px;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  position: relative;
}

.full-image {
  max-width: 90vw;
  max-height: 90vh;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 20px;
  background: none;
  border: none;
  font-size: 30px;
  color: white;
  cursor: pointer;
}



</style>

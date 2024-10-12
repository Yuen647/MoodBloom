<template>
  <div class="app-body">
    <div class="container">
      <div v-for="(news, index) in newsList.slice(0, 7)" :key="index" :class="'box box' + (index + 1)" @click="openNews(index)">
        <img :src="news.thumbnailPicS || require('@/assets/default.png')" :alt="'新闻' + (index + 1)" class="news-image">

        <div class="news-content">
          <h2 class="news-title">{{ news.title || '新闻标题 ' + (index + 1) }}</h2>
          <p class="news-snippet">{{ news.authorName || '新闻摘要内容 ' + (index + 1) }}，点击阅读更多。</p>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import axios from 'axios';

export default {
  name: 'NewsLayout',
  data() {
    return {
      newsList: []  // 用于存储最新的7条新闻
    };
  },
  methods: {
    // 先调用 /fetch 获取并保存新闻数据
    fetchAndSaveNews() {
      axios.get(`https://124.222.156.13/api/news/fetch`)  // 调用后端的 fetch 接口
        .then(() => {
          // 获取成功后，再调用获取最新新闻的接口
          this.fetchLatestNews();
        })
        .catch(error => {
          console.error("获取新闻数据时发生错误: ", error);
        });
    },
    // 获取最新的7条新闻
    fetchLatestNews() {
      axios.get('https://124.222.156.13/api/news/latest')  // 调用后端的 latest 接口
        .then(response => {
          this.newsList = response.data;  // 将最新新闻数据绑定到 newsList
        })
        .catch(error => {
          console.error("获取最新新闻时发生错误: ", error);
        });
    },
    openNews(index) {
      const news = this.newsList[index];
      if (news && news.url) {
        window.open(news.url, '_blank');  // 打开新闻的链接
      } else {
        console.log('无法打开新闻，URL 不存在');
      }
    }
  },
  mounted() {
    // 在组件挂载时，先获取并保存新闻数据，然后展示最新7条新闻
    this.fetchAndSaveNews();
  }
};
</script>


<style scoped>
/* 导入字体 */
@import url('https://fonts.googleapis.com/css?family=Roboto:400,700&display=swap');

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html, body {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.app-body {
  font-family: 'Roboto', sans-serif;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f4f4f4;
  display: flex;
  justify-content: center;
  align-items: center;
}

.container {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr; /* 三列 */
  grid-template-rows: auto;
  grid-template-areas:
    "box1 box1 box2"
    "box3 box4 box5"
    "box6 box7 box7";
  gap: 20px;
  padding: 20px;
  width: 90vw;
  max-width: 1200px;
  max-height: 90vh;
  overflow-y: auto;
}

.box {
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;
}

.box:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.news-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.news-content {
  padding: 15px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.news-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.news-snippet {
  font-size: 14px;
  color: #666;
  flex-grow: 1;
}

.news-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.news-title, .news-snippet {
  word-wrap: break-word; /* 确保长文本不会溢出 */
}

.news-snippet {
  font-size: 14px;
  color: #666;
  flex-grow: 1;
}

.box1 {
  grid-area: box1;
}

.box2 {
  grid-area: box2;
}

.box3 {
  grid-area: box3;
}

.box4 {
  grid-area: box4;
}

.box5 {
  grid-area: box5;
}

.box6 {
  grid-area: box6;
}

.box7 {
  grid-area: box7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    grid-template-columns: 1fr 1fr;
    grid-template-areas:
      "box1 box1"
      "box2 box2"
      "box3 box4"
      "box5 box6"
      "box7 box7";
  }
}
</style>

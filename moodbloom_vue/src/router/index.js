import { createRouter, createWebHistory } from 'vue-router';
import LoginAndRegister from '../views/LoginAndRegister.vue';//登陆注册页面
import HomePage from '../views/HomePage.vue';//主页面
import RecordMood from '../views/RecordMood.vue'; // 心情记录页面
import ViewMood from '../views/ViewMood.vue'; // 查看心情页面
import NewsPage from '../views/NewsPage.vue';//查看每日头条界面


const routes = [
  {
    path: '/',
    name: 'LoginAndRegister',
    component: LoginAndRegister,
    meta:{title:'欢迎加入'}
  },
  {
    path: '/HomePage',
    name: 'HomePage',
    component: HomePage,
    meta:{title:'首页'}
  },
  {
    path: '/RecordMood',
    name: 'RecordMood',
    component: RecordMood,
    meta:{title:'记录'}
  },
  {
    path: '/ViewMood',
    name: 'ViewMood',
    component: ViewMood,
    meta:{title:'回顾'}
  },
  {
    path: '/NewsPage',
    name: 'NewsPage',
    component: NewsPage,
    meta:{title:'今日头条'}
  }
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
  });
  
  export default router;

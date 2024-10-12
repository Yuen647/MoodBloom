// src/config.js
const config = {
    apiBaseUrl: process.env.NODE_ENV === 'production'
      ? 'https://124.222.156.13'  // 生产环境的基础 URL
      : 'https://124.222.156.13'  // 开发环境的基础 URL
  };
  
  export default config;
  
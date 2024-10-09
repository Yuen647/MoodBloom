const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/api': {
        target: 'https://124.222.156.13', // 目标服务器
        changeOrigin: true, // 修改源
        secure: false, // 如果是 HTTPS，请设置为 false
      },
    },
  },
})





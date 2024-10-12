<template>
  <div class="view-mood-container">
    <!-- 日历导航 -->
    <CalendarNav :selectedDate="selectedDate" @date-selected="selectDate" />

    <!-- 已记录心情列表 -->
    <div v-if="moodRecords.length" class="timeline-container">
      <div v-for="moodRecord in moodRecords" :key="moodRecord.moodRecord.id" class="timeline-item">
        <!-- 时间 -->
        <div class="timeline-time">{{ formatTime(moodRecord.moodRecord.recordTime) }}</div>

        <div class="mood-record-container">

          <!-- 心情记录 -->
          <div class="mood-record">
            <p class="record-title">{{ moodRecord.moodRecord.moodText }}</p>
          </div>

          <!-- 图片展示 -->
          <div v-if="moodRecord.imageUrls && moodRecord.imageUrls.length > 0" class="image-gallery">
            <div v-for="(imageUrl, index) in moodRecord.imageUrls" :key="index" class="image-container">
              <img :src="getFullImageUrl(imageUrl)" alt="心情图片" class="mood-image" />
            </div>
          </div>

          <!-- AI 回复 -->
          <div class="ai-response">
            <div class="bubble-connector"></div> <!-- 虚线连接器 -->
            <div class="ai-bubble">
              <i class="fas fa-lightbulb ai-icon"></i> <!-- 添加 ai-icon 类 -->

              <div class="ai-text">
                <p>{{ moodRecord.aiResponse }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty-message">
      <p>这里空空如也~</p>
    </div>

    <!-- 心情记录表单 -->
    <div class="mood-input-container">
      
      <!-- 图片预览区域 -->
      <div v-if="selectedImages.length" class="image-preview-container">
        <div v-for="(image, index) in selectedImages" :key="index" class="image-preview">
          <img :src="image.previewUrl" class="preview-image" alt="心情图片" />
          <!-- 删除按钮 -->
          <button @click="removeSelectedImage(index)" class="remove-button">×</button>
        </div>
      </div>

      <!-- 输入框和按钮 -->
      <div class="input-and-buttons">
        <!-- 图片上传按钮 -->
        <div class="icon-button">
          <label for="image-upload">
            <i class="fas fa-image"></i> <!-- 添加图片图标 -->
          </label>
          <input type="file" id="image-upload" @change="handleImageUpload" accept="image/*" style="display: none;" multiple />
        </div>

        <!-- 生成图片按钮 -->
        <div class="icon-button" @click="openModal">
          <i class="fas fa-paint-brush"></i> <!-- 新增的图标 -->
        </div>

        <!-- 输入框 -->
        <input type="text" v-model="newMoodText" placeholder="想记些什么呢？" />

        <!-- 发送按钮 -->
        <div class="icon-button" @click="addMoodRecord">
          <i class="fas fa-paper-plane"></i> <!-- 纸飞机图标 -->
        </div>
      </div>
    </div>


    <!-- 新增的模态框 -->
    <div v-if="showModal" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <h3>发挥你的阳光积极的想象力！！</h3>
        <input type="text" v-model="inputText" placeholder="请输入描述文本">
        <button @click="generateImage">发送</button>
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
      newMoodText: '', // 新的心情文本
      selectedImages: [] ,// 存储用户选中的图片文件数组
      showModal: false, // 控制模态框显示
      inputText: '', // 存储用户输入的文本
    };
  },
  methods: {
    // 打开模态框
    openModal() {
      this.showModal = true;
    },
    // 关闭模态框
    closeModal() {
      this.showModal = false;
    },
  // 生成图片并打开图片链接
  async generateImage() {
    try {
      // 用户点击发送时生成图片
      const response = await fetch(`https://124.222.156.13/api/txt2img/generate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ text: this.inputText })
      });

      // 检查响应状态码
      if (!response.ok) {
        // 如果状态码不是 2xx，显示友好的提示
        console.error("生成图片失败，状态码: ", response.status);
        alert("哎呀，换一张呢！");
        return;
      }

      // 获取图片链接
      const imageUrl = await response.text();
      console.log("Generated Image URL:", imageUrl);

      if (!imageUrl) {
        // 如果未能成功获取到图片链接，给出友好提示
        alert("哎呀，换一张呢！");
        return;
      }

      // 打开图片链接
      window.open(imageUrl, '_blank');

      // 关闭模态框
      this.showModal = false;
    } catch (error) {
      // 捕获异常并给出友好提示
      console.error("生成图片失败: ", error);
      alert("哎呀，换一张呢！");
    }
  },
    

    // 获取指定日期的心情记录及其AI回复
    async fetchMoodRecords(date) {
      try {
        const formattedDate = format(date, 'yyyy-MM-dd');
        const userId = localStorage.getItem('userId'); 
        const response = await axios.get(`https://124.222.156.13/api/mood/byDate?date=${formattedDate}&userId=${userId}&includeAIResponses=true`);
        
        // 如果有数据，按照recordTime从新到旧进行排序
        this.moodRecords = (response.data || []).sort((a, b) => {
          return new Date(b.moodRecord.recordTime) - new Date(a.moodRecord.recordTime); // 从新到旧排序
        });
      } catch (error) {
        console.error("获取心情记录失败: ", error);
      }
    },
    // 选择日期并获取记录
    selectDate(date) {
      this.selectedDate = date;
      this.fetchMoodRecords(date);
    },
    // 格式化时间为小时和分钟
    formatTime(dateStr) {
      return format(new Date(dateStr), 'HH:mm');
    },

    // 添加心情记录
    async addMoodRecord() {
      // 验证用户是否输入内容或选择了图片
      if (!this.newMoodText.trim() && this.selectedImages.length === 0) {
        alert("记录生活~");
        return;
      }

      try {
        const userId = localStorage.getItem('userId');
        const formData = new FormData();
        formData.append('userId', userId);
        formData.append('moodText', this.newMoodText);

        // 确保所有图片都使用相同的字段名 'images'
        this.selectedImages.forEach((image) => {
          formData.append('images', image.file);  // 所有图片都用 'images' 作为键
        });

        const response = await axios.post('https://124.222.156.13/api/mood/add', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });

        // 将新记录插入数组的顶部
        this.moodRecords.unshift({
          moodRecord: response.data.moodRecord,
          aiResponse: response.data.aiResponse
        });
        
        // 清空输入框和图片预览
        this.newMoodText = '';
        this.selectedImages = [];
        document.getElementById('image-upload').value = ''; // 清空文件输入框
      } catch (error) {
        console.error("添加心情记录失败: ", error);
      }
    },



    handleImageUpload(event) {
      const files = Array.from(event.target.files);
      
      // 调试信息
      console.log("Selected files:", files);
      
      const totalImagesAfterUpload = this.selectedImages.length + files.length;

      if (totalImagesAfterUpload > 3) {
        alert("最多只能上传三张图片。");
        return; // 超过三张图片，停止执行并给出提示
      }

      files.forEach(file => {
        if (file) {
          const reader = new FileReader();
          reader.onload = (e) => {
            console.log("Image preview URL:", e.target.result); // 检查预览 URL
            this.selectedImages.push({ file: file, previewUrl: e.target.result }); // 将文件和预览URL加入数组
          };
          reader.readAsDataURL(file); // 异步读取文件内容并生成图片预览
        }
      });

      // 调试信息：查看 `selectedImages` 数组是否正确更新
      console.log("Updated selectedImages:", this.selectedImages);
    },



    removeSelectedImage(index) {
      this.selectedImages.splice(index, 1); // 根据索引删除选中的图片
    },


    getFullImageUrl(imageUrl) {
      return `https://124.222.156.13${imageUrl}`; 
    },

  },
  mounted() {
    this.fetchMoodRecords(this.selectedDate); // 初始加载当前日期的心情记录
  }
};
</script>

<style scoped>
.view-mood-container {
  padding: 20px;
  background-color: #f9f9f9;
}

.timeline-container {
  display: flex;
  flex-direction: column;
  position: relative;
  padding-left: 40px;
  border-left: 2px dashed #999;
  padding-bottom: 50px; 
}

.timeline-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  position: relative;
}

.timeline-time {
  position: absolute;
  left: -60px;
  font-size: 16px;
  color: #f5a623;
  font-weight: bold;
  margin-top: 10px;
  background-color: #f9f9f9; 
  z-index: 1;
  padding: 8px 10px;
  line-height: 2; 
}

.mood-record-container {
  display: flex;
  flex-direction: column;
  margin-left: 10px;
  padding: 15px;
  background: linear-gradient(135deg, #f9f9f9, #f1f1f1); /* 渐变背景 */
  border: 1px solid #ddd;
  border-radius: 20px;
  margin-bottom: 10px;
  z-index: 0;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 添加阴影 */
  transition: transform 0.3s ease, box-shadow 0.3s ease; /* 添加过渡效果 */
}

.mood-record-container:hover {
  transform: scale(1.01); /* 鼠标悬停时放大效果 */
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 鼠标悬停时增加阴影 */
  border-color: #ccc; /* 改变边框颜色 */
}


.mood-record {
  font-weight: bold;
  font-size: 16px;
  color: #333;
  z-index: 2;
}

.ai-response {
  position: relative;
  margin-top: 10px;
}

.bubble-connector {
  width: 2px;
  height: 20px;
  background: #ddd;
  margin-left: 20px;
  margin-bottom: -10px;
}

.ai-bubble {
  background-color: #f9f9f9;
  border: 2px solid #f5a623;
  border-radius: 15px;
  padding: 10px;
  display: flex;
  align-items: center;
  position: relative;
  margin-left: 40px;
  width: auto; 
}

.ai-bubble:before {
  content: "";
  position: absolute;
  left: -10px;
  top: 15px;
  width: 0;
  height: 0;
  border-top: 10px solid transparent;
  border-right: 10px solid #f9f9f9;
  border-bottom: 10px solid transparent;
}

.ai-response i {
  color: #f5a623;
  margin-right: 10px;
  font-size: 18px;
}

.ai-text {
  font-size: 14px;
  color: #555;
}

.ai-icon {
  color: #f5a623; 
  font-size: 18px; 
  margin-right: 10px; 
}
/* ========================= */
/* 图片展示区域 */
.image-gallery {
  display: flex;
  flex-wrap: wrap; /* 允许图片换行 */
  gap: 10px; /* 图片之间的间距 */
  margin-top: 10px;
}

/* 图片容器，控制图片大小 */
.image-container {
  width: 120px; /* 调整为合适的宽度 */
  height: 120px; /* 调整为合适的高度 */
  overflow: hidden;
  border-radius: 10px; /* 圆角 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 添加轻微的阴影 */
}

/* 图片本身 */
.mood-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保持图片比例，填充容器 */
}
/* ========================= */
.mood-input-container {
  position: fixed;
  bottom: 0;
  width: 100%;
  display: flex;
  flex-direction: column; /* 图片预览在输入框上方 */
  align-items: flex-start; /* 左对齐 */
  padding: 10px 15px;
  background-color: #fff;
  border-top: 2px solid #f9a32c;
  z-index: 2;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.image-preview-container {
  display: flex;
  flex-wrap: wrap; /* 图片换行显示 */
  justify-content: flex-start; /* 左对齐 */
  width: 100%;
  margin-bottom: 10px;
}

.image-preview {
  margin-right: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  overflow: hidden;
  width: 70px;
  height: 70px;
  position: relative;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-button {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: transparent; /* 透明背景 */
  border: none; /* 无边框 */
  color: rgb(181, 177, 177); /* 浅灰色字体 */
  font-size: 18px; /* 设置合适的字体大小 */
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.remove-button:hover {
  color: rgb(131, 128, 128); /* 鼠标悬停时颜色加深 */
}


.input-and-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 95%;
  gap: 5px;
}

input[type="text"] {
  flex-grow: 1;
  padding: 10px 15px;
  font-size: 16px;
  border: none;
  border-radius: 20px;
  background-color: #f5f5f5;
  outline: none;
  width: auto;
  max-width: 80%;
}

.icon-button {
  width: 45px;
  height: 45px;
  background-color: #f5a623;
  color: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

/* 模态框的背景遮罩 */
.modal {
  display: none; /* 默认隐藏，只有在需要时显示 */
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6); /* 增强透明度，使背景更暗 */
  backdrop-filter: blur(5px); /* 增加背景模糊效果 */
  display: flex;
  justify-content: center;
  align-items: center;
  animation: fadeIn 0.3s ease; /* 增加渐入动画 */
}

/* 弹窗内容 */
.modal-content {
  background-color: white;
  border-radius: 10px; /* 圆角效果 */
  padding: 30px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 增加阴影 */
  position: relative;
  animation: slideIn 0.3s ease; /* 增加滑入动画 */
}

/* 关闭按钮 */
.close {
  position: absolute;
  top: 15px;
  right: 15px;
  color: #aaa;
  font-size: 28px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.close:hover,
.close:focus {
  color: #333;
}

/* 输入框 */
.modal-content input[type="text"] {
  width: 100%;
  padding: 12px;
  margin-top: 20px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  transition: border 0.3s ease;
}

.modal-content input[type="text"]:focus {
  border-color: #f5a623;
  outline: none;
}

/* 发送按钮 */
.modal-content button {
  display: block;
  width: 20%;
  padding: 12px;
  margin-top: 20px;
  background-color: #f5a623;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.modal-content button:hover {
  background-color: #e5941c;
}

/* 渐入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 弹窗内容滑入动画 */
@keyframes slideIn {
  from {
    transform: translateY(-50px);
  }
  to {
    transform: translateY(0);
  }
}


</style>

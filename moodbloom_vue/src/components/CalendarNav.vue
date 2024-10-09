<template>
  <div class="calendar-nav-container">
    <!-- 显示当前选中日期并允许用户修改 -->
    <div class="date-input">
      <span class="selected-date" v-html="formatFullDate(selectedDate)" @click="editDate"></span>

      <!-- 日期输入框，点击显示，选择完日期后隐藏 -->
      <input 
        v-if="isEditing" 
        type="date" 
        id="datePicker" 
        v-model="inputDate" 
        @change="updateSelectedDate" 
        @blur="isEditing = false" 
      />
    </div>

    <!-- 横向日历导航 -->
    <div class="calendar-nav">
      <button @click="previousWeek" class="arrow-btn">
        <i class="fas fa-chevron-left"></i>
      </button>
      <div class="date-list">
        <div v-for="date in displayedDates" :key="date" class="date-item" @click="selectDate(date)">
          <span :class="{ selected: selectedDate === date }">{{ formatDateDisplay(date) }}</span>
        </div>
      </div>
      <button @click="nextWeek" class="arrow-btn">
        <i class="fas fa-chevron-right"></i>
      </button>
    </div>
  </div>
</template>

<script>
import { format, addDays, parseISO } from 'date-fns';

export default {
  props: {
    selectedDate: {
      type: Date,
      required: true
    }
  },
  emits: ['date-selected'],
  data() {
    return {
      isEditing: false, // 控制输入框的显示与隐藏
      inputDate: format(this.selectedDate, 'yyyy-MM-dd'), // 输入框的日期
      displayedDates: Array.from({ length: 7 }, (_, i) => addDays(new Date(), i)) // 默认显示7天
    };
  },
  methods: {
    // 选择日期
    selectDate(date) {
      this.$emit('date-selected', date); // 触发事件通知父组件选中的日期
    },
    // 上一周
    previousWeek() {
      this.updateDisplayedDates(-7);
    },
    // 下一周
    nextWeek() {
      this.updateDisplayedDates(7);
    },
    // 更新显示的日期列表
    updateDisplayedDates(offset) {
      const startDate = addDays(this.displayedDates[0], offset);
      this.displayedDates = Array.from({ length: 7 }, (_, i) => addDays(startDate, i));
    },
    // 当用户输入日期时更新选中的日期
    updateSelectedDate() {
      const parsedDate = parseISO(this.inputDate); // 将输入的日期字符串解析为日期对象
      this.selectDate(parsedDate); // 调用选择日期方法
      this.displayedDates = Array.from({ length: 7 }, (_, i) => addDays(parsedDate, i)); // 更新显示的日期
      this.isEditing = false; // 关闭编辑模式
    },
    // 启动编辑模式，显示日期输入框
    editDate() {
      this.isEditing = true;
    },
    // 格式化显示日期
    formatFullDate(date) {
      const year = format(date, 'yyyy');
      const month = format(date, 'M');
      const day = format(date, 'd');

      return `<span class="small-date">${year}</span> 
              <span class="large-date">${month}</span><span class="small-date">月</span> 
              <span class="large-date">${day}</span><span class="small-date">日</span>`;
    },
    // 格式化日期显示，只显示日
    formatDateDisplay(date) {
      return format(date, 'd');
    }
  },
  watch: {
    selectedDate(newDate) {
      this.inputDate = format(newDate, 'yyyy-MM-dd'); // 当父组件更新选中的日期时，更新输入框
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel+Decorative:wght@400;700&display=swap');

.calendar-nav-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.date-input {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #333;
  justify-content: flex-start;
  width: 90%;
}

.selected-date {
  cursor: pointer;
  color: #f5a623; /* 橙色 */
  font-family: 'Cinzel Decorative', serif; 
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: left;
  width: 20%;
}

.small-date {
  font-size: 16px !important;
  font-weight: normal;
  font-family: 'Cinzel Decorative', serif; /* 确保小字体部分也用同样风格 */
}

.large-date {
  font-size: 40px !important; /* 放大月和日以突出 */
  font-weight: bold;
  font-family: 'Cinzel Decorative', serif; /* 中世纪风格 */
}

input[type="date"] {
  margin-left: 10px;
  font-size: 16px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

.calendar-nav {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 改为两端对齐，防止按钮遮挡 */
  width: 100%; /* 确保导航栏充满父容器 */
  position: relative; /* 为左右按钮留出空间 */
}

.arrow-btn {
  background: none;
  border: none;
  font-size: 30px;
  cursor: pointer;
  color: #f5a623;
  padding: 5px;
  z-index: 1;
}

.arrow-btn:hover {
  transform: scale(1.1);
  z-index: 2;
}

.date-list {
  display: flex;
  gap: 15px;
}

.date-item {
  padding: 10px;
  cursor: pointer;
  border-radius: 12px;
  background-color: rgba(245, 245, 245, 0.8);
  text-align: center;
  width: 50px;
  height: 50px;
  line-height: 50px;
  transition: background-color 0.3s, transform 0.3s;
  font-size: 18px;
  color: #f5a623;
  margin: 0 10px; 
}

.date-item:hover {
  background-color: #ffe4c4;
  transform: scale(1.05);
  z-index: 1;
}
</style>

<style>
.small-date {
  font-size: 24px;
  font-weight: normal;
  font-family: 'Cinzel Decorative', serif;
}

.large-date {
  font-size: 60px;
  /* font-weight: bold; */
  font-family: 'Cinzel Decorative', serif;
}
</style>


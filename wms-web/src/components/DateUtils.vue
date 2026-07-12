<template>
  <div class="date-time-container">
    <span class="time" id="time">
      <span class="date">{{ nowTime }}</span>
      <span class="hour">{{ time.hour }}</span>
      <a class="split">:</a>
      <span class="minutes">{{ time.minutes }}</span>
      <a class="split">:</a>
      <span class="seconds">{{ time.seconds }}</span>
    </span>
  </div>
</template>

<script>
export default {
  name: "DateUtils",
  props: ["s"],
  data() {
    return {
      // 定时器ID，用于组件销毁时清除，防止内存泄漏
      timer: null,
      time: {
        hour: "",
        // 修复原代码拼写错误 minitus → minutes
        minutes: "",
        seconds: ""
      },
      nowTime: "",
      week: [
        "星期天",
        "星期一",
        "星期二",
        "星期三",
        "星期四",
        "星期五",
        "星期六"
      ]
    };
  },
  mounted() {
    // 组件挂载后启动时间更新
    this.dateTime();
  },
  beforeDestroy() {
    // 组件销毁前清除定时器，必须加！否则页面切换后定时器仍在运行
    if (this.timer) {
      clearTimeout(this.timer);
    }
  },
  methods: {
    // 时间递归更新方法
    dateTime() {
      this.timeFormat();
      // 修复原代码语法错误，移除错误的 handler: 标识
      this.timer = setTimeout(() => {
        this.dateTime();
      }, 1000);
    },
    // 【补全的核心方法】时间格式化，处理年月日、星期、时分秒
    timeFormat() {
      const now = new Date();
      // 1. 处理年月日 + 星期
      const year = now.getFullYear();
      const month = this.getIncrease(now.getMonth() + 1, 2);
      const day = this.getIncrease(now.getDate(), 2);
      const weekDay = this.week[now.getDay()];
      // 拼接日期字符串，可根据需求修改格式
      this.nowTime = `${year}年${month}月${day}日 ${weekDay}`;

      // 2. 处理时分秒，补零后赋值
      this.time.hour = this.getIncrease(now.getHours(), 2);
      this.time.minutes = this.getIncrease(now.getMinutes(), 2);
      this.time.seconds = this.getIncrease(now.getSeconds(), 2);
    },
    // 数字补零方法（原代码保留）
    getIncrease(num, digit) {
      var increase = "";
      for (var i = 0; i < digit; i++) {
        increase += "0";
      }
      return (increase + num).slice(-digit);
    }
  }
};
</script>

<style scoped>

.time {
  font-family: 'Segoe UI', Roboto, Helvetica Neue, Arial, sans-serif;
  font-size: 18px;
  font-weight: 500;
  color: #e5e7eb;
  background: #111827;
  padding: 6px 14px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.date {
  color: #9ca3af;
}
.hour, .minutes, .seconds {
  color: #f3f4f6;
}

/* 容器样式，可根据你的页面布局调整 */
.date-time-container {
  display: inline-block;
  line-height: 1;
}
.txt-data .time {
  font-size: 1rem;
  margin-right: 0.5rem;
}



</style>
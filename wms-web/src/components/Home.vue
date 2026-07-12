<script>
 import DateUtils from "@/components/DateUtils.vue";
 // 引入 ECharts
 import * as echarts from 'echarts';

 export default {
   name:"Home",
   components: {DateUtils},
   data() {
     return{
       user : JSON.parse(sessionStorage.getItem("CurUser")),
       roleMap: {
         0: { type: 'danger', text: '超级管理员' },
         1: { type: 'primary', text: '管理员' },
         2: { type: 'success', text: '普通用户' },
         3: { type: 'warning', text: '访客' }
         // 可以继续扩展更多值
       },
       sexMap: {
         0: { type: 'danger', text: '女' },
         1: { type: 'primary', text: '男' }
       },
       validMap: {
         'N' : {type:'danger',text:'账号状态异常'},
         'Y' : {type:'success',text:'账号状态正常'},
       },
       myChart: null // 图表实例
     }
   },
   mounted() {
     this.initChart() // 页面加载完成后初始化图表
   },
   methods: {
     // 初始化近7日交易数据折线图
     initChart() {
       this.myChart = echarts.init(document.getElementById('tradeChart'));

       // 近7日日期
       const dateList = ['6日前', '5日前', '4日前', '3日前', '2日前', '昨日', '今日'];
       // 模拟近7日交易数量（可后期对接后端真实数据）
       const dataList = [12, 20, 8, 32, 19, 25, 38];

       this.myChart.setOption({
         title: {
           text: '近7日交易数据统计',
           left: 'center',
           textStyle: { fontSize: 16 }
         },
         tooltip: { trigger: 'axis' },
         grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
         xAxis: {
           type: 'category',
           data: dateList,
           axisLabel: { fontSize: 12 }
         },
         yAxis: { type: 'value' },
         series: [
           {
             name: '交易次数',
             type: 'line',
             data: dataList,
             smooth: true,
             lineStyle: { width: 3 },
             itemStyle: { color: '#409EFF' }
           }
         ]
       });
     }
   },
   beforeDestroy() {
     window.removeEventListener('resize', this.resizeChart);
   },
 }

</script>

<template>
  <div>
    <h1 style="text-align: center">欢迎你！{{ user.name }}</h1>
    <el-descriptions title="个人中心" direction="vertical" :column="2" border>
      <el-descriptions-item label="账号">{{user.no}}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{user.name}}</el-descriptions-item>
      <el-descriptions-item label="性别">
        <el-tag :type="sexMap[user.sex].type" >{{sexMap[user.sex].text}}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="电话">{{user.phone}}</el-descriptions-item>
      <el-descriptions-item label="权限">
        <el-tag :type="roleMap[user.roleId].type" size="small">{{roleMap[user.roleId].text}}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="账号状态">
        <el-tag :type="validMap[user.isvalid].type" size="small">{{validMap[user.isvalid].text}}</el-tag>
      </el-descriptions-item>
    </el-descriptions>



    <!-- ===================== 新增：近7日交易折线图 ===================== -->
    <div style="margin-top: 20px">
      <el-card shadow="hover">
        <div id="tradeChart" style="width: 100%; height: 400px"></div>
      </el-card>
    </div>
    <!-- =============================================================== -->


    <div style="margin-top: 20px;">
      <el-row :gutter="24">
      <el-col :span="24">
      <el-card shadow="hover" style="width: 100%;">
        <div slot="header" class="clearfix">
          <span style="font-size: 26px; font-weight: bold; margin-left: 3%;">文嘉《明日歌》</span>
<!--          <el-button-->
<!--              style="float: right; padding: 3px 0"-->
<!--              type="text"-->
<!--              @click="clickFn"-->
<!--          >暂停</el-button-->
<!--          >-->
        </div>
        <div style="font-size: 26px; font-weight: bold;text-align: center; margin-top: 35px;">
          明日复明日
        </div>
        <div style="font-size: 26px; font-weight: bold;text-align: center;">明日何其多</div>
        <div style="font-size: 26px; font-weight: bold;text-align: center;">我生待明日</div>
        <div style="font-size: 26px; font-weight: bold;text-align: center;">万事成蹉跎</div>
        <div style="margin-top: 35px;"></div>

        <DateUtils style="margin-top: 20px; display: block; text-align: center"></DateUtils>
      </el-card>
    </el-col>
      </el-row>
    </div>

  </div>
</template>

<style scoped>

</style>
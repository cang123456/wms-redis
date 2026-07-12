<script>
import Header from "@/components/Header.vue";
import Aside from "@/components/Aside.vue";

export default {
  name: "AdminShop",
  data() {
    return {
      tableData: [],
      pageSize: 8,
      pageNum: 1,
      total: 0,

      // 查询条件
      orderNo: "",
      orderStatus: "",
    };
  },
  methods: {
    // 加载订单列表
    loadOrderList() {
      this.$axios.post(this.$httpUrl + "/order/listPage", {
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        param: {
          orderNo: this.orderNo,
          orderStatus: this.orderStatus
        }
      }).then(res => res.data).then(res => {
        console.log("订单列表", res);
        if (res.code === 200) {
          this.tableData = res.data;
          this.total = res.total;
        } else {
          this.$message.error("加载失败");
        }
      });
    },

    // 分页
    handleSizeChange(val) {
      this.pageSize = val;
      this.loadOrderList();
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.loadOrderList();
    },

    // 重置查询
    resetParam() {
      this.orderNo = "";
      this.orderStatus = "";
      this.loadOrderList();
    },

    // 订单状态文字
    getStatusText(status) {
      const map = {
        1: "待支付",
        2: "已支付",
        3: "已出库",
        4: "已完成",
        5: "已取消"
      };
      return map[status] || "未知";
    },

    // 订单状态标签颜色
    getStatusType(status) {
      const map = {
        1: "info",
        2: "success",
        3: "primary",
        4: "success",
        5: "danger"
      };
      return map[status] || "";
    },

    // ===================== 订单操作 =====================
    // 支付
    payed(orderNo) {
      this.$confirm("确认该订单已支付？", "提示").then(() => {
        this.$axios.post(this.$httpUrl + "/order/payed?orderNo=" + orderNo)
            .then(res => {
              this.$message.success("操作成功");
              this.loadOrderList();
            });
      });
    },

    // 取消
    cancelOrder(orderNo) {
      this.$confirm("确定取消订单？", "提示").then(() => {
        this.$axios.post(this.$httpUrl + "/order/cancelOrder?orderNo=" + orderNo)
            .then(res => {
              this.$message.success("取消成功");
              this.loadOrderList();
            });
      });
    },

    // 出库
    outStock(orderNo) {
      this.$confirm("确认订单已出库？", "提示").then(() => {
        this.$axios.post(this.$httpUrl + "/order/outStock?orderNo=" + orderNo)
            .then(res => {
              this.$message.success("出库成功");
              this.loadOrderList();
            });
      });
    },

    // 完成
    finishOrder(orderNo) {
      this.$confirm("确认订单已完成？", "提示").then(() => {
        this.$axios.post(this.$httpUrl + "/order/finishOrder?orderNo=" + orderNo)
            .then(res => {
              this.$message.success("订单已完成");
              this.loadOrderList();
            });
      });
    },

    // 重置待支付
    resetToUnpay(orderNo) {
      this.$confirm("确定重置为待支付？", "提示").then(() => {
        this.$axios.post(this.$httpUrl + "/order/resetToUnpay?orderNo=" + orderNo)
            .then(res => {
              this.$message.success("重置成功");
              this.loadOrderList();
            });
      });
    },

    // 表头样式
    headerStyle() {
      return "background:rgb(245,246,247);text-align:center";
    }
  },
  beforeMount() {
    this.loadOrderList();
  }
};
</script>

<template>
  <div>
    <!-- 查询区域 -->
    <div style="margin-bottom: 5px;">
      <el-input
          v-model="orderNo"
          placeholder="订单编号"
          style="width: 220px;"
          @keyup.enter.native="loadOrderList"
      ></el-input>

      <el-select
          style="margin-left: 10px; width: 150px"
          v-model="orderStatus"
          clearable
          placeholder="订单状态"
      >
        <el-option label="待支付" value="1"></el-option>
        <el-option label="已支付" value="2"></el-option>
        <el-option label="已出库" value="3"></el-option>
        <el-option label="已完成" value="4"></el-option>
        <el-option label="已取消" value="5"></el-option>
      </el-select>

      <el-button type="primary" style="margin-left: 5px" @click="loadOrderList">
        查询
      </el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
    </div>

    <!-- 订单表格 -->
    <el-table
        :data="tableData"
        border
        :header-cell-style="headerStyle"
    >
      <el-table-column prop="id" label="ID" width="60"></el-table-column>
      <el-table-column prop="orderNo" label="订单编号" min-width="190"></el-table-column>
      <el-table-column prop="totalNum" label="商品数量" width="90"></el-table-column>
      <el-table-column prop="totalPrice" label="订单金额" width="100"></el-table-column>

      <el-table-column prop="orderStatus" label="订单状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.orderStatus)">
            {{ getStatusText(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="remark" label="备注" min-width="150"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="180"></el-table-column>

      <!-- 操作按钮 -->
      <el-table-column label="操作" min-width="420" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="success" @click="payed(scope.row.orderNo)">
            支付
          </el-button>
          <el-button size="mini" type="warning" @click="cancelOrder(scope.row.orderNo)">
            取消
          </el-button>
          <el-button size="mini" type="primary" @click="outStock(scope.row.orderNo)">
            出库
          </el-button>
          <el-button size="mini" type="success" @click="finishOrder(scope.row.orderNo)">
            完成
          </el-button>
          <el-button size="mini" type="info" @click="resetToUnpay(scope.row.orderNo)">
            重置
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        style="text-align:center;margin:15px 0"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[2,5,8,10]"
        :page-size="pageSize"
        layout="total,sizes,prev,pager,next,jumper"
        :total="total"
    ></el-pagination>
  </div>
</template>

<style scoped>
/* 完全沿用你项目的风格 */
</style>
<template>
  <div class="checkout-container">
    <!-- 顶部导航 -->
    <header class="checkout-header">
      <div class="header-content">
        <div class="logo">
          <h1>WMS商城</h1>
        </div>
        <nav class="nav-links">
          <a href="#/shop" class="nav-link">返回商城</a>
        </nav>
        <div class="user-info" v-if="currentUser">
          <span class="username">{{ currentUser.name }}</span>
        </div>
      </div>
    </header>

    <main class="checkout-main">
      <div class="checkout-content">
        <!-- 订单信息 -->
        <div class="order-section">
          <h2>订单信息</h2>
          <div class="form-group">
            <label>收货人</label>
            <input v-model="orderForm.receiver" type="text" placeholder="请输入收货人姓名" />
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input v-model="orderForm.phone" type="tel" placeholder="请输入联系电话" />
          </div>
          <div class="form-group">
            <label>收货地址</label>
            <textarea v-model="orderForm.address" placeholder="请输入收货地址"></textarea>
          </div>
          <div class="form-group">
            <label>备注</label>
            <input v-model="orderForm.remark" type="text" placeholder="选填，如有特殊要求请备注" />
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="products-section">
          <h2>商品清单</h2>
          <div v-if="cartItems.length === 0" class="empty-cart">
            <p>购物车是空的</p>
            <button @click="$router.push('/shop')">去购物</button>
          </div>
          <div v-else class="cart-items">
            <div v-for="item in cartItems" :key="item.goodsId" class="cart-item">
              <img :src="getImageUrl(item.imgUrl)" :alt="item.goodsName" class="cart-item-image" />
              <div class="cart-item-info">
                <h4 class="cart-item-name">{{ item.goodsName }}</h4>
                <span class="cart-item-price">¥{{ item.sellPrice.toFixed(2) }}</span>
              </div>
              <div class="cart-item-quantity">
                <span>数量：{{ item.goodsNum }}</span>
              </div>
              <div class="cart-item-total">
                ¥{{ (item.sellPrice * item.goodsNum).toFixed(2) }}
              </div>
            </div>
          </div>
        </div>

        <!-- 订单结算 -->
        <div class="summary-section">
          <h2>订单结算</h2>
          <div class="summary-row">
            <span>商品总数：</span>
            <span>{{ totalCount }} 件</span>
          </div>
          <div class="summary-row">
            <span>商品总价：</span>
            <span>¥{{ cartTotal.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费：</span>
            <span>¥0.00</span>
          </div>
          <div class="summary-row total">
            <span>应付金额：</span>
            <span>¥{{ cartTotal.toFixed(2) }}</span>
          </div>
          <button class="submit-order-btn" @click="submitOrder">提交订单</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'Checkout',
  data() {
    return {
      cartItems: [],
      currentUser: null,
      orderForm: {
        receiver: '',
        phone: '',
        address: '',
        remark: ''
      }
    };
  },
  computed: {
    cartTotal() {
      return this.cartItems.reduce((sum, item) => sum + item.sellPrice * item.goodsNum, 0);
    },
    totalCount() {
      return this.cartItems.reduce((sum, item) => sum + item.goodsNum, 0);
    }
  },
  mounted() {
    this.loadUser();
    this.loadCart();
  },
  methods: {
    loadUser() {
      const userStr = sessionStorage.getItem("CurUser");
      if (userStr) {
        this.currentUser = JSON.parse(userStr);
      }
    },
    loadCart() {
      const cart = sessionStorage.getItem('cart');
      if (cart) {
        this.cartItems = JSON.parse(cart);
      }
    },
    getImageUrl(imgUrl) {
      if (!imgUrl) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"%3E%3Crect fill="%23f5f5f5" width="100" height="100"/%3E%3Ctext fill="%23ccc" font-family="sans-serif" font-size="10" x="50%25" y="50%25" text-anchor="middle" dy=".3em"%3E暂无图片%3C/text%3E%3C/svg%3E';
      }
      if (imgUrl.startsWith('http')) {
        return imgUrl;
      }
      return this.$httpUrl + imgUrl;
    },
    submitOrder() {
      if (!this.orderForm.receiver) {
        this.$message.warning('请输入收货人姓名');
        return;
      }
      if (!this.orderForm.phone) {
        this.$message.warning('请输入联系电话');
        return;
      }
      if (!this.orderForm.address) {
        this.$message.warning('请输入收货地址');
        return;
      }
      if (this.cartItems.length === 0) {
        this.$message.warning('购物车是空的');
        return;
      }

      const orderData = {
        orderMaster: {
          receiver: this.orderForm.receiver,
          phone: this.orderForm.phone,
          address: this.orderForm.address,
          remark: this.orderForm.remark
        },
        orderItemList: this.cartItems.map(item => ({
          goodsId: item.goodsId,
          goodsName: item.goodsName,
          goodsNum: item.goodsNum,
          sellPrice: item.sellPrice
        }))
      };

      this.$axios.post(this.$httpUrl + '/order/createOrder', orderData, {
        headers: {
          'token': sessionStorage.getItem('token')
        }
      }).then(res => res.data).then(res => {
        if (res.code === 200) {
          this.$message.success('订单提交成功');
          sessionStorage.removeItem('cart');
          this.cartItems = [];
          setTimeout(() => {
            this.$router.push('/shop');
          }, 1500);
        } else {
          this.$message.error(res.msg || '订单提交失败');
        }
      }).catch(err => {
        this.$message.error('网络错误，请稍后重试');
      });
    }
  }
};
</script>

<style scoped>
.checkout-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.checkout-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 15px 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.logo h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.nav-links {
  display: flex;
  gap: 20px;
}

.nav-link {
  text-decoration: none;
  color: #666;
  font-size: 14px;
}

.nav-link:hover {
  color: #409eff;
}

.user-info {
  color: #666;
  font-size: 14px;
}

.checkout-main {
  max-width: 800px;
  margin: 30px auto;
  padding: 0 20px;
}

.checkout-content {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.order-section, .products-section, .summary-section {
  margin-bottom: 30px;
}

.order-section h2, .products-section h2, .summary-section h2 {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.form-group input, .form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group textarea {
  height: 80px;
  resize: vertical;
}

.empty-cart {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-cart button {
  margin-top: 15px;
  padding: 10px 20px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
}

.cart-item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
}

.cart-item-info {
  flex: 1;
  margin-left: 15px;
}

.cart-item-name {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #333;
}

.cart-item-price {
  font-size: 14px;
  color: #e63946;
}

.cart-item-quantity {
  font-size: 14px;
  color: #999;
  margin-right: 20px;
}

.cart-item-total {
  font-size: 16px;
  font-weight: bold;
  color: #e63946;
}

.summary-section {
  background: #fafafa;
  padding: 20px;
  border-radius: 8px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 14px;
  color: #666;
}

.summary-row.total {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  border-top: 1px dashed #ddd;
  margin-top: 10px;
  padding-top: 20px;
}

.summary-row.total span:last-child {
  color: #e63946;
}

.submit-order-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #e63946 0%, #d62828 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 20px;
  transition: all 0.3s;
}

.submit-order-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(230, 57, 70, 0.4);
}
</style>
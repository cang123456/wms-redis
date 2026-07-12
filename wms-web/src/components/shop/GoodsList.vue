<script>
export default {
  name: "GoodsList",
  data() {
    return {
      goodsList: [],
      cartItems: [],
      searchName: '',
      filteredGoods: [],
      showCart: false
    }
  },
  computed: {
    cartCount() {
      return this.cartItems.reduce((sum, item) => sum + item.quantity, 0)
    },
    cartTotal() {
      return this.cartItems.reduce((sum, item) => sum + item.sellPrice * item.quantity, 0).toFixed(2)
    }
  },
  mounted() {
    this.loadGoods()
    this.loadCart()
  },
  methods: {
    loadGoods() {
      this.$axios.get(this.$httpUrl + '/goods/list').then(res => res.data).then(res => {
        if (res.code === 200) {
          this.goodsList = res.data
          this.filteredGoods = res.data
        }
      })
    },
    loadCart() {
      const cart = sessionStorage.getItem('cart')
      if (cart) {
        this.cartItems = JSON.parse(cart)
      }
    },
    saveCart() {
      sessionStorage.setItem('cart', JSON.stringify(this.cartItems))
    },
    addToCart(goods) {
      const existingItem = this.cartItems.find(item => item.id === goods.id)
      if (existingItem) {
        if (existingItem.quantity < goods.count) {
          existingItem.quantity++
        } else {
          this.$message.warning('库存不足')
          return
        }
      } else {
        this.cartItems.push({
          ...goods,
          quantity: 1
        })
      }
      this.saveCart()
      this.$message.success('已加入购物车')
    },
    searchGoods() {
      if (this.searchName) {
        this.filteredGoods = this.goodsList.filter(goods => 
          goods.name.toLowerCase().includes(this.searchName.toLowerCase())
        )
      } else {
        this.filteredGoods = this.goodsList
      }
    },
    viewCart() {
      this.showCart = true
    },
    closeCart() {
      this.showCart = false
    },
    removeFromCart(index) {
      this.cartItems.splice(index, 1)
      this.saveCart()
    },
    updateQuantity(index, delta) {
      const item = this.cartItems[index]
      const newQuantity = item.quantity + delta
      if (newQuantity >= 1 && newQuantity <= item.count) {
        item.quantity = newQuantity
        this.saveCart()
      } else if (newQuantity > item.count) {
        this.$message.warning('库存不足')
      }
    },
    goToCheckout() {
      if (this.cartItems.length === 0) {
        this.$message.warning('购物车为空')
        return
      }
      this.$router.push('/checkout')
    },
    formatPrice(price) {
      return Number(price).toFixed(2)
    }
  }
}
</script>

<template>
  <div class="goods-container">
    <!-- 顶部搜索栏 -->
    <div class="search-bar">
      <div class="logo">
        <h1>商品商城</h1>
      </div>
      <div class="search-box">
        <el-input 
          v-model="searchName" 
          placeholder="搜索商品" 
          suffix-icon="el-icon-search"
          style="width: 400px;"
          @keyup.enter.native="searchGoods"
        ></el-input>
        <el-button type="primary" @click="searchGoods">搜索</el-button>
      </div>
      <div class="cart-btn" @click="viewCart">
        <el-button type="success" icon="el-icon-shopping-cart">
          购物车 ({{ cartCount }})
        </el-button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="goods-grid">
      <el-card 
        v-for="goods in filteredGoods" 
        :key="goods.id" 
        class="goods-card"
        shadow="hover"
      >
        <div class="goods-image">
          <img 
            :src="goods.imgUrl ? $httpUrl + goods.imgUrl : 'https://via.placeholder.com/200'" 
            :alt="goods.name"
          >
        </div>
        <div class="goods-info">
          <h3 class="goods-name">{{ goods.name }}</h3>
          <p class="goods-price">¥{{ formatPrice(goods.sellPrice) }}</p>
          <p class="goods-stock">库存: {{ goods.count }} 件</p>
          <p class="goods-remark">{{ goods.remark }}</p>
          <el-button 
            type="primary" 
            @click="addToCart(goods)"
            :disabled="goods.count <= 0"
          >
            {{ goods.count > 0 ? '加入购物车' : '暂时缺货' }}
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 购物车弹窗 -->
    <el-dialog 
      title="购物车" 
      :visible.sync="showCart" 
      width="600px"
      @close="closeCart"
    >
      <div v-if="cartItems.length === 0" class="empty-cart">
        <p>购物车为空，请先添加商品</p>
      </div>
      <div v-else>
        <el-table :data="cartItems" border>
          <el-table-column prop="name" label="商品名称"></el-table-column>
          <el-table-column prop="sellPrice" label="单价" :formatter="formatPrice"></el-table-column>
          <el-table-column label="数量">
            <template slot-scope="scope">
              <el-button size="mini" @click="updateQuantity(scope.$index, -1)">-</el-button>
              <span style="margin: 0 10px;">{{ scope.row.quantity }}</span>
              <el-button size="mini" @click="updateQuantity(scope.$index, 1)">+</el-button>
            </template>
          </el-table-column>
          <el-table-column label="小计">
            <template slot-scope="scope">
              ¥{{ (scope.row.sellPrice * scope.row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="mini" type="danger" @click="removeFromCart(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="cart-footer">
          <span class="total-price">合计: ¥{{ cartTotal }}</span>
          <el-button type="primary" @click="goToCheckout">去结算</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.goods-container {
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  padding: 15px 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.logo h1 {
  color: #409EFF;
  margin: 0;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-btn {
  margin-left: auto;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.goods-card {
  margin: 0;
  transition: transform 0.3s;
}

.goods-card:hover {
  transform: translateY(-5px);
}

.goods-image {
  text-align: center;
  margin-bottom: 15px;
}

.goods-image img {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.goods-info {
  text-align: center;
}

.goods-name {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 10px 0;
  color: #303133;
}

.goods-price {
  font-size: 24px;
  color: #E6A23C;
  margin: 0 0 10px 0;
}

.goods-stock {
  font-size: 14px;
  color: #909399;
  margin: 0 0 10px 0;
}

.goods-remark {
  font-size: 14px;
  color: #606266;
  margin: 0 0 15px 0;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #EBEEF5;
}

.total-price {
  font-size: 20px;
  font-weight: bold;
  color: #E6A23C;
}

.empty-cart {
  text-align: center;
  padding: 40px;
  color: #909399;
}
</style>
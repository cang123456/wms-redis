<template>
  <div class="shop-container">
    <!-- 顶部导航栏 -->
    <header class="shop-header">
      <div class="header-content">
        <div class="logo">
          <h1>WMS商城</h1>
        </div>
        <nav class="nav-links">
          <a href="#home" class="nav-link active">首页</a>
          <a href="#products" class="nav-link">全部商品</a>
          <button class="cart-btn" @click="toggleCart">
            <i class="el-icon-shopping-cart"></i>
            <span>购物车</span>
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
          </button>
        </nav>
        <div class="user-info" v-if="currentUser">
          <span class="username">{{ currentUser.name }}</span>
          <button class="logout-btn" @click="logout">退出登录</button>
        </div>
        <button v-else class="login-btn" @click="goLogin">登录</button>
      </div>
    </header>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-box">
        <input 
          v-model="searchKeyword" 
          type="text" 
          placeholder="搜索商品..." 
          class="search-input"
          @keyup.enter="searchGoods"
        />
        <button class="search-btn" @click="searchGoods">
          <i class="el-icon-search"></i>
        </button>
      </div>
      <div class="filter-tags">
        <span 
          v-for="tag in filterTags" 
          :key="tag.id" 
          :class="['filter-tag', { active: activeFilter === tag.id }]"
          @click="setFilter(tag.id)"
        >
          {{ tag.name }}
        </span>
      </div>
    </div>

    <!-- 商品展示区域 -->
    <main class="shop-main">
      <!-- 侧边栏筛选 -->
      <aside class="sidebar">
        <div class="sidebar-title">商品分类</div>
        <div class="category-list">
          <div 
            v-for="cat in categories" 
            :key="cat" 
            :class="['category-item', { active: activeCategory === cat }]"
            @click="setCategory(cat)"
          >
            {{ cat }}
          </div>
        </div>
        
        <div class="sidebar-title">价格区间</div>
        <div class="price-range">
          <div class="price-input-group">
            <input type="number" v-model="minPrice" placeholder="最低价" class="price-input" />
            <span class="price-separator">-</span>
            <input type="number" v-model="maxPrice" placeholder="最高价" class="price-input" />
          </div>
          <button class="filter-btn" @click="filterByPrice">筛选</button>
        </div>
      </aside>

      <!-- 商品列表 -->
      <section class="products-section">
        <div class="section-header">
          <h2>全部商品</h2>
          <div class="sort-options">
            <select v-model="sortBy" class="sort-select" @change="sortGoods">
              <option value="default">默认排序</option>
              <option value="price-asc">价格从低到高</option>
              <option value="price-desc">价格从高到低</option>
            </select>
          </div>
        </div>

        <div class="products-grid">
          <div 
            v-for="goods in displayGoods" 
            :key="goods.id" 
            class="product-card"
            @click="showProductDetail(goods)"
          >
            <div class="product-image">
              <img :src="getImageUrl(goods.imgUrl)" :alt="goods.name" />
              <div class="product-overlay">
                <button class="quick-add-btn" @click.stop="addToCart(goods)">
                  <i class="el-icon-shopping-cart"></i>
                  加入购物车
                </button>
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ goods.name }}</h3>
              <p class="product-desc">{{ goods.remark }}</p>
              <div class="product-footer">
                <span class="product-price">¥{{ goods.sellPrice.toFixed(2) }}</span>
                <span class="product-stock">库存: {{ goods.count }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[12, 24, 36, 48]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          >
          </el-pagination>
        </div>
      </section>
    </main>

    <!-- 购物车弹窗 -->
    <div v-if="showCartPanel" class="cart-overlay" @click="toggleCart">
      <div class="cart-panel" @click.stop>
        <div class="cart-header">
          <h3>购物车</h3>
          <button class="close-cart" @click="toggleCart">
            <i class="el-icon-close"></i>
          </button>
        </div>
        <div class="cart-content">
          <div v-if="cartItems.length === 0" class="empty-cart">
            <i class="el-icon-shopping-cart"></i>
            <p>购物车是空的</p>
          </div>
          <div v-else class="cart-items">
            <div v-for="item in cartItems" :key="item.goodsId" class="cart-item">
              <img :src="getImageUrl(item.imgUrl)" :alt="item.goodsName" class="cart-item-image" />
              <div class="cart-item-info">
                <h4 class="cart-item-name">{{ item.goodsName }}</h4>
                <span class="cart-item-price">¥{{ item.sellPrice.toFixed(2) }}</span>
              </div>
              <div class="cart-item-quantity">
                <button class="qty-btn" @click="decreaseQty(item)">-</button>
                <span>{{ item.goodsNum }}</span>
                <button class="qty-btn" @click="increaseQty(item)">+</button>
              </div>
              <button class="remove-item" @click="removeFromCart(item)">
                <i class="el-icon-delete"></i>
              </button>
            </div>
          </div>
        </div>
        <div v-if="cartItems.length > 0" class="cart-footer">
          <div class="cart-total">
            <span>合计：</span>
            <span class="total-price">¥{{ cartTotal.toFixed(2) }}</span>
          </div>
          <button class="checkout-btn" @click="goCheckout">去结算</button>
        </div>
      </div>
    </div>

    <!-- 商品详情弹窗 -->
    <div v-if="selectedGoods" class="detail-overlay" @click="hideProductDetail">
      <div class="detail-panel" @click.stop>
        <button class="close-detail" @click="hideProductDetail">
          <i class="el-icon-close"></i>
        </button>
        <div class="detail-content">
          <img :src="getImageUrl(selectedGoods.imgUrl)" :alt="selectedGoods.name" class="detail-image" />
          <div class="detail-info">
            <h2>{{ selectedGoods.name }}</h2>
            <p class="detail-desc">{{ selectedGoods.remark }}</p>
            <div class="detail-specs">
              <div class="spec-item">
                <span class="spec-label">类型：</span>
                <span>{{ selectedGoods.goodsType }}</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">仓库：</span>
                <span>{{ selectedGoods.storage }}</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">库存：</span>
                <span>{{ selectedGoods.count }}</span>
              </div>
            </div>
            <div class="detail-price">
              <span class="price-label">售价：</span>
              <span class="price-value">¥{{ selectedGoods.sellPrice.toFixed(2) }}</span>
            </div>
            <div class="detail-actions">
              <div class="quantity-selector">
                <button class="qty-btn" @click="decreaseDetailQty">-</button>
                <span>{{ detailQty }}</span>
                <button class="qty-btn" @click="increaseDetailQty">+</button>
              </div>
              <button class="add-to-cart-btn" @click="addDetailToCart">加入购物车</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Shop",
  data() {
    return {
      // 分页参数
      pageSize: 12,
      pageNum: 1,
      total: 0,
      
      // 查询条件
      searchKeyword: "",
      minPrice: "",
      maxPrice: "",
      activeFilter: "all",
      activeCategory: "",
      sortBy: "default",
      
      // 商品列表
      goodsList: [],
      displayGoods: [],
      categories: [],
      
      // 筛选标签
      filterTags: [
        { id: "all", name: "全部" },
        { id: "hot", name: "热门" },
        { id: "new", name: "新品" }
      ],
      
      // 购物车
      cartItems: [],
      showCartPanel: false,
      
      // 商品详情
      selectedGoods: null,
      detailQty: 1,
      
      // 当前用户
      currentUser: null
    };
  },
  computed: {
    cartCount() {
      return this.cartItems.reduce((sum, item) => sum + item.goodsNum, 0);
    },
    cartTotal() {
      return this.cartItems.reduce((sum, item) => sum + item.sellPrice * item.goodsNum, 0);
    }
  },
  mounted() {
    this.loadUser();
    this.loadGoods();
    this.loadCart();
  },
  methods: {
    loadUser() {
      const userStr = sessionStorage.getItem("CurUser");
      if (userStr) {
        this.currentUser = JSON.parse(userStr);
      }
    },
    loadGoods() {
      this.$axios.post(this.$httpUrl + '/goods/listPage', {
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        param: {
          name: this.searchKeyword,
          goodsType: this.activeCategory || '',
          storage: ''
        }
      }).then(res => res.data).then(res => {
        if (res.code === 200) {
          this.goodsList = res.data;
          this.total = res.total;
          this.extractCategories();
          this.sortGoods();
        }
      })
    },
    extractCategories() {
      const cats = [...new Set(this.goodsList.map(item => item.goodsType).filter(Boolean))];
      this.categories = ['全部', ...cats];
    },
    searchGoods() {
      this.pageNum = 1;
      this.loadGoods();
    },
    setFilter(filterId) {
      this.activeFilter = filterId;
    },
    setCategory(cat) {
      this.activeCategory = cat === '全部' ? '' : cat;
      this.pageNum = 1;
      this.loadGoods();
    },
    filterByPrice() {
      this.pageNum = 1;
      this.loadGoods();
    },
    sortGoods() {
      this.displayGoods = [...this.goodsList];
      if (this.sortBy === 'price-asc') {
        this.displayGoods.sort((a, b) => a.sellPrice - b.sellPrice);
      } else if (this.sortBy === 'price-desc') {
        this.displayGoods.sort((a, b) => b.sellPrice - a.sellPrice);
      }
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.pageNum = 1;
      this.loadGoods();
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.loadGoods();
    },
    toggleCart() {
      this.showCartPanel = !this.showCartPanel;
    },
    addToCart(goods) {
      const existingItem = this.cartItems.find(item => item.goodsId === goods.id);
      if (existingItem) {
        if (existingItem.goodsNum < goods.count) {
          existingItem.goodsNum++;
        } else {
          this.$message.warning('库存不足');
          return;
        }
      } else {
        this.cartItems.push({
          goodsId: goods.id,
          goodsName: goods.name,
          sellPrice: goods.sellPrice,
          goodsNum: 1,
          imgUrl: goods.imgUrl,
          stock: goods.count
        });
      }
      this.saveCart();
      this.$message.success('已加入购物车');
    },
    removeFromCart(item) {
      const index = this.cartItems.findIndex(i => i.goodsId === item.goodsId);
      if (index > -1) {
        this.cartItems.splice(index, 1);
        this.saveCart();
      }
    },
    increaseQty(item) {
      if (item.goodsNum < (item.stock || 999)) {
        item.goodsNum++;
        this.saveCart();
      }
    },
    decreaseQty(item) {
      if (item.goodsNum > 1) {
        item.goodsNum--;
        this.saveCart();
      }
    },
    saveCart() {
      sessionStorage.setItem('cart', JSON.stringify(this.cartItems));
    },
    loadCart() {
      const cart = sessionStorage.getItem('cart');
      if (cart) {
        this.cartItems = JSON.parse(cart);
      }
    },
    showProductDetail(goods) {
      this.selectedGoods = goods;
      this.detailQty = 1;
    },
    hideProductDetail() {
      this.selectedGoods = null;
    },
    increaseDetailQty() {
      if (this.selectedGoods && this.detailQty < this.selectedGoods.count) {
        this.detailQty++;
      }
    },
    decreaseDetailQty() {
      if (this.detailQty > 1) {
        this.detailQty--;
      }
    },
    addDetailToCart() {
      const goods = this.selectedGoods;
      const existingItem = this.cartItems.find(item => item.goodsId === goods.id);
      if (existingItem) {
        if (existingItem.goodsNum + this.detailQty <= goods.count) {
          existingItem.goodsNum += this.detailQty;
        } else {
          this.$message.warning('库存不足');
          return;
        }
      } else {
        this.cartItems.push({
          goodsId: goods.id,
          goodsName: goods.name,
          sellPrice: goods.sellPrice,
          goodsNum: this.detailQty,
          imgUrl: goods.imgUrl,
          stock: goods.count
        });
      }
      this.saveCart();
      this.hideProductDetail();
      this.$message.success(`已加入${this.detailQty}件商品到购物车`);
    },
    getImageUrl(imgUrl) {
      if (!imgUrl) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"%3E%3Crect fill="%23f5f5f5" width="200" height="200"/%3E%3Ctext fill="%23ccc" font-family="sans-serif" font-size="14" x="50%25" y="50%25" text-anchor="middle" dy=".3em"%3E暂无图片%3C/text%3E%3C/svg%3E';
      }
      if (imgUrl.startsWith('http')) {
        return imgUrl;
      }
      return this.$httpUrl + imgUrl;
    },
    goCheckout() {
      this.showCartPanel = false;
      this.$router.push('/checkout');
    },
    goLogin() {
      this.$router.push('/');
    },
    logout() {
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('CurUser');
      sessionStorage.removeItem('cart');
      this.currentUser = null;
      this.cartItems = [];
      this.$router.push('/');
    }
  }
};
</script>

<style scoped>
/* 全局样式 */
.shop-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 头部导航 */
.shop-header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.logo h1 {
  color: #fff;
  font-size: 24px;
  font-weight: 700;
  margin: 0;
}

.nav-links {
  display: flex;
  gap: 30px;
}

.nav-link {
  color: #fff;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
  position: relative;
}

.nav-link:hover,
.nav-link.active {
  color: #ffd700;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #ffd700;
}

.cart-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  background: #ff6b6b;
  color: #fff;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.cart-btn:hover {
  background: #ee5253;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(238, 82, 83, 0.4);
}

.cart-btn i {
  font-size: 18px;
}

.cart-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #ffd700;
  color: #333;
  font-size: 12px;
  font-weight: bold;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.username {
  color: #fff;
  font-size: 14px;
}

.logout-btn,
.login-btn {
  padding: 8px 20px;
  border-radius: 20px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.login-btn {
  background: #ffd700;
  color: #1a1a2e;
}

.login-btn:hover {
  background: #ffc107;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 搜索区域 */
.search-section {
  background: #fff;
  padding: 20px 0;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.search-box {
  max-width: 600px;
  margin: 0 auto 20px;
  display: flex;
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.search-input {
  flex: 1;
  padding: 14px 24px;
  border: none;
  font-size: 15px;
  outline: none;
}

.search-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  border: none;
  padding: 0 30px;
  color: #fff;
  cursor: pointer;
  font-size: 18px;
  transition: transform 0.3s;
}

.search-btn:hover {
  transform: scale(1.05);
}

.filter-tags {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.filter-tag {
  padding: 8px 22px;
  border-radius: 20px;
  background: #f0f0f0;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tag.active {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  color: #fff;
}

/* 主内容区域 */
.shop-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px;
  display: flex;
  gap: 30px;
}

/* 侧边栏 */
.sidebar {
  width: 200px;
  flex-shrink: 0;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #ff6b6b;
}

.category-list {
  margin-bottom: 30px;
}

.category-item {
  padding: 12px 15px;
  color: #555;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.3s;
  margin-bottom: 5px;
}

.category-item:hover {
  background: #f5f7fa;
  padding-left: 20px;
}

.category-item.active {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  color: #fff;
}

.price-range {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
}

.price-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.price-input {
  width: 70px;
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 13px;
  outline: none;
}

.price-separator {
  color: #999;
}

.filter-btn {
  width: 100%;
  padding: 10px;
  background: #333;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

/* 商品区域 */
.products-section {
  flex: 1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-header h2 {
  font-size: 20px;
  color: #333;
}

.sort-select {
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
}

/* 商品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

.product-image {
  position: relative;
  overflow: hidden;
  height: 200px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease-out;
}

.product-card:hover .product-image img {
  transform: scale(1.03);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.4s ease-out;
  backdrop-filter: blur(0);
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.quick-add-btn {
  background: #fff;
  border: none;
  padding: 12px 25px;
  border-radius: 25px;
  font-size: 14px;
  color: #ff6b6b;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: transform 0.3s;
}

.quick-add-btn:hover {
  transform: scale(1.05);
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: #888;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #ff6b6b;
}

.product-stock {
  font-size: 12px;
  color: #999;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 购物车弹窗 */
.cart-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  justify-content: flex-end;
}

.cart-panel {
  width: 400px;
  background: #fff;
  height: 100%;
  display: flex;
  flex-direction: column;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0);
  }
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.cart-header h3 {
  font-size: 18px;
  margin: 0;
}

.close-cart {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.cart-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #999;
}

.empty-cart i {
  font-size: 60px;
  margin-bottom: 15px;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.cart-item-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
}

.cart-item-info {
  flex: 1;
}

.cart-item-name {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cart-item-price {
  font-size: 14px;
  color: #ff6b6b;
  font-weight: 600;
}

.cart-item-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.qty-btn {
  width: 28px;
  height: 28px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
}

.remove-item {
  background: none;
  border: none;
  color: #999;
  font-size: 18px;
  cursor: pointer;
  padding: 5px;
}

.remove-item:hover {
  color: #ff4757;
}

.cart-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  background: #fafafa;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 16px;
}

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
}

.checkout-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s;
}

.checkout-btn:hover {
  transform: scale(1.02);
}

/* 商品详情弹窗 */
.detail-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1001;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-panel {
  background: #fff;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.close-detail {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  border: none;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  z-index: 10;
}

.detail-content {
  display: flex;
  max-height: 90vh;
}

.detail-image {
  width: 400px;
  height: 400px;
  object-fit: cover;
  flex-shrink: 0;
}

.detail-info {
  padding: 40px;
  flex: 1;
  overflow-y: auto;
}

.detail-info h2 {
  font-size: 24px;
  margin-bottom: 15px;
  color: #333;
}

.detail-desc {
  font-size: 15px;
  color: #666;
  margin-bottom: 25px;
  line-height: 1.6;
}

.detail-specs {
  margin-bottom: 25px;
}

.spec-item {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
}

.spec-label {
  color: #999;
  width: 60px;
}

.detail-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 30px;
}

.price-label {
  font-size: 16px;
  color: #666;
}

.price-value {
  font-size: 32px;
  font-weight: 700;
  color: #ff6b6b;
  margin-left: 10px;
}

.detail-actions {
  display: flex;
  gap: 20px;
  align-items: center;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.add-to-cart-btn {
  flex: 1;
  padding: 14px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s;
}

.add-to-cart-btn:hover {
  transform: scale(1.02);
}

/* 响应式 */
@media (max-width: 900px) {
  .shop-main {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .category-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .category-item {
    padding: 10px 20px;
    margin-bottom: 0;
  }
  
  .detail-content {
    flex-direction: column;
  }
  
  .detail-image {
    width: 100%;
    height: 250px;
  }
  
  .detail-info {
    padding: 20px;
  }
}
</style>

import VueRouter   from "vue-router";
import NotFound from "@/components/NotFound.vue";

const routes=[
    {
        path:'/',
        name:'Login',
        component:()=>import('../components/Login.vue')
    },
    {
        path:'/register',
        name:'Register',
        component:()=>import('../components/Register.vue')
    },
    {
        path:'/index',
        name:'Index',
        component:()=>import('../components/Index.vue'),
        children:[
            {
                path:'/home',
                name:'Home',
                meta:{
                    title:'首页'
                },
                component:()=>import('../components/Home.vue')
            },
        ]
    },
    // 前台商城页面
    {
        path:'/shop',
        name:'Shop',
        meta:{
            title:'商品商城'
        },
        component:()=>import('../components/shop/Shop.vue')
    },
    {
        path:'/checkout',
        name:'Checkout',
        meta:{
            title:'订单确认'
        },
        component:()=>import('../components/shop/Checkout.vue')
    },
]

const router = new VueRouter({
    mode:'history',
    routes
})

// 修改路由守卫
router.beforeEach((to, from, next) => {
    const token = sessionStorage.getItem('token')

    if (to.path === '/' || to.path === '/register') {
        if (token) {
            next('/shop')
        } else {
            next()
        }
    } else {
        if (token) {
            next()
        } else {
            next('/')
        }
    }
})


export function resetRouter() {
     router.matcher = new VueRouter({
        mode:'history',
        routes: []
    }).matcher
}


const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to) {
   return VueRouterPush.call(this, to).catch(err=>{
      if(err.name!="NavigationDuplicated"){
         throw err
      }
   })
}

export default router
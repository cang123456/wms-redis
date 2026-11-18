import vue from 'vue'
import Vuex from 'vuex'
import router,{resetRouter} from "@/router";
import createPersistedState from 'vuex-persistedstate'


vue.use(Vuex)


function addNewRouter(menuList) {

    let routes = router.options.routes
    console.log(routes)

    // 2. 遍历后端菜单，逐个添加到 /index 的子路由
    menuList.forEach(menu => {
        // 子路由的 path 应该是相对路径（不以 / 开头）
        // 因为是添加到父路由 Index 下的子路由
        let menuClick = menu.menuClick;
        if (menuClick.startsWith('/')) {
            menuClick = menuClick.substring(1);
        }
        const childRoute = {
            path: menuClick,
            name: menu.menuName,
            meta: {
                title: menu.menuName
            },
            component: () => import('../components/' + menu.menuComponent)
        }

        // 3. Vue Router 给父路由 name 为 "Index" 添加子路由
        // 注意：你的父路由 /index 必须配置 name: 'Index'
        router.addRoute('Index', childRoute)
    })


    // routes.forEach(item => {
    //     console.log( item)
    //     if( item.path === "/index" ){
    //
    //         menuList.forEach(menu =>{
    //             let childRoute =  {
    //                 path:'/'+menu.menuClick,
    //                 name:menu.menuName,
    //                 meta:{
    //                     title:menu.menuName
    //                 },
    //                 component:()=>import('../components/'+menu.menuComponent)
    //             }
    //
    //             item.children.push(childRoute)
    //         })
    //     }
    //
    // })

    // resetRouter()
    // router.addRoutes(routes)

    // {
    //     path:'/admin',
    //     name:'Admin',
    //     meta:{
    //         title:'管理员管理'
    //     },
    //     component:()=>import('../components/admin/AdminManage.vue')
    // },
}

export default new Vuex.Store({ // https://vuex.vuejs.org/zh/guide/state.html
    state: {
        menu:[]
    },
    mutations: {
        setMenu(state,menuList) {
            state.menu = menuList
            addNewRouter(menuList)

            // ✅ 最后添加 404（必须最后）
            router.addRoute({
                path: '/:pathMatch(.*)*',
                name: 'NotFound',
                component: () => import('@/components/NotFound.vue')
            })
        }
    },
    // actions: {
    //     addNAsync(context) {
    //         setTimeout(() => {
    //             context.commit('add')
    //         }, 1000)
    //     }
    // },
    getters: {
        getMenu(state) {
            return  state.menu
        }
    },
    plugins: [
        createPersistedState(createPersistedState())
    ]
})

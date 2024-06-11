import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/',
    name: 'Manager',
    component: () => import('../views/Manager.vue'),
    redirect: '/home',  // 重定向到主页
    children: [
      { path: '403', name: 'NoAuth', meta: { name: '无权限' }, component: () => import('../views/manager/403') },
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/manager/Home') },
      { path: 'supervisor', name: 'Supervisor', meta: { name: '主管信息' }, component: () => import('../views/manager/Supervisor') },
      { path: 'admin', name: 'Admin', meta: { name: '管理员信息' }, component: () => import('../views/manager/Admin') },
      { path: 'user', name: 'User', meta: { name: '用户信息' }, component: () => import('../views/manager/User') },
      { path: 'adminPerson', name: 'AdminPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/AdminPerson') },
      { path: 'password', name: 'Password', meta: { name: '修改密码' }, component: () => import('../views/manager/Password') },
      { path: 'notice', name: 'Notice', meta: { name: '公告信息' }, component: () => import('../views/manager/Notice') },
      { path: 'station', name: 'Station', meta: { name: '站点管理' }, component: () => import('../views/manager/Station') },
      { path: 'route', name: 'Route', meta: { name: '路径管理' }, component: () => import('../views/manager/Route') },
      { path: 'goods', name: 'Goods', meta: { name: '资产管理' }, component: () => import('../views/manager/Goods') },
    ]
  },
  {
    path: '/front',
    name: 'Front',
    component: () => import('../views/Front.vue'),
    children: [
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/front/Home') },
      { path: 'person', name: 'Person', meta: { name: '个人信息' }, component: () => import('../views/front/Person') },
    ]
  },
  { path: '/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/Register.vue') },
  { path: '*', name: 'NotFound', meta: { name: '无法访问' }, component: () => import('../views/404.vue') },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

//禁止USER前往的后端
const restrictedPaths = ['/home', '/admin', '/user','/supervisor', '/notice','/station','/route','/goods']; // 根据需要添加更多路径

// 路由守卫
router.beforeEach((to ,from, next) => {
  let user = JSON.parse(localStorage.getItem("xm-user") || '{}');
  if (to.path === '/' ) {
    if (user.role) {
      if (user.role === 'USER') {
        next('/front/home')
      } else {
        next('/home')
      }
    } else {
      next('/login')
    }
  }else if ((restrictedPaths.includes(to.path) &&user.role === 'USER')){
    next('/front/home');
  }
  else {
    next()
  }
})

export default router

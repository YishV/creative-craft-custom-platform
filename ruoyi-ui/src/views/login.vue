<template>
  <div class="login" :class="'login--' + mode" :style="loginBgStyle">
    <div class="login-form">
      <div class="login-brand">
        <span v-if="modeConfig.eyebrow" class="login-eyebrow">{{ modeConfig.eyebrow }}</span>
        <h3 class="title">{{ modeConfig.title }}</h3>
        <p v-if="modeConfig.subtitle" class="login-subtitle">{{ modeConfig.subtitle }}</p>
      </div>

      <div class="login-tabs" v-if="modeConfig.allowRegister">
        <button
          type="button"
          class="login-tab"
          :class="{ 'is-active': activeTab === 'login' }"
          @click="activeTab = 'login'"
        >登录</button>
        <button
          type="button"
          class="login-tab"
          :class="{ 'is-active': activeTab === 'register' }"
          @click="activeTab = 'register'"
        >注册</button>
      </div>

      <!-- 登录表单 -->
      <el-form
        v-show="activeTab === 'login'"
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            auto-complete="off"
            :placeholder="modeConfig.usernamePlaceholder"
          >
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            auto-complete="off"
            placeholder="密码"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <el-input
            v-model="loginForm.code"
            auto-complete="off"
            placeholder="验证码"
            style="width: 63%"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img"/>
          </div>
        </el-form-item>
        <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
        <el-form-item style="width:100%;">
          <el-button
            :loading="loading"
            size="medium"
            :type="modeConfig.buttonType"
            style="width:100%;"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">{{ modeConfig.buttonText }}</span>
            <span v-else>登 录 中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册表单 -->
      <el-form
        v-show="activeTab === 'register'"
        ref="registerForm"
        :model="registerForm"
        :rules="registerRules"
      >
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" type="text" auto-complete="off" placeholder="设置账号">
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="password" :rules="registerPwdValidator">
          <el-input
            v-model="registerForm.password"
            type="password"
            auto-complete="off"
            placeholder="设置密码"
          >
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            auto-complete="off"
            placeholder="确认密码"
            @keyup.enter.native="handleRegister"
          >
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <el-input
            v-model="registerForm.code"
            auto-complete="off"
            placeholder="验证码"
            style="width: 63%"
            @keyup.enter.native="handleRegister"
          >
            <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img"/>
          </div>
        </el-form-item>
        <div class="identity-tip">{{ modeConfig.registerTip }}</div>
        <el-form-item style="width:100%;">
          <el-button
            :loading="loading"
            size="medium"
            :type="modeConfig.buttonType"
            style="width:100%;"
            @click.native.prevent="handleRegister"
          >
            <span v-if="!loading">{{ modeConfig.registerButtonText }}</span>
            <span v-else>注 册 中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer-links">
        <router-link
          v-for="link in modeConfig.crossLinks"
          :key="link.to"
          class="link-type link-cross"
          :to="link.to"
        >{{ link.text }}</router-link>
      </div>
    </div>

    <!--  底部  -->
    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg, register } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import passwordRule from "@/utils/passwordRule"
import defaultSettings from '@/settings'

const MODE_CONFIG = {
  admin: {
    eyebrow: '',
    title: process.env.VUE_APP_TITLE || '文创手作定制交易平台',
    subtitle: '管理员登录',
    usernamePlaceholder: '账号',
    buttonText: '登 录',
    buttonType: 'primary',
    defaultRedirect: '/',
    defaultUsername: 'admin',
    defaultPassword: 'admin123',
    bg: 'linear-gradient(135deg, rgba(15, 35, 52, .55), rgba(40, 75, 99, .45))',
    allowRegister: false,
    crossLinks: [
      { to: '/buyer/login', text: '买家入口' },
      { to: '/creator/login', text: '创作者入口' }
    ]
  },
  buyer: {
    eyebrow: '文创手作定制交易平台',
    title: '买家中心',
    subtitle: '浏览作品 · 提交需求 · 查看订单',
    usernamePlaceholder: '请输入买家账号',
    buttonText: '登 录 / 进入前台',
    buttonType: 'warning',
    defaultRedirect: '/portal/index',
    defaultUsername: '',
    defaultPassword: '',
    bg: 'linear-gradient(135deg, rgba(217, 119, 6, .68), rgba(192, 86, 33, .55))',
    allowRegister: true,
    identityType: 'buyer',
    registerTip: '买家账号可浏览商品、发布定制需求、管理订单。',
    registerButtonText: '注 册 买 家 账 号',
    registerSuccessTip: '登录后可进入"文创前台"浏览商品并发布定制需求。',
    crossLinks: [
      { to: '/creator/login', text: '我是创作者' },
      { to: '/login', text: '管理员入口' }
    ]
  },
  creator: {
    eyebrow: '文创手作定制交易平台',
    title: '创作者中心',
    subtitle: '管理作品 · 处理报价 · 跟进订单',
    usernamePlaceholder: '请输入创作者账号',
    buttonText: '登 录 / 进入工作台',
    buttonType: 'success',
    defaultRedirect: '/creative/creator/me',
    defaultUsername: '',
    defaultPassword: '',
    bg: 'linear-gradient(135deg, rgba(24, 81, 110, .72), rgba(41, 126, 123, .58))',
    allowRegister: true,
    identityType: 'creator',
    registerTip: '注册后先以买家身份进入系统，再去"创作者管理"提交认证申请。',
    registerButtonText: '注 册 创 作 者 账 号',
    registerSuccessTip: '登录后请进入"文创平台 / 创作者管理"提交创作者申请。',
    crossLinks: [
      { to: '/buyer/login', text: '我是买家' },
      { to: '/login', text: '管理员入口' }
    ]
  }
}

export default {
  name: "Login",
  mixins: [passwordRule],
  data() {
    const mode = (this.$route && this.$route.meta && this.$route.meta.mode) || 'admin'
    const cfg = MODE_CONFIG[mode] || MODE_CONFIG.admin
    return {
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      mode,
      activeTab: 'login',
      loginForm: {
        username: cfg.defaultUsername,
        password: cfg.defaultPassword,
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
        password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        identityType: cfg.identityType || 'buyer',
        code: "",
        uuid: ""
      },
      loading: false,
      captchaEnabled: true,
      redirect: undefined
    }
  },
  computed: {
    modeConfig() {
      return MODE_CONFIG[this.mode] || MODE_CONFIG.admin
    },
    loginBgStyle() {
      return {
        backgroundImage: `${this.modeConfig.bg}, url(${require('../assets/images/login-background.jpg')})`
      }
    },
    registerRules() {
      return {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
          { min: 2, max: 20, message: '用户账号长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: "请再次输入您的密码", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (this.registerForm.password !== value) {
                callback(new Error("两次输入的密码不一致"))
              } else {
                callback()
              }
            }, trigger: "blur"
          }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      }
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
          this.registerForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        ...this.loginForm,
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            const target = this.redirect || this.modeConfig.defaultRedirect
            this.$router.push({ path: target }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          const payload = { ...this.registerForm, identityType: this.modeConfig.identityType }
          register(payload).then(() => {
            const username = this.registerForm.username
            this.$alert(
              "<font color='red'>恭喜你，账号 " + username + " 注册成功！</font><br/>" + this.modeConfig.registerSuccessTip,
              '系统提示',
              { dangerouslyUseHTMLString: true, type: 'success' }
            ).then(() => {
              // 注册成功后切回登录 tab，自动填入账号方便登录
              this.loginForm.username = username
              this.loginForm.password = ''
              this.activeTab = 'login'
              this.loading = false
              if (this.captchaEnabled) {
                this.getCode()
              }
            }).catch(() => {
              this.loading = false
            })
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: background-image .3s ease;
}
.title {
  margin: 0;
  text-align: center;
  color: #1f2937;
  font-size: 22px;
  font-weight: 700;
}
.login-brand {
  margin-bottom: 20px;
  text-align: center;
}
.login-eyebrow {
  display: inline-block;
  padding: 4px 12px;
  margin-bottom: 12px;
  border-radius: 999px;
  background: rgba(41, 126, 123, .12);
  color: #297e7b;
  font-size: 12px;
  letter-spacing: 1px;
}
.login--buyer .login-eyebrow {
  background: rgba(217, 119, 6, .14);
  color: #c0561c;
}
.login-subtitle {
  margin: 8px 0 0;
  color: #6b7280;
  font-size: 13px;
}
.login-tabs {
  display: flex;
  margin-bottom: 18px;
  border-bottom: 1px solid #edf0f5;
}
.login-tab {
  flex: 1;
  padding: 10px 0;
  background: transparent;
  border: 0;
  border-bottom: 2px solid transparent;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  transition: color .2s, border-color .2s;
}
.login-tab.is-active {
  color: #1f2937;
  font-weight: 700;
  border-bottom-color: #297e7b;
}
.login--buyer .login-tab.is-active {
  border-bottom-color: #d97706;
}

.login-form {
  border-radius: 8px;
  background: #ffffff;
  width: 420px;
  padding: 32px 28px 16px;
  z-index: 1;
  box-shadow: 0 12px 32px rgba(15, 35, 52, .18);
  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.identity-tip {
  margin: -12px 0 14px;
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
}
.login-footer-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  margin-top: 4px;
  padding-top: 8px;
  border-top: 1px dashed #edf0f5;
  font-size: 13px;
}
.link-cross {
  color: #6b7280;
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.login-code-img {
  height: 38px;
}
</style>

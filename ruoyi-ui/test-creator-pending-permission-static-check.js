const fs = require('fs')
const path = require('path')

function read(relativePath) {
  return fs.readFileSync(path.join(__dirname, relativePath), 'utf8')
}

function assertIncludes(content, expected, message) {
  if (!content.includes(expected)) {
    throw new Error(message)
  }
}

const permissionStore = read('src/store/modules/permission.js')
const indexPage = read('src/views/index.vue')
const loginPage = read('src/views/login.vue')

assertIncludes(permissionStore, "authType === 'creator' && !auth.hasRole('creator')", 'creator entry should hide dynamic workbench menus until creator role is granted')
assertIncludes(permissionStore, "return []", 'pending creators should receive no unauthorized dynamic sidebar routes')
assertIncludes(indexPage, "getAuthType() === 'creator'", 'root redirect should respect creator login entry')
assertIncludes(indexPage, '"/creative/creator/me"', 'creator entry should land on my store even before approval')
assertIncludes(loginPage, '注册后可在"我的店铺"提交认证申请。', 'creator login page should tell users to apply from my store')
assertIncludes(loginPage, '登录后请进入"我的店铺"提交创作者申请。', 'creator registration success should point to my store')

console.log('creator pending permission static check passed')

const fs = require('fs')
const path = require('path')

function read(file) {
  return fs.readFileSync(path.join(__dirname, file), 'utf8')
}

function assertIncludes(source, text, message) {
  if (!source.includes(text)) {
    throw new Error(message)
  }
}

const auth = read('src/utils/auth.js')
const login = read('src/views/login.vue')
const permission = read('src/permission.js')
const request = read('src/utils/request.js')
const user = read('src/store/modules/user.js')

assertIncludes(auth, 'Buyer-Token', 'auth.js should define an isolated buyer token key')
assertIncludes(auth, 'Creator-Token', 'auth.js should define an isolated creator token key')
assertIncludes(auth, 'Admin-Token', 'auth.js should keep the admin token key')
assertIncludes(auth, 'setAuthType', 'auth.js should expose per-tab auth type switching')
assertIncludes(auth, 'getAuthType', 'auth.js should expose current per-tab auth type')
assertIncludes(login, 'setAuthType(this.mode)', 'login page should set auth type from login entry mode')
assertIncludes(permission, 'ensureAuthType', 'permission guard should establish auth type before checking token')
assertIncludes(request, 'getToken()', 'request interceptor should read the current auth-type token')
assertIncludes(user, 'setToken(res.token, authType)', 'login action should write token to the active auth type only')
assertIncludes(user, 'removeToken(getAuthType())', 'logout should remove the active auth-type token only')

console.log('auth isolation static check passed')

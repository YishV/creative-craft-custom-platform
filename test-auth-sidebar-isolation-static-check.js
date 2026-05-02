const fs = require('fs')
const path = require('path')

const source = fs.readFileSync(path.join(__dirname, 'ruoyi-ui/src/store/modules/permission.js'), 'utf8')

function assertIncludes(text, message) {
  if (!source.includes(text)) {
    throw new Error(message)
  }
}

assertIncludes('getAuthType', 'permission store should read current auth type')
assertIncludes('getSidebarConstantRoutes', 'permission store should filter constant sidebar routes by auth type')
assertIncludes("authType === 'buyer'", 'buyer sidebar should have a dedicated branch')
assertIncludes("authType === 'creator'", 'creator sidebar should have a dedicated branch')
assertIncludes('SET_SIDEBAR_ROUTERS', 'permission store should still commit sidebar routes')
assertIncludes('SET_DEFAULT_ROUTES', 'permission store should still commit default routes')
assertIncludes('getSidebarDynamicRoutes', 'permission store should filter backend dynamic sidebar routes by auth type')

if (source.includes('state.defaultRoutes = constantRoutes.concat(routes)')) {
  throw new Error('default routes should not re-add all constant routes after auth filtering')
}

console.log('auth sidebar isolation static check passed')

const fs = require('fs')
const path = require('path')

const routerPath = path.join(__dirname, 'src/router/index.js')
const source = fs.readFileSync(routerPath, 'utf8')

const portalRoutePattern = /path:\s*['"]\/portal['"][\s\S]*?component:\s*Layout[\s\S]*?redirect:\s*['"]\/portal\/index['"][\s\S]*?meta:\s*\{[^}]*title:\s*['"]买家端['"][^}]*icon:\s*['"]education['"][^}]*\}/

if (!portalRoutePattern.test(source)) {
  throw new Error('Portal parent route must define meta.title and meta.icon for sidebar display.')
}

console.log('Portal parent route meta static check passed.')

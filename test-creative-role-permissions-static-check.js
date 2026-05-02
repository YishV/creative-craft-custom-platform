const fs = require('fs')
const path = require('path')

const root = __dirname
const installSql = fs.readFileSync(path.join(root, 'sql/install/04_system_menus.sql'), 'utf8')

const requiredPermissions = [
  'creative:creator:add', 'creative:creator:edit', 'creative:creator:remove',
  'creative:category:query',
  'creative:product:query', 'creative:product:add', 'creative:product:edit', 'creative:product:remove',
  'creative:demand:query', 'creative:demand:add', 'creative:demand:edit', 'creative:demand:remove',
  'creative:quote:query', 'creative:quote:add', 'creative:quote:edit', 'creative:quote:remove',
  'creative:order:query', 'creative:order:add', 'creative:order:edit', 'creative:order:remove',
  'creative:post:query', 'creative:post:add', 'creative:post:edit', 'creative:post:remove',
  'creative:comment:query', 'creative:comment:add', 'creative:comment:edit', 'creative:comment:remove',
  'creative:favorite:query', 'creative:favorite:add', 'creative:favorite:edit', 'creative:favorite:remove'
]

for (const permission of requiredPermissions) {
  if (!installSql.includes(permission)) {
    throw new Error(`Missing menu permission definition: ${permission}`)
  }
}

const roleMenuPatterns = [
  /\(100,\s*2126\)/, // buyer can publish demands
  /\(100,\s*2131\)/, // buyer can select quotes
  /\(100,\s*2135\)/, // buyer can finish/cancel own orders
  /\(101,\s*2002\)/, // creators can load category options
  /\(101,\s*2122\)/, // creators can add products
  /\(101,\s*2130\)/, // creators can submit quotes
  /\(101,\s*2135\)/, // creators can start/ship own orders
  /\(101,\s*2138\)/, // creators can publish posts
  /\(101,\s*2009\)/, // creators can open favorite/follow management
  /\(101,\s*2145\)/, // creators can query favorites/follows
  /\(101,\s*2148\)/  // creators can remove favorites/follows
]

for (const pattern of roleMenuPatterns) {
  if (!pattern.test(installSql)) {
    throw new Error(`Missing role-menu grant matching ${pattern}`)
  }
}

console.log('creative role permissions static check passed')

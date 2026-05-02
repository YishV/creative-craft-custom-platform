const fs = require('fs')
const path = require('path')

const mapperPath = path.join(__dirname, 'ruoyi-system/src/main/resources/mapper/system/SysMenuMapper.xml')
const mapper = fs.readFileSync(mapperPath, 'utf8')

function assertIncludes(text, message) {
  if (!mapper.includes(text)) {
    throw new Error(message)
  }
}

assertIncludes('parent_name', 'SysMenuMapper should select parent_name for frontend parent menu display')
assertIncludes("'主类目'", 'Root-level menus should expose parent_name as 主类目')
assertIncludes('left join sys_menu pm', 'SysMenuMapper should join parent menu rows for non-root parent names')

console.log('menu parent name static check passed')

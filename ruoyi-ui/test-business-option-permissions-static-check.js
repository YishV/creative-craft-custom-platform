const fs = require('fs')
const path = require('path')

function read(relativePath) {
  return fs.readFileSync(path.join(__dirname, '..', relativePath), 'utf8')
}

function assertIncludes(content, expected, message) {
  if (!content.includes(expected)) {
    throw new Error(message)
  }
}

function assertNotIncludes(content, unexpected, message) {
  if (content.includes(unexpected)) {
    throw new Error(message)
  }
}

const categoryController = read('ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeCategoryController.java')
const userController = read('ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysUserController.java')

const categoryListIndex = categoryController.indexOf('@GetMapping("/list")')
if (categoryListIndex === -1) {
  throw new Error('category list endpoint should exist')
}
const categoryListAnnotations = categoryController.slice(Math.max(0, categoryListIndex - 240), categoryListIndex)
assertNotIncludes(categoryListAnnotations, '@PreAuthorize', 'category list endpoint should be available for logged-in option lookups')
assertIncludes(categoryController, 'SecurityUtils.hasPermi("creative:category:list")', 'category list endpoint should keep full list access for management permission holders')
assertIncludes(categoryController, 'creativeCategory.setStatus("0")', 'category option lookups should be restricted to enabled categories')

const userListIndex = userController.indexOf('@GetMapping("/list")')
if (userListIndex === -1) {
  throw new Error('user list endpoint should exist')
}
const userListAnnotations = userController.slice(Math.max(0, userListIndex - 240), userListIndex)
assertNotIncludes(userListAnnotations, '@PreAuthorize', 'user list endpoint should be available for logged-in option lookups')
assertIncludes(userController, 'SecurityUtils.hasPermi("system:user:list")', 'user list endpoint should keep full list access for management permission holders')
assertIncludes(userController, 'user.setDeptId(BUYER_DEPT_ID)', 'user option lookups should be restricted to buyer accounts')
assertIncludes(userController, 'user.setStatus("0")', 'user option lookups should be restricted to normal accounts')

console.log('business option permission static checks passed')

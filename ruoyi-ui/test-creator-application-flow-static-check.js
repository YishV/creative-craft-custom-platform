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

const mePage = read('ruoyi-ui/src/views/creative/me/index.vue')
const registerPage = read('ruoyi-ui/src/views/register.vue')
const controller = read('ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeCreatorController.java')

assertIncludes(mePage, 'applyCreator', 'my creator page should import and call applyCreator')
assertIncludes(mePage, 'submitApplication', 'my creator page should submit creator applications in-place')
assertIncludes(mePage, '申请提交成功', 'my creator page should show success feedback after applying')
assertNotIncludes(mePage, '创作者管理', 'my creator page should not point new creators to the admin creator management page')
assertNotIncludes(registerPage, '文创平台 / 创作者管理', 'creator registration tip should point to the self-service store page')

const listMappingIndex = controller.indexOf('@GetMapping("/list")')
if (listMappingIndex === -1) {
  throw new Error('creator list endpoint should exist')
}
const listAnnotationBlock = controller.slice(Math.max(0, listMappingIndex - 240), listMappingIndex)
assertNotIncludes(listAnnotationBlock, '@PreAuthorize', 'creator list endpoint should be available for logged-in option lookups')
assertIncludes(controller, 'SecurityUtils.hasPermi("creative:creator:list")', 'creator list endpoint should keep full list access for management permission holders')
assertIncludes(controller, 'creativeCreator.setAuditStatus("approved")', 'creator list endpoint should restrict option lookups to approved creators')
assertIncludes(controller, 'creativeCreator.setStatus("0")', 'creator list endpoint should restrict option lookups to normal creators')

const applyMappingIndex = controller.indexOf('@PostMapping("/apply")')
if (applyMappingIndex === -1) {
  throw new Error('creator apply endpoint should exist')
}
const applyAnnotationBlock = controller.slice(Math.max(0, applyMappingIndex - 240), applyMappingIndex)
assertNotIncludes(applyAnnotationBlock, '@PreAuthorize', 'self-service creator apply endpoint should not require admin creator:add permission')

console.log('creator application flow static checks passed')

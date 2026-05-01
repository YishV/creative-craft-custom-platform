import request from '@/utils/request'

// 查询敏感词列表
export function listSensitive(query) {
  return request({
    url: '/creative/sensitive/list',
    method: 'get',
    params: query
  })
}

// 查询敏感词详情
export function getSensitive(wordId) {
  return request({
    url: '/creative/sensitive/' + wordId,
    method: 'get'
  })
}

// 新增敏感词
export function addSensitive(data) {
  return request({
    url: '/creative/sensitive',
    method: 'post',
    data
  })
}

// 修改敏感词
export function updateSensitive(data) {
  return request({
    url: '/creative/sensitive',
    method: 'put',
    data
  })
}

// 删除敏感词
export function delSensitive(wordIds) {
  return request({
    url: '/creative/sensitive/' + wordIds,
    method: 'delete'
  })
}

// 在线检测文本
export function checkSensitive(text) {
  return request({
    url: '/creative/sensitive/check',
    method: 'post',
    data: { text }
  })
}

// 重建 DFA 树
export function reloadSensitive() {
  return request({
    url: '/creative/sensitive/reload',
    method: 'post'
  })
}

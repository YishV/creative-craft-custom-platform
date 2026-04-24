import request from '@/utils/request'

export function listCategory(query) {
  return request({
    url: '/creative/category/list',
    method: 'get',
    params: query
  })
}

export function getCategory(categoryId) {
  return request({
    url: '/creative/category/' + categoryId,
    method: 'get'
  })
}

export function addCategory(data) {
  return request({
    url: '/creative/category',
    method: 'post',
    data: data
  })
}

export function updateCategory(data) {
  return request({
    url: '/creative/category',
    method: 'put',
    data: data
  })
}

export function delCategory(categoryId) {
  return request({
    url: '/creative/category/' + categoryId,
    method: 'delete'
  })
}

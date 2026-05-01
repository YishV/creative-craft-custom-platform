import request from '@/utils/request'

export function listProduct(query) {
  return request({
    url: '/creative/product/list',
    method: 'get',
    params: query
  })
}

export function getProduct(productId) {
  return request({
    url: '/creative/product/' + productId,
    method: 'get'
  })
}

export function addProduct(data) {
  return request({
    url: '/creative/product',
    method: 'post',
    data: data
  })
}

export function updateProduct(data) {
  return request({
    url: '/creative/product',
    method: 'put',
    data: data
  })
}

export function putOnShelf(productId) {
  return request({
    url: '/creative/product/' + productId + '/putOnShelf',
    method: 'post'
  })
}

export function takeOffShelf(productId) {
  return request({
    url: '/creative/product/' + productId + '/takeOffShelf',
    method: 'post'
  })
}

export function delProduct(productId) {
  return request({
    url: '/creative/product/' + productId,
    method: 'delete'
  })
}

export function submitProductAudit(productId) {
  return request({
    url: '/creative/product/' + productId + '/submitAudit',
    method: 'post'
  })
}

export function approveProductAudit(productId, remark) {
  return request({
    url: '/creative/product/' + productId + '/approveAudit',
    method: 'post',
    data: { remark }
  })
}

export function rejectProductAudit(productId, remark) {
  return request({
    url: '/creative/product/' + productId + '/rejectAudit',
    method: 'post',
    data: { remark }
  })
}

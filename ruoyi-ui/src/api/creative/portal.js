import request from '@/utils/request'

export function listPortalProduct(query) {
  return request({
    url: '/portal/product/list',
    method: 'get',
    params: query
  })
}

export function getPortalProduct(productId) {
  return request({
    url: '/portal/product/' + productId,
    method: 'get'
  })
}

export function listPortalDemand(query) {
  return request({
    url: '/portal/demand/list',
    method: 'get',
    params: query
  })
}

export function getPortalDemand(demandId) {
  return request({
    url: '/portal/demand/' + demandId,
    method: 'get'
  })
}

export function createPortalProductOrder(data) {
  return request({
    url: '/portal/order/product',
    method: 'post',
    data: data
  })
}

export function payPortalOrder(orderId) {
  return request({
    url: '/portal/order/pay/' + orderId,
    method: 'post'
  })
}

export function listPortalCreator(query) {
  return request({
    url: '/portal/creator/list',
    method: 'get',
    params: query
  })
}

export function getPortalCreator(creatorId) {
  return request({
    url: '/portal/creator/' + creatorId,
    method: 'get'
  })
}

export function listPortalPost(query) {
  return request({
    url: '/portal/post/list',
    method: 'get',
    params: query
  })
}

export function getPortalPost(postId) {
  return request({
    url: '/portal/post/' + postId,
    method: 'get'
  })
}

export function listPortalComment(query) {
  return request({
    url: '/portal/comment/list',
    method: 'get',
    params: query
  })
}

export function addPortalComment(data) {
  return request({
    url: '/portal/comment',
    method: 'post',
    data: data
  })
}

export function listPortalFavorite(query) {
  return request({
    url: '/portal/favorite/list',
    method: 'get',
    params: query
  })
}

export function addPortalFavorite(data) {
  return request({
    url: '/portal/favorite',
    method: 'post',
    data: data
  })
}

export function cancelPortalFavorite(favoriteId) {
  return request({
    url: '/portal/favorite/' + favoriteId + '/cancel',
    method: 'post'
  })
}

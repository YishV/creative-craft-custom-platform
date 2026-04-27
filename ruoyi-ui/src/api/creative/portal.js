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

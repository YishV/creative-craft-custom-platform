import request from '@/utils/request'

export function listOrder(query) {
  return request({
    url: '/creative/order/list',
    method: 'get',
    params: query
  })
}

export function getOrder(orderId) {
  return request({
    url: '/creative/order/' + orderId,
    method: 'get'
  })
}

export function addOrder(data) {
  return request({
    url: '/creative/order',
    method: 'post',
    data: data
  })
}

export function updateOrder(data) {
  return request({
    url: '/creative/order',
    method: 'put',
    data: data
  })
}

export function delOrder(orderId) {
  return request({
    url: '/creative/order/' + orderId,
    method: 'delete'
  })
}

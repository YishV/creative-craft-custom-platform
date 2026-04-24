import request from '@/utils/request'

export function listQuote(query) {
  return request({
    url: '/creative/quote/list',
    method: 'get',
    params: query
  })
}

export function getQuote(quoteId) {
  return request({
    url: '/creative/quote/' + quoteId,
    method: 'get'
  })
}

export function addQuote(data) {
  return request({
    url: '/creative/quote',
    method: 'post',
    data: data
  })
}

export function updateQuote(data) {
  return request({
    url: '/creative/quote',
    method: 'put',
    data: data
  })
}

export function delQuote(quoteId) {
  return request({
    url: '/creative/quote/' + quoteId,
    method: 'delete'
  })
}

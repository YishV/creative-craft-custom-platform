import request from '@/utils/request'

export function listFavorite(query) {
  return request({
    url: '/creative/favorite/list',
    method: 'get',
    params: query
  })
}

export function getFavorite(favoriteId) {
  return request({
    url: '/creative/favorite/' + favoriteId,
    method: 'get'
  })
}

export function addFavorite(data) {
  return request({
    url: '/creative/favorite',
    method: 'post',
    data: data
  })
}

export function updateFavorite(data) {
  return request({
    url: '/creative/favorite',
    method: 'put',
    data: data
  })
}

export function delFavorite(favoriteId) {
  return request({
    url: '/creative/favorite/' + favoriteId,
    method: 'delete'
  })
}

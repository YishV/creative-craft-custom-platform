import request from '@/utils/request'

export function listCreator(query) {
  return request({
    url: '/creative/creator/list',
    method: 'get',
    params: query
  })
}

export function getCreator(creatorId) {
  return request({
    url: '/creative/creator/' + creatorId,
    method: 'get'
  })
}

export function addCreator(data) {
  return request({
    url: '/creative/creator',
    method: 'post',
    data: data
  })
}

export function updateCreator(data) {
  return request({
    url: '/creative/creator',
    method: 'put',
    data: data
  })
}

export function delCreator(creatorId) {
  return request({
    url: '/creative/creator/' + creatorId,
    method: 'delete'
  })
}

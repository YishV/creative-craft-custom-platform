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

export function applyCreator(data) {
  return request({
    url: '/creative/creator/apply',
    method: 'post',
    data: data
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

export function approveCreator(creatorId) {
  return request({
    url: '/creative/creator/' + creatorId + '/approve',
    method: 'post'
  })
}

export function rejectCreator(creatorId, auditRemark) {
  return request({
    url: '/creative/creator/' + creatorId + '/reject',
    method: 'post',
    data: { auditRemark }
  })
}

export function delCreator(creatorId) {
  return request({
    url: '/creative/creator/' + creatorId,
    method: 'delete'
  })
}

export function getMyCreatorProfile() {
  return request({
    url: '/creative/creator/me',
    method: 'get'
  })
}

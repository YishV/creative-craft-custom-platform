import request from '@/utils/request'

export function listComment(query) {
  return request({
    url: '/creative/comment/list',
    method: 'get',
    params: query
  })
}

export function getComment(commentId) {
  return request({
    url: '/creative/comment/' + commentId,
    method: 'get'
  })
}

export function addComment(data) {
  return request({
    url: '/creative/comment',
    method: 'post',
    data: data
  })
}

export function updateComment(data) {
  return request({
    url: '/creative/comment',
    method: 'put',
    data: data
  })
}

export function delComment(commentId) {
  return request({
    url: '/creative/comment/' + commentId,
    method: 'delete'
  })
}

export function approveCommentAudit(commentId) {
  return request({
    url: '/creative/comment/' + commentId + '/approveAudit',
    method: 'post'
  })
}

export function rejectCommentAudit(commentId, remark) {
  return request({
    url: '/creative/comment/' + commentId + '/rejectAudit',
    method: 'post',
    data: { remark }
  })
}

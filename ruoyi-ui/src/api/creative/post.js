import request from '@/utils/request'

export function listPost(query) {
  return request({
    url: '/creative/post/list',
    method: 'get',
    params: query
  })
}

export function getPost(postId) {
  return request({
    url: '/creative/post/' + postId,
    method: 'get'
  })
}

export function addPost(data) {
  return request({
    url: '/creative/post',
    method: 'post',
    data: data
  })
}

export function updatePost(data) {
  return request({
    url: '/creative/post',
    method: 'put',
    data: data
  })
}

export function delPost(postId) {
  return request({
    url: '/creative/post/' + postId,
    method: 'delete'
  })
}

import request from '@/utils/request'

// 查询会话列表
export function listSessions(query) {
  return request({
    url: '/portal/chat/session/list',
    method: 'get',
    params: query
  })
}

// 开启或获取会话
export function openSession(data) {
  return request({
    url: '/portal/chat/session',
    method: 'post',
    data: data
  })
}

// 查询消息记录
export function listMessages(sessionId) {
  return request({
    url: '/portal/chat/message/list',
    method: 'get',
    params: { sessionId }
  })
}

// 发送消息
export function sendMessage(data) {
  return request({
    url: '/portal/chat/message',
    method: 'post',
    data: data
  })
}

// 标记已读
export function markRead(sessionId) {
  return request({
    url: '/portal/chat/session/' + sessionId + '/read',
    method: 'post'
  })
}

import request from '@/utils/request'

// 查询评论列表
export function listComments(query) {
  return request({
    url: '/portal/interaction/comment/list',
    method: 'get',
    params: query
  })
}

// 发表评论
export function addComment(data) {
  return request({
    url: '/portal/interaction/comment',
    method: 'post',
    data: data
  })
}

// 点赞/取消点赞
export function toggleLike(targetType, targetId) {
  return request({
    url: '/portal/interaction/like',
    method: 'post',
    params: { targetType, targetId }
  })
}

// 关注/取消关注创作者
export function toggleFollow(creatorId) {
  return request({
    url: '/portal/interaction/follow/' + creatorId,
    method: 'post'
  })
}

// 记录分享
export function addShare(targetType, targetId, platform) {
  return request({
    url: '/portal/interaction/share',
    method: 'post',
    params: { targetType, targetId, platform }
  })
}

// 检查交互状态
export function checkInteractionStatus(targetType, targetId, creatorId) {
  return request({
    url: '/portal/interaction/check',
    method: 'get',
    params: { targetType, targetId, creatorId }
  })
}

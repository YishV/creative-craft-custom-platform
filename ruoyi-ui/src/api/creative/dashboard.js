import request from '@/utils/request'

// 查询数据看板聚合统计
export function getDashboardStats() {
  return request({
    url: '/creative/dashboard/stats',
    method: 'get'
  })
}

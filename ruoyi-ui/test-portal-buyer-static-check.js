const fs = require('fs')
const path = require('path')

const productsPath = path.join(__dirname, 'src/views/portal/products.vue')
const source = fs.readFileSync(productsPath, 'utf8')

function assertIncludes(text, message) {
  if (!source.includes(text)) {
    throw new Error(message)
  }
}

assertIncludes('cartCount', '商品页加入购物车后应更新页面购物车数量状态')
assertIncludes('favoriteMap', '商品页应维护商品收藏状态映射')
assertIncludes('cancelPortalFavorite', '商品页已收藏商品应支持取消收藏')
assertIncludes('loadFavoriteMap', '商品页加载商品列表后应同步当前用户收藏状态')

console.log('portal buyer static check passed')

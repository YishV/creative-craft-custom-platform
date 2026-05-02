const fs = require('fs')
const path = require('path')

const root = __dirname

function read(file) {
  return fs.readFileSync(path.join(root, file), 'utf8')
}

function assertIncludes(source, text, message) {
  if (!source.includes(text)) {
    throw new Error(message)
  }
}

const domainChecks = [
  ['ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeOrder.java', 'private String buyerName;', 'order should expose buyerName'],
  ['ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeOrder.java', 'private String sellerName;', 'order should expose sellerName'],
  ['ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeDemand.java', 'private String userName;', 'demand should expose userName'],
  ['ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeFavorite.java', 'private String userName;', 'favorite should expose userName'],
  ['ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeFavorite.java', 'private String targetName;', 'favorite should expose targetName'],
  ['ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeProduct.java', 'private String creatorName;', 'product should expose creatorName'],
  ['ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativePost.java', 'private String creatorName;', 'post should expose creatorName']
]

for (const [file, text, message] of domainChecks) {
  assertIncludes(read(file), text, message)
}

const mapperChecks = [
  ['ruoyi-system/src/main/resources/mapper/creative/CreativeOrderMapper.xml', 'buyer_name', 'order mapper should select buyer_name'],
  ['ruoyi-system/src/main/resources/mapper/creative/CreativeOrderMapper.xml', 'seller_name', 'order mapper should select seller_name'],
  ['ruoyi-system/src/main/resources/mapper/creative/CreativeDemandMapper.xml', 'user_name', 'demand mapper should select user_name'],
  ['ruoyi-system/src/main/resources/mapper/creative/CreativeFavoriteMapper.xml', 'user_name', 'favorite mapper should select user_name'],
  ['ruoyi-system/src/main/resources/mapper/creative/CreativeFavoriteMapper.xml', 'target_name', 'favorite mapper should select target_name'],
  ['ruoyi-system/src/main/resources/mapper/creative/CreativeCommentMapper.xml', 'user_name', 'comment mapper should select user_name'],
  ['ruoyi-system/src/main/resources/mapper/creative/CreativeProductMapper.xml', 'creator_name', 'product mapper should select creator_name'],
  ['ruoyi-system/src/main/resources/mapper/creative/CreativePostMapper.xml', 'creator_name', 'post mapper should select creator_name']
]

for (const [file, text, message] of mapperChecks) {
  assertIncludes(read(file), text, message)
}

const uiChecks = [
  ['ruoyi-ui/src/views/portal/orders.vue', 'scope.row.buyerName', 'portal order list should render buyerName'],
  ['ruoyi-ui/src/views/portal/orders.vue', 'scope.row.sellerName', 'portal order list should render sellerName'],
  ['ruoyi-ui/src/views/portal/products.vue', 'creatorLabel(item)', 'portal product cards should render creator labels'],
  ['ruoyi-ui/src/views/portal/community.vue', 'creatorLabel(item)', 'portal community cards should render creator labels'],
  ['ruoyi-ui/src/views/portal/post-detail.vue', 'creatorLabel(post)', 'post detail should render creator labels'],
  ['ruoyi-ui/src/views/portal/cart.vue', 'creatorLabel(scope.row)', 'cart should render creator labels']
]

for (const [file, text, message] of uiChecks) {
  assertIncludes(read(file), text, message)
}

const extraUiChecks = [
  ['ruoyi-ui/src/views/creative/comment/index.vue', 'scope.row.userName', 'comment tab should render comment user names'],
  ['ruoyi-ui/src/views/creative/favorite/index.vue', 'scope.row.userName', 'favorite tab should render favorite user names'],
  ['ruoyi-ui/src/views/creative/favorite/index.vue', 'scope.row.targetName', 'favorite tab should render favorited target names'],
  ['ruoyi-ui/src/views/creative/product/index.vue', 'scope.row.creatorName', 'product tab should render creator names from row data']
]

for (const [file, text, message] of extraUiChecks) {
  assertIncludes(read(file), text, message)
}

console.log('creative display name static check passed')

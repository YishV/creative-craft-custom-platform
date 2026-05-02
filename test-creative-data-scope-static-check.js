const fs = require('fs')
const path = require('path')

const productMapper = fs.readFileSync(path.join(__dirname, 'ruoyi-system/src/main/resources/mapper/creative/CreativeProductMapper.xml'), 'utf8')

if (!productMapper.includes('<if test="creatorId != null">and p.creator_id = #{creatorId}</if>')) {
  throw new Error('Creative product list must filter by injected creatorId for creator data scope.')
}

console.log('creative data scope static check passed')

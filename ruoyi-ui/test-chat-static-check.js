const fs = require('fs')
const path = require('path')

const chatPath = path.join(__dirname, 'src/views/creative/chat/index.vue')
const source = fs.readFileSync(chatPath, 'utf8')

function assertIncludes(text, message) {
  if (!source.includes(text)) {
    throw new Error(message)
  }
}

assertIncludes("payload.type !== 'message'", '聊天页应按 WebSocket 包装格式 payload.type 处理消息')
assertIncludes('payload.data', '聊天页应从 payload.data 读取后端推送的消息')
assertIncludes('this.socket.send(JSON.stringify(data))', '聊天页发送文字应优先走 WebSocket 实时通道')
assertIncludes('messageType: \'image\'', '聊天页应支持发送图片消息')

console.log('chat static check passed')

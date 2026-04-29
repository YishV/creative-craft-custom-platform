<template>
  <div class="app-container chat-container">
    <el-row :gutter="20" style="height: calc(100vh - 120px)">
      <!-- 会话列表 -->
      <el-col :span="6" class="session-list">
        <el-card class="box-card" shadow="never">
          <div slot="header" class="clearfix">
            <span>消息列表</span>
          </div>
          <div v-for="item in sessionList" :key="item.sessionId" 
               :class="['session-item', currentSessionId === item.sessionId ? 'active' : '']"
               @click="handleSelectSession(item)">
            <div class="session-info">
              <div class="peer-name">
                {{ item.peerName }}
                <el-badge v-if="item.unreadCount > 0" :value="item.unreadCount" class="item-badge" />
              </div>
              <div class="last-msg text-ellipsis">{{ item.lastMessage || '[暂无消息]' }}</div>
            </div>
            <div class="session-time">{{ parseTime(item.lastMessageTime, '{m}-{d} {h}:{i}') }}</div>
          </div>
          <div v-if="sessionList.length === 0" class="no-data">暂无会话</div>
        </el-card>
      </el-col>

      <!-- 聊天窗口 -->
      <el-col :span="18" class="chat-window">
        <el-card class="box-card" shadow="never" v-if="currentSessionId">
          <div slot="header" class="clearfix">
            <span>{{ currentSession.peerName }}</span>
            <span style="font-size: 12px; color: #999; margin-left: 10px;">({{ currentSession.targetName }})</span>
          </div>
          
          <!-- 消息历史记录 -->
          <div class="message-history" ref="messageBox">
            <div v-for="msg in messageList" :key="msg.messageId" 
                 :class="['message-item', msg.senderId === userId ? 'mine' : 'other']">
              <div class="msg-content">
                <div v-if="msg.messageType === 'text'">{{ msg.content }}</div>
                <div v-else-if="msg.messageType === 'image'">
                  <el-image :src="msg.content" :preview-src-list="[msg.content]" class="msg-img" />
                </div>
              </div>
              <div class="msg-time">{{ parseTime(msg.createTime, '{h}:{i}') }}</div>
            </div>
          </div>

          <!-- 输入框 -->
          <div class="input-area">
            <div class="tool-row">
              <el-upload
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :before-upload="beforeImageUpload"
                :on-success="handleImageSuccess"
                :on-error="handleImageError"
              >
                <el-button size="mini" icon="el-icon-picture-outline">图片</el-button>
              </el-upload>
              <span :class="['socket-state', socketReady ? 'online' : 'offline']">
                {{ socketReady ? '实时在线' : '实时连接中' }}
              </span>
            </div>
            <el-input
              type="textarea"
              :rows="3"
              placeholder="请输入内容..."
              v-model="inputMsg"
              @keyup.enter.native="handleSend"
            >
            </el-input>
            <div class="btn-group">
              <el-button type="primary" size="small" @click="handleSend" :disabled="!inputMsg.trim()">发送 (Enter)</el-button>
            </div>
          </div>
        </el-card>
        <div v-else class="no-session">
          <el-empty description="请选择一个会话开始聊天"></el-empty>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listSessions, listMessages, sendMessage, markRead } from "@/api/creative/chat";
import { getToken } from "@/utils/auth";

export default {
  name: "CreativeChat",
  data() {
    return {
      userId: this.$store.getters.id,
      sessionList: [],
      messageList: [],
      currentSessionId: null,
      currentSession: {},
      inputMsg: "",
      socket: null,
      socketReady: false,
      uploadUrl: process.env.VUE_APP_BASE_API + "/common/upload",
      uploadHeaders: {
        Authorization: "Bearer " + getToken()
      }
    };
  },
  created() {
    this.getList();
    this.initWebSocket();
  },
  destroyed() {
    if (this.socket) {
      this.socket.close();
    }
  },
  methods: {
    getList() {
      listSessions().then(res => {
        this.sessionList = res.rows;
      });
    },
    handleSelectSession(session) {
      this.currentSessionId = session.sessionId;
      this.currentSession = session;
      this.getMessages();
      this.handleMarkRead(session);
    },
    getMessages() {
      listMessages(this.currentSessionId).then(res => {
        this.messageList = res.data;
        this.scrollToBottom();
      });
    },
    handleSend() {
      if (!this.inputMsg.trim()) return;
      const data = {
        sessionId: this.currentSessionId,
        messageType: 'text',
        content: this.inputMsg.trim()
      };
      if (this.sendBySocket(data)) {
        this.inputMsg = "";
        return;
      }
      this.sendByRest(data);
    },
    sendBySocket(data) {
      if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
        return false;
      }
      this.socket.send(JSON.stringify(data));
      return true;
    },
    sendByRest(data) {
      sendMessage(data).then(res => {
        this.appendMessage(res.data);
        this.refreshCurrentSession(data.messageType === 'image' ? '[图片]' : data.content);
        if (data.messageType === 'text') {
          this.inputMsg = "";
        }
      });
    },
    handleMarkRead(session) {
      if (session.unreadCount > 0) {
        markRead(session.sessionId).then(() => {
          session.unreadCount = 0;
        });
      }
    },
    initWebSocket() {
      const protocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
      const host = window.location.host;
      const token = getToken();
      this.socket = new WebSocket(`${protocol}${host}${process.env.VUE_APP_BASE_API}/ws/chat?token=${encodeURIComponent(token)}`);

      this.socket.onopen = () => {
        this.socketReady = true;
      };

      this.socket.onmessage = (event) => {
        const payload = JSON.parse(event.data);
        if (payload.type === 'error') {
          this.$modal.msgError(payload.message || '消息发送失败');
          return;
        }
        if (payload.type !== 'message' || !payload.data) {
          return;
        }
        const msg = payload.data;
        if (this.currentSessionId === msg.sessionId) {
          this.appendMessage(msg);
          this.handleMarkRead(this.currentSession);
        } else {
          this.getList();
        }
      };

      this.socket.onerror = () => {
        this.socketReady = false;
      };
      
      this.socket.onclose = () => {
        this.socketReady = false;
        console.log("WebSocket连接已关闭");
      };
    },
    appendMessage(message) {
      if (!message) return;
      const exists = this.messageList.some(item => item.messageId && item.messageId === message.messageId);
      if (!exists) {
        this.messageList.push(message);
      }
      this.refreshCurrentSession(message.messageType === 'image' ? '[图片]' : message.content);
      this.scrollToBottom();
    },
    refreshCurrentSession(lastMessage) {
      const session = this.sessionList.find(i => i.sessionId === this.currentSessionId);
      if (session) {
        session.lastMessage = lastMessage;
        session.lastMessageTime = new Date();
      }
    },
    beforeImageUpload(file) {
      const isImage = file.type && file.type.indexOf('image/') === 0;
      if (!isImage) {
        this.$modal.msgError('只能上传图片');
      }
      return isImage;
    },
    handleImageSuccess(res) {
      if (res.code !== 200) {
        this.$modal.msgError(res.msg || '图片上传失败');
        return;
      }
      const data = {
        sessionId: this.currentSessionId,
        messageType: 'image',
        content: res.url
      };
      if (!this.sendBySocket(data)) {
        this.sendByRest(data);
      }
    },
    handleImageError() {
      this.$modal.msgError('图片上传失败');
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const box = this.$refs.messageBox;
        if (box) {
          box.scrollTop = box.scrollHeight;
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.chat-container {
  background-color: #f5f7f9;
  .box-card { height: 100%; display: flex; flex-direction: column; }
  ::v-deep .el-card__body { flex: 1; overflow: hidden; padding: 0; display: flex; flex-direction: column; }
  
  .session-list {
    .session-item {
      padding: 15px;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      display: flex;
      justify-content: space-between;
      &:hover { background-color: #f9f9f9; }
      &.active { background-color: #eef5fe; border-right: 3px solid #1890ff; }
      
      .session-info {
        flex: 1;
        .peer-name { font-weight: bold; margin-bottom: 5px; display: flex; align-items: center; }
        .last-msg { font-size: 13px; color: #888; width: 180px; }
      }
      .session-time { font-size: 12px; color: #ccc; }
    }
  }

  .chat-window {
    .message-history {
      flex: 1;
      overflow-y: auto;
      padding: 20px;
      background-color: #fff;
      
      .message-item {
        margin-bottom: 20px;
        display: flex;
        flex-direction: column;
        &.mine { align-items: flex-end; 
          .msg-content { background-color: #95ec69; color: #000; border-radius: 6px 0 6px 6px; }
        }
        &.other { align-items: flex-start;
          .msg-content { background-color: #f3f3f3; color: #000; border-radius: 0 6px 6px 6px; }
        }
        
        .msg-content {
          padding: 10px 15px;
          max-width: 70%;
          word-break: break-all;
          font-size: 14px;
          line-height: 1.5;
        }
        .msg-time { font-size: 12px; color: #ccc; margin-top: 5px; }
      }
    }
    
    .input-area {
      border-top: 1px solid #f0f0f0;
      padding: 15px;
      background-color: #fff;
      .tool-row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 8px;
      }
      .socket-state {
        font-size: 12px;
        &.online { color: #67c23a; }
        &.offline { color: #909399; }
      }
      .btn-group { text-align: right; margin-top: 10px; }
    }
  }
  
  .no-session { height: 100%; display: flex; align-items: center; justify-content: center; background: #fff; }
  .no-data { text-align: center; color: #999; padding: 20px; }
  .text-ellipsis { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
}
</style>

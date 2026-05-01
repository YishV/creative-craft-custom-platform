<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="敏感词" prop="word">
        <el-input v-model="queryParams.word" placeholder="敏感词关键字" clearable @keyup.enter.native="handleQuery" style="width: 200px" />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="分类" clearable style="width: 150px">
          <el-option v-for="c in categoryOptions" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="等级" prop="severity">
        <el-select v-model="queryParams.severity" placeholder="等级" clearable style="width: 130px">
          <el-option label="低" value="1" />
          <el-option label="中" value="2" />
          <el-option label="高" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 130px">
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['creative:sensitive:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['creative:sensitive:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['creative:sensitive:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-refresh-right" size="mini" @click="handleReload" v-hasPermi="['creative:sensitive:edit']">重建词库</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-search" size="mini" @click="checkDialogVisible = true">在线检测</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="wordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="wordId" width="80" />
      <el-table-column label="敏感词" prop="word" />
      <el-table-column label="分类" align="center" prop="category" width="120">
        <template slot-scope="scope">
          <el-tag :type="categoryTag(scope.row.category)" size="mini">{{ categoryLabel(scope.row.category) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="等级" align="center" prop="severity" width="80">
        <template slot-scope="scope">
          <el-tag :type="severityTag(scope.row.severity)" size="mini">{{ severityLabel(scope.row.severity) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="mini">{{ scope.row.status === '0' ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="170" />
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['creative:sensitive:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['creative:sensitive:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="敏感词" prop="word">
          <el-input v-model="form.word" placeholder="请输入敏感词，最长 64 字" maxlength="64" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categoryOptions" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级" prop="severity">
          <el-radio-group v-model="form.severity">
            <el-radio label="1">低</el-radio>
            <el-radio label="2">中</el-radio>
            <el-radio label="3">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="200" show-word-limit placeholder="选填" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 在线检测 -->
    <el-dialog title="在线检测" :visible.sync="checkDialogVisible" width="600px" append-to-body>
      <el-input type="textarea" :rows="5" v-model="checkText" placeholder="粘贴待检测文本，例如评论或聊天内容" />
      <div style="margin-top: 12px; text-align: right">
        <el-button type="primary" :loading="checking" @click="runCheck">检测</el-button>
        <el-button @click="checkDialogVisible = false">关闭</el-button>
      </div>
      <div v-if="checkResult" style="margin-top: 16px">
        <el-alert v-if="!checkResult.hit" type="success" title="未命中任何敏感词" :closable="false" />
        <el-alert v-else type="error" :closable="false" :title="`命中 ${checkResult.hits.length} 个敏感词`">
          <div style="margin-top: 6px">
            <el-tag v-for="w in checkResult.hits" :key="w" type="danger" size="mini" style="margin-right: 4px">{{ w }}</el-tag>
          </div>
          <div style="margin-top: 10px; color: #666">替换后：</div>
          <div style="background: #f5f5f5; padding: 8px; border-radius: 4px; word-break: break-all">{{ checkResult.masked }}</div>
        </el-alert>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSensitive, addSensitive, updateSensitive, delSensitive, checkSensitive, reloadSensitive } from '@/api/creative/sensitive'

export default {
  name: 'CreativeSensitive',
  data() {
    return {
      loading: false,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      wordList: [],
      title: '',
      open: false,
      checkDialogVisible: false,
      checking: false,
      checkText: '',
      checkResult: null,
      categoryOptions: [
        { value: 'general', label: '通用' },
        { value: 'politics', label: '涉政' },
        { value: 'abuse', label: '辱骂' },
        { value: 'ad', label: '广告' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        word: undefined,
        category: undefined,
        severity: undefined,
        status: undefined
      },
      form: {},
      rules: {
        word: [{ required: true, message: '敏感词不能为空', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }],
        severity: [{ required: true, message: '请选择等级', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSensitive(this.queryParams)
        .then(res => {
          this.wordList = res.rows
          this.total = res.total
        })
        .finally(() => {
          this.loading = false
        })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = { wordId: undefined, word: '', category: 'general', severity: '1', status: '0', remark: '' }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.wordId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增敏感词'
    },
    handleUpdate(row) {
      this.reset()
      const wordId = row.wordId || (this.ids.length === 1 ? this.ids[0] : null)
      const target = row.wordId ? row : this.wordList.find(w => w.wordId === wordId)
      if (target) {
        this.form = Object.assign({}, target)
      }
      this.open = true
      this.title = '修改敏感词'
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) return
        const action = this.form.wordId ? updateSensitive(this.form) : addSensitive(this.form)
        action.then(() => {
          this.$modal.msgSuccess(this.form.wordId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const wordIds = row.wordId ? [row.wordId] : this.ids
      this.$modal
        .confirm('是否确认删除选中的敏感词？删除后将立即从拦截规则中移除')
        .then(() => delSensitive(wordIds))
        .then(() => {
          this.getList()
          this.$modal.msgSuccess('删除成功')
        })
        .catch(() => {})
    },
    handleReload() {
      reloadSensitive().then(() => {
        this.$modal.msgSuccess('词库已重建')
      })
    },
    runCheck() {
      if (!this.checkText) {
        this.$modal.msgWarning('请输入待检测文本')
        return
      }
      this.checking = true
      checkSensitive(this.checkText)
        .then(res => {
          this.checkResult = res.data
        })
        .finally(() => {
          this.checking = false
        })
    },
    categoryLabel(v) {
      const hit = this.categoryOptions.find(c => c.value === v)
      return hit ? hit.label : v
    },
    categoryTag(v) {
      return { general: '', politics: 'danger', abuse: 'warning', ad: 'info' }[v] || ''
    },
    severityLabel(v) {
      return { '1': '低', '2': '中', '3': '高' }[v] || v
    },
    severityTag(v) {
      return { '1': 'info', '2': 'warning', '3': 'danger' }[v] || ''
    }
  }
}
</script>

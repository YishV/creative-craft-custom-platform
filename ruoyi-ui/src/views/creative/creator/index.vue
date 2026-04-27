<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="创作者名" prop="creatorName">
        <el-input v-model="queryParams.creatorName" placeholder="请输入创作者名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="店铺名称" prop="storeName">
        <el-input v-model="queryParams.storeName" placeholder="请输入店铺名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="档案状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择档案状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable>
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['creative:creator:add']">新增申请</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['creative:creator:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['creative:creator:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="creatorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="创作者ID" align="center" prop="creatorId" width="90" />
      <el-table-column label="绑定账号" align="center" prop="userId" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.userId ? `账号#${scope.row.userId}` : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创作者名" align="center" prop="creatorName" :show-overflow-tooltip="true" />
      <el-table-column label="店铺名称" align="center" prop="storeName" :show-overflow-tooltip="true" />
      <el-table-column label="等级" align="center" prop="creatorLevel" width="100">
        <template slot-scope="scope">
          <span>{{ levelText(scope.row.creatorLevel) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="档案状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="statusMeta(scope.row.status).tag" size="mini">{{ statusMeta(scope.row.status).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus" width="100">
        <template slot-scope="scope">
          <el-tag :type="auditMeta(scope.row.auditStatus).tag" size="mini">{{ auditMeta(scope.row.auditStatus).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核备注" align="center" prop="auditRemark" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.auditRemark || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.auditStatus === 'pending'"
            size="mini"
            type="text"
            icon="el-icon-circle-check"
            @click="handleApprove(scope.row)"
            v-hasPermi="['creative:creator:edit']"
          >通过</el-button>
          <el-button
            v-if="scope.row.auditStatus === 'pending'"
            size="mini"
            type="text"
            icon="el-icon-circle-close"
            @click="handleReject(scope.row)"
            v-hasPermi="['creative:creator:edit']"
          >驳回</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['creative:creator:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['creative:creator:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item v-if="form.creatorId === undefined" label="申请账号">
          <el-input :value="currentUserLabel" disabled />
        </el-form-item>
        <el-form-item v-else label="绑定账号">
          <el-input :value="boundUserLabel" disabled />
        </el-form-item>
        <el-form-item label="创作者名" prop="creatorName">
          <el-input v-model="form.creatorName" placeholder="请输入创作者名" />
        </el-form-item>
        <el-form-item label="店铺名称" prop="storeName">
          <el-input v-model="form.storeName" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="等级" prop="creatorLevel">
          <el-select v-model="form.creatorLevel" placeholder="请选择等级">
            <el-option label="新人" value="newbie" />
            <el-option label="普通" value="normal" />
            <el-option label="优质" value="excellent" />
            <el-option label="金牌" value="gold" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.creatorId !== undefined" label="档案状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCreator, getCreator, applyCreator, updateCreator, approveCreator, rejectCreator, delCreator } from '@/api/creative/creator'

const LEVEL_MAP = {
  newbie: '新人',
  normal: '普通',
  excellent: '优质',
  gold: '金牌'
}

const STATUS_MAP = {
  '0': { label: '正常', tag: 'success' },
  '1': { label: '停用', tag: 'info' }
}

const AUDIT_MAP = {
  pending: { label: '待审核', tag: 'warning' },
  approved: { label: '已通过', tag: 'success' },
  rejected: { label: '已驳回', tag: 'danger' }
}

export default {
  name: 'CreativeCreator',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      creatorList: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        creatorName: undefined,
        storeName: undefined,
        status: undefined,
        auditStatus: undefined
      },
      form: {},
      rules: {
        creatorName: [{ required: true, message: '创作者名不能为空', trigger: 'blur' }],
        storeName: [{ required: true, message: '店铺名称不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    currentUserLabel() {
      const nickName = this.$store.getters.nickName || this.$store.getters.name || '当前登录账号'
      return `${nickName}（当前登录账号）`
    },
    boundUserLabel() {
      return this.form.userId ? `账号#${this.form.userId}` : '-'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listCreator(this.queryParams).then(response => {
        this.creatorList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    levelText(level) {
      return LEVEL_MAP[level] || level || '-'
    },
    statusMeta(status) {
      return STATUS_MAP[status] || { label: status || '-', tag: 'info' }
    },
    auditMeta(auditStatus) {
      return AUDIT_MAP[auditStatus] || { label: auditStatus || '-', tag: 'info' }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        creatorId: undefined,
        userId: this.$store.getters.id,
        creatorName: undefined,
        storeName: undefined,
        creatorLevel: 'newbie',
        status: '1',
        auditStatus: undefined,
        auditRemark: undefined,
        remark: undefined
      }
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
      this.ids = selection.map(item => item.creatorId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增创作者申请'
    },
    handleUpdate(row) {
      this.reset()
      const creatorId = row.creatorId || this.ids[0]
      getCreator(creatorId).then(response => {
        this.form = {
          ...response.data,
          status: response.data.status || '1'
        }
        this.open = true
        this.title = '修改创作者'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const payload = { ...this.form }
        if (payload.creatorId === undefined) {
          delete payload.userId
        }
        const request = payload.creatorId !== undefined ? updateCreator(payload) : applyCreator(payload)
        request.then(() => {
          this.$modal.msgSuccess(this.form.creatorId !== undefined ? '修改成功' : '申请提交成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleApprove(row) {
      this.$modal.confirm(`是否确认通过创作者申请“${row.creatorName}”？`).then(() => {
        return approveCreator(row.creatorId)
      }).then(() => {
        this.$modal.msgSuccess('审核通过成功')
        this.getList()
      }).catch(() => {})
    },
    handleReject(row) {
      this.$prompt('请输入驳回原因', '驳回申请', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '驳回原因不能为空'
      }).then(({ value }) => {
        return rejectCreator(row.creatorId, value.trim())
      }).then(() => {
        this.$modal.msgSuccess('驳回成功')
        this.getList()
      }).catch(() => {})
    },
    handleDelete(row) {
      const creatorIds = row.creatorId || this.ids
      this.$modal.confirm(`是否确认删除创作者编号为“${creatorIds}”的数据项？`).then(() => {
        return delCreator(creatorIds)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

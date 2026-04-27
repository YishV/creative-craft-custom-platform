<template>
  <div class="portal-page">
    <el-form :model="queryParams" size="small" inline class="toolbar">
      <el-form-item label="需求">
        <el-input v-model="queryParams.demandTitle" placeholder="搜索需求标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.demandStatus" placeholder="需求状态">
          <el-option label="待报价" value="published" />
          <el-option label="报价中" value="quoting" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button type="success" icon="el-icon-plus" @click="openDemandDialog">发布需求</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="demands" class="portal-table">
      <el-table-column label="需求标题" prop="demandTitle" min-width="180" />
      <el-table-column label="预算" width="130">
        <template slot-scope="scope">￥{{ money(scope.row.budgetAmount) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template slot-scope="scope">
          <el-tag :type="scope.row.demandStatus === 'quoting' ? 'warning' : 'success'" size="mini">
            {{ scope.row.demandStatus === 'quoting' ? '报价中' : '待报价' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="说明" prop="remark" min-width="220" :show-overflow-tooltip="true" />
      <el-table-column label="操作" width="150" align="center">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-chat-dot-round" @click="openQuoteDialog(scope.row)">提交报价</el-button>
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

    <el-dialog title="发布定制需求" :visible.sync="demandOpen" width="520px" append-to-body>
      <el-form ref="demandForm" :model="demandForm" :rules="demandRules" label-width="90px">
        <el-form-item label="需求标题" prop="demandTitle">
          <el-input v-model="demandForm.demandTitle" placeholder="例如：定制一枚刺绣胸针" />
        </el-form-item>
        <el-form-item label="预算金额" prop="budgetAmount">
          <el-input-number v-model="demandForm.budgetAmount" :min="0" :precision="2" controls-position="right" />
        </el-form-item>
        <el-form-item label="说明" prop="remark">
          <el-input v-model="demandForm.remark" type="textarea" :rows="4" placeholder="尺寸、材质、风格、交付时间等要求" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="demandOpen = false">取消</el-button>
        <el-button type="primary" @click="submitDemand">发布</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="quoteTitle" :visible.sync="quoteOpen" width="520px" append-to-body>
      <el-form ref="quoteForm" :model="quoteForm" :rules="quoteRules" label-width="90px">
        <el-form-item label="报价金额" prop="quoteAmount">
          <el-input-number v-model="quoteForm.quoteAmount" :min="0" :precision="2" controls-position="right" />
        </el-form-item>
        <el-form-item label="交付天数" prop="deliveryDays">
          <el-input-number v-model="quoteForm.deliveryDays" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="报价说明" prop="remark">
          <el-input v-model="quoteForm.remark" type="textarea" :rows="4" placeholder="说明材料、工期、交付方式等" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="quoteOpen = false">取消</el-button>
        <el-button type="primary" @click="submitQuote">提交报价</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPortalDemand } from '@/api/creative/portal'
import { addDemand } from '@/api/creative/demand'
import { addQuote } from '@/api/creative/quote'

export default {
  name: 'PortalDemands',
  data() {
    return {
      loading: false,
      demands: [],
      total: 0,
      demandOpen: false,
      quoteOpen: false,
      quoteTitle: '提交报价',
      currentDemand: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        demandTitle: undefined,
        demandStatus: 'published'
      },
      demandForm: {
        demandTitle: undefined,
        budgetAmount: 0,
        demandStatus: 'published',
        remark: undefined
      },
      quoteForm: {
        demandId: undefined,
        quoteAmount: 0,
        deliveryDays: 7,
        remark: undefined
      },
      demandRules: {
        demandTitle: [{ required: true, message: '需求标题不能为空', trigger: 'blur' }]
      },
      quoteRules: {
        quoteAmount: [{ required: true, message: '报价金额不能为空', trigger: 'blur' }],
        deliveryDays: [{ required: true, message: '交付天数不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPortalDemand(this.queryParams).then(res => {
        this.demands = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    openDemandDialog() {
      this.demandForm = {
        demandTitle: undefined,
        budgetAmount: 0,
        demandStatus: 'published',
        remark: undefined
      }
      this.demandOpen = true
    },
    submitDemand() {
      this.$refs.demandForm.validate(valid => {
        if (!valid) return
        addDemand(this.demandForm).then(() => {
          this.$modal.msgSuccess('需求已发布')
          this.demandOpen = false
          this.getList()
        })
      })
    },
    openQuoteDialog(row) {
      this.currentDemand = row
      this.quoteTitle = `给“${row.demandTitle}”报价`
      this.quoteForm = {
        demandId: row.demandId,
        quoteAmount: row.budgetAmount || 0,
        deliveryDays: 7,
        remark: undefined
      }
      this.quoteOpen = true
    },
    submitQuote() {
      this.$refs.quoteForm.validate(valid => {
        if (!valid) return
        addQuote(this.quoteForm).then(() => {
          this.$modal.msgSuccess('报价已提交')
          this.quoteOpen = false
          this.queryParams.demandStatus = 'quoting'
          this.getList()
        })
      })
    },
    money(value) {
      return Number(value || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>
.portal-page {
  padding: 20px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.toolbar,
.portal-table {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.toolbar {
  padding: 16px 16px 0;
  margin-bottom: 16px;
}
</style>

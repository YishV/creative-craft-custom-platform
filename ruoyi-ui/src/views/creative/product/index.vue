<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商品类型" prop="productType">
        <el-select v-model="queryParams.productType" placeholder="请选择类型" clearable>
          <el-option label="现货" value="spot" />
          <el-option label="定制" value="custom" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="上架" value="0" />
          <el-option label="下架" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['creative:product:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['creative:product:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['creative:product:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品ID" align="center" prop="productId" width="90" />
      <el-table-column label="商品名称" align="center" prop="productName" :show-overflow-tooltip="true" />
      <el-table-column label="创作者" align="center" prop="creatorId" width="140">
        <template slot-scope="scope">{{ creatorName(scope.row.creatorId) }}</template>
      </el-table-column>
      <el-table-column label="分类" align="center" prop="categoryId" width="120">
        <template slot-scope="scope">{{ categoryName(scope.row.categoryId) }}</template>
      </el-table-column>
      <el-table-column label="商品类型" align="center" prop="productType" width="90">
        <template slot-scope="scope">{{ productTypeText(scope.row.productType) }}</template>
      </el-table-column>
      <el-table-column label="价格" align="center" prop="price" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="mini">{{ scope.row.status === '0' ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-top" @click="handlePutOnShelf(scope.row)" v-if="scope.row.status === '1'" v-hasPermi="['creative:product:edit']">上架</el-button>
          <el-button size="mini" type="text" icon="el-icon-bottom" @click="handleTakeOffShelf(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['creative:product:edit']">下架</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['creative:product:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['creative:product:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="创作者" prop="creatorId">
          <el-select v-model="form.creatorId" placeholder="请选择创作者" filterable clearable>
            <el-option v-for="c in creatorOptions" :key="c.creatorId" :label="c.creatorName" :value="c.creatorId" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" filterable clearable>
            <el-option v-for="item in categoryOptions" :key="item.categoryId" :label="item.categoryName" :value="item.categoryId" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品类型" prop="productType">
          <el-radio-group v-model="form.productType">
            <el-radio label="spot">现货</el-radio>
            <el-radio label="custom">定制</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" controls-position="right" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入商品说明" />
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
import { listProduct, getProduct, delProduct, addProduct, updateProduct, putOnShelf, takeOffShelf } from '@/api/creative/product'
import { listCategory } from '@/api/creative/category'
import { listCreator } from '@/api/creative/creator'

export default {
  name: 'CreativeProduct',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      productList: [],
      categoryOptions: [],
      creatorOptions: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productName: undefined,
        productType: undefined,
        status: undefined
      },
      form: {},
      rules: {
        productName: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }],
        creatorId: [{ required: true, message: '创作者ID不能为空', trigger: 'blur' }],
        categoryId: [{ required: true, message: '分类不能为空', trigger: 'change' }],
        price: [{ required: true, message: '价格不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getCategoryOptions()
    this.getCreatorOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listProduct(this.queryParams).then(response => {
        this.productList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getCategoryOptions() {
      listCategory({ pageNum: 1, pageSize: 1000, status: '0' }).then(response => {
        this.categoryOptions = response.rows || []
      })
    },
    getCreatorOptions() {
      listCreator({ pageNum: 1, pageSize: 1000, status: '0' }).then(response => {
        this.creatorOptions = response.rows || []
      })
    },
    creatorName(id) {
      const c = this.creatorOptions.find(i => i.creatorId === id)
      return c ? c.creatorName : id
    },
    categoryName(id) {
      const c = this.categoryOptions.find(i => i.categoryId === id)
      return c ? c.categoryName : id
    },
    productTypeText(type) {
      return type === 'custom' ? '定制' : '现货'
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        productId: undefined,
        creatorId: undefined,
        categoryId: undefined,
        productName: undefined,
        productType: 'spot',
        price: 0,
        status: '1',
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
      this.ids = selection.map(item => item.productId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增手作商品'
    },
    handleUpdate(row) {
      this.reset()
      const productId = row.productId || this.ids
      getProduct(productId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改手作商品'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.productId !== undefined) {
            updateProduct(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addProduct(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const productIds = row.productId || this.ids
      this.$modal.confirm('是否确认删除商品编号为"' + productIds + '"的数据项？').then(function() {
        return delProduct(productIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handlePutOnShelf(row) {
      this.$modal.confirm('确认上架商品“' + row.productName + '”吗？').then(function() {
        return putOnShelf(row.productId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('上架成功')
      }).catch(() => {})
    },
    handleTakeOffShelf(row) {
      this.$modal.confirm('确认下架商品“' + row.productName + '”吗？').then(function() {
        return takeOffShelf(row.productId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('下架成功')
      }).catch(() => {})
    }
  }
}
</script>

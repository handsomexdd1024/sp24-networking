<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入货物名称查询" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
      <el-select v-model="selectedStation" placeholder="选择站点" @change="load(1)" style="width: 200px; margin-left: 10px;">
        <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id"></el-option>
      </el-select>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain style="margin-left: 10px" @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <div>站点货物管理</div>
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="货物名字"></el-table-column>
        <el-table-column prop="category" label="货物类别"></el-table-column>
        <el-table-column prop="quantity" label="货物量"></el-table-column>
        <el-table-column prop="stationName" label="所属站点"></el-table-column>
        <el-table-column label="操作" align="center" width="180">
          <template v-slot="scope">
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total"
        ></el-pagination>
      </div>
    </div>

    <el-dialog title="编辑货物" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="货物名称" prop="name">
          <el-input v-model="form.name" placeholder="货物名称"></el-input>
        </el-form-item>
        <el-form-item label="货物类别" prop="category">
          <el-select v-model="form.category" placeholder="选择货物类别" :disabled="!!newCategory">
            <el-option v-for="category in categories" :key="category" :label="category" :value="category"></el-option>
          </el-select>
          <el-input v-model="newCategory" placeholder="新增货物类别" style="margin-top: 10px;" @input="disableSelect"></el-input>
        </el-form-item>
        <el-form-item label="站点" prop="stationId">
          <el-select v-model="form.stationId" placeholder="选择站点" @change="updateStorage">
            <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="剩余空间" prop="storage">
          <el-input v-model="selectedStationStorage" disabled></el-input>
        </el-form-item>
        <el-form-item label="货物量" prop="quantity">
          <el-input type="number" v-model.number="form.quantity" placeholder="货物量"></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Goods",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      name: null,
      fromVisible: false,
      form: {
        name: '',
        category: '',
        stationId: null,
        quantity: 0
      },
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        name: [
          { required: true, message: '请输入货物名称', trigger: 'blur' },
        ],
        category: [
          { required: true, message: '请选择或输入货物类别', trigger: 'blur', validator: this.validateCategory }
        ],
        stationId: [
          { required: true, message: '请选择站点', trigger: 'change' },
        ],
        quantity: [
          { required: true, message: '请输入货物量', trigger: 'blur' },
          { type: 'number', message: '货物量必须是数字', trigger: 'blur' },
        ]
      },
      ids: [],
      stations: [],
      categories: [],
      newCategory: '',
      selectedStation: null,
      selectedStationStorage: 0
    }
  },
  created() {
    this.load(1);
    this.loadStations();
    this.loadCategories();
  },
  methods: {
    handleAdd() {
      this.form = {}
      this.newCategory = '';
      this.selectedStation = null;
      this.selectedStationStorage = 0;
      this.fromVisible = true
    },
    handleEdit(row) {
      this.form = {
        ...row,
        stationId: null // 确保初始化时 stationId 为 null
      };
      this.newCategory = '';
      this.$request.get(`/station-goods/selectByGoodsId/${this.form.id}`).then(res => {
        if (res.code === '200') {
          const stationGoods = res.data;
          if (stationGoods) {
            this.form.stationId = stationGoods.stationId;
            this.updateStorage();
          }
        }
      });
      this.fromVisible = true
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          if (this.newCategory) {
            this.form.category = this.newCategory;
          }
          if (this.form.quantity > this.selectedStationStorage) {
            this.$message.warning('货物量超过了站点的剩余空间');
            return;
          }
          const method = this.form.id ? 'PUT' : 'POST';
          const url = this.form.id ? '/goods/update' : '/goods/add';
          const data = this.form.id
              ? { ...this.form, stationId: this.form.stationId, quantity: this.form.quantity }
              : this.form;
          this.$request({
            url,
            method,
            data: data
          }).then(res => {
            if (res.code === '200') {
              this.$message.success('保存成功')
              this.load(1)
              this.loadCategories();
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
    del(id) {
      this.$confirm('您确定删除吗？', '确认删除', { type: "warning" }).then(response => {
        this.$request.delete('/goods/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => { })
    },
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id)
    },
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', { type: "warning" }).then(response => {
        this.$request.delete('/goods/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => { })
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        name: this.name,
        stationId: this.selectedStation // 添加 stationId 参数
      };
      this.$request.get('/goods/selectPageWithStation', { params }).then(res => {
        this.tableData = res.data?.list;
        this.total = res.data?.total;
      });
    },
    reset() {
      this.name = null;
      this.selectedStation = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    loadStations() {
      this.$request.get('/station/selectAll').then(res => {
        this.stations = res.data;
      });
    },
    loadCategories() {
      this.$request.get('/goods/categories').then(res => {
        this.categories = res.data;
      });
    },
    updateStorage() {
      const station = this.stations.find(station => station.id === this.form.stationId);
      if (station) {
        this.selectedStationStorage = station.storage;
      }
    },
    validateCategory(rule, value, callback) {
      if (!this.form.category && !this.newCategory) {
        callback(new Error('请选择或输入货物类别'));
      } else {
        callback();
      }
    },
    disableSelect() {
      if (this.newCategory) {
        this.form.category = '';
      }
    }
  }
}
</script>

<style scoped>
</style>

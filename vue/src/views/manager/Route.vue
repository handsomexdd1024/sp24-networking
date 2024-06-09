<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入站点名称查询" style="width: 200px" v-model="keyword"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <div>路径管理</div>
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <el-table-column prop="fromStationName" label="始发站"></el-table-column>
        <el-table-column prop="toStationName" label="终点站"></el-table-column>
        <el-table-column prop="routeType" label="路线类型" :formatter="formatRouteType"></el-table-column>
        <el-table-column prop="disableFlag" label="路线可用性" :formatter="formatDisableFlag"></el-table-column>
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
            :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="编辑路径" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="始发站" prop="fromStationId">
          <el-select v-model="form.fromStationId" placeholder="选择始发站" @change="handleFromStationChange">
            <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id" :disabled="station.id === form.toStationId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="终点站" prop="toStationId">
          <el-select v-model="form.toStationId" placeholder="选择终点站" @change="handleToStationChange">
            <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id" :disabled="station.id === form.fromStationId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="路线类型" prop="routeType">
          <el-select v-model="form.routeType" placeholder="选择路线类型">
            <el-option label="航空" value="flight"></el-option>
            <el-option label="公路" value="road"></el-option>
            <el-option label="铁路" value="rail"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="路线可用性" prop="disableFlag">
          <el-select v-model="form.disableFlag" placeholder="请选择">
            <el-option label="可用" :value="0"></el-option>
            <el-option label="不可用" :value="1"></el-option>
          </el-select>
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
  name: "Route",
  data() {
    return {
      tableData: [], // 所有的数据
      pageNum: 1, // 当前的页码
      pageSize: 10, // 每页显示的个数
      total: 0,
      keyword: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        fromStationId: [
          { required: true, message: '请选择始发站', trigger: 'change' }
        ],
        toStationId: [
          { required: true, message: '请选择终点站', trigger: 'change' }
        ],
        routeType: [
          { required: true, message: '请选择路线类型', trigger: 'change' }
        ],
        disableFlag: [
          { required: true, message: '请选择路线可用性', trigger: 'change' }
        ]
      },
      ids: [],
      stations: []
    };
  },
  created() {
    this.load(1);
    this.fetchStations();
  },
  methods: {
    handleAdd() {
      this.form = { disableFlag: 0 };
      this.fromVisible = true;
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.fromVisible = true;
    },
    save() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/route/update' : '/route/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {
              this.$message.success('保存成功');
              this.load(1);
              this.fromVisible = false;
            } else {
              this.$message.error(res.msg);
            }
          }).catch(err => {
            if (err.response && err.response.data && err.response.data.code === 5006) {
              this.$message.error('该路线已存在');
            } else {
              this.$message.error('保存失败');
            }
          });
        }
      });
    },
    del(id) {
      this.$confirm('您确定删除吗？', '确认删除', { type: 'warning' }).then(response => {
        this.$request.delete('/route/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功');
            this.load(1);
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => {});
    },
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id);
    },
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据');
        return;
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', { type: 'warning' }).then(response => {
        this.$request.delete('/route/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功');
            this.load(1);
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => {});
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.$request.get('/route/selectPageWithKeyWord', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          keyword:this.keyword,
        }
      }).then(res => {
        this.tableData = res.data?.list;
        this.total = res.data?.total;
      });
    },
    reset() {
      this.keyword = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
    fetchStations() {
      this.$request.get('/station/selectAll').then(res => {
        this.stations = res.data;
      });
    },
    handleFromStationChange(value) {
      if (this.form.toStationId === value) {
        this.form.toStationId = null;
      }
    },
    handleToStationChange(value) {
      if (this.form.fromStationId === value) {
        this.form.fromStationId = null;
      }
    },
    formatDisableFlag(row, column, cellValue) {
      return cellValue === '0' ? '可用' : '不可用';
    },
    formatRouteType(row, column, cellValue){
      if(cellValue ==='flight'){
        return '航空'
      }else if(cellValue ==='road'){
        return '公路'
      }else if(cellValue ==='rail'){
        return '铁路'
      }

    },

  }
};
</script>

<style scoped>
</style>

<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入站点名称查询" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <div>站点管理</div>
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="latitude" label="经度"></el-table-column>
        <el-table-column prop="longitude" label="纬度"></el-table-column>
        <el-table-column prop="storage" label="仓储量"></el-table-column>
        <el-table-column prop="disableFlag" label="仓库可用性" :formatter="formatDisableFlag"></el-table-column>
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

    <el-dialog title="编辑站点" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item v-if="!isManualInput" label="站点" prop="name">
          <el-select v-model="selectedCity" placeholder="选择站点" @change="handleCityChange">
            <el-option v-for="city in cities" :key="city.name" :label="city.name" :value="city.name" :disabled="city.disabled"></el-option>
          </el-select>
          <el-button @click="isManualInput = true" type="text">加入临时站点</el-button>
        </el-form-item>
        <el-form-item v-if="isManualInput || isEditing" label="站点名称" prop="name">
          <el-input v-model="form.name" placeholder="站点名称"></el-input>
        </el-form-item>
        <el-form-item v-if="isManualInput || isEditing" label="经度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="经度"></el-input>
        </el-form-item>
        <el-form-item v-if="isManualInput || isEditing" label="纬度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="纬度"></el-input>
        </el-form-item>
        <el-form-item label="仓储量" prop="storage">
          <el-input v-model="form.storage" placeholder="仓储量(单位:吨)"></el-input>
        </el-form-item>
        <el-form-item label="仓库可用性" prop="disableFlag">
          <el-select v-model="form.disableFlag" placeholder="请选择">
            <el-option label="是" :value="0"></el-option>
            <el-option label="否" :value="1"></el-option>
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
  name: "Station",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      name: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        name: [
          { required: true, message: '请输入站点名称', trigger: 'blur' },
        ]
      },
      ids: [],
      cities: [],
      selectedCity: null,
      isManualInput: false,
      isEditing: false
    };
  },
  created() {
    this.load(1);
    this.fetchCities();
  },
  methods: {
    handleAdd() {
      this.form = { disableFlag: 0 };
      this.isManualInput = false;
      this.isEditing = false;
      this.selectedCity = null;
      this.fromVisible = true;
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.isManualInput = true;
      this.isEditing = true;
      this.fromVisible = true;
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/station/update' : '/station/add',
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
          });
        }
      });
    },
    del(id) {
      this.$confirm('您确定删除吗？', '确认删除', { type: "warning" }).then(response => {
        this.$request.delete('/station/delete/' + id).then(res => {
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
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', { type: "warning" }).then(response => {
        this.$request.delete('/station/delete/batch', { data: this.ids }).then(res => {
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
      this.$request.get('/station/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data?.list;
        this.total = res.data?.total;
        this.fetchCities(); // 确保在加载完站点数据后更新城市列表
      });
    },
    reset() {
      this.name = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
    fetchCities() {
      this.$request.get('/station/selectAll').then(res => {
        const existingStations = res.data.map(station => station.name);
        this.$request.get('/station/allCities').then(cityRes => {
          this.cities = cityRes.data.map(city => ({
            ...city,
            disabled: existingStations.includes(city.name)
          }));
        });
      });
    },
    handleCityChange(cityName) {
      const city = this.cities.find(c => c.name === cityName);
      if (city) {
        this.form.name = city.name;
        this.form.latitude = city.latitude;
        this.form.longitude = city.longitude;
      }
    },
    formatDisableFlag(row, column, cellValue) {
      return cellValue === 0 ? '可用' : '不可用';
    }
  }
};
</script>

<style scoped>
</style>

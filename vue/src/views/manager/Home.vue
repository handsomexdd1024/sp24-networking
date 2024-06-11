<template>
  <div>
    <div class="card" style="padding: 15px">
      您好，{{ user?.name }}！欢迎使用本系统
    </div>

    <div style="margin: 10px 0">
      <div style="width: 100%;" class="card">
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">公告列表</div>
        <div>
          <el-timeline reverse slot="reference">
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <div style="display: flex; flex-wrap: wrap; justify-content: space-between;">
        <div style="width: 48%; height: 300px;" class="card" id="standStorage"></div>
        <div style="width: 48%; height: 300px;" class="card" id="standAvaliable"></div>
        <div style="width: 48%; height: 300px;" class="card" id="routeType"></div>
        <div style="width: 48%; height: 300px;" class="card" id="goodsType"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

//站点仓储量表格
let storageBar = {
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: [120, 100, 150, 80, 70, 110, 130],
      type: 'bar',
      showBackground: true,
      backgroundStyle: {
        color: 'rgba(180, 180, 180, 0.2)'
      }
    }
  ]
};

//站点可用性统计饼状图
let storagePie = {
  tooltip: {
    trigger: 'item'
  },
  legend: {
    top: '5%',
    left: 'center'
  },
  series: [
    {
      name: '站点仓库可用性',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 40,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 1048, name: '可用' },
        { value: 735, name: '不可用' },
      ]
    }
  ]
};
// 路线类型
let routeTypePie = {
  tooltip: {
    trigger: 'item'
  },
  legend: {
    top: '5%',
    left: 'center'
  },
  series: [
    {
      name: '路线类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      padAngle: 5,
      itemStyle: {
        borderRadius: 10
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 40,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 1048, name: 'raod' },
        { value: 735, name: 'rail' },
        { value: 580, name: 'flight' },
      ]
    }
  ]
}

// 货物类型
let goodsTypePie = {
  tooltip: {
    trigger: 'item'
  },
  legend: {
    top: '5%',
    left: 'center'
  },
  series: [
    {
      name: '货物类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      padAngle: 5,
      itemStyle: {
        borderRadius: 10
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 40,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        // { value: 12, name: '医疗用品' },
        // { value: 15, name: '食品' },
      ]
    }
  ]
}
export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: [],


    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
  },
  mounted() {
    this.load()
  },
  methods:{
    load(){
      this.storageShow();
      this.avaliableShow();
      this.routeTypeShow();
      this.goodsTypeShow();
    },
    storageShow(){
      let BarChart = echarts.init(document.getElementById('standStorage'));
      this.$request.get('/station/getAllName').then(res => {
        if (res.code === '200') {
          storageBar.xAxis.data=res.data[0];
          storageBar.series[0].data=res.data[1];
          BarChart.setOption(storageBar);
        } else {
          this.$message.error(res.msg);
          }
      })
    },
    avaliableShow(){
      let PieChart1 = echarts.init(document.getElementById('standAvaliable'));
      // this.$request.get('/station/isAvailable').then(res => {
      this.$request.get('/station/isAvailable').then(res => {
        if (res.code === '200') {
          storagePie.series[0].data[0].value=res.data[0];
          storagePie.series[0].data[1].value=res.data[1];
          PieChart1.setOption(storagePie);
        } else {
          this.$message.error(res.msg);
          }
      })
    },
    routeTypeShow(){
      let PieChart2 = echarts.init(document.getElementById('routeType'));
      this.$request.get('/route/getRouteTypeNum').then(res => {
        if (res.code === '200') {
          routeTypePie.series[0].data[0].value=res.data[0];
          routeTypePie.series[0].data[1].value=res.data[1];
          routeTypePie.series[0].data[2].value=res.data[2];
          PieChart2.setOption(routeTypePie);
        } else {
          this.$message.error(res.msg);
          }
      })
    },
    goodsTypeShow(){
      let PieChart3 = echarts.init(document.getElementById('goodsType'));
      this.$request.get('/goods/getGoodsNum').then(res => {
        if (res.code === '200') {
          const arrayLength = res.data.length;
          for (let i = 0; i < arrayLength; i++) {
            goodsTypePie.series[0].data.push({ value:res.data[i].quantity , name: res.data[i].category });
          }
          PieChart3.setOption(goodsTypePie);
        } else {
          this.$message.error(res.msg);
          }
      })
    },

  }

}

</script>

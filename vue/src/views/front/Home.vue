<template>
  <div class="main-content">
    <div id="china-map" style="width: 100%; height: 600px;"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  data() {
    return {};
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      var chartDom = document.getElementById('china-map');
      var myChart = echarts.init(chartDom);

      // 异步加载地图数据
      myChart.showLoading();
      fetch('https://geo.datav.aliyun.com/areas/bound/100000_full.json')
          .then(response => response.json())
          .then(chinaJson => {
            echarts.registerMap('china', chinaJson);
            myChart.hideLoading();

            var geoCoordMap = {
              '北京': [116.405285, 39.904989],
              '天津': [117.190182, 39.125596],
              '上海': [121.472644, 31.231706],
              '重庆': [106.504962, 29.533155],
              '哈尔滨': [126.642464, 45.756967],
              '长春': [125.3245, 43.886841],
              '沈阳': [123.429096, 41.796767],
              '呼和浩特': [111.670801, 40.818311],
              '石家庄': [114.502461, 38.045474],
              '太原': [112.549248, 37.857014],
              '西安': [108.948024, 34.263161],
              '济南': [117.000923, 36.675807],
              '郑州': [113.665412, 34.757975],
              '南京': [118.767413, 32.041544],
              '武汉': [114.298572, 30.584355],
              '长沙': [112.982279, 28.19409],
              '成都': [104.065735, 30.659462],
              '贵阳': [106.713478, 26.578343],
              '昆明': [102.712251, 25.040609],
              '南宁': [108.320004, 22.82402],
              '拉萨': [91.132212, 29.660361],
              '杭州': [120.153576, 30.287459],
              '南昌': [115.892151, 28.676493],
              '广州': [113.280637, 23.125178],
              '福州': [119.306239, 26.075302],
              '海口': [110.33119, 20.031971],
              '兰州': [103.823557, 36.058039],
              '西宁': [101.778916, 36.623178],
              '银川': [106.278179, 38.46637],
              '乌鲁木齐': [87.617733, 43.792818]
            };

            var data = [
              { name: '北京', value: [116.405285, 39.904989] },
              { name: '天津', value: [117.190182, 39.125596] },
              { name: '上海', value: [121.472644, 31.231706] },
              { name: '重庆', value: [106.504962, 29.533155] },
              { name: '哈尔滨', value: [126.642464, 45.756967] },
              { name: '长春', value: [125.3245, 43.886841] },
              { name: '沈阳', value: [123.429096, 41.796767] },
              { name: '呼和浩特', value: [111.670801, 40.818311] },
              { name: '石家庄', value: [114.502461, 38.045474] },
              { name: '太原', value: [112.549248, 37.857014] },
              { name: '西安', value: [108.948024, 34.263161] },
              { name: '济南', value: [117.000923, 36.675807] },
              { name: '郑州', value: [113.665412, 34.757975] },
              { name: '南京', value: [118.767413, 32.041544] },
              { name: '武汉', value: [114.298572, 30.584355] },
              { name: '长沙', value: [112.982279, 28.19409] },
              { name: '成都', value: [104.065735, 30.659462] },
              { name: '贵阳', value: [106.713478, 26.578343] },
              { name: '昆明', value: [102.712251, 25.040609] },
              { name: '南宁', value: [108.320004, 22.82402] },
              { name: '拉萨', value: [91.132212, 29.660361] },
              { name: '杭州', value: [120.153576, 30.287459] },
              { name: '南昌', value: [115.892151, 28.676493] },
              { name: '广州', value: [113.280637, 23.125178] },
              { name: '福州', value: [119.306239, 26.075302] },
              { name: '海口', value: [110.33119, 20.031971] },
              { name: '兰州', value: [103.823557, 36.058039] },
              { name: '西宁', value: [101.778916, 36.623178] },
              { name: '银川', value: [106.278179, 38.46637] },
              { name: '乌鲁木齐', value: [87.617733, 43.792818] }
            ];

            // 定义公路站点的相连关系
            var roadLinks = [
              ['北京', '天津'],
              ['北京', '石家庄'],
              ['上海', '南京'],
              ['广州', '长沙'],
              ['武汉', '长沙'],
              ['成都', '重庆'],
              ['哈尔滨', '长春'],
              ['长春', '沈阳'],
              ['呼和浩特', '太原'],
              ['石家庄', '郑州'],
              ['郑州', '济南'],
              ['南京', '杭州'],
              ['南昌', '福州'],
              ['福州', '广州'],
              ['海口', '广州'],
              ['兰州', '西宁'],
              ['西宁', '乌鲁木齐']
              // 可以继续添加其他相连站点
            ];

            // 定义站点的铁路相连关系
            var railLinks = [
              ['北京', '上海'],
              ['北京', '广州'],
              ['上海', '广州'],
              ['广州', '成都'],
              ['成都', '重庆'],
              ['重庆', '武汉'],
              ['武汉', '北京'],
              ['天津', '上海'],
              ['长春', '哈尔滨'],
              ['沈阳', '大连'],
              // 可以继续添加其他相连站点
            ];

            //火车小图标
            // 火车头图标路径
            var trainPath = 'path://M1705.1,317.7c-90.6,0-164.1-73.5-164.1-164.1S1614.5-10.5,1705.1-10.5S1869.2,63,1869.2,153.6S1795.7,317.7,1705.1,317.7z M1705.1,96.4c-31.6,0-57.3,25.7-57.3,57.3s25.7,57.3,57.3,57.3s57.3-25.7,57.3-57.3S1736.7,96.4,1705.1,96.4z';
            // 货车图标路径
            var truckPath = 'path://M1317.6,616.5H410.2c-27.8,0-50.4-22.6-50.4-50.4v-406c0-27.8,22.6-50.4,50.4-50.4h907.3c27.8,0,50.4,22.6,50.4,50.4v406C1368,593.9,1345.4,616.5,1317.6,616.5z M1091.6,616.5h-574c-27.8,0-50.4,22.6-50.4,50.4v70.2c0,27.8,22.6,50.4,50.4,50.4h574c27.8,0,50.4-22.6,50.4-50.4v-70.2C1142,639.1,1119.4,616.5,1091.6,616.5z M492.5,566.1c55.5,0,100.4-44.9,100.4-100.4s-44.9-100.4-100.4-100.4s-100.4,44.9-100.4,100.4S437,566.1,492.5,566.1zM1091.6,566.1c55.5,0,100.4-44.9,100.4-100.4s-44.9-100.4-100.4-100.4s-100.4,44.9-100.4,100.4S1036.1,566.1,1091.6,566.1z';

            // 飞线站点遍历
            var convertData = function(data) {
              var res = [];
              for (var i = 0; i < data.length; i++) {
                for (var j = 0; j < data.length; j++) {
                  if (i !== j) {
                    var fromCoord = geoCoordMap[data[i].name];
                    var toCoord = geoCoordMap[data[j].name];
                    if (fromCoord && toCoord) {
                      res.push({
                        fromName: data[i].name,
                        toName: data[j].name,
                        coords: [fromCoord, toCoord]
                      });
                    }
                  }
                }
              }
              return res;
            };

            // 生成公路数据
            var createRoadData = function(links) {
              var res = [];
              for (var i = 0; i < links.length; i++) {
                var fromCoord = geoCoordMap[links[i][0]];
                var toCoord = geoCoordMap[links[i][1]];
                if (fromCoord && toCoord) {
                  res.push({
                    fromName: links[i][0],
                    toName: links[i][1],
                    coords: [fromCoord, toCoord]
                  });
                }
              }
              return res;
            };

            // 生成铁路数据
            var createRailData = function(links) {
              var res = [];
              for (var i = 0; i < links.length; i++) {
                var fromCoord = geoCoordMap[links[i][0]];
                var toCoord = geoCoordMap[links[i][1]];
                // 确保铁路路径在中国地图边界范围内
                if (fromCoord && toCoord) {
                  res.push({
                    fromName: links[i][0],
                    toName: links[i][1],
                    coords: [fromCoord, toCoord],
                  });
                }
              }
              return res;
            };





            var option = {
              title: {
                text: '中国地图',
                left: 'center'
              },
              tooltip: {
                trigger: 'item'
              },
              geo: {
                map: 'china',
                roam: true,
                label: {
                  show: true
                },
                itemStyle: {
                  normal: {
                    borderColor: 'rgba(0, 0, 0, 0.2)'
                  },
                  emphasis: {
                    areaColor: null,
                    shadowOffsetX: 0,
                    shadowOffsetY: 0,
                    shadowBlur: 20,
                    borderWidth: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                }
              },
              series: [
                {
                  name: '省会',
                  type: 'scatter',
                  coordinateSystem: 'geo',
                  data: data,
                  symbolSize: 12,
                  label: {
                    formatter: '{b}',
                    position: 'right',
                    show: false
                  },
                  emphasis: {
                    label: {
                      show: true
                    }
                  }
                },
                {
                  name: '飞行路线',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 2,
                  large: true,
                  lineStyle: {
                    normal: {
                      color: '#4fa977',
                      width: 1,
                      opacity: 0.6,
                      curveness: 0.2
                    }
                  },
                  data: convertData(data)
                },
                {
                  name: '飞行路线动态',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 2,
                  effect: {
                    show: true,
                    period: 6,
                    trailLength: 0.7,
                    color: '#a6c84c',
                    symbolSize: 3
                  },
                  lineStyle: {
                    normal: {
                      color: '#a6c84c',
                      width: 0,
                      curveness: 0.2
                    }
                  },
                  data: convertData(data)
                },
                {
                  name: '公路',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 1,
                  large: true,
                  effect: {
                    show: true,
                    period: 8,
                    color:'#ffffff',
                    symbol: truckPath,
                    symbolSize: 10,
                    trailLength: 0
                  },
                  lineStyle: {
                    normal: {
                      color: '#ffde00',
                      width: 5,
                      opacity: 0.8,
                    }
                  },
                  data: createRoadData(roadLinks)
                },
                {
                  name: '铁路',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 1,
                  large: true,
                  effect: {
                    show: true,
                    period: 8,
                    symbol: trainPath,
                    color:'#ffffff',
                    symbolSize: 10,
                    trailLength: 0
                  },
                  lineStyle: {
                    normal: {
                      color:'#000000',
                      width: 5,
                    },
                  },
                  progressiveThreshold: 500,
                  progressive: 200,
                  data: createRailData(railLinks)
                },
                {
                  name: '铁路背景',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 1,
                  large: true,
                  lineStyle: {
                      color:"#fff",
                      width:4,
                      type:"dashed",
                  },
                  progressiveThreshold: 500,
                  progressive: 200,
                  data: createRailData(railLinks)
                },
              ]
            };

            myChart.setOption(option);
          });
    }
  }
};
</script>

<style scoped>
#china-map {
  width: 100%;
  height: 100%;
}
</style>

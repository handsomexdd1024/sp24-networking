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
              // 添加其他省会的坐标
              // ...
            };


            var data = [
              { name: '北京', value: [116.405285, 39.904989] },
              { name: '天津', value: [117.190182, 39.125596] },
              { name: '上海', value: [121.472644, 31.231706] },
              { name: '重庆', value: [106.504962, 29.533155] },
              // 添加其他省会的数据
              // ...
            ];

            var convertData = function(data) {
              var res = [];
              for (var i = 0; i < data.length; i++) {
                var fromCoord = geoCoordMap[data[i].name];
                var toCoord = geoCoordMap['北京']; // 飞线的终点设置为北京，或者其他城市
                if (fromCoord && toCoord) {
                  res.push({
                    fromName: data[i].name,
                    toName: '北京',
                    coords: [fromCoord, toCoord]
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
                  data: [
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
                  ],
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
                  effect: {
                    show: true,
                    period: 6,
                    trailLength: 0.7,
                    color: '#fff',
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
                  name: '飞行路线',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 2,
                  large: true,
                  lineStyle: {
                    normal: {
                      color: '#1e56e5',
                      width: 1,
                      opacity: 0.6,
                      curveness: 0.2
                    }
                  },
                  data: convertData(data)
                }
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

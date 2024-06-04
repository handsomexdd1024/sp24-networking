<template>
  <div class="main-content">
    <div id="china-map" style="width: 100%; height: 600px;"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  data() {
    return {
      stations: [],
      routes: []
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      // 获取站点数据
      this.$request.get('/station/selectAll').then(res => {
        if (res.code === '200') {
          this.stations = res.data;
          this.checkAndInitChart();
        } else {
          this.$message.error(res.msg);
        }
      });

      // 获取路径数据
      this.$request.get('/route/selectAll').then(res => {
        if (res.code === '200') {
          this.routes = res.data;
          this.checkAndInitChart();
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    checkAndInitChart() {
      if (this.stations.length > 0 && this.routes.length > 0) {
        this.initChart();
      }
    },
    initChart() {
      var chartDom = document.getElementById('china-map');
      var myChart = echarts.init(chartDom);

      myChart.showLoading();
      fetch('https://geo.datav.aliyun.com/areas/bound/100000_full.json')
          .then(response => response.json())
          .then(chinaJson => {
            echarts.registerMap('china', chinaJson);
            myChart.hideLoading();

            const geoCoordMap = {};
            const data = this.stations.map(station => {
              geoCoordMap[station.name] = [station.longitude, station.latitude];
              return {
                name: station.name,
                value: [station.longitude, station.latitude],
                storage: station.storage,
                disableFlag: station.disableFlag
              };
            });

            const createLinkData = (links) => {
              const res = [];
              links.forEach(link => {
                const fromCoord = geoCoordMap[link.fromStationName];
                const toCoord = geoCoordMap[link.toStationName];
                if (fromCoord && toCoord) {
                  res.push({
                    fromName: link.fromStationName,
                    toName: link.toStationName,
                    coords: [fromCoord, toCoord],
                    disableFlag: link.disableFlag,
                    routeType: link.routeType
                  });
                }
              });
              return res;
            };

            const flightLinks = createLinkData(this.routes.filter(route => route.routeType === 'flight'));
            const roadLinks = createLinkData(this.routes.filter(route => route.routeType === 'road'));
            const railLinks = createLinkData(this.routes.filter(route => route.routeType === 'rail'));

            const trainPath = 'path://M1705.1,317.7c-90.6,0-164.1-73.5-164.1-164.1S1614.5-10.5,1705.1-10.5S1869.2,63,1869.2,153.6S1795.7,317.7,1705.1,317.7z M1705.1,96.4c-31.6,0-57.3,25.7-57.3,57.3s25.7,57.3,57.3,57.3s57.3-25.7,57.3-57.3S1736.7,96.4,1705.1,96.4z';
            const truckPath = 'path://M1317.6,616.5H410.2c-27.8,0-50.4-22.6-50.4-50.4v-406c0-27.8,22.6-50.4,50.4-50.4h907.3c27.8,0,50.4,22.6,50.4,50.4v406C1368,593.9,1345.4,616.5,1317.6,616.5z M1091.6,616.5h-574c-27.8,0-50.4,22.6-50.4,50.4v70.2c0,27.8,22.6,50.4,50.4,50.4h574c27.8,0,50.4-22.6,50.4-50.4v-70.2C1142,639.1,1119.4,616.5,1091.6,616.5z M492.5,566.1c55.5,0,100.4-44.9,100.4-100.4s-44.9-100.4-100.4-100.4s-100.4,44.9-100.4,100.4S437,566.1,492.5,566.1zM1091.6,566.1c55.5,0,100.4-44.9,100.4-100.4s-44.9-100.4-100.4-100.4s-100.4,44.9-100.4,100.4S1036.1,566.1,1091.6,566.1z';

            const offsetCoords = (coords, offset) => {
              const theta = Math.atan2(coords[1][1] - coords[0][1], coords[1][0] - coords[0][0]);
              const dx = offset * Math.sin(theta);
              const dy = offset * Math.cos(theta);
              return [
                [coords[0][0] - dx, coords[0][1] + dy],
                [coords[1][0] - dx, coords[1][1] + dy],
              ];
            };

            const option = {
              title: {
                text: '中国地图',
                left: 'center'
              },
              tooltip: {
                trigger: 'item',
                formatter: function (params) {
                  if (params.seriesType === 'scatter') {
                    const station = params.data;
                    return `${station.name}<br/>经度: ${station.value[0]}<br/>纬度: ${station.value[1]}<br/>仓储量: ${station.storage}<br/>可用性: ${station.disableFlag === '0' ? '可用' : '不可用'}`;
                  } else if (params.seriesType === 'lines') {
                    const link = params.data;
                    const routeType = link.routeType === 'flight' ? '航空' : (link.routeType === 'road' ? '公路' : '铁路');
                    return `路径: ${link.fromName} > ${link.toName}<br/>类型: ${routeType}<br/>可用性: ${link.disableFlag === '0' ? '可用' : '不可用'}`;
                  }
                }
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
                  name: '站点',
                  type: 'scatter',
                  coordinateSystem: 'geo',
                  data: data,
                  symbolSize: 16,
                  zlevel: 4,
                  label: {
                    formatter: '{b}',
                    position: 'right',
                    show: false
                  },
                  emphasis: {
                    label: {
                      show: true
                    }
                  },
                  itemStyle: {
                    normal: {
                      color: function (params) {
                        return params.data.disableFlag === '1' ? 'red' : '#738ace';
                      }
                    }
                  }
                },
                {
                  name: '飞行路线',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 3,
                  large: true,
                  lineStyle: {
                    normal: {
                      color: function (params) {
                        return params.data.disableFlag === '1' ? 'red' : '#4fa977';
                      },
                      width: 4,
                      opacity: 0.6,
                      curveness: 0.4
                    }
                  },
                  data: flightLinks
                },
                {
                  name: '飞行路线动态',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 3,
                  effect: {
                    show: true,
                    period: 2,
                    trailLength: 0.7,
                    color: '#dffa92',
                    symbolSize: 5
                  },
                  lineStyle: {
                    normal: {
                      color: '#a6c84c',
                      width: 0,
                      curveness: 0.4
                    }
                  },
                  data: flightLinks.filter(link => link.disableFlag === '0')
                },
                {
                  name: '公路',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 2,
                  large: true,
                  effect: {
                    show: true,
                    period: 6,
                    color: '#ffffff',
                    symbol: truckPath,
                    symbolSize: 10,
                    trailLength: 0
                  },
                  lineStyle: {
                    normal: {
                      color: function (params) {
                        return params.data.disableFlag === '1' ? 'red' : '#ffde00';
                      },
                      width: 5,
                      opacity: 0.8,
                      curveness: 0.1,
                    }
                  },
                  data: roadLinks.map(link => ({ ...link, coords: offsetCoords(link.coords, 0.1) }))
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
                    color: '#ffffff',
                    symbolSize: 10,
                    trailLength: 0
                  },
                  lineStyle: {
                    normal: {
                      color: function (params) {
                        return params.data.disableFlag === '1' ? 'red' : '#000000';
                      },
                      width: 5
                    }
                  },
                  progressiveThreshold: 500,
                  progressive: 200,
                  data: railLinks.map(link => ({ ...link, coords: offsetCoords(link.coords, -0.1) }))
                },
                {
                  name: '铁路背景',
                  type: 'lines',
                  coordinateSystem: 'geo',
                  zlevel: 1,
                  large: true,
                  lineStyle: {
                    color: '#fff',
                    width: 4,
                    type: 'dashed'
                  },
                  progressiveThreshold: 500,
                  progressive: 200,
                  data: railLinks.map(link => ({ ...link, coords: offsetCoords(link.coords, -0.1) }))
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

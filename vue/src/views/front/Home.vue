<template>
  <div class="main-content">
    <div class="control-panel" v-if="user.role === 'USER'">
      <h3 style="text-align: center">用户面板</h3>
      <el-tabs v-model="activeName" @tab-click="handleSurfaceClick">
        <el-tab-pane label="模拟调货" name="first"></el-tab-pane>
        <el-tab-pane label="调货记录" name="second"></el-tab-pane>
      </el-tabs>
      <div v-if="activeName === 'first'" class="simulate">
        <div style="margin-left: 25px">
          <el-steps :active="activeStep" finish-status="success">
            <el-step title="选择地点"></el-step>
            <el-step title="选择货物"></el-step>
            <el-step title="选择方案"></el-step>
          </el-steps>
        </div>

        <el-select v-model="selectedStation" style="width: 100%" placeholder="你在哪？" @change="updateStep(1)">
          <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id"></el-option>
        </el-select>
        <el-select v-model="selectedCategory" style="margin-top: 10px; width: 100%" placeholder="请选择你要调货货物类型" @change="updateStep(2); disableGoodsSelect = true" :disabled="disableCategorySelect">
          <el-option v-for="category in categories" :key="category" :label="category" :value="category"></el-option>
        </el-select>
        <el-select v-model="selectedGoods" style="margin-top: 10px; width: 100%" placeholder="请选择你要调货货物名称" @change="updateStep(2); disableCategorySelect = true" :disabled="disableGoodsSelect">
          <el-option v-for="goods in goodsList" :key="goods.id" :label="goods.name" :value="goods.name"></el-option>
        </el-select>
        <el-input v-model="quantity" style="margin-top: 10px" placeholder="请输入你要调货货物数量(单位:吨)" @input="updateStep(2)"></el-input>

        <div style="margin-top: 10px">选择运输方案</div>
        <el-tabs v-model="transportScheme" type="card" style="margin-top: 10px" @tab-click="updateStep(3)">
          <el-tab-pane label="最快速" name="fastest">最快速</el-tab-pane>
          <el-tab-pane label="最经济" name="economic">最经济</el-tab-pane>
          <el-tab-pane label="蚁群最短路径" name="Ant">蚁群最短路径</el-tab-pane>
        </el-tabs>

        <div style="display: flex; justify-content: center; margin-top: 10px">
          <el-button type="primary" @click="simulateDispatch">模拟调货</el-button>
          <el-button type="warning" @click="resetFields" style="margin-left: 10px">重置</el-button>
        </div>

        <div style="margin-top: 20px">模拟调货进度</div>
        <el-progress :percentage="progress"></el-progress>
        <div class="result" v-html="result"></div>
      </div>
      <div v-if="activeName === 'second'" class="records">
        <div class="records-text" v-html="result"></div>
      </div>
    </div>

    <div class="control-panel" v-if="user.role === 'ADMIN'">
      <h3 style="text-align: center">管理员面板</h3>
    </div>

    <div id="china-map" style="width: 100%; height: 600px;"></div>

    <!-- 新增的站点货物信息块 -->
    <el-dialog :visible.sync="goodsDialogVisible" title="站点货物信息" width="500px">
      <div>
        <h4>{{ selectedStationName }}</h4>
        <el-table :data="stationGoods" style="width: 100%; max-height: 400px; overflow-y: auto;">
          <el-table-column prop="name" label="货物名称"></el-table-column>
          <el-table-column prop="category" label="货物类别"></el-table-column>
          <el-table-column prop="quantity" label="货物量"></el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="goodsDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="graphDialogVisible" title="蚁群算法生成的图" width="500px">
      <div id="graph-container" style="width: 100%; height: 400px;"></div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="graphDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  data() {
    return {
      stations: [],
      routes: [],
      categories: [],
      goodsList: [],
      selectedCity: '',
      selectedAntCity: '',
      selectedStation: '',
      selectedCategory: '',
      selectedGoods: '',
      quantity: '',
      disableGoodsSelect: false,
      disableCategorySelect: false,
      transportScheme: 'fastest',
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      progress: 0,
      result: '',
      antResult: '',
      activeName: 'first',
      activeStep: 0,
      myChart: null,
      originalOption: null,
      goodsDialogVisible: false,  // 新增
      selectedStationName: '',  // 新增
      stationGoods: [],  // 新增
      graphDialogVisible: false,  // 新增
      graphData: null,  // 新增
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.$request.get('/station/selectAll').then((res) => {
        if (res.code === '200') {
          this.stations = res.data;
          this.checkAndInitChart();
        } else {
          this.$message.error(res.msg);
        }
      });

      this.$request.get('/route/selectAll').then((res) => {
        if (res.code === '200') {
          this.routes = res.data;
          this.checkAndInitChart();
        } else {
          this.$message.error(res.msg);
        }
      });

      this.$request.get('/goods/categories').then((res) => {
        if (res.code === '200') {
          this.categories = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });

      this.$request.get('/goods/selectAll').then((res) => {
        if (res.code === '200') {
          this.goodsList = res.data;
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
      this.myChart = echarts.init(chartDom);

      this.myChart.showLoading();
      fetch('https://geo.datav.aliyun.com/areas/bound/100000_full.json')
          .then((response) => response.json())
          .then((chinaJson) => {
            echarts.registerMap('china', chinaJson);
            this.myChart.hideLoading();

            const geoCoordMap = {};
            const data = this.stations.map((station) => {
              geoCoordMap[station.name] = [station.longitude, station.latitude];
              return {
                name: station.name,
                value: [station.longitude, station.latitude],
                storage: station.storage,
                disableFlag: station.disableFlag,
              };
            });

            const createLinkData = (links) => {
              const res = [];
              links.forEach((link) => {
                const fromCoord = geoCoordMap[link.fromStationName];
                const toCoord = geoCoordMap[link.toStationName];
                if (fromCoord && toCoord) {
                  res.push({
                    fromName: link.fromStationName,
                    toName: link.toStationName,
                    coords: [fromCoord, toCoord],
                    disableFlag: link.disableFlag,
                    routeType: link.routeType,
                  });
                }
              });
              return res;
            };

            const flightLinks = createLinkData(this.routes.filter((route) => route.routeType === 'flight'));
            const roadLinks = createLinkData(this.routes.filter((route) => route.routeType === 'road'));
            const railLinks = createLinkData(this.routes.filter((route) => route.routeType === 'rail'));

            const trainPath =
                'path://M1705.1,317.7c-90.6,0-164.1-73.5-164.1-164.1S1614.5-10.5,1705.1-10.5S1869.2,63,1869.2,153.6S1795.7,317.7,1705.1,317.7z M1705.1,96.4c-31.6,0-57.3,25.7-57.3,57.3s25.7,57.3,57.3,57.3s57.3-25.7,57.3-57.3S1736.7,96.4,1705.1,96.4z';
            const truckPath =
                'path://M1317.6,616.5H410.2c-27.8,0-50.4-22.6-50.4-50.4v-406c0-27.8,22.6-50.4,50.4-50.4h907.3c27.8,0,50.4,22.6,50.4,50.4v406C1368,593.9,1345.4,616.5,1317.6,616.5z M1091.6,616.5h-574c-27.8,0-50.4,22.6-50.4,50.4v70.2c0,27.8,22.6,50.4,50.4,50.4h574c27.8,0,50.4-22.6,50.4-50.4v-70.2C1142,639.1,1119.4,616.5,1091.6,616.5z M492.5,566.1c55.5,0,100.4-44.9,100.4-100.4s-44.9-100.4-100.4-100.4s-100.4,44.9-100.4,100.4S437,566.1,492.5,566.1zM1091.6,566.1c55.5,0,100.4-44.9,100.4-100.4s-44.9-100.4-100.4-100.4s-100.4,44.9-100.4,100.4S1036.1,566.1,1091.6,566.1z';

            const offsetCoords = (coords, offset) => {
              const theta = Math.atan2(coords[1][1] - coords[0][1], coords[1][0] - coords[0][0]);
              const dx = offset * Math.sin(theta);
              const dy = offset * Math.cos(theta);
              return [
                [coords[0][0] - dx, coords[0][1] + dy],
                [coords[1][0] - dx, coords[1][1] + dy],
              ];
            };

            this.originalOption = {
              title: {
                text: '中国地图',
                left: 'center',
              },
              tooltip: {
                trigger: 'item',
                formatter: function (params) {
                  if (params.seriesType === 'scatter') {
                    const station = params.data;
                    return `${station.name}<br/>经度: ${station.value[0]}<br/>纬度: ${station.value[1]}<br/>仓储量: ${station.storage}<br/>可用性: ${
                        station.disableFlag === '0' ? '可用' : '不可用'
                    }`;
                  } else if (params.seriesType === 'lines') {
                    const link = params.data;
                    const routeType =
                        link.routeType === 'flight' ? '航空' : link.routeType === 'road' ? '公路' : '铁路';
                    return `路径: ${link.fromName} > ${link.toName}<br/>类型: ${routeType}<br/>可用性: ${
                        link.disableFlag === '0' ? '可用' : '不可用'
                    }`;
                  }
                },
              },
              geo: {
                map: 'china',
                roam: true,
                label: {
                  show: true,
                },
                itemStyle: {
                  normal: {
                    borderColor: 'rgba(0, 0, 0, 0.2)',
                  },
                  emphasis: {
                    areaColor: null,
                    shadowOffsetX: 0,
                    shadowOffsetY: 0,
                    shadowBlur: 20,
                    borderWidth: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)',
                  },
                },
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
                    show: false,
                  },
                  emphasis: {
                    label: {
                      show: true,
                    },
                  },
                  itemStyle: {
                    normal: {
                      color: function (params) {
                        return params.data.disableFlag === '1' ? 'red' : '#738ace';
                      },
                    },
                  },
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
                      curveness: 0.4,
                    },
                  },
                  data: flightLinks,
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
                    symbolSize: 5,
                  },
                  lineStyle: {
                    normal: {
                      color: '#a6c84c',
                      width: 0,
                      curveness: 0.4,
                    },
                  },
                  data: flightLinks.filter((link) => link.disableFlag === '0'),
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
                    trailLength: 0,
                  },
                  lineStyle: {
                    normal: {
                      color: function (params) {
                        return params.data.disableFlag === '1' ? 'red' : '#ffde00';
                      },
                      width: 5,
                      opacity: 0.8,
                      curveness: 0.1,
                    },
                  },
                  data: roadLinks.map((link) => ({
                    ...link,
                    coords: offsetCoords(link.coords, 0.1),
                  })),
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
                    trailLength: 0,
                  },
                  lineStyle: {
                    normal: {
                      color: function (params) {
                        return params.data.disableFlag === '1' ? 'red' : '#000000';
                      },
                      width: 5,
                    },
                  },
                  progressiveThreshold: 500,
                  progressive: 200,
                  data: railLinks.map((link) => ({
                    ...link,
                    coords: offsetCoords(link.coords, -0.1),
                  })),
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
                    type: 'dashed',
                  },
                  progressiveThreshold: 500,
                  progressive: 200,
                  data: railLinks.map((link) => ({
                    ...link,
                    coords: offsetCoords(link.coords, -0.1),
                  })),
                },
              ],
            };

            this.myChart.setOption(this.originalOption);

            // 添加点击事件监听器
            this.myChart.on('click', (params) => {
              if (params.componentType === 'series' && params.seriesType === 'scatter') {
                const stationName = params.data.name;
                const station = this.stations.find(station => station.name === stationName);
                if (station) {
                  this.fetchStationGoods(station.id, station.name);
                }
              }
            });
          });
    },
    disableGoods() {
      this.disableGoodsSelect = !!this.selectedCategory;
      if (this.disableGoodsSelect) this.selectedGoods = '';
    },
    disableCategory() {
      this.disableCategorySelect = !!this.selectedGoods;
      if (this.disableCategorySelect) this.selectedCategory = '';
    },
    simulateDispatch() {
      const payload = {
        targetStationId: this.selectedStation,
        goodsName: this.selectedGoods ? this.selectedGoods : this.selectedCategory,
        quantity: parseInt(this.quantity),
      };

      const queryString = new URLSearchParams(payload).toString();

      this.progress = 0;
      this.result = '模拟进行中...';

      let apiEndpoint = '/dispatch/simulateFastest';
      if (this.transportScheme === 'economic') {
        apiEndpoint = '/dispatch/simulateEconomic';
      } else if (this.transportScheme === 'Ant') {
        apiEndpoint = '/dispatch/simulateAntColony';
      }

      this.$request.post(`${apiEndpoint}?${queryString}`).then((res) => {
        console.log('Response from backend:', res);

        if (res.code === '200') {
          const [dispatchResult, operations] = res.data;
          console.log('Dispatch Result:', dispatchResult);
          console.log('Operations:', operations);

          this.progress = (dispatchResult.totalDispatched / payload.quantity) * 100;
          let resultHtml = `<p>调货日志:</p><ul>${dispatchResult.logs
              .map((log) => `<li>${log}</li>`)
              .join('')}</ul><p>总共调取: ${dispatchResult.totalDispatched} 吨货物</p><p>总需求量: ${payload.quantity} 吨货物</p>`;

          if (this.transportScheme !== 'Ant') {
            resultHtml += `<p>最长时间: ${dispatchResult.maxTime.toFixed(2)} 小时</p><p>总成本: ${dispatchResult.totalCost.toFixed(2)} 元</p>`;
          }

          this.result = resultHtml;

          if (dispatchResult.usedPaths && dispatchResult.usedPaths.length > 0) {
            const paths = this.extractPaths(dispatchResult.usedPaths);
            this.highlightPaths(paths);
          }
        } else {
          this.$message.error(res.msg);
          this.result = '模拟失败，请重试';
        }
      }).catch((error) => {
        console.error(error);
        this.$message.error('模拟失败，请重试');
        this.result = '模拟失败，请重试';
      });
    },
    resetFields() {
      this.selectedStation = '';
      this.selectedCategory = '';
      this.selectedGoods = '';
      this.quantity = '';
      this.disableGoodsSelect = false;
      this.disableCategorySelect = false;
      this.activeStep = 0;
      this.progress = 0;
      this.result = '';
      if (this.myChart) {
        this.myChart.setOption(this.originalOption);
      }
    },
    handleSurfaceClick(tab) {
      this.activeName = tab.name;
    },
    updateStep(step) {
      if (step === 1 && this.selectedStation) {
        this.activeStep = 1;
      } else if (step === 2 && (this.selectedCategory || this.selectedGoods) && this.quantity) {
        this.activeStep = 2;
      } else if (step === 3 && this.transportScheme) {
        this.activeStep = 3;
      }
    },
    formatResult(data) {
      let resultHtml = '<h4>调货结果</h4>';
      resultHtml += `<p>总共调货: ${data.totalDispatched} 吨</p>`;
      resultHtml += '<h5>详细记录:</h5>';
      resultHtml += '<ul>';
      data.logs.forEach((log) => {
        resultHtml += `<li>${log}</li>`;
      });
      resultHtml += '</ul>';
      return resultHtml;
    },
    submitRequest() {
      this.$message.success('请求已发送');
    },
    requestDispatch() {
      this.$message.success('调货请求已发送');
    },
    testAntColony() {
      const selectedStation = this.stations.find((station) => station.id === this.selectedAntCity);
      if (!selectedStation) {
        this.$message.error('请选择有效的城市');
        return;
      }

      const payload = {
        startCity: selectedStation.name,
      };

      const queryString = new URLSearchParams(payload).toString();

      this.antResult = '蚁群算法计算中...';

      this.$request.post(`/dispatch/findShortestPathUsingAntColony?${queryString}`).then((res) => {
        console.log('Response from backend:', res);

        if (res.code === '200') {
          const path = res.data;
          console.log('Ant Colony Path:', path);

          this.antResult = `<p>蚁群算法最短路径:</p><ul>${path.map((node) => `<li>${node.name}</li>`).join('')}</ul>`;
        } else {
          this.$message.error(res.msg);
          this.antResult = '蚁群算法计算失败，请重试';
        }
      }).catch((error) => {
        console.error(error);
        this.$message.error('蚁群算法计算失败，请重试');
        this.antResult = '蚁群算法计算失败，请重试';
      });
    },
    extractPaths(usedPaths) {
      const paths = [];
      let currentNode = usedPaths[0];
      while (currentNode && currentNode.previous) {
        paths.push({
          from: currentNode.previous.stationId,
          to: currentNode.stationId,
          routeType: currentNode.routeType,
        });
        currentNode = currentNode.previous;
      }
      return paths;
    },
    highlightPaths(paths) {
      if (!this.myChart) return;

      const geoCoordMap = {};
      this.stations.forEach((station) => {
        geoCoordMap[station.id] = [station.longitude, station.latitude];
      });

      const highlightedLinks = paths.map((path) => {
        return {
          fromName: this.stations.find((station) => station.id === path.to).name,
          // this.stations.find((station) => station.id === path.from).name,
          toName: this.stations.find((station) => station.id === path.from).name,
          coords: [geoCoordMap[path.from], geoCoordMap[path.to]],
          routeType: path.routeType,
        };
      });

      const updateSeries = (series, routeType) => {
        return {
          ...series,
          data: series.data.map((link) => {
            const highlightedLink = highlightedLinks.find(
                (hl) =>
                    hl.fromName === link.fromName &&
                    hl.toName === link.toName &&
                    hl.routeType === routeType
            );
            if (highlightedLink) {
              return {
                ...link,
                lineStyle: {
                  ...link.lineStyle,
                  color: 'purple',
                  opacity: 1,
                },
              };
            }
            return {
              ...link,
              lineStyle: {
                ...link.lineStyle,
                opacity: 0.1,
              },
            };
          }),
        };
      };

      const option = {
        series: this.originalOption.series.map((series) => {
          if (series.name === '飞行路线') {
            return updateSeries(series, 'flight');
          } else if (series.name === '公路') {
            return updateSeries(series, 'road');
          } else if (series.name === '铁路') {
            return updateSeries(series, 'rail');
          } else if (series.name === '飞行路线动态') {
            return {
              ...series,
              data: [],
            };
          }
          return series;
        }),
      };

      this.myChart.setOption(option);
    },
    fetchStationGoods(stationId, stationName) {
      this.$request.get(`/station-goods/selectByStationId/${stationId}`).then((res) => {
        if (res.code === '200') {
          this.stationGoods = res.data;
          this.selectedStationName = stationName;
          this.goodsDialogVisible = true;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
  },
};
</script>

<style scoped>
#china-map {
  width: 100%;
  height: 100%;
}
@import '@/assets/css/home.css';
</style>

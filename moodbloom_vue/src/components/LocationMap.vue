<template>
  <div class="location-wrapper">
    <div id="map-container" class="map-container"></div> <!-- 地图容器，覆盖显示 -->
    <div class="location-info">{{ statusMessage }}</div> <!-- 显示当前位置信息 -->
  </div>
</template>

<script>
/* global AMap */
export default {
  data() {
    return {
      statusMessage: "定位中...",
      resultMessage: "",
      map: null  // 保存地图实例
    };
  },
  mounted() {
    this.loadGeolocation();
  },
  methods: {
    loadGeolocation() {
      // 检查是否已经加载了高德地图脚本，避免重复加载
      if (!window.AMap) {
        window._AMapSecurityConfig = {
          securityJsCode: "37652e7f4e6a36d44a26b9135b35ecbd"
        };

        const script = document.createElement("script");
        script.src = "https://webapi.amap.com/maps?v=2.0&key=f6ee75cf708809feecc24c159cc6ebb1";
        script.onload = () => {
          this.initMap();  // 初始化地图
          this.initGeolocation();
        };
        document.head.appendChild(script);
      } else {
        this.initMap(); // 如果已经加载，直接初始化地图
        this.initGeolocation();
      }
    },
    initMap() {
      // 初始化地图，绑定到 #map-container 容器
      this.map = new AMap.Map("map-container", {
        zoom: 15,  // 地图缩放等级
        center: [116.397428, 39.90923],  // 初始地图中心点
      });
    },
    initGeolocation() {
      AMap.plugin("AMap.Geolocation", () => {
        const geolocation = new AMap.Geolocation({
          enableHighAccuracy: true,
          timeout: 10000,
          needAddress: true
        });
        geolocation.getCurrentPosition((status, result) => {
          if (status === "complete") {
            this.onComplete(result);
          } else {
            this.onError(result);
          }
        });
      });
    },
    onComplete(data) {
      this.statusMessage = `${data.formattedAddress}`; // 显示地址信息
      const lngLat = `${data.position.lng}, ${data.position.lat}`;

      // 将地图的中心点设置为当前定位的坐标
      this.map.setCenter([data.position.lng, data.position.lat]);

      // 在地图上添加定位点标记
      const marker = new AMap.Marker({
        position: new AMap.LngLat(data.position.lng, data.position.lat)
      });
      this.map.add(marker);
      
      // 从 localStorage 中获取 user_id
      const userId = localStorage.getItem('userId');
      if (!userId) {
        console.error('用户未登录或 user_id 未找到');
        return;
      }

      // 将位置信息通过POST请求发送到后端
      fetch(`https://124.222.156.13/api/location/save?userId=${userId}`, {  // 将 userId 作为请求参数传递
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          location_msg: data.formattedAddress,
          lonlat: lngLat
        })
      })
        .then(response => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error("Failed to save location");
          }
        })
        .then(data => {
          console.log("位置信息已成功保存", data);
        })
        .catch(error => {
          console.error("保存位置信息失败：", error.message);
        });
    },
    onError(data) {
      this.statusMessage = "定位失败";
      console.error("定位失败：", data);
    }
  }
};
</script>

<style scoped>
/* 为地图设置为覆盖层，并确保box6的布局不变 */
.location-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  padding-bottom: 70%; 
}

.map-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.location-info {
  position: absolute;
  bottom: 10px;
  left: 10px;
  z-index: 2;
  color: #fff;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 14px;
}
</style>

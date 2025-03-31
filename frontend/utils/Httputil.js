// utils/http.js
class Http {
  /**
   * 构造函数
   */
  constructor() {
    this.baseUrl = 'http://localhost:8080'; // 替换为你的后端基础URL
  }

  /**
   * GET 请求
   * @param {String} url - 请求路径
   * @param {Object} data - 请求参数
   * @param {Function} success - 成功回调
   * @param {Function} fail - 失败回调
   */
  get(url, data = {}, success, fail) {
    this.request('GET', url, data, success, fail);
  }

  /**
   * POST 请求
   * @param {String} url - 请求路径
   * @param {Object} data - 请求体数据
   * @param {Function} success - 成功回调
   * @param {Function} fail - 失败回调
   */
  post(url, data = {}, success, fail) {
    this.request('POST', url, data, success, fail);
  }

  /**
   * PUT 请求
   * @param {String} url - 请求路径
   * @param {Object} data - 请求体数据
   * @param {Function} success - 成功回调
   * @param {Function} fail - 失败回调
   */
  put(url, data = {}, success, fail) {
    this.request('PUT', url, data, success, fail);
  }

  /**
   * DELETE 请求
   * @param {String} url - 请求路径
   * @param {Object} data - 请求参数
   * @param {Function} success - 成功回调
   * @param {Function} fail - 失败回调
   */
  delete(url, data = {}, success, fail) {
    this.request('DELETE', url, data, success, fail);
  }

  /**
   * 发起请求
   * @param {String} method - 请求方法
   * @param {String} url - 请求路径
   * @param {Object} data - 请求数据
   * @param {Function} success - 成功回调
   * @param {Function} fail - 失败回调
   */
  request(method, url, data = {}, success,
    fail = (err) => {
      console.error('请求失败:', err);

      wx.showToast({
        title: '网络错误',
        icon: 'none',
        duration: 2000
      });
    }
  ) {
    wx.request({
      url: this.baseUrl + url,
      method: method,
      data: data,
      header: {
        'Authorization': wx.getStorageSync("token") || null,
        'Content-Type': 'application/json'
      },
      success: (res) => {
        console.log("服务器返回:", res);
        if (res.statusCode >= 200 && res.statusCode < 300) {
          // 请求成功
          if (typeof success === 'function') {
            success(res.data);
          }
        } else {
          // 请求失败
          if (typeof fail === 'function') {
            fail(res);
          } else {
            wx.showToast({
              title: `请求失败: ${res.statusCode}`,
              icon: 'none',
              duration: 2000
            });
          }
        }
      },
      fail: (err) => {
        // 网络请求失败
        if (typeof fail === 'function') {
          fail(err);
        } else {
          wx.showToast({
            title: '网络请求失败',
            icon: 'none',
            duration: 2000
          });
        }
      }
    });
  }
}

// 导出实例
const http = new Http();
export default http;
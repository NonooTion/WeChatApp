// pages/login/login.js
import http from "../../utils/Httputil"
Page({

  /**
   * 页面的初始数据
   */
  data: {
    loginForm:{
      usernameOrPhone:"",
      password:""
    }
  },

  navigateToRegister()
  {
    console.log("跳转到注册");
    wx.navigateTo({
      url: '/pages/register/register',
    })
  },

  navigateToForgot()
  {
    console.log("跳转到忘记密码");
    wx.navigateTo({
      url: '/pages/forget/forget',
    })
  },

    // 绑定用户名或手机号输入
    bindUsernameOrPhoneInput(e) {
      this.setData({
        'loginForm.usernameOrPhone': e.detail.value
      });
    },
  
    // 绑定密码输入
    bindPasswordInput(e) {
      this.setData({
        'loginForm.password': e.detail.value
      });
    },

  onLogin() {
    console.log("开始登录");

    // 获取用户输入的用户名或手机号和密码
    const { usernameOrPhone, password } = this.data.loginForm;
    
    // 检查输入是否为空
    if (usernameOrPhone=="" || password=="") {
      wx.showToast({
        title: '请输入用户名或手机号和密码',
        icon: 'none',
        duration: 2000
      });
      return;
    }

    // 构造请求数据
    const loginData = {
      usernameOrPhone: usernameOrPhone,
      password: password
    };
    console.log(loginData)
    
    // 发起登录请求
    const http_success = (res) => {
      console.log('登录请求响应:', res);
      
      // 检查响应状态码
      if (res.code === 200) {
        // 登录成功，获取token
        const token = res.data.token; // 假设后端返回的token字段名为token
        console.log(res)
        if (token) {
          // 保存token到本地存储
          wx.setStorageSync('token', token);
          // 跳转到主页
          wx.switchTab({
            url: '/pages/index/index',
          });

          wx.showToast({
            title: '登录成功',
            icon: 'success',
            duration: 2000
          });
        } else {
          // 登录失败，提示错误信息
          wx.showToast({
            title: res.message || '登录失败',
            icon: 'none',
            duration: 2000
          });
        }
      } else {
        // 请求失败，提示错误信息
        wx.showToast({
          title: res.message,
          icon: 'none',
          duration: 2000
        });
      }
    }
    http.post("/user/login",loginData,http_success)
  },
})
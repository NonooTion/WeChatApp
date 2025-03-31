// pages/register/register.js
import http from "../../utils/Httputil"
Page({

  /**
   * 页面的初始数据
   */
  data: {
    registerForm:{
      username:"123",
      phone:"",
      password:"",
      confirmPassword:""
    }
  },
  navigateToLogin()
  {
    wx.navigateTo({
      url: '/pages/login/login',
    })
  },
      // 注册函数
      onRegister() {
        console.log("开始注册");
    
        // 获取用户输入的信息
        const { username, phone, password, confirmPassword } = this.data.registerForm;
    
        // 验证输入是否为空
        if (!username || !phone || !password || !confirmPassword) {
          wx.showToast({
            title: '请输入所有信息',
            icon: 'none',
            duration: 2000
          });
          return;
        }
    
        // 验证密码是否一致
        if (password !== confirmPassword) {
          wx.showToast({
            title: '两次输入的密码不一致',
            icon: 'none',
            duration: 2000
          });
          return;
        }
    
        // 构造请求数据
        const registerData = {
          username: username,
          phone: phone,
          password: password
        };
        const http_success= (res)=>{
          console.log('注册请求响应:', res);
          // 检查响应状态码
            // 注册成功
            if (res.code === 200) {
              wx.showToast({
                title: '注册成功',
                icon: 'success',
                duration: 2000
              });
  
              // 跳转到登录页面
              wx.navigateTo({
                url: '/pages/login/login',
              });
            } else {
              // 注册失败，提示错误信息
              wx.showToast({
                title: res.message || '注册失败',
                icon: 'none',
                duration: 2000
              });
            }  
        }
        // 发起注册请求
        http.post("/user/register",registerData,http_success)
        },
          // 绑定用户名输入
          onUsernameInput(e) {
            this.setData({
              'registerForm.username': e.detail.value
            });
          },
        
          // 绑定手机号输入
          onContactInput(e) {
            this.setData({
              'registerForm.phone': e.detail.value
            });
          },
        
          // 绑定密码输入
          onPasswordInput(e) {
            this.setData({
              'registerForm.password': e.detail.value
            });
          },
        
          // 绑定确认密码输入
          onConfirmPasswordInput(e) {
            this.setData({
              'registerForm.confirmPassword': e.detail.value
            });
          }
})
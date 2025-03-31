// pages/forget-password/forget-password.js
import http from "../../utils/Httputil"
import { formatTime } from "../../utils/util"

Page({
  /**
   * 页面的初始数据
   */
  data: {
    forgetForm: {
      username: "",
      newPassword: "",
      confirmNewPassword: ""
    },
  },



  // 跳转到登录页面
  navigateToLogin() {
    wx.navigateTo({
      url: '/pages/login/login',
    });
  },

  // 重置密码
  onResetPassword() {
    const { username, newPassword, confirmNewPassword } = this.data.forgetForm;
    
    // 验证输入
    if (!username) {
      wx.showToast({
        title: '请输入用户名',
        icon: 'none',
        duration: 2000
      });
      return;
    }
    
    if (!newPassword || !confirmNewPassword) {
      wx.showToast({
        title: '请输入新密码',
        icon: 'none',
        duration: 2000
      });
      return;
    }
    
    if (newPassword !== confirmNewPassword) {
      wx.showToast({
        title: '两次输入的密码不一致',
        icon: 'none',
        duration: 2000
      });
      return;
    }

    // 构造请求数据
    const resetData = {
      username: username,
      newPassword: newPassword
    };
    const http_success= (res) => {
      if (res.code === 200) {
        wx.showToast({
          title: '密码重置成功',
          icon: 'success',
          duration: 2000
        });
        
        // 跳转到登录页面
        setTimeout(() => {
          wx.navigateTo({
            url: '/pages/login/login',
          });
        }, 2000);
      } else {
        wx.showToast({
          title: res.message || '密码重置失败',
          icon: 'none',
          duration: 2000
        });
      }
    }
    // 发起重置密码请求
    http.post("/user/forget-password",resetData,http_success)
  },

  // 绑定用户名输入
  bindUsernameInput(e) {
    this.setData({
      'forgetForm.username': e.detail.value
    });
  },

  // 绑定新密码输入
  bindNewPasswordInput(e) {
    this.setData({
      'forgetForm.newPassword': e.detail.value
    });
  },

  // 绑定确认新密码输入
  bindConfirmNewPasswordInput(e) {
    this.setData({
      'forgetForm.confirmNewPassword': e.detail.value
    });
  }
})
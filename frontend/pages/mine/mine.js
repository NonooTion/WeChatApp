import http from '../../utils/Httputil';

Page({
  data: {
    userInfo: {
      userId: null,
      username: '',
      name: '',
      email: '',
      phone: '',
      createTime: '',
      role: '',
      imgUrl: '',
      password: ''
    },
    showEditPopup: false,
    avatarTempFilePath: ''
  },
  onShow() {
    this.getUserInfo();
  },
  getUserInfo() {
    http.get("/user/get-by-user-id", {},
      (data) => {
        console.log("获取用户信息:", data);
        const userInfo = data.data;
        // 拼接完整的图片 URL
        const completeImgUrl = http.baseUrl + userInfo.imgUrl;
        this.setData({
          userInfo: {
            ...userInfo,
            imgUrl: completeImgUrl
          }
        });
        console.log(this.data.userInfo)
      }
    );
  },
  handleAvatarTap() {
    const that = this;
    wx.chooseImage({
        count: 1, // 只允许选择一张图片
        sizeType: ['original', 'compressed'], // 可以选择原图或压缩图
        sourceType: ['album', 'camera'], // 可以从相册或相机选择
        success(res) {
            const tempFilePaths = res.tempFilePaths;
            
            // 检查 tempFilePaths 是否存在且不为空
            if (tempFilePaths && tempFilePaths.length > 0) {
                that.setData({ avatarTempFilePath: tempFilePaths[0] });

                // 上传文件到服务器
                wx.uploadFile({
                    url: http.baseUrl+'/user/upload/avatar', // 替换为实际的上传接口
                    filePath: tempFilePaths[0],
                    name: 'file', // 文件参数名，必须与后端一致
                    header: {
                        'Authorization': wx.getStorageSync('token') // 添加授权信息
                    },
                    success(uploadRes) {
                        const response = JSON.parse(uploadRes.data);
                        if (response.code === 200) {
                            that.getUserInfo(); // 确保 this 指向正确
                            wx.showToast({
                                title: '上传成功',
                                icon: 'success'
                            });
                        } else {
                            wx.showToast({
                                title: '上传失败',
                                icon: 'none'
                            });
                        }
                    },
                    fail(err) {
                        console.error('上传失败:', err);
                        wx.showToast({
                            title: '上传失败',
                            icon: 'none'
                        });
                    }
                });
            } else {
                wx.showToast({
                    title: '未选择图片',
                    icon: 'none'
                });
            }
        },
        fail(err) {
            console.error('选择图片失败:', err);
            wx.showToast({
                title: '选择图片失败',
                icon: 'none'
            });
        }
    });
},
  showEditPopup() {
    this.setData({ showEditPopup: true });
  },
  closeEditPopup() {
    this.setData({ showEditPopup: false });
  },
  handleInputChange(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({
      [`userInfo.${field}`]: e.detail.value
    });
  },
  updateUserInfo() {
   const userInfo = this.data.userInfo
    const editData={
      name:userInfo.name,
      email:userInfo.email,
      phone:userInfo.phone,
    }
    userInfo.name=String(userInfo.name)
    userInfo.email=String(userInfo.email)
    userInfo.phone=String(userInfo.phone)
    if (!userInfo.name.trim() || !userInfo.email.trim() || !userInfo.phone.trim()) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' });
      return;
    }
    const queryParams = Object.keys(editData)
    .map(key => `${key}=${encodeURIComponent(editData[key])}`)
    .join('&');
    http.put(`/user/update?${queryParams}`, userInfo, (res) => {
      if (res.code === 200) {
        this.setData({ showEditPopup: false });
        wx.showToast({ title: '更新成功', icon: 'success' });
      } else {
        wx.showToast({ title: '更新失败', icon: 'none' });
      }
    });
  },
  logout() {
    wx.removeStorageSync('userInfo');
    wx.navigateTo({ url: '/pages/login/login' });
  },
  onPullDownRefresh()
  {
    console.log("123")
    this.data.userInfo=this.getUserInfo
  }
});

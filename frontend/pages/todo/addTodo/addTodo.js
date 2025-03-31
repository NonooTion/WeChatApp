import http from "../../../utils/Httputil"
Page({
  data: {
    showPriority: false,
    showTime: false,
    priorityOptions: ['高', '中', '低'],
    priorityText: '',
    remindTime: '',
    minDate: new Date().getTime(),
    maxDate: new Date().getTime() + 365 * 24 * 60 * 60 * 1000 * 10, // 未来10年
    content: '',
  },

  // 内容改变时
  onContentChange(e) {
    this.setData({
      content: e.detail
    });
  },

  // 显示优先级弹窗
  showPriorityPopup() {
    this.setData({
      showPriority: true
    });
  },

  // 关闭优先级弹窗
  closePriorityPopup() {
    this.setData({
      showPriority: false
    });
  },

  // 优先级确认
  onPriorityConfirm(e) {
    this.setData({
      priorityText: e.detail.value,
      showPriority: false
    });
  },

  // 显示时间弹窗
  showTimePopup() {
    this.setData({
      showTime: true
    });
  },

  // 关闭时间弹窗
  closeTimePopup() {
    this.setData({
      showTime: false
    });
  },

  // 时间确认
  onTimeConfirm(e) {
    const date = new Date(e.detail);
    
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 月份从0开始，需要加1
    const day = date.getDate();
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const seconds = date.getSeconds();
    
    // 格式化为 yyyy-MM-dd'T'HH:mm:ss
    const remindTime = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}T${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    this.setData({
      remindTime,
      showTime: false
    });
  },

  // 表单提交
  onSubmit() {
    const { content, priorityText, remindTime } = this.data;
    if (!content || !priorityText || !remindTime) {
      wx.showToast({
        title: '请填写完整信息',
        icon: 'none'
      });
      return;
    }
    
    const http_success = (res) => {
      console.log('添加待办事项请求响应:', res);
      
      // 检查响应状态码
      if (res.code === 200) {
          // 跳转到主页
          wx.switchTab({
            url: '/pages/todo/todo',
            success: function(res){
              const pages = getCurrentPages();
              const prevPage = pages[pages.length - 1];
              // 调用上一个页面的方法或获取其数据
              prevPage.getTodos();
            }
          });

          wx.showToast({
            title: '添加成功',
            icon: 'success',
            duration: 2000
          });
        } else {
          // 登录失败，提示错误信息
          wx.showToast({
            title: res.message || '添加失败',
            icon: 'none',
            duration: 2000
          });
        }
    }
    const todoData={
      content: content,
      priority: priorityText,
      remindTime: remindTime
    }
    http.post("/todo/add",todoData,http_success);

    
    // 清空表单
    this.setData({
      content: '',
      priorityText: '',
      remindTime: ''
    });
  }
});
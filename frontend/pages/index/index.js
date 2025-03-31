Page({
  data: {
    animationData1: {},
    animationData2: {},
    animationData3: {},
    animationData4: {}
  },

  onLoad() {
    this.startAnimations();
  },

  startAnimations() {
    // 动画效果：卡片依次从不同方向滑入并淡入
    let animation1 = wx.createAnimation({
      duration: 1000,
      timingFunction: 'cubic-bezier(0.4, 0, 0.2, 1)',
      delay: 200
    });
    animation1.translateX(-100).opacity(0).step();
    animation1.translateX(0).opacity(1).step();
    this.setData({ animationData1: animation1.export() });

    let animation2 = wx.createAnimation({
      duration: 1000,
      timingFunction: 'cubic-bezier(0.4, 0, 0.2, 1)',
      delay: 400
    });
    animation2.translateX(100).opacity(0).step();
    animation2.translateX(0).opacity(1).step();
    this.setData({ animationData2: animation2.export() });

    let animation3 = wx.createAnimation({
      duration: 1000,
      timingFunction: 'cubic-bezier(0.4, 0, 0.2, 1)',
      delay: 600
    });
    animation3.translateY(-100).opacity(0).step();
    animation3.translateY(0).opacity(1).step();
    this.setData({ animationData3: animation3.export() });

    let animation4 = wx.createAnimation({
      duration: 1000,
      timingFunction: 'cubic-bezier(0.4, 0, 0.2, 1)',
      delay: 800
    });
    animation4.translateY(100).opacity(0).step();
    animation4.translateY(0).opacity(1).step();
    this.setData({ animationData4: animation4.export() });
  },

  navigateToTodo() {
    wx.switchTab({
      url: '/pages/todo/todo'
    });
  },

  navigateToCourse() {
    wx.switchTab({
      url: '/pages/course/course'
    });
  },

  navigateToTopic() {
    wx.navigateTo({
      url: '/pages/topic/topic'
    });
  },

  navigateToLostAndFound() {
    wx.navigateTo({
      url: '/pages/lostAndFound/lostAndFound'
    });
  }
});
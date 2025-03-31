import http from "../../utils/Httputil"
import { formatTime } from "../../utils/util"

Page({
  data: {
    baseUrl: http.baseUrl,
    activeTab: 'all',
    topics: [],
    searchValue: '',
    showActionSheet: false,
    showPublishPopup: false,
    publishForm: {
      title: '',
      content: '',
      isAnonymous: false
    },
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    isLoading: false,
    isRefreshing: false
  },

  onLoad() {
    this.getTopics()
  },

  onShow() {
    if (this.data.isRefreshing) {
      this.getTopics()
    }
  },

  // 获取话题列表
  getTopics() {
    if (this.data.isLoading || (!this.data.hasMore && !this.data.isRefreshing)) return

    this.setData({ isLoading: true })

    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize,
      type: this.data.activeTab === 'all' ? '' : this.data.activeTab,
      keyword: this.data.searchValue
    }

    console.log('发送请求参数:', params)

    const http_success = (res) => {
      console.log('获取话题列表响应:', res)
      if (res.code === 200) {
        const { records, total, current, pages } = res.data;
        console.log('分页信息:', { total, current, pages })
        this.setData({
          topics: this.data.isRefreshing ? records : [...this.data.topics, ...records],
          hasMore: current < pages,
          pageNum: this.data.isRefreshing ? 1 : this.data.pageNum + 1,
          isLoading: false,
          isRefreshing: false
        })
        wx.stopPullDownRefresh()
      } else {
        wx.showToast({
          title: res.message || '获取话题列表失败',
          icon: 'none',
          duration: 2000
        })
        this.setData({ isLoading: false, isRefreshing: false })
        wx.stopPullDownRefresh()
      }
    }

    http.get('/topic/list', params, http_success)
  },

  // 下拉刷新
  onRefresh() {
    this.setData({ isRefreshing: true })
    this.getTopics()
  },

  // 上拉加载更多
  onLoadMore() {
    if (this.data.hasMore && !this.data.isLoading) {
      this.getTopics()
    }
  },

  // 切换标签
  onTabChange(event) {
    this.setData({
      activeTab: event.detail.name,
      topics: [],
      pageNum: 1,
      hasMore: true
    })
    this.getTopics()
  },

  // 搜索相关
  onSearchChange(event) {
    this.setData({
      searchValue: event.detail
    })
  },

  onSearch() {
    this.setData({
      topics: [],
      pageNum: 1,
      hasMore: true
    })
    this.getTopics()
  },

  // 显示发布选项
  showActionSheet() {
    this.setData({
      showActionSheet: true
    })
  },

  // 关闭发布选项
  onActionSheetClose() {
    this.setData({
      showActionSheet: false
    })
  },

  // 显示发布弹窗
  showPublishPopup() {
    this.setData({
      showPublishPopup: true,
      showActionSheet: false,
      publishForm: {
        title: '',
        content: '',
        isAnonymous: false
      }
    })
  },

  // 关闭发布弹窗
  onPublishPopupClose() {
    this.setData({
      showPublishPopup: false,
      publishForm: {
        title: '',
        content: '',
        isAnonymous: false
      }
    })
  },

  // 表单输入处理
  onTitleInput(event) {
    this.setData({
      'publishForm.title': event.detail.value
    })
  },

  onContentInput(event) {
    this.setData({
      'publishForm.content': event.detail.value
    })
  },

  onAnonymousChange(event) {
    this.setData({
      'publishForm.isAnonymous': event.detail
    })
  },

  // 发布话题
  submitTopic() {
    const { title, content } = this.data.publishForm
    if (!String(title).trim()) {
      wx.showToast({
        title: '请输入标题',
        icon: 'none',
        duration: 2000
      })
      return
    }
    if (!String(content).trim()) {
      wx.showToast({
        title: '请输入内容',
        icon: 'none',
        duration: 2000
      })
      return
    }

    const topicData = {
      title: String(title).trim(),
      content: String(content).trim(),
      isAnonymous: this.data.publishForm.isAnonymous
    }

    const http_success = (res) => {
      console.log('发布话题响应:', res)
      if (res.code === 200) {
        wx.showToast({
          title: '发布成功',
          icon: 'success',
          duration: 2000
        })
        this.setData({
          showPublishPopup: false,
          publishForm: {
            title: '',
            content: '',
            isAnonymous: false
          }
        })
        this.getTopics()
      } else {
        wx.showToast({
          title: res.message || '发布失败',
          icon: 'none',
          duration: 2000
        })
      }
    }

    http.post('/topic/publish', topicData, http_success)
  },

  // 跳转到详情页
  goToDetail(event) {
    const { id } = event.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/topicDetail/topicDetail?topicId=${id}`
    })
  },

  // 跳转到我的话题页面
  goToMyTopics() {
    this.setData({
      showActionSheet: false
    })
    wx.navigateTo({
      url: '/pages/myTopic/myTopic'
    })
  },

  // 格式化时间
  formatTime
})
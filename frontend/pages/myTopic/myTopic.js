import http from "../../utils/Httputil"
import { formatTime } from "../../utils/util"

Page({
  data: {
    baseUrl: http.baseUrl,
    topics: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    isLoading: false,
    isRefreshing: false
  },

  onLoad() {
    this.getMyTopics()
  },

  onShow() {
    if (this.data.isRefreshing) {
      this.getMyTopics()
    }
  },

  // 获取我的话题列表
  getMyTopics() {
    if (this.data.isLoading || (!this.data.hasMore && !this.data.isRefreshing)) return

    this.setData({ isLoading: true })

    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    }

    console.log('发送请求参数:', params)

    const http_success = (res) => {
      console.log('获取我的话题列表响应:', res)
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
          title: res.message || '获取我的话题列表失败',
          icon: 'none',
          duration: 2000
        })
        this.setData({ isLoading: false, isRefreshing: false })
        wx.stopPullDownRefresh()
      }
    }

    http.get('/topic/my', params, http_success)
  },

  // 下拉刷新
  onRefresh() {
    this.setData({ isRefreshing: true })
    this.getMyTopics()
  },

  // 上拉加载更多
  onLoadMore() {
    if (this.data.hasMore && !this.data.isLoading) {
      this.getMyTopics()
    }
  },

  // 返回上一页
  onBack() {
    wx.navigateBack()
  },

  // 跳转到话题详情
  goToDetail(event) {
    const topicId = event.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/topicDetail/topicDetail?topicId=${topicId}`
    })
  },

  // 删除话题
  deleteTopic(event) {
    const topicId = event.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定要删除这个话题吗？',
      success: (res) => {
        if (res.confirm) {
          http.delete(`/topic/${topicId}`, {}, (res) => {
            if (res.code === 200) {
              wx.showToast({
                title: '删除成功',
                icon: 'success',
                duration: 2000
              })
              // 刷新列表
              this.setData({
                topics: [],
                pageNum: 1,
                hasMore: true
              })
              this.getMyTopics()
            } else {
              wx.showToast({
                title: res.message || '删除失败',
                icon: 'none',
                duration: 2000
              })
            }
          })
        }
      }
    })
  }
})
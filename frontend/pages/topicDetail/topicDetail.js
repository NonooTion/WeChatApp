import http from "../../utils/Httputil"
import { formatTime } from "../../utils/util"

Page({
  data: {
    baseUrl:'',
    topicId: null,
    topic: null,
    comments: [],
    commentInput: '',
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    isLoading: false,
    isRefreshing: false
  },

  onLoad(options) {
    this.data.baseUrl=http.baseUrl
    if (options.topicId) {
      this.setData({ topicId: parseInt(options.topicId) })
      this.getTopicDetail()
      this.getComments()
    }
  },

  // 获取话题详情
  getTopicDetail() {
    console.log('topicId',this.data.topicId)
    http.get(`/topic/detail/${this.data.topicId}`, {}, (res) => {
      console.log("获取话题详细信息:",res.data)
      const topic=res.data;
      const completeUrl=http.baseUrl+topic.imgUrl
      
      if (res.code === 200) {
        this.setData({
          topic:{
            ...topic,
            imgUrl:completeUrl
          }
        })
      } else {
        wx.showToast({
          title: res.message || '获取话题详情失败',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  // 获取评论列表
  getComments() {
    if (this.data.isLoading || (!this.data.hasMore && !this.data.isRefreshing)) return

    this.setData({ isLoading: true })

    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    }

    http.get(`/comment/topic/${this.data.topicId}`, params, (res) => {
      console.log("获取评论:",res.data.records)
      const records = res.data.records
      const comments=records.map(x=>{
        console.log(x)
        const completeUrl=http.baseUrl+x.avatar;
        x.avatar=completeUrl
        return x;
      })
      console.log("处理评论列表头像Url",comments)
      if (res.code === 200) {
        const {  total, current, pages } = res.data
        this.setData({
          comments: this.data.isRefreshing ? comments : [...this.data.comments, ...comments],
          hasMore: current < pages,
          pageNum: this.data.isRefreshing ? 1 : this.data.pageNum + 1,
          isLoading: false,
          isRefreshing: false
        })
        wx.stopPullDownRefresh()
      } else {
        wx.showToast({
          title: res.message || '获取评论失败',
          icon: 'none',
          duration: 2000
        })
        this.setData({ isLoading: false, isRefreshing: false })
        wx.stopPullDownRefresh()
      }
    })
  },

  // 返回上一页
  onBack() {
    wx.navigateBack();
  },

  // 格式化时间
  formatTime(time) {
    const date = new Date(time);
    const now = new Date();
    const diff = now - date;
    
    // 小于1分钟
    if (diff < 60000) {
      return '刚刚';
    }
    // 小于1小时
    if (diff < 3600000) {
      return Math.floor(diff / 60000) + '分钟前';
    }
    // 小于24小时
    if (diff < 86400000) {
      return Math.floor(diff / 3600000) + '小时前';
    }
    // 小于30天
    if (diff < 2592000000) {
      return Math.floor(diff / 86400000) + '天前';
    }
    // 大于30天显示具体日期
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  },

  // 点赞话题
  likeTopic() {
    http.post(`/topic/like/${this.data.topicId}`, {}, (res) => {
      if (res.code === 200) {
        const topic = this.data.topic
        topic.likeCount += 1
        this.setData({ topic })
      } else {
        wx.showToast({
          title: res.message || '操作失败',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  // 评论输入
  onCommentInput(event) {
    this.setData({
      commentInput: event.detail.value
    })
  },

  // 提交评论
  submitComment() {
    if (!this.data.commentInput.trim()) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none',
        duration: 2000
      })
      return
    }

    http.post(`/topic/comment/${this.data.topicId}`, this.data.commentInput, (res) => {
      if (res.code === 200) {
        this.setData({
          commentInput: '',
          isRefreshing: true,
          pageNum: 1
        })
        this.getComments()
        this.getTopicDetail()
      } else {
        wx.showToast({
          title: res.message || '评论失败',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  // 删除评论
  deleteComment(event) {
    const commentId = event.currentTarget.dataset.commentId
    wx.showModal({
      title: '提示',
      content: '确定要删除这条评论吗？',
      success: (res) => {
        if (res.confirm) {
          http.delete(`/comment/delete/${commentId}`, {}, (res) => {
            if (res.code === 200) {
              wx.showToast({
                title: '删除成功',
                icon: 'success'
              })
              this.setData({
                isRefreshing: true,
                pageNum: 1
              })
              this.getComments()
              this.getTopicDetail()
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
  },

  // 下拉刷新
  onRefresh() {
    this.setData({ isRefreshing: true })
    this.getComments()
    this.getTopicDetail()
  },

  // 上拉加载更多
  onLoadMore() {
    if (this.data.hasMore && !this.data.isLoading) {
      this.getComments()
    }
  }
})
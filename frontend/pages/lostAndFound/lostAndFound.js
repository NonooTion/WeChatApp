import http from "../../utils/Httputil"
import { formatTime } from "../../utils/util"

Page({
  data: {
    baseUrl: http.baseUrl,
    activeTab: 'all',
    items: [],
    searchValue: '',
    showActionSheet: false,
    showPublishPopup: false,
    publishForm: {
      type: 'lost',
      title: '',
      content: '',
      location: '',
      fileList: []
    },
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    isLoading: false,
    isRefreshing: false
  },

  onLoad() {
    this.getItems()
  },

  onShow() {
    if (this.data.isRefreshing) {
      this.getItems()
    }
  },

  // 获取失物招领列表
  getItems() {
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
      console.log('获取失物招领列表响应:', res)
      if (res.code === 200) {
        const { records, total, current, pages } = res.data;
        console.log('分页信息:', { total, current, pages })
        this.setData({
          items: this.data.isRefreshing ? records : [...this.data.items, ...records],
          hasMore: current < pages,
          pageNum: this.data.isRefreshing ? 1 : this.data.pageNum + 1,
          isLoading: false,
          isRefreshing: false
        })
        wx.stopPullDownRefresh()
      } else {
        wx.showToast({
          title: res.message || '获取失物招领列表失败',
          icon: 'none',
          duration: 2000
        })
        this.setData({ isLoading: false, isRefreshing: false })
        wx.stopPullDownRefresh()
      }
    }

    http.get('/lost-find/list', params, http_success)
  },

  // 下拉刷新
  onRefresh() {
    this.setData({ isRefreshing: true })
    this.getItems()
  },

  // 上拉加载更多
  onLoadMore() {
    if (this.data.hasMore && !this.data.isLoading) {
      this.getItems()
    }
  },

  // 切换标签
  onTabChange(event) {
    this.setData({
      activeTab: event.detail.name,
      items: [],
      pageNum: 1,
      hasMore: true
    })
    this.getItems()
  },

  // 搜索相关
  onSearchChange(event) {
    this.setData({
      searchValue: event.detail
    })
  },

  onSearch() {
    this.setData({
      items: [],
      pageNum: 1,
      hasMore: true
    })
    this.getItems()
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
        type: 'lost',
        title: '',
        content: '',
        location: '',
        fileList: []
      }
    })
  },

  // 关闭发布弹窗
  onPublishPopupClose() {
    this.setData({
      showPublishPopup: false,
      publishForm: {
        type: 'lost',
        title: '',
        content: '',
        location: '',
        fileList: []
      }
    })
  },

  // 表单输入处理
  onTypeChange(event) {
    this.setData({
      'publishForm.type': event.detail
    })
  },

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

  onLocationInput(event) {
    this.setData({
      'publishForm.location': event.detail.value
    })
  },

  // 图片上传相关
  afterRead(event) {
    const { file } = event.detail;
    const token = wx.getStorageSync('token');
    const uploadTask = wx.uploadFile({
      url: http.baseUrl + '/lost-find/upload',
      filePath: file.url,
      name: 'file',
      header: {
        'Authorization': token
      },
      success: (res) => {
        const data = JSON.parse(res.data);
        if (data.code === 200) {
          this.setData({
            'publishForm.imgUrl': data.data.url,
            'publishForm.fileList': [{
              url: http.baseUrl + data.data.url,
              name: '图片',
              isImage: true
            }]
          });
        } else {
          wx.showToast({
            title: data.message || '上传失败',
            icon: 'none'
          });
        }
      },
      fail: () => {
        wx.showToast({
          title: '上传失败',
          icon: 'none'
        });
      }
    });
  },

  onDelete() {
    this.setData({
      'publishForm.imgUrl': '',
      'publishForm.fileList': []
    });
  },

  // 发布失物招领信息
  submitItem() {
    const { type, title, content, location, imgUrl } = this.data.publishForm
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

    const itemData = {
      type: type,
      title: String(title).trim(),
      content: String(content).trim(),
      location: String(location).trim(),
      itemImgUrl: imgUrl || '',
      status: 'pending'
    }

    const http_success = (res) => {
      console.log('发布失物招领信息响应:', res)
      if (res.code === 200) {
        wx.showToast({
          title: '发布成功',
          icon: 'success',
          duration: 2000
        })
        this.setData({
          showPublishPopup: false,
          publishForm: {
            type: 'lost',
            title: '',
            content: '',
            location: '',
            fileList: []
          }
        })
        this.getItems()
      } else {
        wx.showToast({
          title: res.message || '发布失败',
          icon: 'none',
          duration: 2000
        })
      }
    }

    http.post('/lost-find/publish', itemData, http_success)
  },

  // 跳转到详情页
  goToDetail(event) {
    const { id } = event.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/lostAndFoundDetail/lostAndFoundDetail?lfId=${id}`
    })
  },

  // 跳转到我的发布页面
  goToMyItems() {
    this.setData({
      showActionSheet: false
    })
    wx.navigateTo({
      url: '/pages/myLostAndFound/myLostAndFound'
    })
  },

  // 格式化时间
  formatTime
}) 
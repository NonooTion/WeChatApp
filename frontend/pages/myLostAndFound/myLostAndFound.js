import http from "../../utils/Httputil"
import { formatTime } from "../../utils/util"

Page({
  data: {
    baseUrl: http.baseUrl,
    activeTab: 'all',
    items: [],
    showEditPopup: false,
    editForm: {
      lfId: null,
      type: 'lost',
      status: '待处理',
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

  // 获取我的失物招领列表
  getItems() {
    if (this.data.isLoading || (!this.data.hasMore && !this.data.isRefreshing)) return

    this.setData({ isLoading: true })

    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize,
      type: this.data.activeTab === 'all' ? '' : this.data.activeTab
    }
    
    console.log('发送请求参数:', params)

    const http_success = (res) => {
      console.log('获取我的失物招领列表响应:', res)
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
          title: res.message || '获取列表失败',
          icon: 'none',
          duration: 2000
        })
        this.setData({ isLoading: false, isRefreshing: false })
        wx.stopPullDownRefresh()
      }
    }

    http.get('/lost-find/user-list', params, http_success)
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

  // 跳转到详情页
  goToDetail(event) {
    const { id } = event.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/lostAndFoundDetail/lostAndFoundDetail?lfId=${id}`
    })
  },

  // 显示编辑弹窗
  handleEdit(event) {
    const { id } = event.currentTarget.dataset
    const item = this.data.items.find(item => item.lfId === id)
    if (item) {
      this.setData({
        showEditPopup: true,
        editForm: {
          lfId: item.lfId,
          type: item.type,
          status: item.status,
          title: item.title,
          content: item.content,
          location: item.location,
          fileList: item.imgUrl ? [{
            url: this.data.baseUrl + item.imgUrl
          }] : []
        }
      })
    }
  },

  // 关闭编辑弹窗
  onEditPopupClose() {
    this.setData({
      showEditPopup: false,
      editForm: {
        lfId: null,
        type: 'lost',
        status: '待处理',
        title: '',
        content: '',
        location: '',
        imgUrl:"",
        fileList: []
      }
    })
  },

  // 表单输入处理
  onTypeChange(event) {
    this.setData({
      'editForm.type': event.detail
    })
  },

  onStatusChange(event) {
    this.setData({
      'editForm.status': event.detail
    })
  },

  onTitleInput(event) {
    this.setData({
      'editForm.title': event.detail
    })
  },

  onContentInput(event) {
    this.setData({
      'editForm.content': event.detail
    })
  },

  onLocationInput(event) {
    this.setData({
      'editForm.location': event.detail
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
            'editForm.imgUrl': data.data.url
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
      'editForm.imgUrl': ''
    });
  },

  // 提交编辑
  submitEdit() {
    const { lfId, type, status, title, content, location, imgUrl } = this.data.editForm
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
      lfId: lfId,
      type: type,
      status: status,
      title: String(title).trim(),
      content: String(content).trim(),
      location: String(location).trim(),
      itemImgUrl: imgUrl || ''
    }

    const http_success = (res) => {
      console.log('编辑失物招领信息响应:', res)
      if (res.code === 200) {
        wx.showToast({
          title: '保存成功',
          icon: 'success',
          duration: 2000
        })
        this.setData({
          showEditPopup: false,
          editForm: {
            lfId: null,
            type: 'lost',
            status: '待处理',
            title: '',
            content: '',
            location: '',
            fileList: []
          }
        })
        this.getItems()
      } else {
        wx.showToast({
          title: res.message || '保存失败',
          icon: 'none',
          duration: 2000
        })
      }
    }

    http.put('/lost-find/update', itemData, http_success)
  },

  // 删除
  handleDelete(event) {
    const { id } = event.currentTarget.dataset
    wx.showModal({
      title: '确认',
      content: '确定要删除这条信息吗？',
      success: (res) => {
        if (res.confirm) {
          const http_success = (res) => {
            if (res.code === 200) {
              wx.showToast({
                title: '删除成功',
                icon: 'success'
              })
              this.getItems()
            } else {
              wx.showToast({
                title: res.message || '删除失败',
                icon: 'none'
              })
            }
          }

          http.delete(`/lost-find/delete/${id}`, {}, http_success)
        }
      }
    })
  },

  // 格式化时间
  formatTime
}) 
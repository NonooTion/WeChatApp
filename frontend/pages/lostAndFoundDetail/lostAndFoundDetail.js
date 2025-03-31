import http from "../../utils/Httputil"
import { formatTime } from "../../utils/util"

Page({
  data: {
    baseUrl: http.baseUrl,
    lfId: null,
    item: null,
    isOwner: false
  },

  onLoad(options) {
    if (options.lfId) {
      this.setData({ lfId: options.lfId })
      this.getDetail()
    }
  },

  // 获取详情
  getDetail() {
    const http_success = (res) => {
      if (res.code === 200) {
        const item = res.data
        item.createTime = item.createTime
        this.setData({ 
          item,
          isOwner: item.userId === wx.getStorageSync('userId')
        })
      } else {
        wx.showToast({
          title: res.message || '获取详情失败',
          icon: 'none',
          duration: 2000
        })
      }
    }

    http.get(`/lost-find/detail/${this.data.lfId}`, {}, http_success)
  },

  // 预览图片
  previewImage() {
    if (this.data.item.imgUrl) {
      wx.previewImage({
        urls: [this.data.baseUrl + this.data.item.imgUrl]
      })
    }
  },

  // 联系发布者
  handleContact() {
    if (this.data.item.phone) {
      wx.makePhoneCall({
        phoneNumber: this.data.item.phone
      })
    } else if (this.data.item.email) {
      wx.setClipboardData({
        data: this.data.item.email,
        success: () => {
          wx.showToast({
            title: '邮箱已复制',
            icon: 'success'
          })
        }
      })
    } else {
      wx.showToast({
        title: '暂无联系方式',
        icon: 'none'
      })
    }
  },

  // 标记已解决
  handleResolve() {
    wx.showModal({
      title: '确认',
      content: '确定要将此信息标记为已解决吗？',
      success: (res) => {
        if (res.confirm) {
          const http_success = (res) => {
            if (res.code === 200) {
              wx.showToast({
                title: '操作成功',
                icon: 'success'
              })
              this.getDetail()
            } else {
              wx.showToast({
                title: res.message || '操作失败',
                icon: 'none'
              })
            }
          }

          http.put('/lost-find/update', {
            lfId: this.data.lfId,
            status: 'resolved'
          }, http_success)
        }
      }
    })
  },

  // 删除
  handleDelete() {
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
              setTimeout(() => {
                wx.navigateBack()
              }, 1500)
            } else {
              wx.showToast({
                title: res.message || '删除失败',
                icon: 'none'
              })
            }
          }

          http.delete(`/lost-find/delete/${this.data.lfId}`, {}, http_success)
        }
      }
    })
  }
}) 
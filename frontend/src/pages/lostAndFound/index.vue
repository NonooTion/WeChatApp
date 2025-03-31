<template>
  <view class="container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input type="text" v-model="keyword" placeholder="搜索失物招领" @input="handleSearch" />
    </view>
    
    <!-- 发布按钮 -->
    <view class="publish-btn" @click="showPublishPopup">
      <text class="iconfont icon-add"></text>
      <text>发布</text>
    </view>
    
    <!-- 失物招领列表 -->
    <view class="list">
      <view class="item" v-for="item in list" :key="item.lfId" @click="goToDetail(item.lfId)">
        <image :src="item.imgUrl" mode="aspectFill" class="item-image" v-if="item.imgUrl"></image>
        <view class="item-content">
          <view class="item-title">{{ item.title }}</view>
          <view class="item-desc">{{ item.content }}</view>
          <view class="item-info">
            <text class="item-type">{{ item.type === 'lost' ? '寻物' : '招领' }}</text>
            <text class="item-status">{{ item.status === 'unfound' ? '未找到' : '已找到' }}</text>
            <text class="item-time">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 发布弹窗 -->
    <uni-popup ref="publishPopup" type="center">
      <view class="publish-popup">
        <view class="popup-header">
          <text>发布失物招领</text>
          <text class="close" @click="closePublishPopup">×</text>
        </view>
        <view class="popup-content">
          <view class="type-select">
            <text :class="['type-item', publishForm.type === 'lost' ? 'active' : '']" @click="publishForm.type = 'lost'">寻物</text>
            <text :class="['type-item', publishForm.type === 'found' ? 'active' : '']" @click="publishForm.type = 'found'">招领</text>
          </view>
          <input type="text" v-model="publishForm.title" placeholder="请输入标题" class="input" />
          <textarea v-model="publishForm.content" placeholder="请输入详细描述" class="textarea" />
          <view class="upload-image" @click="chooseImage">
            <text class="iconfont icon-camera"></text>
            <text>上传图片</text>
          </view>
          <image :src="publishForm.imgUrl" mode="aspectFill" class="preview-image" v-if="publishForm.imgUrl"></image>
        </view>
        <view class="popup-footer">
          <button class="cancel-btn" @click="closePublishPopup">取消</button>
          <button class="confirm-btn" @click="handlePublish">发布</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { request } from '@/utils/request'
import { formatTime } from '@/utils/format'

export default {
  setup() {
    const list = ref([])
    const keyword = ref('')
    const pageNum = ref(1)
    const pageSize = ref(10)
    const publishPopup = ref(null)
    const publishForm = ref({
      type: 'lost',
      title: '',
      content: '',
      imgUrl: '',
      status: 'unfound'
    })
    
    // 获取列表数据
    const getList = async () => {
      try {
        const res = await request.get('/lost-find/list', {
          params: {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            keyword: keyword.value
          }
        })
        if (res.code === 200) {
          list.value = res.data.records
        }
      } catch (error) {
        console.error('获取列表失败:', error)
      }
    }
    
    // 搜索
    const handleSearch = () => {
      pageNum.value = 1
      getList()
    }
    
    // 显示发布弹窗
    const showPublishPopup = () => {
      publishPopup.value.open()
    }
    
    // 关闭发布弹窗
    const closePublishPopup = () => {
      publishPopup.value.close()
      publishForm.value = {
        type: 'lost',
        title: '',
        content: '',
        imgUrl: '',
        status: 'unfound'
      }
    }
    
    // 选择图片
    const chooseImage = () => {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          publishForm.value.imgUrl = res.tempFilePaths[0]
        }
      })
    }
    
    // 发布
    const handlePublish = async () => {
      if (!publishForm.value.title || !publishForm.value.content) {
        uni.showToast({
          title: '请填写完整信息',
          icon: 'none'
        })
        return
      }
      
      try {
        const res = await request.post('/lost-find/publish', publishForm.value)
        if (res.code === 200) {
          uni.showToast({
            title: '发布成功',
            icon: 'success'
          })
          closePublishPopup()
          getList()
        }
      } catch (error) {
        console.error('发布失败:', error)
      }
    }
    
    // 跳转到详情页
    const goToDetail = (id) => {
      uni.navigateTo({
        url: `/pages/lostAndFound/detail?id=${id}`
      })
    }
    
    onMounted(() => {
      getList()
    })
    
    return {
      list,
      keyword,
      publishPopup,
      publishForm,
      handleSearch,
      showPublishPopup,
      closePublishPopup,
      chooseImage,
      handlePublish,
      goToDetail,
      formatTime
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding: 20rpx;
  
  .search-bar {
    background: #f5f5f5;
    border-radius: 30rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    
    input {
      width: 100%;
      height: 60rpx;
      padding: 0 20rpx;
    }
  }
  
  .publish-btn {
    position: fixed;
    right: 40rpx;
    bottom: 40rpx;
    background: #007AFF;
    color: #fff;
    padding: 20rpx 40rpx;
    border-radius: 40rpx;
    display: flex;
    align-items: center;
    
    .iconfont {
      margin-right: 10rpx;
    }
  }
  
  .list {
    .item {
      background: #fff;
      border-radius: 20rpx;
      padding: 20rpx;
      margin-bottom: 20rpx;
      display: flex;
      
      .item-image {
        width: 200rpx;
        height: 200rpx;
        border-radius: 10rpx;
        margin-right: 20rpx;
      }
      
      .item-content {
        flex: 1;
        
        .item-title {
          font-size: 32rpx;
          font-weight: bold;
          margin-bottom: 10rpx;
        }
        
        .item-desc {
          font-size: 28rpx;
          color: #666;
          margin-bottom: 10rpx;
        }
        
        .item-info {
          display: flex;
          align-items: center;
          font-size: 24rpx;
          color: #999;
          
          .item-type {
            margin-right: 20rpx;
          }
          
          .item-status {
            margin-right: 20rpx;
          }
        }
      }
    }
  }
  
  .publish-popup {
    background: #fff;
    border-radius: 20rpx;
    width: 600rpx;
    
    .popup-header {
      padding: 20rpx;
      border-bottom: 1rpx solid #eee;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .close {
        font-size: 40rpx;
        color: #999;
      }
    }
    
    .popup-content {
      padding: 20rpx;
      
      .type-select {
        display: flex;
        margin-bottom: 20rpx;
        
        .type-item {
          flex: 1;
          text-align: center;
          padding: 20rpx;
          background: #f5f5f5;
          margin: 0 10rpx;
          border-radius: 10rpx;
          
          &.active {
            background: #007AFF;
            color: #fff;
          }
        }
      }
      
      .input {
        border: 1rpx solid #eee;
        border-radius: 10rpx;
        padding: 20rpx;
        margin-bottom: 20rpx;
      }
      
      .textarea {
        border: 1rpx solid #eee;
        border-radius: 10rpx;
        padding: 20rpx;
        height: 200rpx;
        margin-bottom: 20rpx;
      }
      
      .upload-image {
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f5f5;
        border-radius: 10rpx;
        padding: 20rpx;
        margin-bottom: 20rpx;
        
        .iconfont {
          margin-right: 10rpx;
        }
      }
      
      .preview-image {
        width: 200rpx;
        height: 200rpx;
        border-radius: 10rpx;
      }
    }
    
    .popup-footer {
      padding: 20rpx;
      border-top: 1rpx solid #eee;
      display: flex;
      justify-content: space-between;
      
      button {
        width: 45%;
        height: 80rpx;
        line-height: 80rpx;
        text-align: center;
        border-radius: 40rpx;
        
        &.cancel-btn {
          background: #f5f5f5;
          color: #666;
        }
        
        &.confirm-btn {
          background: #007AFF;
          color: #fff;
        }
      }
    }
  }
}
</style> 
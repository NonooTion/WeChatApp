import http from '../../utils/Httputil';

Page({
  data: {
    days: ['一', '二', '三', '四', '五', '六', '日'],
    courseTimes: [1, 2, 3, 4, 5],
    courses: [],
    showPopup: false,
    popupTitle: '',
    courseForm: {
      courseId: null,
      courseName: '',
      classroom: '',
      day: 1,
      time: 1
    },
    showConfirm: false,
    currentCourseId: null,
    // 新增详情弹窗状态
    showDetail: false,
    currentCourse: null,
    showHelpPopup: false
  },
  onLoad()
  {
    this.getCourses();
  },
  getCourses()
  {
    http.get("/course/get-by-user-id",{},
    (data)=>{
      console.log("后台响应:",data)
      const courses=data.data
      console.log("获取课程:",courses)
      this.setData({courses})
    }
    )
  },
  // 点击单元格事件（查看详情/添加课程）
  handleCourseTap(e) {
    const { day, time } = e.currentTarget.dataset
    const course = this.data.courses.find(c => c.day == day && c.time == time)

    if (course) {
      this.showCourseDetail(course, day, time)
    } else {
      this.showAddPopup(day, time)
    }
  },

  // 长按单元格事件（操作菜单）
  handleCourseLongPress(e) {
    const { day, time } = e.currentTarget.dataset
    const course = this.data.courses.find(c => c.day == day && c.time == time)
    
    if (course) {
      this.showActionMenu(course, day, time)
    }
  },

  // 显示课程详情
  showCourseDetail(course, day, time) {
    this.setData({
      showDetail: true,
      currentCourse: {
        ...course,
        day: day,
        time: time
      }
    })
  },

  // 显示添加课程弹窗
  showAddPopup(day, time) {
    this.setData({
      showPopup: true,
      popupTitle: '添加课程',
      courseForm: {
        CourseId: null,
        courseName: '',
        classroom: '',
        day: day,
        time: time
      }
    })
  },

  // 显示操作菜单
  showActionMenu(course, day, time) {
    wx.showActionSheet({
      itemList: ['修改', '删除'],
      success: (res) => {
        if (res.tapIndex === 0) {
          this.showEditPopup(course, day, time)
        } else if (res.tapIndex === 1) {
          this.showDeleteConfirm(course.courseId)
        }
      }
    })
  },

  // 显示编辑弹窗
  showEditPopup(course, day, time) {
    this.setData({
      showPopup: true,
      popupTitle: '编辑课程',
      courseForm: {
        courseId: course.courseId,
        courseName: course.courseName,
        classroom: course.classroom,
        day: day,
        time: time
      }
    })
  },

  // 显示删除确认
  showDeleteConfirm(courseId) {
    this.setData({
      showConfirm: true,
      currentCourseId: courseId
    })
  },

  // 表单输入处理
  onCourseNameInput(e) {
    this.setData({
      'courseForm.courseName': e.detail.value
    })
  },

  onClassroomInput(e) {
    this.setData({
      'courseForm.classroom': e.detail.value
    })
  },

  // 提交课程表单
  submitCourse() {
    const { courseForm } = this.data
    
    if (!courseForm.courseName.trim() || !courseForm.classroom.trim()) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' })
      return
    }
    if(courseForm.courseId == null){
    const http_success = (res)=>{
      console.log("添加课程请求响应:",res);
      if(res.code === 200){
        this.getCourses();
        this.setData({showPopup: false })
        wx.showToast({ title: '操作成功', icon: 'success' })
      }else {
        wx.showToast({
          title: res.message || "添加课程失败",
          icon: "none",
          duration: 2000
        })
      }
    }
    http.post("/course/add",courseForm,http_success)
  }
  else{
    const http_success = (res)=>{
      console.log("修改课程请求响应:",res);
      if(res.code === 200){
        this.getCourses();
        this.setData({showPopup: false })
        wx.showToast({ title: '操作成功', icon: 'success' })
      }else {
        wx.showToast({
          title: res.message || "修改课程失败",
          icon: "none",
          duration: 2000
        })
      }
    }
    const queryParams = Object.keys(courseForm)
    .map(key => `${key}=${encodeURIComponent(courseForm[key])}`)
    .join('&');
    console.log(queryParams)
    http.put(`/course/update?${queryParams}`,{},http_success)
  }
  },

  // 更新现有课程
  updateExistingCourse(courses, formData) {
    return courses.map(c => 
      c.courseId === formData.courseId
        ? { ...c, ...formData }
        : c
    )
  },


  // 删除课程
  deleteCourse() {
    const { currentCourseId } = this.data
    const http_success = (data) => {
      wx.showToast({
        title: '删除成功',
        icon: 'success',
        duration: 2000
      });
      this.getCourses();
    }
    console.log(currentCourseId)
    http.delete("/course/delete", {courseId:currentCourseId},http_success)
    this.setData({
      showConfirm: false
    })
  },

  // 关闭弹窗方法
  closePopup() {
    this.setData({ showPopup: false })
  },

  closeConfirm() {
    this.setData({ showConfirm: false })
  },

  closeDetail() {
    this.setData({ showDetail: false })
  },

  showHelp() {
    this.setData({ showHelpPopup: true });
  },
  
  closeHelp() {
    this.setData({ showHelpPopup: false });
  }
})

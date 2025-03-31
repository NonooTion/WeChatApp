import http from '../../utils/Httputil';

Page({
  data: {
    todos: [],
    newTodoContent: '',
    priorityColorMap: {
      '高': '#ff4757',
      '中': '#ffa502',
      '低': '#1296db'
    },
    buttons: [
      {
        text: '取消',
        type: 'default',
        bindtap: 'cancel'
      },
      {
        text: '确定',
        type: 'primary',
        bindtap: 'confirm'
      }
    ],
    scrollHeight: 0
  },

  onLoad() {
    // 计算可滚动区域的高度
    const query = wx.createSelectorQuery();
    query.select('.container').boundingClientRect();
    query.select('.header').boundingClientRect();
    query.select('.button-bar').boundingClientRect();
    query.select('.footer').boundingClientRect();
    query.exec((res) => {
      const containerHeight = res[0].height;
      const headerHeight = res[1].height;
      const buttonBarHeight = res[2].height;
      const footerHeight = res[3].height;
      const scrollHeight = containerHeight - headerHeight - buttonBarHeight - footerHeight - 40;
      this.setData({
        scrollHeight: scrollHeight
      });
    });

    // 获取待办事项列表数据
    this.getTodos();
  },

  // 获取待办事项列表
  getTodos() {
    http.get('/todo/get-by-user-id', {}, (data) => {
      const todos = data.data.map((todo, index) => ({
        ...todo,
        priorityColor: this.data.priorityColorMap[todo.priority] || '#1296db',
        animationData: {},
        delay: index * 100
      }));
      console.log("获取待办事项列表:",todos)
      this.setData({ todos });
      this.triggerEntryAnimation();
    }, (err) => {
      console.error('获取待办事项失败:', err);
    });
  },

  // 触发逐条出现的动画
  triggerEntryAnimation() {
    const todos = this.data.todos.map((todo, index) => {
      const animation = wx.createAnimation({
        duration: 1000,
        timingFunction: 'ease',
        delay: todo.delay
      });

      animation.opacity(1).scale(1).step();

      return {
        ...todo,
        animationData: animation.export()
      };
    });

    this.setData({ todos });
  },

  // 添加待办事项
  navigateToAdd() {
    wx.navigateTo({
      url: '/pages/todo/addTodo/addTodo',
    });
  },

  // 显示删除动画
  showDeleteAnimation(e) {
    const todoId = e.currentTarget.dataset.todoId;
    const todoIndex = this.data.todos.findIndex(todo => todo.todoId === todoId);
    if (todoIndex === -1) return;

    // 创建动画
    const animation = wx.createAnimation({
      duration: 300,
      timingFunction: 'ease',
    });

    // 设置动画内容
    animation.opacity(0).scale(0.8).step();

    // 应用动画到指定待办事项
    const todos = this.data.todos.map((todo, index) => {
      if (index === todoIndex) {
        return {
          ...todo,
          animationData: animation.export()
        };
      }
      return todo;
    });

    this.setData({ todos });

    // 动画结束后删除待办事项
    setTimeout(() => {
      this.deleteTodo(todoId);
    }, 300);
  },

  // 删除待办事项
  deleteTodo(todoId) {
    wx.showModal({
      title: '确认删除',
      content: '确定要删除该待办事项吗？',
      success: (res) => {
        if (res.confirm) {
          http.delete('/todo/delete', { todoId }, (data) => {
            wx.showToast({
              title: '删除成功',
              icon: 'success',
              duration: 2000
            });
            this.getTodos();
          }, (err) => {
            console.error('删除待办事项失败:', err);
          });
        } else {
          // 取消删除，恢复动画
          this.restoreTodo(todoId);
        }
      }
    });
  },

  // 恢复待办事项
  restoreTodo(todoId) {
    const todoIndex = this.data.todos.findIndex(todo => todo.todoId === todoId);
    if (todoIndex === -1) return;

    // 创建恢复动画
    const animation = wx.createAnimation({
      duration: 300,
      timingFunction: 'ease',
    });

    // 设置恢复动画内容
    animation.opacity(1).scale(1).step();

    // 应用恢复动画到指定待办事项
    const todos = this.data.todos.map((todo, index) => {
      if (index === todoIndex) {
        return {
          ...todo,
          animationData: animation.export()
        };
      }
      return todo;
    });

    this.setData({ todos });
  },

  // 编辑待办事项
  editTodo(e) {
    const todoId = e.currentTarget.dataset.todoId;
    const todo = this.data.todos.find(t => t.todoId === todoId);
    if (!todo) return;
    console.log("编辑待办事项",todoId)
    wx.navigateTo({
      url: `./editTodo/editTodo?todoId=${todoId}&content=${encodeURIComponent(todo.content)}&priority=${todo.priority}&remindTime=${todo.remindTime}`
    });
  },

  // 显示待办事项详情
  showTodoDetail(e) {
    const todoId = e.currentTarget.dataset.todoId;
    const todo = this.data.todos.find(t => t.todoId === todoId);
    if (!todo) return;

    wx.navigateTo({
      url: `/pages/todo-detail/index?todoId=${todoId}`
    });
  },

  // 输入框内容变化
  onTodoInput(e) {
    this.setData({ newTodoContent: e.detail.value });
  },

  // 选择优先级
  selectPriority(e) {
    this.setData({ selectedPriority: e.currentTarget.dataset.priority });
  }
});
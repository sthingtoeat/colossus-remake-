<template>
  <ContentField>
    <div class="common-layout">
      <el-container>
        <el-aside class="aside" width="300px" style="background-color: rgb(125, 125, 0)">
           <el-row><!--这里是总侧边栏的载体，包括了左边的两列内容-->
            <el-col class="user-info-box" :span="6" style="height:800px;background-color:rgb(127,127,127)"><!--这里是聊天室最左边那一小列的载体，下面的才是具体的内容 -->
              <el-avatar style="margin-left:15%" :size="54" :src="circleUrl"/>
              <el-avatar style="margin-left:15%;margin-top:20%" :size="54" :src="circleUrl"/>
              <el-avatar style="margin-left:15%" :size="54" :src="circleUrl"/>
              <el-avatar style="margin-left:15%" :size="54" :src="circleUrl"/>
            </el-col>
            <el-col :span="18"><!--这里是侧边栏稍微宽一点的一列-->
                <el-input v-model="input1" style="width: full;margin-top:3%" size="large" placeholder="搜索好友" :prefix-icon="Search"/>
                <el-menu style="margin-top:5%" default-active="2" class="el-menu-vertical-demo">
                  <el-menu-item index="1">
                    <el-icon><icon-menu /></el-icon>
                    <span>Navigator one</span>
                  </el-menu-item>
                    <el-menu-item index="2">
                    <el-icon><icon-menu /></el-icon>
                    <span>Navigator Two</span>
                  </el-menu-item>
                </el-menu>
            </el-col>
          </el-row>
        </el-aside>
        <el-container>
          <el-header>
            <div style="height:60px;width:100%;background-color:rgb(128, 128, 128)"></div>
          </el-header>
          <el-main>
            <el-row :gutter="20">
              <el-col :span="18">
                <div style="height:630px;background-color:yellow">
                  <ul  class="infinite-list" style="overflow: auto">
                    <li v-for="i in content_list" :key="i" class="infinite-list-item">{{ i }}</li>
                  </ul>
                </div>
                <div>
                  <el-input :span="18" v-model="input_content" placeholder="说点什么吧~" />
                  <el-button @click="sendMessage(input_content)" style="float:right;margin-top:5px" type="primary">发送</el-button>
                </div>
              </el-col>
              <el-col :span="6">
                <div style="height:700px;background-color:white">
                  <el-scrollbar height="400px">
                    <div>
                      好友列表
                    </div>
                    <div>
                      好友列表
                    </div>
                  </el-scrollbar>
                </div>
              </el-col>
            </el-row>
          </el-main>
        </el-container>
      </el-container>
    </div>
  </ContentField>
</template>
<script>
import ContentField from "@/components/ContentField.vue";
import { useStore } from "vuex";
import { ref ,reactive, onMounted, onUnmounted } from "vue"
import $ from "jquery";
export default {
  components: {
    ContentField,
  },
  setup() {

    const store = useStore();
    const user_id = store.state.user.id;
    const socketUrl = `ws://127.0.0.1:3003/websocket/${user_id}`;

    let input_content = ref("");
    let content_list = reactive(
      [
        {
          user_id:"",
          user_content:"",
          time:"",
        },
      ],
    ) 
    
    //发送消息给后端
    const sendMessage = (message) =>{
      $.ajax({
          url:"http://127.0.0.1:3003/message/send1",
          type:"post",
          data:{
            user_id:user_id,
            user_content:message,
            time:getDate(),
          },
          success(resp){
            addContent(message);
            console.log(resp);
          },
          error(resp){
            console.log(resp);
          }
      })
      input_content.value=""; //清除输入框中的内容
    }

    //前台提交信息，添加到content_list数组
    const addContent = (message) =>{
      content_list.push({
        user_id:user_id,
        user_content:message,
        time:getDate(),
      })
    }

    //处理来自后端的消息并添加到content_list数组
    const updateContent = (message) => {
      content_list.push({
        user_id:message.user_id,
        user_content:message.user_content,
        time:message.time,
      })
    }

    //获取当前时间
    const getDate = () => {
      const date = new Date();
      const minute = date.getMinutes()/10 === 0 ? "0" + date.getMinutes() : date.getMinutes();
      const now = date.getFullYear() + "年" + (date.getMonth() + 1)+ "月" + date.getDate() + "日" + date.getHours() + ":" + minute;
      return now;
    }

    let socket = null;
    //挂载时(进入聊天室)自动调用这个函数，同时持续到取消挂载为止
    onMounted(() => {
      socket = new WebSocket(socketUrl)

      socket.onopen = () => {
        console.log(user_id +"你已成功连接");
      }
 
      socket.onmessage = (msg) => {
        const message = JSON.parse(msg.data);
        
        updateContent(message)
        console.log("收到了来自后端的消息"+ message);
      }

      socket.onerror = () =>{
        
      }

      socket.onclose = () => {
        console.log(user_id + "已断开连接");
      }

    });
    
    //取消挂载(退出浏览器)时自动调用这个函数
    onUnmounted(() => {
      socket.close();
    });

    return {
      input_content,
      content_list,
      sendMessage,
      addContent,
      updateContent,
      getDate,
    }
  },
};
</script>

<style scoped>
</style>
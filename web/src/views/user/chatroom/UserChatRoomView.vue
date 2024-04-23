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
                  <ul class="infinite-list" style="overflow: auto">
                    <li v-for="item in content_list" :key="item" style="list-style-type:none" class="infinite-list-item">
                      <ChatBubble>
                        <span class="chat-time">{{item.time}}</span>
                        <div class="chat-item" >
                          <img class="chat-avatar" :src="item.user_photo" alt="">
                          <el-col style="display:inline-block;" class="chat-item-box">
                            <div class="user-info">{{item.user_id}}</div>
                            <div class="chat-content">{{item.user_content}}</div>
                          </el-col>
                        </div>
                      </ChatBubble>
                    </li>
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
                    <span>当前在线人数:{{member_num}}</span>
                    <div v-for="item in member_list" :key="item">
                      <img :src="item.user_photo" alt="" style="width:30px;height:30px;border-radius:50%">
                      {{item.user_name}}
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
import ChatBubble from "@/components/ChatBubble.vue";
import { useStore } from "vuex";
import { ref ,reactive, onMounted, onUnmounted } from "vue"
import { ElNotification } from "element-plus"
import $ from "jquery";

export default {
  components: {
    ContentField,
    ChatBubble,
  },
  setup() {

    const store = useStore();
    const user_id = store.state.user.id;
    const socketUrl = `ws://127.0.0.1:3003/websocket/${user_id}`;

    let input_content = ref("");
    let content_list = reactive(
      [
        // {
        //   user_id:"",
        //   user_content:"",
        //   user_photo:null,
        //   time:"",
        // },
      ],
    ) 
    let member_num = ref("0");
    let member_list = reactive(
      [
        //  {
        //   member_size:"",
        //   user_photo:"",
        //   user_name:"",
        //   is_member_info:"",
        //   member_id:""
        //  }
      ]
    )
    
    //发送聊天消息给后端
    const sendMessage = (message) =>{
      $.ajax({
          url:"http://127.0.0.1:3003/message/send",
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
        user_photo:"https://cdn.acwing.com/media/user/profile/photo/150655_lg_8d32256772.jpg",
        time:getDate(),
      })
    }

    //处理来自后端的消息并添加到content_list数组
    const updateContent = (message) => {
      content_list.push({
        user_id:message.user_id,
        user_content:message.user_content,
        user_photo:"https://cdn.acwing.com/media/user/profile/photo/150655_lg_8d32256772.jpg",
        time:message.time,
      })
    }

    //移除在线成员
    const removeMember = (id) =>{
      for(let i = 0 ; i < member_list.length ; i ++){
        if(member_list[i].member_id == id){
          member_list.splice(i,1);
          //
          member_num.value --;
        }
      }
    }

    //添加在线成员
    const addMember = () =>{
      
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
        ElNotification({
          title: '你成功连接上了聊天室的网络！开始聊天吧！',
          type: 'success',
        })
        console.log(user_id +"你已成功连接");
      }
 
      //从后台接收到的信息可以有聊天消息和成员信息，需要进行区分
      socket.onmessage = (msg) => {
        //首先将接收的信息解析为Js对象，因为发过来的是JSON字符串
        const message = JSON.parse(msg.data);
        if(message.is_offline_info){
          removeMember(message.offline_id);
          console.log("当前下线的用户id:" + message.offline_id);
          return ;
        }
        //区分成员信息还是聊天内容
        if(message.is_member_info){
          //传来成员列表的参数有member_size、user_photo、user_name、is_member_info、member_id
          member_list.push(message);
          member_num.value = message.member_size;
          console.log("收到成员列表信息："+ message);
        }else{
          updateContent(message)
          console.log("收到了来自后端的消息"+ message);
        }
      }

      socket.onerror = () =>{
        
      }

      socket.onclose = () => {
        ElNotification({
          title: '注意哦，你断开了连接！收不到消息了哦！',
          type: 'warning',
        })

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
      member_list,
      member_num,
      sendMessage,
      addContent,
      updateContent,
      removeMember,
      addMember,
      getDate,
    }
  },
};
</script>

<style scoped>
.chat-avatar{
  width:40px;
  height:40px;
  border-radius:50%;
  margin-top:-25px;
}
.user-info{
  color:red;
}
.chat-content{
  background-color:grey;
}
</style>
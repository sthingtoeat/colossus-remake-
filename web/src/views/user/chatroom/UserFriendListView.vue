<template>
  <ContentField>
    <div class="common-layout">
      <el-container>
        <el-aside class="aside" width="300px" style="background-color: rgb(0,0,0,60%)">
           <el-row><!--<--这里是总侧边栏的载体，包括了左边的两列内容-->
            <!--这里是聊天室最左边那一小列的载体，下面的才是具体的内容 -->
            <el-col class="user-info-box" :span="6" style="height:800px;background-color:rgb(0,0,0,60%);border-radius:7px">
              <el-avatar style="margin-left:15%" :size="54" :src="user_photo"/>
            </el-col>
            <el-col :span="18"><!--这里是侧边栏稍微宽一点的一列-->
              <div >  
                <el-input v-model="input1" style="width: full;margin-top:3%" size="large" placeholder="搜索好友" :prefix-icon="Search"/>
                <!-- 搜索栏下方的列表框 -->
                <el-scrollbar max-height="600px">
                  <div @click="chooseFriend(item.friend_id)" v-for="item in friend_list" :key="item" style="margin-top:10px;color:white;background-color:grey;border-radius:7px;">
                    <el-avatar style="margin-left:10px;margin-top:5px" :size="54" :src="item.friend_photo"/>
                    <el-col style="display:inline-block;" >
                      <div style="margin-left:15%;margin-bottom:10%;width:80px">{{item.friend_name}}</div>
                      <div style="margin-left:15%">....</div>
                    </el-col>
                  </div>
                </el-scrollbar>
              </div> 
            </el-col>
          </el-row>
        </el-aside>
        <el-container>
          <el-header>
            <div style="height:60px;width:100%;background-color:rgb(0, 0, 0,60%);border-radius:7px">
              <label style="float:center;color:white;margin-top:2%;margin-left:2%">
                {{toWhichFriend}}
              </label>
            </div>
          </el-header>
          <el-main>
            <el-row :gutter="20">
              <el-col :span="18">
                <div style="height:630px">
                  <el-scrollbar max-height="630px" >
                    <ChatBubbleListBox style="clear:both" v-for="item in content_list" :key="item">
                      <div :class="user_id != item.user_id ? 'chat-time':'chat-time-right'">
                        <span>{{item.time}}</span>
                      </div>
                      <div :class="user_id != item.user_id ? 'chat-item':'chat-item-right'">
                        <img :class="user_id != item.user_id ? 'chat-avatar':'chat-avatar-right'" :src="item.user_photo" alt="">
                        <el-col style="display:inline-block;" class="chat-item-box">
                          <div :class="user_id != item.user_id ? 'user-info':'user-info-right'">{{item.user_name}}</div>
                          <div :class="user_id != item.user_id ? 'chat-content':'chat-content-right'">{{item.user_content}}</div>
                        </el-col>
                      </div>
                    </ChatBubbleListBox>
                  </el-scrollbar>
                </div>
                <div v-if="toWhichFriend != null">
                  <el-input :span="18" v-model="input_content" @keydown.enter="sendMessage(input_content)"  placeholder="说点什么吧~" />
                  <el-button @click="sendMessage(input_content)" style="float:right;margin-top:5px" type="primary">发送</el-button>
                </div>
                <div v-else>
                  <el-input disabled :span="18" v-model="input_content" @keydown.enter="sendMessage(input_content)"  placeholder="说点什么吧~" />
                  <el-button disabled @click="sendMessage(input_content)" style="float:right;margin-top:5px" type="primary">发送</el-button>
                </div>
              </el-col>
              <el-col :span="6">
                <div style="height:700px;">
                  <el-scrollbar height="700px" style="padding-left:10px;padding-right:10px;background-color:rgb(0,0,0,60%);border-radius:7px">
                    <span style="color:white;">当前在线好友:{{member_num}}</span>
                    <div style="margin-top:10px" v-for="item in member_list" :key="item">
                      <img :src="item.user_photo" alt="" style="width:30px;height:30px;border-radius:50%">
                      <label style="color:rgb(239,241,2);">{{item.user_name}}</label>
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
import ChatBubbleListBox from "@/components/ChatBubbleListBox.vue";
import { useStore } from "vuex";
import { ref ,reactive, onMounted, onUnmounted} from "vue"
import { ElNotification } from "element-plus"
import $ from "jquery";

export default {
  components: {
    ContentField,
    ChatBubbleListBox,
  },
  computed:{
  
  },
  setup() {

    const store = useStore();
    const user_id = store.state.user.id;
    const user_name = store.state.user.username;
    const user_photo = store.state.user.photo;
    const socketUrl = `ws://127.0.0.1:3003/chatApi/websocket/friendList/${user_id}`;

    let input_content = ref("");
    let content_list = reactive(
      [
        // {
        //   user_id:"",
        //   user_name:"test",
        //   user_content:"123",
        //   user_photo:null,
        //   time:"2024年4月25日16:49",
        // },
      ],
    ) 
    let member_num = ref("0");
    //在这个页面中，member_list就是好友列表
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
    let friend_list = reactive(
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

    //存储给哪个朋友发消息的朋友id
    let toWhichFriend = ref();
    
    //发送聊天消息给后端
    const sendMessage = (message) =>{
      if(message == ""){
        ElNotification({
          title: '发送消息不可以为空哦~',
          type: 'error',
        })
        return ;
      }
      $.ajax({
          url:"http://127.0.0.1:3003/chatApi/message/sendFriend",
          type:"post",
          data:{
            user_id:user_id,
            friend_id:toWhichFriend.value,
            user_content:message,
            time:getDateIntervar(),
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

    //获取好友列表
    const getFriendList = () => {
      $.ajax({
        url:"http://127.0.0.1:3003/chatApi/friendList/get",
        type:"post",
        success(resp){
          for(let i = 0 ; i < resp.length ; i ++){
              if(resp[i].id == user_id){
                continue;
              }
              friend_list.push({
              friend_id:resp[i].id,
              friend_photo:resp[i].photo,
              friend_name:resp[i].username,
            });
          }
          
          console.log(resp);
        },
        error(resp){
          console.log(resp);
        }
      })
    }

    //选择朋友，修改toWhichFriend这个变量,同时修改content_list中的内容
    const chooseFriend = (id) =>{
      toWhichFriend.value = id;

      //清空content_list
      content_list.length = 0;
      
      let list_temp = reactive(getStoreInfo(id));

      for(let i = 0 ; i < list_temp.length ; i ++){

        content_list.push({
          user_id:list_temp[i].user_id,
          user_name:list_temp[i].user_name,
          user_photo:list_temp[i].user_photo,
          user_content:list_temp[i].user_content,
          time:list_temp[i].time,
        });
      }
      console.log(list_temp);
      console.log(content_list);
    }

    const getStoreInfo = (id) =>{
      return store.getters.getContentById(id);
    }

    //临时启用！！！,message只有发送内容，没有任何其他内容
    const addContent = (message) =>{
      let temp = reactive({
        user_id:user_id,
        user_name:user_name,
        user_content:message,
        user_photo:user_photo,
        time:getDate(getDateIntervar()),
      })

      content_list.push({   //请不要直接push这个temp
        user_id:user_id,
        user_name:user_name,
        user_content:message,
        user_photo:user_photo,
        time:getDate(getDateIntervar()),
      })

      saveMyContentToStore(temp);
    }

    //将输入消息保存到store中,传入参数除了聊天消息的内容，其他都现有
    const saveMyContentToStore = (message) => {
      store.commit({
        type:"addContent",
        id:toWhichFriend.value + "",//修改为字符类型以统一类型，后端传的是string类型
        user_id:message.user_id,
        user_name:message.user_name,
        user_content:message.user_content,
        user_photo:message.user_photo,
        time:getDate(message.time),
      })
    }
    //将接收的消息存入store中
    const saveReceiveContentToStore = (message) => {
      console.log("正在将接收的消息存入store:"+message.user_id);
      store.commit({
        type:"addContent",
        id:message.user_id,
        user_id:message.user_id,
        user_name:message.user_name,
        user_content:message.user_content,
        user_photo:message.user_photo,
        time:getDate(message.time),
      })
    }

    //处理来自后端的消息并添加到content_list数组
    const updateContent = (message) => {
      //时间临时变量
      let time = ref("");
      //消息中的时间戳
      let receive_time = getDate(message.time);

      //如果这是第一条消息或者间隔时间超过五分钟，则需要显示时间
      if(content_list.length < 2 || (getDateIntervar() - receive_time) > 5*60*1000){
        time.value = receive_time;
      }

      //消息是发给我的且我刚好在和他对话，是则显示出来，后面这个需要使用==不能用===,可能是类型问题？
      if(message.friend_id === user_id && message.user_id == toWhichFriend.value){
        content_list.push({
          user_id:message.user_id,
          user_name:message.user_name,
          user_content:message.user_content,
          user_photo:message.user_photo,
          time:time.value,
        })
      }
    
      saveReceiveContentToStore(message);
    }
    
    //移除在线成员
    const removeMember = (id) =>{
      for(let i = 0 ; i < member_list.length ; i ++){
        if(member_list[i].member_id == id){
          member_list.splice(i,1);
          //成员数-1
          member_num.value --;
        }
      }
    }

    //添加在线成员
    const addMember = () =>{
      
    }
    
    //时间戳转换成当前时间
    const getDate = (x) => {
      //当前系统时间戳
      const current_time_intervar = new Date();
      //消息发送时的时间戳
      const date = new Date(Number(x));
      const years = date.getFullYear();
      const months = date.getMonth() + 1;
      const days = date.getDate();
      const hours = date.getHours();
      const minute = date.getMinutes().toString().padStart(2, '0');
    
      //时间戳差值如果跨年则显示包括年份的完整时间
      if(current_time_intervar.getFullYear() - years > 0){
        return years + "年" + months+ "月" + days + "日" + hours + ":" + minute;
      }
      //时间戳差值超过一个星期则显示包含月份的日期
      if(current_time_intervar.getDate() - days > 6){
        return (months + 1)+ "月" + days + "日" + hours + ":" + minute;
      }
      //时间戳差值超过两天则显示星期几，此处待优化
      if(current_time_intervar.getDate() - days > 2){
        const week = ["星期日","星期一","星期二","星期三","星期四","星期五","星期六"];
        return week[days % 7] + date.getHours() + ":" + minute;
      }
      //时间戳差值如果夸日则显示昨天
      if(current_time_intervar.getDate() - days === 1){
        return "昨天" + hours + ":" + minute;
      }
      //判断凌晨、上午、下午
      //消息发送时的时间戳如果是在下午
      if(hours < 6){
        return "凌晨" + hours + ":" + minute;
      }else if(hours < 12){
        return "上午" + hours + ":" + minute;
      }else if(hours == 12){
        return "中午" + hours + ":" + minute;
      }else if(hours < 18){
        return "下午" + hours + ":" + minute;
      }else if(hours < 24){
        return "晚上" + hours + ":" + minute;
      }
      
    }

    //获取时间戳
    const getDateIntervar = () => {
      const date = new Date();
      const sedonds = date.getTime();
      return sedonds;
    }

    let socket = null;
    //挂载时(进入聊天室)自动调用这个函数，同时持续到取消挂载为止
    onMounted(() => {

      socket = new WebSocket(socketUrl);
     
      socket.onopen = () => {
        //链接建立的时候需要获取好友列表
        getFriendList();
        ElNotification({
          title: '连接成功，开始和你的好友聊天吧！',
          type: 'success',
        })
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
          ElNotification({
          title:"来自："+message.user_name+"的消息："+message.user_content,
          type: 'success',
          })
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
      user_id,
      user_photo,
      input_content,
      content_list,
      member_list,
      friend_list,
      member_num,
      toWhichFriend,
      sendMessage,
      updateContent,
      removeMember,
      addMember,
      getDate,
      getDateIntervar,
      getFriendList,
      chooseFriend,
      addContent,
      saveMyContentToStore,
      getStoreInfo,
    }
  },
};
</script>

<style scoped>
.chat-time{
  font-size: 10px;
  color:white;
  text-align: center;
}
.chat-avatar{
  width:40px;
  height:40px;
  border-radius:50%;
  float: left;
}
.chat-item-box{
  max-width: 600px;
}
.user-info{
  color:rgb(239,241,2);
  font-size:15px;
}
.chat-content{
  color:black;
  max-width: 400px;
  padding-left:5px;
  padding-right:5px;
  background-color:white;
  border-radius:7px;
}
.chat-time-right{
  font-size: 10px;
  color:white;
  text-align: center;
}
.chat-item-right{
  float:right;
}
.chat-avatar-right{
  width:40px;
  height:40px;
  border-radius:50%;
  float: right;
}
.chat-item-box-right{
  max-width: 600px;
}
.user-info-right{
  color:rgb(239,241,2);
  font-size:15px;
  text-align: right;
}
.chat-content-right{
  color:black;
  max-width: 400px;
  padding-left:5px;
  padding-right:5px;
  background-color:white;
  border-radius:7px;
  display: inline;
  float: right;
}
</style>
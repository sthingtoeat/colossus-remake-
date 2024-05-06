// import store from ".";

export default {
    state: [{
        // id:"",          //聊天对象的id
        // content_list:[{
        //     user_id:"",     //发送聊天消息的用户id
        //     user_name:"",
        //     user_content:"",
        //     user_photo:"",
        //     time:"", 
        // }]     
    }],
    getters: {
        getContentById:(state) => (id) =>{
            //这里id可能会出现字符类型也可能是int类型，所以不能使用===
            const item = state.find(item => item.id == id);  
            return item ? item.content_list : [];
        }
    },
    mutations: {
        //content相较于上面的content_list,里面多传了一个id
        addContent(state, content){
            let found = false;

            let contentInfo = ({
                user_id:content.user_id,
                user_name:content.user_name,
                user_content:content.user_content,
                user_photo:content.user_photo,
                time:content.time,
            })
            for(let i = 0 ; i < state.length ; i ++){
                //如果有聊天记录,则找到对应id的content_list并添加
                if(state[i].id === content.id){
                    state[i].content_list.push(contentInfo);
                    found = true;
                    break;
                }
            }
            if(!found){
                state.push({
                    id:content.id + "",//以防id是int类型
                    content_list:[contentInfo]      //此处的中括号是关键
                })                                  //用中括号包裹则不会重新创建state对象
            }//此处不加上中括号，则会导致content_list与上面设定的不一致,导致创建state对象
            //导致state.length增加

            // console.log("当前state大小："+state.length);
            // console.log("拥有聊天记录数量：" + state[1].content_list.length);
            console.log(state);
            // console.log("其最新内容为" + state[1].content_list[state[1].content_list.length - 1].user_content)
        }
    },
    actions: {
        
    },
    modules: {
    }
}
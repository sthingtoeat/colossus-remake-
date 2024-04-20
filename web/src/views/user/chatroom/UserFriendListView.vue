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

                </div>
                <div>
                  <el-input :span="18" v-model="input" placeholder="说点什么吧~" />
                  <el-button style="float:right;margin-top:5px" type="primary">发送</el-button>
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
export default {
  components: {
    ContentField,
  },
  setup() {},
};
</script>

<style scoped>
</style>
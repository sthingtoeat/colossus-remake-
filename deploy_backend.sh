# 传送四个jar包到宿主机

scp -r backendcloud/backend/target/backend-0.0.1-SNAPSHOT.jar space:kob  	

scp -r backendcloud/botrunningsystem/target/botrunningsystem-0.0.1-SNAPSHOT.jar space:kob  

scp -r backendcloud/chatsystem/target/chatsystem-0.0.1-SNAPSHOT.jar space:kob  

scp -r backendcloud/matchingsystem/target/matchingsystem-0.0.1-SNAPSHOT.jar space:kob  

# 调用宿主机中的脚本，脚本内容大致为传送jar包到kob容器里

ssh space "./update_kob.sh"

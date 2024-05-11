# 传送dist

scp -r web/dist space:kob  	

# 调用宿主机中的脚本，脚本内容大致为传送dist到kob容器里

ssh space "./update_kob_web.sh"

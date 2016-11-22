## 谨慎修改 by liangchaolei

TASK_RDP_NAME=`/export/servers/storm/storm/bin/storm list | grep rdpStorm | awk '{print $1}'`
if [ -n "$TASK_RDP_NAME" ];then
	echo "begin stop rdpStorm==========="
	/export/servers/storm/storm/bin/storm kill rdpStorm
else
	echo "rdpStorm is not in active==========="
fi

i=1
while(($i<20))
do
	TASK_RDP_NAME=`/export/servers/storm/storm/bin/storm list | grep rdpStorm | awk '{print $1}'`
	if [ -n "$TASK_RDP_NAME" ];then
		echo  "rdpStrom is stopping==========="$i
		i=$(($i+1))
		sleep 5
	else
		i=21
	fi
done

if [[ $i = 21 ]];then
	echo "rdpStorm stop success==========="
else
	echo "rdpStorm stop fail==========="${i}
fi
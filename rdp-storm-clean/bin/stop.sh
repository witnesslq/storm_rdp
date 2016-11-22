## 谨慎修改 by liangchaolei

TASK_RDP_NAME=`/export/servers/storm/storm/bin/storm list | grep cleanStorm | awk '{print $1}'`
if [ -n "$TASK_RDP_NAME" ];then
	echo "begin stop rdpStorm==========="
	/export/servers/storm/storm/bin/storm kill cleanStorm
else
	echo "cleanStorm is not in active==========="
fi

i=1
while(($i<20))
do
	TASK_RDP_NAME=`/export/servers/storm/storm/bin/storm list | grep cleanStorm | awk '{print $1}'`
	if [ -n "$TASK_RDP_NAME" ];then
		echo  "cleanStorm is stopping==========="$i
		i=$(($i+1))
		sleep 5
	else
		i=21
	fi
done

if [[ $i = 21 ]];then
	echo "cleanStorm stop success==========="
else
	echo "cleanStorm stop fail==========="${i}
fi
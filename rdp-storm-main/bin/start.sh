## 谨慎修改

TASK=`/export/servers/storm/storm/bin/storm list | grep rdpStorm | awk '{print $1}'`
i=1
echo $TASK
while(($i<4))
do
	if [ -n "$TASK" ];then
		echo "rdpStorm is running...,start error,try time :"${i}
		i=$(($i+1))
		sleep 3
	else
		i=5
		echo "rdpStorm will start"
		/export/servers/storm/storm/bin/storm jar /export/Instances/mja-sdk-storm/server1/runtime/target/rdp-storm-main-1.0-SNAPSHOT.jar  com.jd.mja.rdp.storm.main.Builder "rdpStorm";
	fi
done

if [[ $i = 5 ]];then
	echo "rdpStorm start success======"
else
	echo "rdpStorm start fail======"${i}
fi
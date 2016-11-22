## 谨慎修改

TASK=`/export/servers/storm/storm/bin/storm list | grep cleanStorm | awk '{print $1}'`
i=1
echo $TASK
while(($i<4))
do
	if [ -n "$TASK" ];then
		echo "cleanStorm is running...,start error,try time :"${i}
		i=$(($i+1))
		sleep 3
	else
		i=5
		echo "cleanStorm will start"
		/export/servers/storm/storm/bin/storm jar /export/Instances/mja-sdk-dataclean-storm/server1/runtime/target/rdp-storm-clean-1.0-SNAPSHOT.jar  com.jd.mja.rdp.storm.clean.Builder "cleanStorm";
	fi
done

if [[ $i = 5 ]];then
	echo "cleanStorm start success======"
else
	echo "cleanStorm start fail======"${i}
fi
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lxb on 2015/7/6.
 */
public class MyTest {

    public static void main(String[] args){
         Long t=System.currentTimeMillis();
         System.out.println(t/(10*1000)*(10*1000));

        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss:SSS");

        System.out.println( sdf.format(new Date(t)));
        System.out.println( sdf.format(new Date(t/(10*1000)*(10*1000))));

     }
}

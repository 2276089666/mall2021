import com.aliyun.oss.OSSClient;
import com.aliyuncs.exceptions.ClientException;
import com.cloud.mall.thirdParty.MallThirdPartyApplication;
import com.cloud.mall.thirdParty.component.SmsComponent;
import com.cloud.mall.thirdParty.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ws
 * @Date 2021/1/23 17:28
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallThirdPartyApplication.class)
public class ThridPartyTest {
    @Autowired
    OSSClient ossClient;
    @Test
    public void testFile() throws FileNotFoundException {
        String bucketName = "mall2021";
        // 上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        String fileUrl = "C:\\Users\\22760\\Desktop\\学习笔记\\cloud\\2020-11-15 版本选择 203420.assets\\image-20201114103248744.png";
        // 上传文件流。
        InputStream inputStream = new FileInputStream(fileUrl);
        //<yourObjectName>
        String content = "c.png";
        ossClient.putObject(bucketName, content, inputStream);
        System.out.println("上传成功!!!");
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    @Test
    public void phoneCodeTest() throws ClientException {
        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "c16e94aeffff476abc8f2c080f60a594";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", "13235438770");
        querys.put("param", "**code**:9999,**minute**:5");
        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", "a09602b817fd47e59e7c6e603d3f088d");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void codeSendTest(){
        smsComponent.sendSmsCode("13235438770","4475");
    }
}

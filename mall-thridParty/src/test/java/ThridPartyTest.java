import com.aliyun.oss.OSSClient;
import com.cloud.mall.thirdParty.MallThirdPartyApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
}

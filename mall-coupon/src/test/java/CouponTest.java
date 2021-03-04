import com.cloud.mall.coupon.MallCouponApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author ws
 * @Date 2021/1/9 16:23
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallCouponApplication.class)
public class CouponTest {
    @Value("${user}")
    String name;

    @Test
    public void test(){

        System.out.println(name);
    }
}

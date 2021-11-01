package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.date.DateTime;
import com.whaleal.icefrog.core.date.DateUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * 身份证单元测试
 *
 * @author Looly
 * @author wh
 */
public class IdentityCardUtilTest {

    private static final String ID_18 = "321083197812162119";
    private static final String ID_15 = "150102880730303";

    @Test
    public void isValidCardTest() {
        boolean valid = IdentityCardUtil.isValidCard(ID_18);
        Assert.assertTrue(valid);

        boolean valid15 = IdentityCardUtil.isValidCard(ID_15);
        Assert.assertTrue(valid15);

        // 无效
        String idCard = "360198910283844";
        Assert.assertFalse(IdentityCardUtil.isValidCard(idCard));

        // 生日无效
        idCard = "201511221897205960";
        Assert.assertFalse(IdentityCardUtil.isValidCard(idCard));

        // 生日无效
        idCard = "815727834224151";
        Assert.assertFalse(IdentityCardUtil.isValidCard(idCard));
    }

    @Test
    public void convert15To18Test() {
        String convert15To18 = IdentityCardUtil.convert15To18(ID_15);
        Assert.assertEquals("150102198807303035", convert15To18);

        String convert15To18Second = IdentityCardUtil.convert15To18("330102200403064");
        Assert.assertEquals("33010219200403064X", convert15To18Second);
    }

    @Test
    public void getAgeByIdCardTest() {
        DateTime date = DateUtil.parse("2017-04-10");

        int age = IdentityCardUtil.getAgeByIdCard(ID_18, date);
        Assert.assertEquals(age, 38);

        int age2 = IdentityCardUtil.getAgeByIdCard(ID_15, date);
        Assert.assertEquals(age2, 28);
    }

    @Test
    public void getBirthByIdCardTest() {
        String birth = IdentityCardUtil.getBirthByIdCard(ID_18);
        Assert.assertEquals(birth, "19781216");

        String birth2 = IdentityCardUtil.getBirthByIdCard(ID_15);
        Assert.assertEquals(birth2, "19880730");
    }

    @Test
    public void getProvinceByIdCardTest() {
        String province = IdentityCardUtil.getProvinceByIdCard(ID_18);
        Assert.assertEquals(province, "江苏");

        String province2 = IdentityCardUtil.getProvinceByIdCard(ID_15);
        Assert.assertEquals(province2, "内蒙古");
    }

    @Test
    public void getCityCodeByIdCardTest() {
        String codeByIdCard = IdentityCardUtil.getCityCodeByIdCard(ID_18);
        Assert.assertEquals("32108", codeByIdCard);
    }

    @Test
    public void getGenderByIdCardTest() {
        int gender = IdentityCardUtil.getGenderByIdCard(ID_18);
        Assert.assertEquals(1, gender);
    }

    @Test
    public void isValidCard18Test() {
        boolean isValidCard18 = IdentityCardUtil.isValidCard18("3301022011022000D6");
        Assert.assertFalse(isValidCard18);

        // 不忽略大小写情况下，X严格校验必须大写
        isValidCard18 = IdentityCardUtil.isValidCard18("33010219200403064x", false);
        Assert.assertFalse(isValidCard18);
        isValidCard18 = IdentityCardUtil.isValidCard18("33010219200403064X", false);
        Assert.assertTrue(isValidCard18);

        // 非严格校验下大小写皆可
        isValidCard18 = IdentityCardUtil.isValidCard18("33010219200403064x");
        Assert.assertTrue(isValidCard18);
        isValidCard18 = IdentityCardUtil.isValidCard18("33010219200403064X");
        Assert.assertTrue(isValidCard18);
    }

    @Test
    public void isValidHKCardIdTest() {
        String hkCard = "P174468(6)";
        boolean flag = IdentityCardUtil.isValidHKCard(hkCard);
        Assert.assertTrue(flag);
    }

    @Test
    public void isValidTWCardIdTest() {
        String twCard = "B221690311";
        boolean flag = IdentityCardUtil.isValidTWCard(twCard);
        Assert.assertTrue(flag);
        String errTwCard1 = "M517086311";
        flag = IdentityCardUtil.isValidTWCard(errTwCard1);
        Assert.assertFalse(flag);
        String errTwCard2 = "B2216903112";
        flag = IdentityCardUtil.isValidTWCard(errTwCard2);
        Assert.assertFalse(flag);
    }
}

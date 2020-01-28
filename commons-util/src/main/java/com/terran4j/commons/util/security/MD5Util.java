package com.terran4j.commons.util.security;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class MD5Util {

    public static final String ALGORITHM_MD5 = "MD5";

    private static MessageDigest md = null;

    private static final MessageDigest getMessageDigest() throws BusinessException {
        if (md != null) {
            return md;
        }
        synchronized (MD5Util.class) {
            if (md != null) {
                return md;
            }
            try {
                md = MessageDigest.getInstance(ALGORITHM_MD5);
                return md;
            } catch (NoSuchAlgorithmException e) {
                log.error("NoSuchAlgorithmException: " + e.getMessage(), e);
                throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).put("algorithm", ALGORITHM_MD5)
                        .setMessage("No Such Algorithm: ${algorithm}");
            }
        }
    }

    /**
     * 给一组数据计算签名。<br>
     * 签名生成的通用步骤如下：<br>
     * 第一步，设所有发送或者接收到的数据为集合M，将集合M内非空参数值的参数
     * 按照参数名ASCII码从小到大排序（字典序），使用URL键值对的
     * 格式（即key1=value1&key2=value2…）拼接成字符串stringA。<br>
     * 特别注意以下重要规则：<br>
     * <li>参数名ASCII码从小到大排序（字典序）；</li>
     * <li>如果参数的值为空不参与签名；</li>
     * <li>参数名区分大小写；</li>
     * <li>验证调用返回或微信主动通知签名时，传送的sign参数不参与签名，
     * 将生成的签名与该sign值作校验。</li>
     * 第二步，在 stringA 最后拼接上 key 得到 stringSignTemp 字符串，
     * 并对 stringSignTemp 进行MD5运算，再将得到的字符串所有字符转换为大写，
     * 得到 sign 值 signValue 。
     *
     * @param data      数据。
     * @param secretKey 密钥。
     * @return 签名串。
     * @throws BusinessException 签名出错。
     */
    public static String signature(Map<String, String> data, String secretKey) throws BusinessException {

        StringBuilder sb = new StringBuilder();

        Object[] keys = data.keySet().toArray();
        Arrays.sort(keys);
        for (Object key : keys) {
            String value = data.get(key);
            if (!StringUtils.isEmpty(value)) {
                sb.append(key + "=" + value + "&");
            }
        }

        String stringA = "";
        if (sb.length() > 0) {
            stringA += sb.substring(0, sb.length() - 1);
        }

        String stringSignTemp = stringA + secretKey;
        log.info("MD5 signature, data: {}", data);
        String signValue = md5(stringSignTemp).toUpperCase();
        return signValue;
    }

    public static String md5(String text) throws BusinessException {
        try {
            byte[] input = text.getBytes();
            byte[] data = getMessageDigest().digest(input);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < data.length; i++) {
                sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (BusinessException e) {
            throw e.put("text", text);
        }
    }

}

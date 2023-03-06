package util;

import com.yubico.webauthn.data.ByteArray;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Random;

public class FidoUtil {
    /**
     * 生成并获取挑战Challenge。随机生成Challenge并进行base64编码
     *
     * @return 一个ByteArray数组
     * TODO:确认返回类型是ByteArray还是Base64.getEncoder().encodeToString(challengeBytes)
     */
    public ByteArray generateChallenge() {
        byte[] challengeBytes = new byte[32];
        new Random().nextBytes(challengeBytes);
        System.out.println(Base64.getEncoder().encodeToString(challengeBytes));
//        return Base64.getEncoder().encodeToString(challengeBytes);
        return ByteArray.fromBase64(Base64.getEncoder().encodeToString(challengeBytes));
    }
}

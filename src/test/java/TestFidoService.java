import org.junit.Test;
import service.FidoService;

import java.util.Arrays;

public class TestFidoService {
    @Test
    public void testIsRegistered() {
        FidoService fidoService = new FidoService();

        boolean result = fidoService.isRegistered("Liu");
        System.out.println("[+] TestFidoService.testIsRegistered: " + result);

        result = fidoService.isRegistered("Xie");
        System.out.println("[+] TestFidoService.testIsRegistered: " + result);
    }

    @Test
    public void testRegisterFidoUser() {
        FidoService fidoService = new FidoService();

        byte[] testPubKey = new byte[]{1, 2, 3};

        System.out.println(Arrays.toString(testPubKey));

        fidoService.RegisterFidoUser("Xie", testPubKey);
    }
}

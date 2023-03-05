import old.FIDO.FidoServer;
import org.junit.Test;

import java.io.IOException;

public class FidoServerTest {
    @Test
    public static void main(String[] args) throws IOException {
        FidoServer fidoServer = new FidoServer();
        System.out.println((fidoServer.isRegister(1)));
        fidoServer.fidoRegister("Test","PubKey");
    }
}

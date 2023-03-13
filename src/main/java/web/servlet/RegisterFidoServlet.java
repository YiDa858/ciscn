package web.servlet;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import service.FidoService;
import util.CredentialRepositoryImpl;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterFidoServlet extends HttpServlet {
    private FidoService fidoService = new FidoService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 实例化RelyingParty对象
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder().id("http://localhost:8080").name("test").build();
        RelyingParty rp = RelyingParty.builder().identity(rpIdentity).credentialRepository(new CredentialRepositoryImpl()).build();

        // 1. 生成验证挑战

        // 2. 将验证挑战返回给浏览器
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
}

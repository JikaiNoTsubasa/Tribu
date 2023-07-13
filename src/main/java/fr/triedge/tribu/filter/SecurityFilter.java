package fr.triedge.tribu.filter;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.tribu.api.LoginController;
import fr.triedge.tribu.db.DB;
import fr.triedge.tribu.model.User;
import fr.triedge.tribu.utils.Vars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@Component
@Order(1)
public class SecurityFilter implements Filter {

    Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        log.debug("Executing SecurityFilter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String url = req.getRequestURI();// /todo/home

        if (url.contains(Vars.LOGIN) || url.contains("css") || url.contains("png")){
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = req.getSession(true);
        User user = (User)session.getAttribute("user");

        if (user == null){
            log.debug("No user found in session.");
            // Verify cookie
            Cookie cookie = getCookie(req.getCookies(), "TribuUser");
            if (cookie != null){
                log.debug("Parsing cookie...");
                SPassword pwd = new SPassword(cookie.getValue());
                User u = null;
                try {
                    u = DB.getInstance().loadUser(pwd.getDecrypted());
                    if (u != null){
                        log.debug("User authenticated.");
                        session.setAttribute("user", u);
                        res.sendRedirect("home");
                        return;
                    }else{
                        log.debug("Could not find user for this cookie.");
                        res.sendRedirect("login");
                        return;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }else{
                log.debug("Cookie is null.");
                res.sendRedirect("login");
                return;
            }
        }
        log.debug("User authenticated.");
        chain.doFilter(req, res);
    }

    private Cookie getCookie(Cookie[] cookies, String name){
        if (cookies == null || name == null)
            return null;
        for (Cookie c : cookies){
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }
}

package fr.triedge.tribu.api;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.tribu.db.DB;
import fr.triedge.tribu.model.User;
import fr.triedge.tribu.utils.Utils;
import fr.triedge.tribu.utils.Vars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import java.sql.SQLException;

@RestController
public class LoginController extends AbstractController{

    Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(path = Vars.LOGIN, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView login(
            @RequestParam(value = "username", required = false)String username,
            @RequestParam(value = "password", required = false)String password
    ){
        log.debug("Loading login page.");
        ModelAndView model = new ModelAndView("login.html");

        if (Utils.isValid(username) && Utils.isValid(password)){
            log.debug("Sent login request for "+username);
            try {
                User user = DB.getInstance().getUser(username, password);
                if (user != null){
                    log.debug("User authenticated.");
                    getSession().setAttribute("user", user);
                    createLoginCookie("TribuUser", new SPassword(username).getEncrypted());
                    return new ModelAndView("redirect:home");
                }else{
                    model.addObject("error", "Username or password incorrect");
                }
            } catch (SQLException e) {
                System.err.println(e);
                model.addObject("error", "Unexpected server error");
            }
        }

        return model;
    }

    @GetMapping(Vars.DISCONNECT)
    public ModelAndView disconnect(){
        getSession().setAttribute("user", null);
        deleteLoginCookie("TribuUser");
        return new ModelAndView("redirect:home");
    }

    public void createLoginCookie(String name, String value){
        SPassword pwd = new SPassword(value);
        Cookie cookie = new Cookie(name, pwd.getEncrypted());
        cookie.setMaxAge(86400);
        getHttpRep().addCookie(cookie);
    }

    public void deleteLoginCookie(String name){
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        getHttpRep().addCookie(cookie);
    }
}

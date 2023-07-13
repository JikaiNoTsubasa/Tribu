package fr.triedge.tribu.api;

import fr.triedge.tribu.db.DB;
import fr.triedge.tribu.utils.Vars;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

@RestController
public class UIController {

    @GetMapping(Vars.HOME)
    public ModelAndView home(){
        ModelAndView model = new ModelAndView("home.html");
        try {
            DB.getInstance().getCurrentConversations(null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return model;
    }
}

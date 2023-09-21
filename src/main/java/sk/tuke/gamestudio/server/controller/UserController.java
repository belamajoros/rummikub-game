package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/user")
public class UserController {

    private ArrayList<String> users = new ArrayList<>();

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login(String login) {
        users.add(login);

        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout() {
        users.clear();
        return "redirect:/";
    }

    public String getLoggedUser(int id) {
        return users.get(id);
    }

    public int getUsersSize()
    {
        return users.size();
    }

    public boolean isLogged(int id) {
        return users.get(id) != null;
    }
}
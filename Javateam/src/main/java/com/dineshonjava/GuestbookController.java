package com.dineshonjava;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
public class GuestbookController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String list(Model model, HttpServletRequest request) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Greeting").addSort("date", Query.SortDirection.DESCENDING);
        List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(30));
        model.addAttribute("greetings", greetings);

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user != null) {
            String username = user.getEmail();
            model.addAttribute("username", username.contains("@") ? username.split("@")[0] : username);
            model.addAttribute("logoutUrl", userService.createLogoutURL(request.getRequestURI()));
        } else {
            model.addAttribute("loginUrl", userService.createLoginURL(request.getRequestURI()));
        }
        return "list";
    }

    @RequestMapping(value = "greet", method = RequestMethod.POST)
    String greet(@RequestParam("name") String name,
                 @RequestParam("content") String content) {
        Entity greeting = new Entity("Greeting");
        greeting.setProperty("name", name);
        greeting.setProperty("content", content);
        greeting.setProperty("date", new Date());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(greeting);

        return "redirect:/";
    }
}

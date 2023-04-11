package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.dto.UserDTO;
import com.awbd.bankingApp.exceptions.ResourceNotFoundException;
import com.awbd.bankingApp.service.ClientService;
import com.awbd.bankingApp.utils.UserChecker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class HomeController {
    private final ManagementAPI managementAPI;
    private final ClientService clientService;

    public HomeController(ManagementAPI managementAPI, ClientService clientService) {
        this.managementAPI = managementAPI;
        this.clientService = clientService;
    }

    @GetMapping(value={"", "/"})
    public String main(Model model, HttpSession session, @AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        if (principal != null) {
            UserDTO userDTO = new UserDTO();
            String uid = (String) principal.getClaims().get("sub");
            User userAPI = managementAPI.users().get(uid, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
            Map<String, Object> metadata = userAPI.getUserMetadata();

            try {
                clientService.findByUid(uid);
            } catch (ResourceNotFoundException e) {
                System.out.println(e.getMessage());
                Client client = new Client(uid);
                clientService.save(client);
            }

            userDTO.setId(uid);
            userDTO.setLastName(userAPI.getName());
            userDTO.setEmail(userAPI.getEmail());

            if (metadata != null) {
                if (metadata.containsKey("phone")) {
                    userDTO.setPhone((String) metadata.get("phone"));
                } else {
                    userDTO.setPhone("-");
                }
            } else {
                userDTO.setPhone("-");
            }

            model.addAttribute("isAdmin", UserChecker.isAdmin(metadata));
            model.addAttribute("userData", userDTO);
            model.addAttribute("profile", principal.getClaims());
        }

        return "homepage";
    }
}
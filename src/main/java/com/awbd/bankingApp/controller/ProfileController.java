package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.dto.UserDTO;
import com.awbd.bankingApp.exceptions.InvalidPhoneNumberException;
import com.awbd.bankingApp.utils.PhoneNumberValidator;
import com.awbd.bankingApp.utils.UserChecker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Controller
public class ProfileController {
    private final ManagementAPI managementAPI;

    public ProfileController(ManagementAPI managementAPI) {
        this.managementAPI = managementAPI;
    }

    @PostMapping("/saveProfile")
    public String saveOrUpdate(@Valid @ModelAttribute UserDTO user,
                               BindingResult bindingResult) throws Auth0Exception {
        if (bindingResult.hasErrors()){
            return "profile";
        }

        User userAPI = managementAPI.users().get(user.getId(), new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();

        Map<String, Object> metadata = userAPI.getUserMetadata();
        if (user.getPhone().length() > 0) {
            try {
                PhoneNumberValidator.validate(user.getPhone());
                if (metadata != null) {
                    if (!metadata.containsKey("phone")) {
                        metadata.put("phone", user.getPhone());
                    } else {
                        metadata.replace("phone", user.getPhone());
                    }
                } else {
                    metadata = new HashMap<>();
                    metadata.put("phone", user.getPhone());
                }
            } catch (InvalidPhoneNumberException e) {
                System.out.println(e.getMessage());
            }
        }

        userAPI.setUserMetadata(metadata);
        userAPI.setEmail(user.getEmail());
        userAPI.setName(user.getLastName());

        // Call the update user endpoint to save the changes
        try {
            managementAPI.users().update(user.getId(), userAPI).execute();
        } catch (Auth0Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/" ;
    }

    @GetMapping("/profile")
    public ModelAndView profile() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;
        String code = getUserAsJSON(oauthToken.getPrincipal().getAttributes());

        ModelAndView mav = new ModelAndView("profile");
        ObjectMapper mapper = new ObjectMapper();
        UserDTO userDTO = new UserDTO();

        try {
            JsonNode data = mapper.readTree(code);
            String uid = data.get("sub").asText();

            try {
                User userAPI = managementAPI.users().get(uid, new UserFilter().withTotals(false).withFields("user_metadata,email,name,username", true)).execute();
                userDTO.setLastName(userAPI.getName());
                userDTO.setEmail(userAPI.getEmail());
                if (userAPI.getUserMetadata() != null) {
                    if (userAPI.getUserMetadata().containsKey("phone")) {
                        userDTO.setPhone((String) userAPI.getUserMetadata().get("phone"));
                    }
                }
                mav.addObject("isAdmin", UserChecker.isAdmin(userAPI.getUserMetadata()));
                userDTO.setId(uid);
            } catch (Auth0Exception e) {
                System.out.println(e.getMessage());
            }

            mav.addObject("code", data);
            mav.addObject("user", oauthToken.getPrincipal().getAttributes());
            mav.addObject("userData", userDTO);
            mav.addObject("uid", uid);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        return mav;
    }

    private String getUserAsJSON(Object attributes) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            return "{\"message\":\"" + e + "\"}";
        }
    }
}
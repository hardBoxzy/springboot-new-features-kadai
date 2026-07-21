package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;

@Controller
public class FavoriteController {
	private final FavoriteRepository favoriteRepository ;
    private final UserRepository userRepository; 
    private final FavoriteService favoriteService;
    private final HouseRepository houseRepository;   
    public FavoriteController(FavoriteRepository favoriteRepository,UserRepository userRepository,
    		HouseRepository houseRepository,FavoriteService favoriteService) {
        this.userRepository = userRepository; 
        this.favoriteRepository = favoriteRepository;
        this.favoriteService = favoriteService;
        this.houseRepository = houseRepository;   
    }    
    
    @GetMapping("/user/favorites")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
    		Model model) {     
    	
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        Page<Favorite> favoritePage;
        favoritePage = favoriteRepository.findByUserOrderByCreatedAtDesc(user,pageable);
        model.addAttribute("favoritePage", favoritePage);
        return "user/favorites/index";
    }
    
    @PostMapping("/houses/{id}/favorites/create")
    public String create(
    		@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		@PathVariable(name = "id") Integer houseId,
    		@ModelAttribute House house,
    		RedirectAttributes redirectAttributes) {        
        
        User user = userDetailsImpl.getUser();
        
        favoriteService.create(house, user); 
        
        return "redirect:/houses/" + houseId;
    }    
    
    @PostMapping("/houses/{id}/favorites/delete")
    public String delete(
    		@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		@PathVariable(name = "id") Integer houseId,
    		RedirectAttributes redirectAttributes) {  
   	
    	  House house = houseRepository.getReferenceById(houseId);
    	  User user = userDetailsImpl.getUser();
    	  Favorite favorite = favoriteRepository.findFirstByUserAndHouse(user,house).orElse(null);
    	    if (favorite != null) {
    	        favoriteRepository.delete(favorite);
    	    }
        
        return "redirect:/houses/" + houseId;
    } 
}
package com.cos.costagram.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.cos.costagram.model.Follow;
import com.cos.costagram.model.Image;
import com.cos.costagram.model.Likes;
import com.cos.costagram.model.User;
import com.cos.costagram.repository.FollowRepository;
import com.cos.costagram.repository.ImageRepository;
import com.cos.costagram.repository.LikesRepository;
import com.cos.costagram.repository.UserRepository;
import com.cos.costagram.service.CustomUserDetails;

@Controller
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LikesRepository likesRepository;
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired
	private FollowRepository followRepository;
	
	@GetMapping("/")
	public String home() {
		return "/auth/join";
	}
	
	@GetMapping("/auth/login")
	public String authLogin() {
		return "/auth/login";
	}
	
	@GetMapping("/auth/join")
	public String authJoin() {
		return "/auth/join";
	}
	
	@PostMapping("/auth/create")
	public String create(User user) {
		String rawPassword = user.getPassword();
		String encPassword = passwordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);
		return "/auth/login";
	}
	
	@GetMapping("/user/{id}")
	public String userDetail(@PathVariable int id, Model model, @AuthenticationPrincipal CustomUserDetails userDetail) {
		Optional<User> userO = userRepository.findById(userDetail.getUser().getId());
		
		//유저정보(username, name, bio, website)
		User user = userO.get();
		//이미지정보+좋아요카운트정보(User:Image), (User:Image.count())
		List<Image> imageList = imageRepository.findByUserIdOrderByCreateDateDesc(id);
		int imageCount = imageList.size();
		for(Image i : imageList) {
			List<Likes> likeList = likesRepository.findByImageId(i.getId());
			i.setLikeCount(likeList.size());
		}
		
		//팔로우정보(User:Follow:User.count())
		List<Follow> followList = followRepository.findByFromUserId(id);
		int followCount = followList.size();
		//팔로워정보(User:Follower:User.count())
		List<Follow> followerList = followRepository.findByToUserId(id);
		int followerCount = followerList.size();
		
		model.addAttribute("user", user);
		model.addAttribute("id", id);
		model.addAttribute("imageList", imageList);
		model.addAttribute("imageCount", imageCount);
		model.addAttribute("followCount", followCount);
		model.addAttribute("followerCount", followerCount);
		return "/user/user";
	}
	
	@GetMapping("/explore")
	public String explore() {
		return "/user/explore";
	}
}

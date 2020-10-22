package com.example.htmlunitrepro;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WelcomeController {

	@GetMapping("/{name}")
	public String init(String name, Model model) {
		model.addAttribute("myParam", "Guillaume");
		return name;
	}

	@PostMapping("/submit")
	public @ResponseBody String submit(@RequestParam String subject, @RequestParam String message, Model model) {
		return subject + " " + message;
	}

}

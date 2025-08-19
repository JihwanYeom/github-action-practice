package org.example.githubactionpractice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return "list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "form";
    }

    @PostMapping
    public String createPost(@ModelAttribute Post post) {
        post.setCreatedAt(java.time.LocalDateTime.now());
        postRepository.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        postRepository.findById(id).ifPresent(post -> model.addAttribute("post", post));
        return "view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        postRepository.findById(id).ifPresent(post -> model.addAttribute("post", post));
        return "form";
    }

    @PostMapping("/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {
        post.setId(id);
        post.setCreatedAt(postRepository.findById(id).get().getCreatedAt()); // Preserve original creation time
        postRepository.save(post);
        return "redirect:/posts/{id}";
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return "redirect:/posts";
    }
}

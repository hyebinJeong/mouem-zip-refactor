package org.scoula.register.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {
    @GetMapping("/safety-check")
    public String safetyCheckPage() {
        return "forward:/index.html";  // 또는 적절한 뷰 이름
    }

    @GetMapping("/safety-check/{registerId}")
    public String analysisResultPage(@PathVariable("registerId") Integer registerId, Model model) {
        model.addAttribute("registerId", registerId);
        return "forward:/index.html"; // 또는 분석 결과 전용 페이지
    }
}

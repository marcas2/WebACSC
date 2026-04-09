package com.tuapp.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/anomalias")
public class AnomaliaManagementController {

    @GetMapping
    public String redirectToCategorias() {
        return "redirect:/dashboard/categorias-anomalias";
    }
}

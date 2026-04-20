package CodeBloom.AlquilaTusVehiculos.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

@Controller
public class TestSecurityController {

    @GetMapping("/public/test")
    @ResponseBody
    public String publicTest() {
        return "Ruta pública OK";
    }

    @GetMapping("/user/test")
    @ResponseBody
    public String userTest() {
        return "Ruta USER OK";
    }

    @GetMapping("/admin/test")
    @ResponseBody
    public String adminTest() {
        return "Ruta ADMIN OK";
    }
}
package ma.enset.productsapp.web;

import lombok.Data;
import ma.enset.productsapp.repositories.ProductRepository;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.client.RestTemplate;

@Controller
public class ProductController{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private KeycloakRestTemplate keycloakRestTemplate;

    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/products")
    public String products(Model model){
        model.addAttribute("products",productRepository.findAll());
        return "products";
    }
    @GetMapping("/suppliers")
    public String suppliers(Model model){

        /*KeycloakAuthenticationToken token=(KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal=(KeycloakPrincipal) token.getPrincipal();//on recupere l'utilisateur authentifier
        KeycloakSecurityContext sessionUser=principal.getKeycloakSecurityContext();//context Keyclock ou il ya les infos de user
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("Authorization","Bearer "+sessionUser.getTokenString());
        HttpEntity httpEntity=new HttpEntity(httpHeaders);
        ResponseEntity<PagedModel<Supplier>> response=restTemplate.exchange(
                "http://localhost:8083/suppliers",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<PagedModel<Supplier>>() {
                    @Override
                    public Type getType() {
                        return super.getType();
                    }
                }
        );
        */
        //keycloakRestTemplate il ajout le jwt
        PagedModel<Supplier> pageSuppliers=  keycloakRestTemplate.getForObject("http://localhost:8083/suppliers",PagedModel.class);
     model.addAttribute("suppliers",pageSuppliers);
        return "suppliers";
    }

    @GetMapping("/jwt")
    @ResponseBody
    public Map<String,String> map(HttpServletRequest request){
        KeycloakAuthenticationToken token=(KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal=(KeycloakPrincipal) token.getPrincipal();//on recupere l'utilisateur authentifier
        KeycloakSecurityContext sessionUser=principal.getKeycloakSecurityContext();//context Keyclock ou il ya les infos de user
        Map<String,String> map=new HashMap<>();
        map.put("access_token",sessionUser.getTokenString());
        return map;
    }
    
    @ExceptionHandler(Exception.class)
    public String expceptionHandler(Exception e,Model model){
        model.addAttribute("errorMessage","pas Authoriser");
        return "errors";
    }
}
@Data
class Supplier{
    private Long id;
    private String name;
    private String email;
}

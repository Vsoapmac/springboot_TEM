package smg.Vsoapmac.controller;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smg.Vsoapmac.bean.city;

@Controller
public class indexController {
    @Autowired
    private smg.Vsoapmac.Service.cityService cityService;

    @PostMapping(value = "/connectVue")
    @ResponseBody
    public String connectVue(){

        JSONObject jo = new JSONObject();
        city city = cityService.findById(1).get();
        jo.put("success", city.getName());
        return jo.toString();
    }
}
